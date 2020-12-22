package kscience.kmath.nd4j

import kscience.kmath.operations.*
import kscience.kmath.structures.NDAlgebra
import kscience.kmath.structures.NDField
import kscience.kmath.structures.NDRing
import kscience.kmath.structures.NDSpace
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.factory.Nd4j

/**
 * Represents [NDAlgebra] over [Nd4jArrayAlgebra].
 *
 * @param T the type of ND-structure element.
 * @param C the type of the element context.
 */
public interface Nd4jArrayAlgebra<T, C> : NDAlgebra<T, C, Nd4jArrayStructure<T>> {
    /**
     * Wraps [INDArray] to [N].
     */
    public fun INDArray.wrap(): Nd4jArrayStructure<T>

    public override fun produce(initializer: C.(IntArray) -> T): Nd4jArrayStructure<T> {
        val struct = Nd4j.create(*shape)!!.wrap()
        struct.indicesIterator().forEach { struct[it] = elementContext.initializer(it) }
        return struct
    }

    public override fun map(arg: Nd4jArrayStructure<T>, transform: C.(T) -> T): Nd4jArrayStructure<T> {
        check(arg)
        val newStruct = arg.ndArray.dup().wrap()
        newStruct.elements().forEach { (idx, value) -> newStruct[idx] = elementContext.transform(value) }
        return newStruct
    }

    public override fun mapIndexed(
        arg: Nd4jArrayStructure<T>,
        transform: C.(index: IntArray, T) -> T
    ): Nd4jArrayStructure<T> {
        check(arg)
        val new = Nd4j.create(*shape).wrap()
        new.indicesIterator().forEach { idx -> new[idx] = elementContext.transform(idx, arg[idx]) }
        return new
    }

