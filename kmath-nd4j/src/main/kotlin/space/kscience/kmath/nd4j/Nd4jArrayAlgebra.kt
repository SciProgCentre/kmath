package space.kscience.kmath.nd4j

import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.factory.Nd4j
import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.nd.*
import space.kscience.kmath.operations.*
import space.kscience.kmath.structures.*

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
public interface Nd4jArrayAlgebra<T, C : Algebra<T>> : AlgebraND<T, C> {
    /**
     * Wraps [INDArray] to [N].
     */
    public fun INDArray.wrap(): Nd4jArrayStructure<T>

    public val StructureND<T>.ndArray: INDArray
        get() = when {
            !shape.contentEquals(this@Nd4jArrayAlgebra.shape) -> throw ShapeMismatchException(
                this@Nd4jArrayAlgebra.shape,
                shape
            )
            this is Nd4jArrayStructure -> ndArray //TODO check strides
            else -> {
                TODO()
            }
        }

    public override fun produce(initializer: C.(IntArray) -> T): Nd4jArrayStructure<T> {
        val struct = Nd4j.create(*shape)!!.wrap()
        struct.indicesIterator().forEach { struct[it] = elementContext.initializer(it) }
        return struct
    }

    public override fun StructureND<T>.map(transform: C.(T) -> T): Nd4jArrayStructure<T> {
        val newStruct = ndArray.dup().wrap()
        newStruct.elements().forEach { (idx, value) -> newStruct[idx] = elementContext.transform(value) }
        return newStruct
    }

    public override fun StructureND<T>.mapIndexed(
        transform: C.(index: IntArray, T) -> T,
    ): Nd4jArrayStructure<T> {
        val new = Nd4j.create(*this@Nd4jArrayAlgebra.shape).wrap()
        new.indicesIterator().forEach { idx -> new[idx] = elementContext.transform(idx, this[idx]) }
        return new
    }

