package space.kscience.kmath.nd4j

import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.factory.Nd4j
import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.nd.*
import space.kscience.kmath.operations.*
import space.kscience.kmath.structures.*

internal fun NDAlgebra<*, *>.checkShape(array: INDArray): INDArray {
    val arrayShape = array.shape().toIntArray()
    if (!shape.contentEquals(arrayShape)) throw ShapeMismatchException(shape, arrayShape)
    return array
}


/**
 * Represents [NDAlgebra] over [Nd4jArrayAlgebra].
 *
 * @param T the type of ND-structure element.
 * @param C the type of the element context.
 */
public interface Nd4jArrayAlgebra<T, C> : NDAlgebra<T, C> {
    /**
     * Wraps [INDArray] to [N].
     */
    public fun INDArray.wrap(): Nd4jArrayStructure<T>

    public val NDStructure<T>.ndArray: INDArray
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

    public override fun NDStructure<T>.map(transform: C.(T) -> T): Nd4jArrayStructure<T> {
        val newStruct = ndArray.dup().wrap()
        newStruct.elements().forEach { (idx, value) -> newStruct[idx] = elementContext.transform(value) }
        return newStruct
    }

    public override fun NDStructure<T>.mapIndexed(
        transform: C.(index: IntArray, T) -> T,
    ): Nd4jArrayStructure<T> {
        val new = Nd4j.create(*this@Nd4jArrayAlgebra.shape).wrap()
        new.indicesIterator().forEach { idx -> new[idx] = elementContext.transform(idx, this[idx]) }
        return new
    }

    public override fun combine(
        a: NDStructure<T>,
        b: NDStructure<T>,
        transform: C.(T, T) -> T,
    ): Nd4jArrayStructure<T> {
        val new = Nd4j.create(*shape).wrap()
        new.indicesIterator().forEach { idx -> new[idx] = elementContext.transform(a[idx], b[idx]) }
        return new
    }
}

/**
 * Represents [NDSpace] over [Nd4jArrayStructure].
 *
 * @param T the type of the element contained in ND structure.
 * @param S the type of space of structure elements.
 */
public interface Nd4jArraySpace<T, S : Space<T>> : NDSpace<T, S>, Nd4jArrayAlgebra<T, S> {

    public override val zero: Nd4jArrayStructure<T>
        get() = Nd4j.zeros(*shape).wrap()

    public override fun add(a: NDStructure<T>, b: NDStructure<T>): Nd4jArrayStructure<T> {
        return a.ndArray.add(b.ndArray).wrap()
    }

    public override operator fun NDStructure<T>.minus(b: NDStructure<T>): Nd4jArrayStructure<T> {
        return ndArray.sub(b.ndArray).wrap()
    }

    public override operator fun NDStructure<T>.unaryMinus(): Nd4jArrayStructure<T> {
        return ndArray.neg().wrap()
    }

    public override fun multiply(a: NDStructure<T>, k: Number): Nd4jArrayStructure<T> {
        return a.ndArray.mul(k).wrap()
    }

    public override operator fun NDStructure<T>.div(k: Number): Nd4jArrayStructure<T> {
        return ndArray.div(k).wrap()
    }

    public override operator fun NDStructure<T>.times(k: Number): Nd4jArrayStructure<T> {
        return ndArray.mul(k).wrap()
    }
}

/**
 * Represents [NDRing] over [Nd4jArrayStructure].
 *
 * @param T the type of the element contained in ND structure.
 * @param R the type of ring of structure elements.
 */
@OptIn(UnstableKMathAPI::class)
public interface Nd4jArrayRing<T, R : Ring<T>> : NDRing<T, R>, Nd4jArraySpace<T, R> {

    public override val one: Nd4jArrayStructure<T>
        get() = Nd4j.ones(*shape).wrap()

    public override fun multiply(a: NDStructure<T>, b: NDStructure<T>): Nd4jArrayStructure<T> {
        return a.ndArray.mul(b.ndArray).wrap()
    }
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
         * Creates an [NDRing] for [Int] values or pull it from cache if it was created previously.
         */
        public fun int(vararg shape: Int): Nd4jArrayRing<Int, IntRing> =
            intNd4jArrayRingCache.get().getOrPut(shape) { IntNd4jArrayRing(shape) }

        /**
         * Creates an [NDRing] for [Long] values or pull it from cache if it was created previously.
         */
        public fun long(vararg shape: Int): Nd4jArrayRing<Long, LongRing> =
            longNd4jArrayRingCache.get().getOrPut(shape) { LongNd4jArrayRing(shape) }

        /**
         * Creates a most suitable implementation of [NDRing] using reified class.
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
 * Represents [NDField] over [Nd4jArrayStructure].
 *
 * @param T the type of the element contained in ND structure.
 * @param N the type of ND structure.
 * @param F the type field of structure elements.
 */
public interface Nd4jArrayField<T, F : Field<T>> : NDField<T, F>, Nd4jArrayRing<T, F> {

    public override fun divide(a: NDStructure<T>, b: NDStructure<T>): Nd4jArrayStructure<T> =
        a.ndArray.div(b.ndArray).wrap()

    public override operator fun Number.div(b: NDStructure<T>): Nd4jArrayStructure<T> = b.ndArray.rdiv(this).wrap()


