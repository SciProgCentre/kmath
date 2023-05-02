/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.nd4j

import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.api.ops.impl.transforms.strict.ACosh
import org.nd4j.linalg.api.ops.impl.transforms.strict.ASinh
import org.nd4j.linalg.factory.Nd4j
import org.nd4j.linalg.ops.transforms.Transforms
import space.kscience.kmath.PerformancePitfall
import space.kscience.kmath.UnsafeKMathAPI
import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.nd.*
import space.kscience.kmath.operations.*

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
     * Unwraps to or get [INDArray] from [StructureND].
     */
    public val StructureND<T>.ndArray: INDArray

    @OptIn(PerformancePitfall::class)
    override fun structureND(shape: ShapeND, initializer: C.(IntArray) -> T): Nd4jArrayStructure<T> {
        @OptIn(UnsafeKMathAPI::class)
        val struct: Nd4jArrayStructure<T> = Nd4j.create(*shape.asArray())!!.wrap()
        struct.indicesIterator().forEach { struct[it] = elementAlgebra.initializer(it) }
        return struct
    }

    @OptIn(PerformancePitfall::class)
    override fun StructureND<T>.map(transform: C.(T) -> T): Nd4jArrayStructure<T> {
        val newStruct = ndArray.dup().wrap()
        newStruct.elements().forEach { (idx, value) -> newStruct[idx] = elementAlgebra.transform(value) }
        return newStruct
    }

    @OptIn(PerformancePitfall::class, UnsafeKMathAPI::class)
    override fun StructureND<T>.mapIndexed(
        transform: C.(index: IntArray, T) -> T,
    ): Nd4jArrayStructure<T> {
        val new = Nd4j.create(*shape.asArray()).wrap()
        new.indicesIterator().forEach { idx -> new[idx] = elementAlgebra.transform(idx, this[idx]) }
        return new
    }

    @OptIn(PerformancePitfall::class, UnsafeKMathAPI::class)
    override fun zip(
        left: StructureND<T>,
        right: StructureND<T>,
        transform: C.(T, T) -> T,
    ): Nd4jArrayStructure<T> {
        require(left.shape.contentEquals(right.shape)) { "Can't zip tow structures of shape ${left.shape} and ${right.shape}" }
        val new = Nd4j.create(*left.shape.asArray()).wrap()
        new.indicesIterator().forEach { idx -> new[idx] = elementAlgebra.transform(left[idx], right[idx]) }
        return new
    }
}

/**
 * Represents [GroupND] over [Nd4jArrayStructure].
 *
 * @param T the type of the element contained in ND structure.
 * @param S the type of space of structure elements.
 */
public sealed interface Nd4jArrayGroupOps<T, out S : Ring<T>> : GroupOpsND<T, S>, Nd4jArrayAlgebra<T, S> {

    override fun add(left: StructureND<T>, right: StructureND<T>): Nd4jArrayStructure<T> =
        left.ndArray.add(right.ndArray).wrap()

    override operator fun StructureND<T>.minus(arg: StructureND<T>): Nd4jArrayStructure<T> =
        ndArray.sub(arg.ndArray).wrap()

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
public sealed interface Nd4jArrayRingOps<T, out R : Ring<T>> : RingOpsND<T, R>, Nd4jArrayGroupOps<T, R> {

