/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.nd4j

import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.api.ops.impl.transforms.strict.ACosh
import org.nd4j.linalg.api.ops.impl.transforms.strict.ASinh
import org.nd4j.linalg.factory.Nd4j
import org.nd4j.linalg.ops.transforms.Transforms
import space.kscience.kmath.misc.PerformancePitfall
import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.nd.*
import space.kscience.kmath.operations.*

internal fun AlgebraND<*, *>.checkShape(array: INDArray): INDArray {
    val arrayShape = array.shape().toIntArray()
    if (!shape.contentEquals(arrayShape)) throw ShapeMismatchException(shape, arrayShape)
    return array
}


/**
 * Represents [AlgebraND] over [Nd4jArrayAlgebra].
 *
 * @param T the type of ND-structure element.
 * @param C the type of the element context.
 */
public sealed interface Nd4jArrayAlgebra<T, out C : Algebra<T>> : AlgebraND<T, C> {
    /**
     * Wraps [INDArray] to [Nd4jArrayStructure].
     */
    public fun INDArray.wrap(): Nd4jArrayStructure<T>

    /**
     * Unwraps to or acquires [INDArray] from [StructureND].
     */
    public val StructureND<T>.ndArray: INDArray

    override fun produce(initializer: C.(IntArray) -> T): Nd4jArrayStructure<T> {
        val struct = Nd4j.create(*shape)!!.wrap()
        struct.indicesIterator().forEach { struct[it] = elementContext.initializer(it) }
        return struct
    }

    @PerformancePitfall
    override fun StructureND<T>.map(transform: C.(T) -> T): Nd4jArrayStructure<T> {
        val newStruct = ndArray.dup().wrap()
        newStruct.elements().forEach { (idx, value) -> newStruct[idx] = elementContext.transform(value) }
        return newStruct
    }

    override fun StructureND<T>.mapIndexed(
        transform: C.(index: IntArray, T) -> T,
    ): Nd4jArrayStructure<T> {
        val new = Nd4j.create(*this@Nd4jArrayAlgebra.shape).wrap()
        new.indicesIterator().forEach { idx -> new[idx] = elementContext.transform(idx, this[idx]) }
        return new
    }

    override fun combine(
        a: StructureND<T>,
        b: StructureND<T>,
        transform: C.(T, T) -> T,
    ): Nd4jArrayStructure<T> {
        val new = Nd4j.create(*shape).wrap()
        new.indicesIterator().forEach { idx -> new[idx] = elementContext.transform(a[idx], b[idx]) }
        return new
    }
}

/**
 * Represents [GroupND] over [Nd4jArrayStructure].
 *
 * @param T the type of the element contained in ND structure.
 * @param S the type of space of structure elements.
 */
public sealed interface Nd4jArrayGroup<T, out S : Ring<T>> : GroupND<T, S>, Nd4jArrayAlgebra<T, S> {

    override val zero: Nd4jArrayStructure<T>
        get() = Nd4j.zeros(*shape).wrap()

    override fun add(a: StructureND<T>, b: StructureND<T>): Nd4jArrayStructure<T> =
        a.ndArray.add(b.ndArray).wrap()

    override operator fun StructureND<T>.minus(b: StructureND<T>): Nd4jArrayStructure<T> =
        ndArray.sub(b.ndArray).wrap()

    override operator fun StructureND<T>.unaryMinus(): Nd4jArrayStructure<T> =
        ndArray.neg().wrap()

    public fun multiply(a: StructureND<T>, k: Number): Nd4jArrayStructure<T> =
        a.ndArray.mul(k).wrap()
}

/**
 * Represents [RingND] over [Nd4jArrayStructure].
 *
 * @param T the type of the element contained in ND structure.
 * @param R the type of ring of structure elements.
 */
@OptIn(UnstableKMathAPI::class)
public sealed interface Nd4jArrayRing<T, out R : Ring<T>> : RingND<T, R>, Nd4jArrayGroup<T, R> {

    override val one: Nd4jArrayStructure<T>
        get() = Nd4j.ones(*shape).wrap()

    override fun multiply(a: StructureND<T>, b: StructureND<T>): Nd4jArrayStructure<T> =
        a.ndArray.mul(b.ndArray).wrap()
//
//    override operator fun Nd4jArrayStructure<T>.minus(b: Number): Nd4jArrayStructure<T> {
//        check(this)
//        return ndArray.sub(b).wrap()
//    }
//
//    override operator fun Nd4jArrayStructure<T>.plus(b: Number): Nd4jArrayStructure<T> {
//        check(this)
//        return ndArray.add(b).wrap()
//    }
//
//    override operator fun Number.minus(b: Nd4jArrayStructure<T>): Nd4jArrayStructure<T> {
//        check(b)
//        return b.ndArray.rsub(this).wrap()
//    }