    public override fun combine(
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
public interface Nd4JArrayGroup<T, S : Ring<T>> : GroupND<T, S>, Nd4jArrayAlgebra<T, S> {

    public override val zero: Nd4jArrayStructure<T>
        get() = Nd4j.zeros(*shape).wrap()

    public override fun add(a: StructureND<T>, b: StructureND<T>): Nd4jArrayStructure<T> =
        a.ndArray.add(b.ndArray).wrap()

    public override operator fun StructureND<T>.minus(b: StructureND<T>): Nd4jArrayStructure<T> =
        ndArray.sub(b.ndArray).wrap()

    public override operator fun StructureND<T>.unaryMinus(): Nd4jArrayStructure<T> =
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
public interface Nd4jArrayRing<T, R : Ring<T>> : RingND<T, R>, Nd4JArrayGroup<T, R> {

    public override val one: Nd4jArrayStructure<T>
        get() = Nd4j.ones(*shape).wrap()

    public override fun multiply(a: StructureND<T>, b: StructureND<T>): Nd4jArrayStructure<T> =
        a.ndArray.mul(b.ndArray).wrap()
//
//    public override operator fun Nd4jArrayStructure<T>.minus(b: Number): Nd4jArrayStructure<T> {
//        check(this)
//        return ndArray.sub(b).wrap()
//    }
//
//    public override operator fun Nd4jArrayStructure<T>.plus(b: Number): Nd4jArrayStructure<T> {
//        check(this)
//        return ndArray.add(b).wrap()
//    }
//
//    public override operator fun Number.minus(b: Nd4jArrayStructure<T>): Nd4jArrayStructure<T> {
//        check(b)
//        return b.ndArray.rsub(this).wrap()
//    }

    public companion object {
        private val intNd4jArrayRingCache: ThreadLocal<MutableMap<IntArray, IntNd4jArrayRing>> =
            ThreadLocal.withInitial { hashMapOf() }

        private val longNd4jArrayRingCache: ThreadLocal<MutableMap<IntArray, LongNd4jArrayRing>> =
            ThreadLocal.withInitial { hashMapOf() }

        /**
         * Creates an [RingND] for [Int] values or pull it from cache if it was created previously.
         */
        public fun int(vararg shape: Int): Nd4jArrayRing<Int, IntRing> =
            intNd4jArrayRingCache.get().getOrPut(shape) { IntNd4jArrayRing(shape) }

        /**
         * Creates an [RingND] for [Long] values or pull it from cache if it was created previously.
         */
        public fun long(vararg shape: Int): Nd4jArrayRing<Long, LongRing> =
            longNd4jArrayRingCache.get().getOrPut(shape) { LongNd4jArrayRing(shape) }

        /**
         * Creates a most suitable implementation of [RingND] using reified class.
         */
        @Suppress("UNCHECKED_CAST")
        public inline fun <reified T : Any> auto(vararg shape: Int): Nd4jArrayRing<T, out Ring<T>> = when {
            T::class == Int::class -> int(*shape) as Nd4jArrayRing<T, out Ring<T>>
            T::class == Long::class -> long(*shape) as Nd4jArrayRing<T, out Ring<T>>
            else -> throw UnsupportedOperationException("This factory method only supports Int and Long types.")
        }
    }
}

/**
 * Represents [FieldND] over [Nd4jArrayStructure].
 *
 * @param T the type of the element contained in ND structure.
 * @param N the type of ND structure.
 * @param F the type field of structure elements.
 */
public interface Nd4jArrayField<T, F : Field<T>> : FieldND<T, F>, Nd4jArrayRing<T, F> {

    public override fun divide(a: StructureND<T>, b: StructureND<T>): Nd4jArrayStructure<T> =
        a.ndArray.div(b.ndArray).wrap()

    public operator fun Number.div(b: StructureND<T>): Nd4jArrayStructure<T> = b.ndArray.rdiv(this).wrap()

    public companion object {
        private val floatNd4jArrayFieldCache: ThreadLocal<MutableMap<IntArray, FloatNd4jArrayField>> =
            ThreadLocal.withInitial { hashMapOf() }

        private val doubleNd4JArrayFieldCache: ThreadLocal<MutableMap<IntArray, DoubleNd4jArrayField>> =
            ThreadLocal.withInitial { hashMapOf() }

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
         * Creates a most suitable implementation of [RingND] using reified class.
         */
        @Suppress("UNCHECKED_CAST")
        public inline fun <reified T : Any> auto(vararg shape: Int): Nd4jArrayField<T, out Field<T>> = when {
            T::class == Float::class -> float(*shape) as Nd4jArrayField<T, out Field<T>>
            T::class == Double::class -> real(*shape) as Nd4jArrayField<T, out Field<T>>
            else -> throw UnsupportedOperationException("This factory method only supports Float and Double types.")
        }
    }
}

/**
 * Represents [FieldND] over [Nd4jArrayDoubleStructure].
 */
public class DoubleNd4jArrayField(public override val shape: IntArray) : Nd4jArrayField<Double, DoubleField> {
    public override val elementContext: DoubleField get() = DoubleField

    public override fun INDArray.wrap(): Nd4jArrayStructure<Double> = checkShape(this).asDoubleStructure()

    override fun scale(a: StructureND<Double>, value: Double): Nd4jArrayStructure<Double> {
        return a.ndArray.mul(value).wrap()
    }

    public override operator fun StructureND<Double>.div(arg: Double): Nd4jArrayStructure<Double> {
        return ndArray.div(arg).wrap()
    }

    public override operator fun StructureND<Double>.plus(arg: Double): Nd4jArrayStructure<Double> {
        return ndArray.add(arg).wrap()
    }

    public override operator fun StructureND<Double>.minus(arg: Double): Nd4jArrayStructure<Double> {
        return ndArray.sub(arg).wrap()
    }

    public override operator fun StructureND<Double>.times(arg: Double): Nd4jArrayStructure<Double> {
        return ndArray.mul(arg).wrap()
    }

    public override operator fun Double.div(arg: StructureND<Double>): Nd4jArrayStructure<Double> {
        return arg.ndArray.rdiv(this).wrap()
    }

    public override operator fun Double.minus(arg: StructureND<Double>): Nd4jArrayStructure<Double> {
        return arg.ndArray.rsub(this).wrap()
    }
}

/**
 * Represents [FieldND] over [Nd4jArrayStructure] of [Float].
 */
public class FloatNd4jArrayField(public override val shape: IntArray) : Nd4jArrayField<Float, FloatField> {
    public override val elementContext: FloatField
        get() = FloatField

    public override fun INDArray.wrap(): Nd4jArrayStructure<Float> = checkShape(this).asFloatStructure()

    override fun scale(a: StructureND<Float>, value: Double): StructureND<Float> =
        a.ndArray.mul(value).wrap()

    public override operator fun StructureND<Float>.div(arg: Float): Nd4jArrayStructure<Float> =
        ndArray.div(arg).wrap()

    public override operator fun StructureND<Float>.plus(arg: Float): Nd4jArrayStructure<Float> =
        ndArray.add(arg).wrap()

    public override operator fun StructureND<Float>.minus(arg: Float): Nd4jArrayStructure<Float> =
        ndArray.sub(arg).wrap()

    public override operator fun StructureND<Float>.times(arg: Float): Nd4jArrayStructure<Float> =
        ndArray.mul(arg).wrap()

    public override operator fun Float.div(arg: StructureND<Float>): Nd4jArrayStructure<Float> =
        arg.ndArray.rdiv(this).wrap()

    public override operator fun Float.minus(arg: StructureND<Float>): Nd4jArrayStructure<Float> =
        arg.ndArray.rsub(this).wrap()
}

/**
 * Represents [RingND] over [Nd4jArrayIntStructure].
 */
public class IntNd4jArrayRing(public override val shape: IntArray) : Nd4jArrayRing<Int, IntRing> {
    public override val elementContext: IntRing
        get() = IntRing

    public override fun INDArray.wrap(): Nd4jArrayStructure<Int> = checkShape(this).asIntStructure()

    public override operator fun StructureND<Int>.plus(arg: Int): Nd4jArrayStructure<Int> =
        ndArray.add(arg).wrap()

    public override operator fun StructureND<Int>.minus(arg: Int): Nd4jArrayStructure<Int> =
        ndArray.sub(arg).wrap()

    public override operator fun StructureND<Int>.times(arg: Int): Nd4jArrayStructure<Int> =
        ndArray.mul(arg).wrap()

    public override operator fun Int.minus(arg: StructureND<Int>): Nd4jArrayStructure<Int> =
        arg.ndArray.rsub(this).wrap()
}

/**
 * Represents [RingND] over [Nd4jArrayStructure] of [Long].
 */
public class LongNd4jArrayRing(public override val shape: IntArray) : Nd4jArrayRing<Long, LongRing> {
    public override val elementContext: LongRing
        get() = LongRing

    public override fun INDArray.wrap(): Nd4jArrayStructure<Long> = checkShape(this).asLongStructure()

    public override operator fun StructureND<Long>.plus(arg: Long): Nd4jArrayStructure<Long> =
        ndArray.add(arg).wrap()

    public override operator fun StructureND<Long>.minus(arg: Long): Nd4jArrayStructure<Long> =
        ndArray.sub(arg).wrap()

    public override operator fun StructureND<Long>.times(arg: Long): Nd4jArrayStructure<Long> =
        ndArray.mul(arg).wrap()

    public override operator fun Long.minus(arg: StructureND<Long>): Nd4jArrayStructure<Long> =
        arg.ndArray.rsub(this).wrap()
}