    override fun multiply(left: StructureND<T>, right: StructureND<T>): Nd4jArrayStructure<T> =
        left.ndArray.mul(right.ndArray).wrap()
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
        /**
         * Creates a most suitable implementation of [RingND] using reified class.
         */
        @Suppress("UNCHECKED_CAST")
        public inline fun <reified T : Number> auto(): Nd4jArrayRingOps<T, Ring<T>> = when {
            T::class == Int::class -> IntRing.nd4j as Nd4jArrayRingOps<T, Ring<T>>
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
public sealed interface Nd4jArrayField<T, out F : Field<T>> : FieldOpsND<T, F>, Nd4jArrayRingOps<T, F> {

    override fun divide(left: StructureND<T>, right: StructureND<T>): Nd4jArrayStructure<T> =
        left.ndArray.div(right.ndArray).wrap()

    public operator fun Number.div(b: StructureND<T>): Nd4jArrayStructure<T> = b.ndArray.rdiv(this).wrap()

    public companion object {
        /**
         * Creates a most suitable implementation of [FieldND] using reified class.
         */
        @Suppress("UNCHECKED_CAST")
        public inline fun <reified T : Any> auto(): Nd4jArrayField<T, Field<T>> = when {
            T::class == Float::class -> FloatField.nd4j as Nd4jArrayField<T, Field<T>>
            T::class == Double::class -> DoubleField.nd4j as Nd4jArrayField<T, Field<T>>
            else -> throw UnsupportedOperationException("This factory method only supports Float and Double types.")
        }
    }
}

/**
 * Represents intersection of [ExtendedField] and [Field] over [Nd4jArrayStructure].
 */
public sealed interface Nd4jArrayExtendedFieldOps<T, out F : ExtendedField<T>> :
    ExtendedFieldOps<StructureND<T>>, Nd4jArrayField<T, F>, PowerOperations<StructureND<T>> {

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
public open class DoubleNd4jArrayFieldOps : Nd4jArrayExtendedFieldOps<Double, DoubleField> {
    override val elementAlgebra: DoubleField get() = DoubleField

    override fun INDArray.wrap(): Nd4jArrayStructure<Double> = asDoubleStructure()

    @OptIn(PerformancePitfall::class, UnsafeKMathAPI::class)
    override val StructureND<Double>.ndArray: INDArray
        get() = when (this) {
            is Nd4jArrayStructure<Double> -> ndArray
            else -> Nd4j.zeros(*shape.asArray()).also {
                elements().forEach { (idx, value) -> it.putScalar(idx, value) }
            }
        }

    override fun scale(a: StructureND<Double>, value: Double): Nd4jArrayStructure<Double> = a.ndArray.mul(value).wrap()

    override operator fun StructureND<Double>.div(arg: Double): Nd4jArrayStructure<Double> = ndArray.div(arg).wrap()

    override operator fun StructureND<Double>.plus(arg: Double): Nd4jArrayStructure<Double> = ndArray.add(arg).wrap()

    override operator fun StructureND<Double>.minus(arg: Double): Nd4jArrayStructure<Double> = ndArray.sub(arg).wrap()

    override operator fun StructureND<Double>.times(arg: Double): Nd4jArrayStructure<Double> = ndArray.mul(arg).wrap()

    override operator fun Double.div(arg: StructureND<Double>): Nd4jArrayStructure<Double> =
        arg.ndArray.rdiv(this).wrap()

    override operator fun Double.minus(arg: StructureND<Double>): Nd4jArrayStructure<Double> =
        arg.ndArray.rsub(this).wrap()

    public companion object : DoubleNd4jArrayFieldOps()
}

public val DoubleField.nd4j: DoubleNd4jArrayFieldOps get() = DoubleNd4jArrayFieldOps

public class DoubleNd4jArrayField(override val shape: ShapeND) : DoubleNd4jArrayFieldOps(), FieldND<Double, DoubleField>

public fun DoubleField.nd4j(shapeFirst: Int, vararg shapeRest: Int): DoubleNd4jArrayField =
    DoubleNd4jArrayField(ShapeND(shapeFirst, * shapeRest))


/**
 * Represents [FieldND] over [Nd4jArrayStructure] of [Float].
 */
public open class FloatNd4jArrayFieldOps : Nd4jArrayExtendedFieldOps<Float, FloatField> {
    override val elementAlgebra: FloatField get() = FloatField

    override fun INDArray.wrap(): Nd4jArrayStructure<Float> = asFloatStructure()

    @OptIn(PerformancePitfall::class, UnsafeKMathAPI::class)
    override val StructureND<Float>.ndArray: INDArray
        get() = when (this) {
            is Nd4jArrayStructure<Float> -> ndArray
            else -> Nd4j.zeros(*shape.asArray()).also {
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

    public companion object : FloatNd4jArrayFieldOps()
}

public class FloatNd4jArrayField(override val shape: ShapeND) : FloatNd4jArrayFieldOps(), RingND<Float, FloatField>

public val FloatField.nd4j: FloatNd4jArrayFieldOps get() = FloatNd4jArrayFieldOps

public fun FloatField.nd4j(shapeFirst: Int, vararg shapeRest: Int): FloatNd4jArrayField =
    FloatNd4jArrayField(ShapeND(shapeFirst, * shapeRest))

/**
 * Represents [RingND] over [Nd4jArrayIntStructure].
 */
public open class IntNd4jArrayRingOps : Nd4jArrayRingOps<Int, IntRing> {
    override val elementAlgebra: IntRing get() = IntRing

    override fun INDArray.wrap(): Nd4jArrayStructure<Int> = asIntStructure()

    @OptIn(PerformancePitfall::class, UnsafeKMathAPI::class)
    override val StructureND<Int>.ndArray: INDArray
        get() = when (this) {
            is Nd4jArrayStructure<Int> -> ndArray
            else -> Nd4j.zeros(*shape.asArray()).also {
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

    public companion object : IntNd4jArrayRingOps()
}

public val IntRing.nd4j: IntNd4jArrayRingOps get() = IntNd4jArrayRingOps

public class IntNd4jArrayRing(override val shape: ShapeND) : IntNd4jArrayRingOps(), RingND<Int, IntRing>

public fun IntRing.nd4j(shapeFirst: Int, vararg shapeRest: Int): IntNd4jArrayRing =
    IntNd4jArrayRing(ShapeND(shapeFirst, * shapeRest))