    public companion object {
        private val intNd4jArrayRingCache: ThreadLocal<MutableMap<IntArray, IntNd4jArrayRing>> =
            ThreadLocal.withInitial(::HashMap)

        /**
         * Creates an [RingND] for [Int] values or pull it from cache if it was created previously.
         */
        public fun int(vararg shape: Int): Nd4jArrayRing<Int, IntRing> =
            intNd4jArrayRingCache.get().getOrPut(shape) { IntNd4jArrayRing(shape) }

        /**
         * Creates a most suitable implementation of [RingND] using reified class.
         */
        @Suppress("UNCHECKED_CAST")
        public inline fun <reified T : Number> auto(vararg shape: Int): Nd4jArrayRing<T, Ring<T>> = when {
            T::class == Int::class -> int(*shape) as Nd4jArrayRing<T, Ring<T>>
            else -> throw UnsupportedOperationException("This factory method only supports Long type.")
        }
    }
}

/**
 * Represents [FieldND] over [Nd4jArrayStructure].
 *
 * @param T the type of the element contained in ND structure.
 * @param F the type field of structure elements.
 */
public sealed interface Nd4jArrayField<T, out F : Field<T>> : FieldND<T, F>, Nd4jArrayRing<T, F> {
    override fun divide(a: StructureND<T>, b: StructureND<T>): Nd4jArrayStructure<T> =
        a.ndArray.div(b.ndArray).wrap()

    public operator fun Number.div(b: StructureND<T>): Nd4jArrayStructure<T> = b.ndArray.rdiv(this).wrap()

    public companion object {
        private val floatNd4jArrayFieldCache: ThreadLocal<MutableMap<IntArray, FloatNd4jArrayField>> =
            ThreadLocal.withInitial(::HashMap)

        private val doubleNd4JArrayFieldCache: ThreadLocal<MutableMap<IntArray, DoubleNd4jArrayField>> =
            ThreadLocal.withInitial(::HashMap)

        /**
         * Creates an [FieldND] for [Float] values or pull it from cache if it was created previously.
         */
        public fun float(vararg shape: Int): Nd4jArrayRing<Float, FloatField> =
            floatNd4jArrayFieldCache.get().getOrPut(shape) { FloatNd4jArrayField(shape) }

        /**
         * Creates an [FieldND] for [Double] values or pull it from cache if it was created previously.
         */
        public fun real(vararg shape: Int): Nd4jArrayRing<Double, DoubleField> =
            doubleNd4JArrayFieldCache.get().getOrPut(shape) { DoubleNd4jArrayField(shape) }

        /**
         * Creates a most suitable implementation of [FieldND] using reified class.
         */
        @Suppress("UNCHECKED_CAST")
        public inline fun <reified T : Any> auto(vararg shape: Int): Nd4jArrayField<T, Field<T>> = when {
            T::class == Float::class -> float(*shape) as Nd4jArrayField<T, Field<T>>
            T::class == Double::class -> real(*shape) as Nd4jArrayField<T, Field<T>>
            else -> throw UnsupportedOperationException("This factory method only supports Float and Double types.")
        }
    }
}

/**
 * Represents intersection of [ExtendedField] and [Field] over [Nd4jArrayStructure].
 */
public sealed interface Nd4jArrayExtendedField<T, out F : ExtendedField<T>> : ExtendedField<StructureND<T>>,
    Nd4jArrayField<T, F> {
    override fun sin(arg: StructureND<T>): StructureND<T> = Transforms.sin(arg.ndArray).wrap()
    override fun cos(arg: StructureND<T>): StructureND<T> = Transforms.cos(arg.ndArray).wrap()
    override fun asin(arg: StructureND<T>): StructureND<T> = Transforms.asin(arg.ndArray).wrap()
    override fun acos(arg: StructureND<T>): StructureND<T> = Transforms.acos(arg.ndArray).wrap()
    override fun atan(arg: StructureND<T>): StructureND<T> = Transforms.atan(arg.ndArray).wrap()

    override fun power(arg: StructureND<T>, pow: Number): StructureND<T> =
        Transforms.pow(arg.ndArray, pow).wrap()

    override fun exp(arg: StructureND<T>): StructureND<T> = Transforms.exp(arg.ndArray).wrap()
    override fun ln(arg: StructureND<T>): StructureND<T> = Transforms.log(arg.ndArray).wrap()
    override fun sqrt(arg: StructureND<T>): StructureND<T> = Transforms.sqrt(arg.ndArray).wrap()
    override fun sinh(arg: StructureND<T>): StructureND<T> = Transforms.sinh(arg.ndArray).wrap()
    override fun cosh(arg: StructureND<T>): StructureND<T> = Transforms.cosh(arg.ndArray).wrap()
    override fun tanh(arg: StructureND<T>): StructureND<T> = Transforms.tanh(arg.ndArray).wrap()

    override fun asinh(arg: StructureND<T>): StructureND<T> =
        Nd4j.getExecutioner().exec(ASinh(arg.ndArray, arg.ndArray.ulike())).wrap()

    override fun acosh(arg: StructureND<T>): StructureND<T> =
        Nd4j.getExecutioner().exec(ACosh(arg.ndArray, arg.ndArray.ulike())).wrap()

    override fun atanh(arg: StructureND<T>): StructureND<T> = Transforms.atanh(arg.ndArray).wrap()
}