    public override fun combine(
        a: Nd4jArrayStructure<T>,
        b: Nd4jArrayStructure<T>,
        transform: C.(T, T) -> T
    ): Nd4jArrayStructure<T> {
        check(a, b)
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
public interface Nd4jArraySpace<T, S> : NDSpace<T, S, Nd4jArrayStructure<T>>,
    Nd4jArrayAlgebra<T, S> where S : Space<T> {
    public override val zero: Nd4jArrayStructure<T>
        get() = Nd4j.zeros(*shape).wrap()

    public override fun add(a: Nd4jArrayStructure<T>, b: Nd4jArrayStructure<T>): Nd4jArrayStructure<T> {
        check(a, b)
        return a.ndArray.add(b.ndArray).wrap()
    }

    public override operator fun Nd4jArrayStructure<T>.minus(b: Nd4jArrayStructure<T>): Nd4jArrayStructure<T> {
        check(this, b)
        return ndArray.sub(b.ndArray).wrap()
    }

    public override operator fun Nd4jArrayStructure<T>.unaryMinus(): Nd4jArrayStructure<T> {
        check(this)
        return ndArray.neg().wrap()
    }

    public override fun multiply(a: Nd4jArrayStructure<T>, k: Number): Nd4jArrayStructure<T> {
        check(a)
        return a.ndArray.mul(k).wrap()
    }

    public override operator fun Nd4jArrayStructure<T>.div(k: Number): Nd4jArrayStructure<T> {
        check(this)
        return ndArray.div(k).wrap()
    }

    public override operator fun Nd4jArrayStructure<T>.times(k: Number): Nd4jArrayStructure<T> {
        check(this)
        return ndArray.mul(k).wrap()
    }
}

/**
 * Represents [NDRing] over [Nd4jArrayStructure].
 *
 * @param T the type of the element contained in ND structure.
 * @param R the type of ring of structure elements.
 */
public interface Nd4jArrayRing<T, R> : NDRing<T, R, Nd4jArrayStructure<T>>, Nd4jArraySpace<T, R> where R : Ring<T> {
    public override val one: Nd4jArrayStructure<T>
        get() = Nd4j.ones(*shape).wrap()

    public override fun multiply(a: Nd4jArrayStructure<T>, b: Nd4jArrayStructure<T>): Nd4jArrayStructure<T> {
        check(a, b)
        return a.ndArray.mul(b.ndArray).wrap()
    }

    public override operator fun Nd4jArrayStructure<T>.minus(b: Number): Nd4jArrayStructure<T> {
        check(this)
        return ndArray.sub(b).wrap()
    }

    public override operator fun Nd4jArrayStructure<T>.plus(b: Number): Nd4jArrayStructure<T> {
        check(this)
        return ndArray.add(b).wrap()
    }

    public override operator fun Number.minus(b: Nd4jArrayStructure<T>): Nd4jArrayStructure<T> {
        check(b)
        return b.ndArray.rsub(this).wrap()
    }

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
public interface Nd4jArrayField<T, F> : NDField<T, F, Nd4jArrayStructure<T>>, Nd4jArrayRing<T, F> where F : Field<T> {
    public override fun divide(a: Nd4jArrayStructure<T>, b: Nd4jArrayStructure<T>): Nd4jArrayStructure<T> {
        check(a, b)
        return a.ndArray.div(b.ndArray).wrap()
    }

    public override operator fun Number.div(b: Nd4jArrayStructure<T>): Nd4jArrayStructure<T> {
        check(b)
        return b.ndArray.rdiv(this).wrap()
    }


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

    public override fun INDArray.wrap(): Nd4jArrayStructure<Double> = check(asRealStructure())

    public override operator fun Nd4jArrayStructure<Double>.div(arg: Double): Nd4jArrayStructure<Double> {
        check(this)
        return ndArray.div(arg).wrap()
    }

    public override operator fun Nd4jArrayStructure<Double>.plus(arg: Double): Nd4jArrayStructure<Double> {
        check(this)
        return ndArray.add(arg).wrap()
    }

    public override operator fun Nd4jArrayStructure<Double>.minus(arg: Double): Nd4jArrayStructure<Double> {
        check(this)
        return ndArray.sub(arg).wrap()
    }

    public override operator fun Nd4jArrayStructure<Double>.times(arg: Double): Nd4jArrayStructure<Double> {
        check(this)
        return ndArray.mul(arg).wrap()
    }

    public override operator fun Double.div(arg: Nd4jArrayStructure<Double>): Nd4jArrayStructure<Double> {
        check(arg)
        return arg.ndArray.rdiv(this).wrap()
    }

    public override operator fun Double.minus(arg: Nd4jArrayStructure<Double>): Nd4jArrayStructure<Double> {
        check(arg)
        return arg.ndArray.rsub(this).wrap()
    }
}

/**
 * Represents [NDField] over [Nd4jArrayStructure] of [Float].
 */
public class FloatNd4jArrayField(public override val shape: IntArray) : Nd4jArrayField<Float, FloatField> {
    public override val elementContext: FloatField
        get() = FloatField

    public override fun INDArray.wrap(): Nd4jArrayStructure<Float> = check(asFloatStructure())

    public override operator fun Nd4jArrayStructure<Float>.div(arg: Float): Nd4jArrayStructure<Float> {
        check(this)
        return ndArray.div(arg).wrap()
    }

    public override operator fun Nd4jArrayStructure<Float>.plus(arg: Float): Nd4jArrayStructure<Float> {
        check(this)
        return ndArray.add(arg).wrap()
    }

    public override operator fun Nd4jArrayStructure<Float>.minus(arg: Float): Nd4jArrayStructure<Float> {
        check(this)
        return ndArray.sub(arg).wrap()
    }

    public override operator fun Nd4jArrayStructure<Float>.times(arg: Float): Nd4jArrayStructure<Float> {
        check(this)
        return ndArray.mul(arg).wrap()
    }

    public override operator fun Float.div(arg: Nd4jArrayStructure<Float>): Nd4jArrayStructure<Float> {
        check(arg)
        return arg.ndArray.rdiv(this).wrap()
    }

    public override operator fun Float.minus(arg: Nd4jArrayStructure<Float>): Nd4jArrayStructure<Float> {
        check(arg)
        return arg.ndArray.rsub(this).wrap()
    }
}

/**
 * Represents [NDRing] over [Nd4jArrayIntStructure].
 */
public class IntNd4jArrayRing(public override val shape: IntArray) : Nd4jArrayRing<Int, IntRing> {
    public override val elementContext: IntRing
        get() = IntRing

    public override fun INDArray.wrap(): Nd4jArrayStructure<Int> = check(asIntStructure())

    public override operator fun Nd4jArrayStructure<Int>.plus(arg: Int): Nd4jArrayStructure<Int> {
        check(this)
        return ndArray.add(arg).wrap()
    }

    public override operator fun Nd4jArrayStructure<Int>.minus(arg: Int): Nd4jArrayStructure<Int> {
        check(this)
        return ndArray.sub(arg).wrap()
    }

    public override operator fun Nd4jArrayStructure<Int>.times(arg: Int): Nd4jArrayStructure<Int> {
        check(this)
        return ndArray.mul(arg).wrap()
    }

    public override operator fun Int.minus(arg: Nd4jArrayStructure<Int>): Nd4jArrayStructure<Int> {
        check(arg)
        return arg.ndArray.rsub(this).wrap()
    }
}

/**
 * Represents [NDRing] over [Nd4jArrayStructure] of [Long].
 */
public class LongNd4jArrayRing(public override val shape: IntArray) : Nd4jArrayRing<Long, LongRing> {
    public override val elementContext: LongRing
        get() = LongRing

    public override fun INDArray.wrap(): Nd4jArrayStructure<Long> = check(asLongStructure())

    public override operator fun Nd4jArrayStructure<Long>.plus(arg: Long): Nd4jArrayStructure<Long> {
        check(this)
        return ndArray.add(arg).wrap()
    }

    public override operator fun Nd4jArrayStructure<Long>.minus(arg: Long): Nd4jArrayStructure<Long> {
        check(this)
        return ndArray.sub(arg).wrap()
    }

    public override operator fun Nd4jArrayStructure<Long>.times(arg: Long): Nd4jArrayStructure<Long> {
        check(this)
        return ndArray.mul(arg).wrap()
    }

    public override operator fun Long.minus(arg: Nd4jArrayStructure<Long>): Nd4jArrayStructure<Long> {
        check(arg)
        return arg.ndArray.rsub(this).wrap()
    }
}
