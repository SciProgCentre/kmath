package kscience.kmath.nd4j

import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.factory.Nd4j
import kscience.kmath.operations.*
import kscience.kmath.structures.*

/**
 * Represents [NDAlgebra] over [INDArrayAlgebra].
 *
 * @param T the type of ND-structure element.
 * @param C the type of the element context.
 */
public interface INDArrayAlgebra<T, C> : NDAlgebra<T, C, INDArrayStructure<T>> {
    /**
     * Wraps [INDArray] to [N].
     */
    public fun INDArray.wrap(): INDArrayStructure<T>

    public override fun produce(initializer: C.(IntArray) -> T): INDArrayStructure<T> {
        val struct = Nd4j.create(*shape)!!.wrap()
        struct.indicesIterator().forEach { struct[it] = elementContext.initializer(it) }
        return struct
    }

    public override fun map(arg: INDArrayStructure<T>, transform: C.(T) -> T): INDArrayStructure<T> {
        check(arg)
        val newStruct = arg.ndArray.dup().wrap()
        newStruct.elements().forEach { (idx, value) -> newStruct[idx] = elementContext.transform(value) }
        return newStruct
    }

    public override fun mapIndexed(
        arg: INDArrayStructure<T>,
        transform: C.(index: IntArray, T) -> T
    ): INDArrayStructure<T> {
        check(arg)
        val new = Nd4j.create(*shape).wrap()
        new.indicesIterator().forEach { idx -> new[idx] = elementContext.transform(idx, arg[idx]) }
        return new
    }

    public override fun combine(
        a: INDArrayStructure<T>,
        b: INDArrayStructure<T>,
        transform: C.(T, T) -> T
    ): INDArrayStructure<T> {
        check(a, b)
        val new = Nd4j.create(*shape).wrap()
        new.indicesIterator().forEach { idx -> new[idx] = elementContext.transform(a[idx], b[idx]) }
        return new
    }
}

/**
 * Represents [NDSpace] over [INDArrayStructure].
 *
 * @param T the type of the element contained in ND structure.
 * @param S the type of space of structure elements.
 */
public interface INDArraySpace<T, S> : NDSpace<T, S, INDArrayStructure<T>>, INDArrayAlgebra<T, S> where S : Space<T> {
    public override val zero: INDArrayStructure<T>
        get() = Nd4j.zeros(*shape).wrap()

    public override fun add(a: INDArrayStructure<T>, b: INDArrayStructure<T>): INDArrayStructure<T> {
        check(a, b)
        return a.ndArray.add(b.ndArray).wrap()
    }

    public override operator fun INDArrayStructure<T>.minus(b: INDArrayStructure<T>): INDArrayStructure<T> {
        check(this, b)
        return ndArray.sub(b.ndArray).wrap()
    }

    public override operator fun INDArrayStructure<T>.unaryMinus(): INDArrayStructure<T> {
        check(this)
        return ndArray.neg().wrap()
    }

    public override fun multiply(a: INDArrayStructure<T>, k: Number): INDArrayStructure<T> {
        check(a)
        return a.ndArray.mul(k).wrap()
    }

    public override operator fun INDArrayStructure<T>.div(k: Number): INDArrayStructure<T> {
        check(this)
        return ndArray.div(k).wrap()
    }

    public override operator fun INDArrayStructure<T>.times(k: Number): INDArrayStructure<T> {
        check(this)
        return ndArray.mul(k).wrap()
    }
}

/**
 * Represents [NDRing] over [INDArrayStructure].
 *
 * @param T the type of the element contained in ND structure.
 * @param R the type of ring of structure elements.
 */
public interface INDArrayRing<T, R> : NDRing<T, R, INDArrayStructure<T>>, INDArraySpace<T, R> where R : Ring<T> {
    public override val one: INDArrayStructure<T>
        get() = Nd4j.ones(*shape).wrap()

    public override fun multiply(a: INDArrayStructure<T>, b: INDArrayStructure<T>): INDArrayStructure<T> {
        check(a, b)
        return a.ndArray.mul(b.ndArray).wrap()
    }

    public override operator fun INDArrayStructure<T>.minus(b: Number): INDArrayStructure<T> {
        check(this)
        return ndArray.sub(b).wrap()
    }

    public override operator fun INDArrayStructure<T>.plus(b: Number): INDArrayStructure<T> {
        check(this)
        return ndArray.add(b).wrap()
    }

    public override operator fun Number.minus(b: INDArrayStructure<T>): INDArrayStructure<T> {
        check(b)
        return b.ndArray.rsub(this).wrap()
    }
}

/**
 * Represents [NDField] over [INDArrayStructure].
 *
 * @param T the type of the element contained in ND structure.
 * @param N the type of ND structure.
 * @param F the type field of structure elements.
 */
public interface INDArrayField<T, F> : NDField<T, F, INDArrayStructure<T>>, INDArrayRing<T, F> where F : Field<T> {
    public override fun divide(a: INDArrayStructure<T>, b: INDArrayStructure<T>): INDArrayStructure<T> {
        check(a, b)
        return a.ndArray.div(b.ndArray).wrap()
    }