    public companion object {
        private val floatNd4jArrayFieldCache: ThreadLocal<MutableMap<IntArray, FloatNd4jArrayField>> =
            ThreadLocal.withInitial { hashMapOf() }

        private val realNd4jArrayFieldCache: ThreadLocal<MutableMap<IntArray, RealNd4jArrayField>> =
            ThreadLocal.withInitial { hashMapOf() }

        /**
         * Creates an [NDField] for [Float] values or pull it from cache if it was created previously.
         */
        public fun float(vararg shape: Int): Nd4jArrayRing<Float, FloatField> =
            floatNd4jArrayFieldCache.get().getOrPut(shape) { FloatNd4jArrayField(shape) }

        /**
         * Creates an [NDField] for [Double] values or pull it from cache if it was created previously.
         */
        public fun real(vararg shape: Int): Nd4jArrayRing<Double, RealField> =
            realNd4jArrayFieldCache.get().getOrPut(shape) { RealNd4jArrayField(shape) }

        /**
         * Creates a most suitable implementation of [NDRing] using reified class.
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
 * Represents [NDField] over [Nd4jArrayRealStructure].
 */
public class RealNd4jArrayField(public override val shape: IntArray) : Nd4jArrayField<Double, RealField> {
    public override val elementContext: RealField
        get() = RealField

    public override fun INDArray.wrap(): Nd4jArrayStructure<Double> = checkShape(this).asRealStructure()

    public override operator fun NDStructure<Double>.div(arg: Double): Nd4jArrayStructure<Double> {
        return ndArray.div(arg).wrap()
    }

    public override operator fun NDStructure<Double>.plus(arg: Double): Nd4jArrayStructure<Double> {
        return ndArray.add(arg).wrap()
    }

    public override operator fun NDStructure<Double>.minus(arg: Double): Nd4jArrayStructure<Double> {
        return ndArray.sub(arg).wrap()
    }

    public override operator fun NDStructure<Double>.times(arg: Double): Nd4jArrayStructure<Double> {
        return ndArray.mul(arg).wrap()
    }

    public override operator fun Double.div(arg: NDStructure<Double>): Nd4jArrayStructure<Double> {
        return arg.ndArray.rdiv(this).wrap()
    }

    public override operator fun Double.minus(arg: NDStructure<Double>): Nd4jArrayStructure<Double> {
        return arg.ndArray.rsub(this).wrap()
    }
}

/**
 * Represents [NDField] over [Nd4jArrayStructure] of [Float].
 */
public class FloatNd4jArrayField(public override val shape: IntArray) : Nd4jArrayField<Float, FloatField> {
    public override val elementContext: FloatField
        get() = FloatField

    public override fun INDArray.wrap(): Nd4jArrayStructure<Float> = checkShape(this).asFloatStructure()

    public override operator fun NDStructure<Float>.div(arg: Float): Nd4jArrayStructure<Float> {
        return ndArray.div(arg).wrap()
    }

    public override operator fun NDStructure<Float>.plus(arg: Float): Nd4jArrayStructure<Float> {
        return ndArray.add(arg).wrap()
    }

    public override operator fun NDStructure<Float>.minus(arg: Float): Nd4jArrayStructure<Float> {
        return ndArray.sub(arg).wrap()
    }

    public override operator fun NDStructure<Float>.times(arg: Float): Nd4jArrayStructure<Float> {
        return ndArray.mul(arg).wrap()
    }

    public override operator fun Float.div(arg: NDStructure<Float>): Nd4jArrayStructure<Float> {
        return arg.ndArray.rdiv(this).wrap()
    }

    public override operator fun Float.minus(arg: NDStructure<Float>): Nd4jArrayStructure<Float> {
        return arg.ndArray.rsub(this).wrap()
    }
}

/**
 * Represents [NDRing] over [Nd4jArrayIntStructure].
 */
public class IntNd4jArrayRing(public override val shape: IntArray) : Nd4jArrayRing<Int, IntRing> {
    public override val elementContext: IntRing
        get() = IntRing

    public override fun INDArray.wrap(): Nd4jArrayStructure<Int> = checkShape(this).asIntStructure()

    public override operator fun NDStructure<Int>.plus(arg: Int): Nd4jArrayStructure<Int> {
        return ndArray.add(arg).wrap()
    }

    public override operator fun NDStructure<Int>.minus(arg: Int): Nd4jArrayStructure<Int> {
        return ndArray.sub(arg).wrap()
    }

    public override operator fun NDStructure<Int>.times(arg: Int): Nd4jArrayStructure<Int> {
        return ndArray.mul(arg).wrap()
    }

    public override operator fun Int.minus(arg: NDStructure<Int>): Nd4jArrayStructure<Int> {
        return arg.ndArray.rsub(this).wrap()
    }
}

/**
 * Represents [NDRing] over [Nd4jArrayStructure] of [Long].
 */
public class LongNd4jArrayRing(public override val shape: IntArray) : Nd4jArrayRing<Long, LongRing> {
    public override val elementContext: LongRing
        get() = LongRing

    public override fun INDArray.wrap(): Nd4jArrayStructure<Long> = checkShape(this).asLongStructure()

    public override operator fun NDStructure<Long>.plus(arg: Long): Nd4jArrayStructure<Long> {
        return ndArray.add(arg).wrap()
    }

    public override operator fun NDStructure<Long>.minus(arg: Long): Nd4jArrayStructure<Long> {
        return ndArray.sub(arg).wrap()
    }

    public override operator fun NDStructure<Long>.times(arg: Long): Nd4jArrayStructure<Long> {
        return ndArray.mul(arg).wrap()
    }

    public override operator fun Long.minus(arg: NDStructure<Long>): Nd4jArrayStructure<Long> {
        return arg.ndArray.rsub(this).wrap()
    }
}