/**
 * Represents [FieldND] over [Nd4jArrayDoubleStructure].
 */
public class DoubleNd4jArrayField(override val shape: IntArray) : Nd4jArrayExtendedField<Double, DoubleField> {
    override val elementContext: DoubleField get() = DoubleField

    override fun INDArray.wrap(): Nd4jArrayStructure<Double> = checkShape(this).asDoubleStructure()

    @OptIn(PerformancePitfall::class)
    override val StructureND<Double>.ndArray: INDArray
        get() = when (this) {
            is Nd4jArrayStructure<Double> -> checkShape(ndArray)
            else -> Nd4j.zeros(*shape).also {
                elements().forEach { (idx, value) -> it.putScalar(idx, value) }
            }
        }

    override fun scale(a: StructureND<Double>, value: Double): Nd4jArrayStructure<Double> {
        return a.ndArray.mul(value).wrap()
    }

    override operator fun StructureND<Double>.div(arg: Double): Nd4jArrayStructure<Double> {
        return ndArray.div(arg).wrap()
    }

    override operator fun StructureND<Double>.plus(arg: Double): Nd4jArrayStructure<Double> {
        return ndArray.add(arg).wrap()
    }

    override operator fun StructureND<Double>.minus(arg: Double): Nd4jArrayStructure<Double> {
        return ndArray.sub(arg).wrap()
    }

    override operator fun StructureND<Double>.times(arg: Double): Nd4jArrayStructure<Double> {
        return ndArray.mul(arg).wrap()
    }

    override operator fun Double.div(arg: StructureND<Double>): Nd4jArrayStructure<Double> {
        return arg.ndArray.rdiv(this).wrap()
    }

    override operator fun Double.minus(arg: StructureND<Double>): Nd4jArrayStructure<Double> {
        return arg.ndArray.rsub(this).wrap()
    }
}

/**
 * Represents [FieldND] over [Nd4jArrayStructure] of [Float].
 */
public class FloatNd4jArrayField(override val shape: IntArray) : Nd4jArrayExtendedField<Float, FloatField> {
    override val elementContext: FloatField get() = FloatField

    override fun INDArray.wrap(): Nd4jArrayStructure<Float> = checkShape(this).asFloatStructure()

    @OptIn(PerformancePitfall::class)
    override val StructureND<Float>.ndArray: INDArray
        get() = when (this) {
            is Nd4jArrayStructure<Float> -> checkShape(ndArray)
            else -> Nd4j.zeros(*shape).also {
                elements().forEach { (idx, value) -> it.putScalar(idx, value) }
            }
        }

    override fun scale(a: StructureND<Float>, value: Double): StructureND<Float> =
        a.ndArray.mul(value).wrap()

    override operator fun StructureND<Float>.div(arg: Float): Nd4jArrayStructure<Float> =
        ndArray.div(arg).wrap()

    override operator fun StructureND<Float>.plus(arg: Float): Nd4jArrayStructure<Float> =
        ndArray.add(arg).wrap()

    override operator fun StructureND<Float>.minus(arg: Float): Nd4jArrayStructure<Float> =
        ndArray.sub(arg).wrap()

    override operator fun StructureND<Float>.times(arg: Float): Nd4jArrayStructure<Float> =
        ndArray.mul(arg).wrap()

    override operator fun Float.div(arg: StructureND<Float>): Nd4jArrayStructure<Float> =
        arg.ndArray.rdiv(this).wrap()

    override operator fun Float.minus(arg: StructureND<Float>): Nd4jArrayStructure<Float> =
        arg.ndArray.rsub(this).wrap()
}

/**
 * Represents [RingND] over [Nd4jArrayIntStructure].
 */
public class IntNd4jArrayRing(override val shape: IntArray) : Nd4jArrayRing<Int, IntRing> {
    override val elementContext: IntRing
        get() = IntRing

    override fun INDArray.wrap(): Nd4jArrayStructure<Int> = checkShape(this).asIntStructure()

    @OptIn(PerformancePitfall::class)
    override val StructureND<Int>.ndArray: INDArray
        get() = when (this) {
            is Nd4jArrayStructure<Int> -> checkShape(ndArray)
            else -> Nd4j.zeros(*shape).also {
                elements().forEach { (idx, value) -> it.putScalar(idx, value) }
            }
        }

    override operator fun StructureND<Int>.plus(arg: Int): Nd4jArrayStructure<Int> =
        ndArray.add(arg).wrap()

    override operator fun StructureND<Int>.minus(arg: Int): Nd4jArrayStructure<Int> =
        ndArray.sub(arg).wrap()

    override operator fun StructureND<Int>.times(arg: Int): Nd4jArrayStructure<Int> =
        ndArray.mul(arg).wrap()

    override operator fun Int.minus(arg: StructureND<Int>): Nd4jArrayStructure<Int> =
        arg.ndArray.rsub(this).wrap()
}