    public override operator fun Number.div(b: INDArrayStructure<T>): INDArrayStructure<T> {
        check(b)
        return b.ndArray.rdiv(this).wrap()
    }
}

/**
 * Represents [NDField] over [INDArrayRealStructure].
 */
public class RealINDArrayField(public override val shape: IntArray) : INDArrayField<Double, RealField> {
    public override val elementContext: RealField
        get() = RealField

    public override fun INDArray.wrap(): INDArrayStructure<Double> = check(asRealStructure())

    public override operator fun INDArrayStructure<Double>.div(arg: Double): INDArrayStructure<Double> {
        check(this)
        return ndArray.div(arg).wrap()
    }

    public override operator fun INDArrayStructure<Double>.plus(arg: Double): INDArrayStructure<Double> {
        check(this)
        return ndArray.add(arg).wrap()
    }

    public override operator fun INDArrayStructure<Double>.minus(arg: Double): INDArrayStructure<Double> {
        check(this)
        return ndArray.sub(arg).wrap()
    }

    public override operator fun INDArrayStructure<Double>.times(arg: Double): INDArrayStructure<Double> {
        check(this)
        return ndArray.mul(arg).wrap()
    }

    public override operator fun Double.div(arg: INDArrayStructure<Double>): INDArrayStructure<Double> {
        check(arg)
        return arg.ndArray.rdiv(this).wrap()
    }

    public override operator fun Double.minus(arg: INDArrayStructure<Double>): INDArrayStructure<Double> {
        check(arg)
        return arg.ndArray.rsub(this).wrap()
    }
}

/**
 * Represents [NDField] over [INDArrayStructure] of [Float].
 */
public class FloatINDArrayField(public override val shape: IntArray) : INDArrayField<Float, FloatField> {
    public override val elementContext: FloatField
        get() = FloatField

    public override fun INDArray.wrap(): INDArrayStructure<Float> = check(asFloatStructure())

    public override operator fun INDArrayStructure<Float>.div(arg: Float): INDArrayStructure<Float> {
        check(this)
        return ndArray.div(arg).wrap()
    }

    public override operator fun INDArrayStructure<Float>.plus(arg: Float): INDArrayStructure<Float> {
        check(this)
        return ndArray.add(arg).wrap()
    }

    public override operator fun INDArrayStructure<Float>.minus(arg: Float): INDArrayStructure<Float> {
        check(this)
        return ndArray.sub(arg).wrap()
    }

    public override operator fun INDArrayStructure<Float>.times(arg: Float): INDArrayStructure<Float> {
        check(this)
        return ndArray.mul(arg).wrap()
    }

    public override operator fun Float.div(arg: INDArrayStructure<Float>): INDArrayStructure<Float> {
        check(arg)
        return arg.ndArray.rdiv(this).wrap()
    }

    public override operator fun Float.minus(arg: INDArrayStructure<Float>): INDArrayStructure<Float> {
        check(arg)
        return arg.ndArray.rsub(this).wrap()
    }
}

/**
 * Represents [NDRing] over [INDArrayIntStructure].
 */
public class IntINDArrayRing(public override val shape: IntArray) : INDArrayRing<Int, IntRing> {
    public override val elementContext: IntRing
        get() = IntRing

    public override fun INDArray.wrap(): INDArrayStructure<Int> = check(asIntStructure())

    public override operator fun INDArrayStructure<Int>.plus(arg: Int): INDArrayStructure<Int> {
        check(this)
        return ndArray.add(arg).wrap()
    }

    public override operator fun INDArrayStructure<Int>.minus(arg: Int): INDArrayStructure<Int> {
        check(this)
        return ndArray.sub(arg).wrap()
    }

    public override operator fun INDArrayStructure<Int>.times(arg: Int): INDArrayStructure<Int> {
        check(this)
        return ndArray.mul(arg).wrap()
    }

    public override operator fun Int.minus(arg: INDArrayStructure<Int>): INDArrayStructure<Int> {
        check(arg)
        return arg.ndArray.rsub(this).wrap()
    }
}

/**
 * Represents [NDRing] over [INDArrayStructure] of [Long].
 */
public class LongINDArrayRing(public override val shape: IntArray) : INDArrayRing<Long, LongRing> {
    public override val elementContext: LongRing
        get() = LongRing

    public override fun INDArray.wrap(): INDArrayStructure<Long> = check(asLongStructure())

    public override operator fun INDArrayStructure<Long>.plus(arg: Long): INDArrayStructure<Long> {
        check(this)
        return ndArray.add(arg).wrap()
    }

    public override operator fun INDArrayStructure<Long>.minus(arg: Long): INDArrayStructure<Long> {
        check(this)
        return ndArray.sub(arg).wrap()
    }

    public override operator fun INDArrayStructure<Long>.times(arg: Long): INDArrayStructure<Long> {
        check(this)
        return ndArray.mul(arg).wrap()
    }

    public override operator fun Long.minus(arg: INDArrayStructure<Long>): INDArrayStructure<Long> {
        check(arg)
        return arg.ndArray.rsub(this).wrap()
    }
}
