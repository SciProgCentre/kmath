package scientifik.kmath.nd4j

import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.factory.Nd4j
import scientifik.kmath.operations.*
import scientifik.kmath.structures.*

/**
 * Represents [NDAlgebra] over [INDArrayAlgebra].
 *
 * @param T the type of ND-structure element.
 * @param C the type of the element context.
 * @param N the type of the structure.
 */
interface INDArrayAlgebra<T, C, N> : NDAlgebra<T, C, N> where N : INDArrayStructure<T>, N : MutableNDStructure<T> {
    /**
     * Wraps [INDArray] to [N].
     */
    fun INDArray.wrap(): N

    override fun produce(initializer: C.(IntArray) -> T): N {
        val struct = Nd4j.create(*shape)!!.wrap()
        struct.indicesIterator().forEach { struct[it] = elementContext.initializer(it) }
        return struct
    }

    override fun map(arg: N, transform: C.(T) -> T): N {
        check(arg)
        val newStruct = arg.ndArray.dup().wrap()
        newStruct.elements().forEach { (idx, value) -> newStruct[idx] = elementContext.transform(value) }
        return newStruct
    }

    override fun mapIndexed(arg: N, transform: C.(index: IntArray, T) -> T): N {
        check(arg)
        val new = Nd4j.create(*shape).wrap()
        new.indicesIterator().forEach { idx -> new[idx] = elementContext.transform(idx, arg[idx]) }
        return new
    }

    override fun combine(a: N, b: N, transform: C.(T, T) -> T): N {
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
 * @param N the type of ND structure.
 * @param S the type of space of structure elements.
 */
interface INDArraySpace<T, S, N> : NDSpace<T, S, N>, INDArrayAlgebra<T, S, N>
        where S : Space<T>, N : INDArrayStructure<T>, N : MutableNDStructure<T> {

    override val zero: N
        get() = Nd4j.zeros(*shape).wrap()

    override fun add(a: N, b: N): N {
        check(a, b)
        return a.ndArray.add(b.ndArray).wrap()
    }

    override operator fun N.minus(b: N): N {
        check(this, b)
        return ndArray.sub(b.ndArray).wrap()
    }

    override operator fun N.unaryMinus(): N {
        check(this)
        return ndArray.neg().wrap()
    }

    override fun multiply(a: N, k: Number): N {
        check(a)
        return a.ndArray.mul(k).wrap()
    }

    override operator fun N.div(k: Number): N {
        check(this)
        return ndArray.div(k).wrap()
    }

    override operator fun N.times(k: Number): N {
        check(this)
        return ndArray.mul(k).wrap()
    }
}

/**
 * Represents [NDRing] over [INDArrayStructure].
 *
 * @param T the type of the element contained in ND structure.
 * @param N the type of ND structure.
 * @param R the type of ring of structure elements.
 */
interface INDArrayRing<T, R, N> : NDRing<T, R, N>, INDArraySpace<T, R, N>
        where R : Ring<T>, N : INDArrayStructure<T>, N : MutableNDStructure<T> {

    override val one: N
        get() = Nd4j.ones(*shape).wrap()

    override fun multiply(a: N, b: N): N {
        check(a, b)
        return a.ndArray.mul(b.ndArray).wrap()
    }

    override operator fun N.minus(b: Number): N {
        check(this)
        return ndArray.sub(b).wrap()
    }

    override operator fun N.plus(b: Number): N {
        check(this)
        return ndArray.add(b).wrap()
    }

    override operator fun Number.minus(b: N): N {
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
interface INDArrayField<T, F, N> : NDField<T, F, N>, INDArrayRing<T, F, N>
        where F : Field<T>, N : INDArrayStructure<T>, N : MutableNDStructure<T> {
    override fun divide(a: N, b: N): N {
        check(a, b)
        return a.ndArray.div(b.ndArray).wrap()
    }

    override operator fun Number.div(b: N): N {
        check(b)
        return b.ndArray.rdiv(this).wrap()
    }
}

/**
 * Represents [NDField] over [INDArrayRealStructure].
 */
class RealINDArrayField(override val shape: IntArray, override val elementContext: Field<Double> = RealField) :
    INDArrayField<Double, Field<Double>, INDArrayRealStructure> {
    override fun INDArray.wrap(): INDArrayRealStructure = check(asRealStructure())
    override operator fun INDArrayRealStructure.div(arg: Double): INDArrayRealStructure {
        check(this)
        return ndArray.div(arg).wrap()
    }

    override operator fun INDArrayRealStructure.plus(arg: Double): INDArrayRealStructure {
        check(this)
        return ndArray.add(arg).wrap()
    }

    override operator fun INDArrayRealStructure.minus(arg: Double): INDArrayRealStructure {
        check(this)
        return ndArray.sub(arg).wrap()
    }

    override operator fun INDArrayRealStructure.times(arg: Double): INDArrayRealStructure {
        check(this)
        return ndArray.mul(arg).wrap()
    }

    override operator fun Double.div(arg: INDArrayRealStructure): INDArrayRealStructure {
        check(arg)
        return arg.ndArray.rdiv(this).wrap()
    }

    override operator fun Double.minus(arg: INDArrayRealStructure): INDArrayRealStructure {
        check(arg)
        return arg.ndArray.rsub(this).wrap()
    }
}

/**
 * Represents [NDField] over [INDArrayFloatStructure].
 */
class FloatINDArrayField(override val shape: IntArray, override val elementContext: Field<Float> = FloatField) :
    INDArrayField<Float, Field<Float>, INDArrayFloatStructure> {
    override fun INDArray.wrap(): INDArrayFloatStructure = check(asFloatStructure())
    override operator fun INDArrayFloatStructure.div(arg: Float): INDArrayFloatStructure {
        check(this)
        return ndArray.div(arg).wrap()
    }

    override operator fun INDArrayFloatStructure.plus(arg: Float): INDArrayFloatStructure {
        check(this)
        return ndArray.add(arg).wrap()
    }

    override operator fun INDArrayFloatStructure.minus(arg: Float): INDArrayFloatStructure {
        check(this)
        return ndArray.sub(arg).wrap()
    }

    override operator fun INDArrayFloatStructure.times(arg: Float): INDArrayFloatStructure {
        check(this)
        return ndArray.mul(arg).wrap()
    }

    override operator fun Float.div(arg: INDArrayFloatStructure): INDArrayFloatStructure {
        check(arg)
        return arg.ndArray.rdiv(this).wrap()
    }

    override operator fun Float.minus(arg: INDArrayFloatStructure): INDArrayFloatStructure {
        check(arg)
        return arg.ndArray.rsub(this).wrap()
    }
}

/**
 * Represents [NDRing] over [INDArrayIntStructure].
 */
class IntINDArrayRing(override val shape: IntArray, override val elementContext: Ring<Int> = IntRing) :
    INDArrayRing<Int, Ring<Int>, INDArrayIntStructure> {
    override fun INDArray.wrap(): INDArrayIntStructure = check(asIntStructure())
    override operator fun INDArrayIntStructure.plus(arg: Int): INDArrayIntStructure {
        check(this)
        return ndArray.add(arg).wrap()
    }

    override operator fun INDArrayIntStructure.minus(arg: Int): INDArrayIntStructure {
        check(this)
        return ndArray.sub(arg).wrap()
    }

    override operator fun INDArrayIntStructure.times(arg: Int): INDArrayIntStructure {
        check(this)
        return ndArray.mul(arg).wrap()
    }

    override operator fun Int.minus(arg: INDArrayIntStructure): INDArrayIntStructure {
        check(arg)
        return arg.ndArray.rsub(this).wrap()
    }
}

/**
 * Represents [NDRing] over [INDArrayLongStructure].
 */
class LongINDArrayRing(override val shape: IntArray, override val elementContext: Ring<Long> = LongRing) :
    INDArrayRing<Long, Ring<Long>, INDArrayLongStructure> {
    override fun INDArray.wrap(): INDArrayLongStructure = check(asLongStructure())
    override operator fun INDArrayLongStructure.plus(arg: Long): INDArrayLongStructure {
        check(this)
        return ndArray.add(arg).wrap()
    }

    override operator fun INDArrayLongStructure.minus(arg: Long): INDArrayLongStructure {
        check(this)
        return ndArray.sub(arg).wrap()
    }

    override operator fun INDArrayLongStructure.times(arg: Long): INDArrayLongStructure {
        check(this)
        return ndArray.mul(arg).wrap()
    }

    override operator fun Long.minus(arg: INDArrayLongStructure): INDArrayLongStructure {
        check(arg)
        return arg.ndArray.rsub(this).wrap()
    }
}
