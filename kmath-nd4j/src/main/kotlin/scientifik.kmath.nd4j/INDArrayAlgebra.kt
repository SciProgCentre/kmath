package scientifik.kmath.nd4j

import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.factory.Nd4j
import scientifik.kmath.operations.*
import scientifik.kmath.structures.MutableNDStructure
import scientifik.kmath.structures.NDField
import scientifik.kmath.structures.NDRing

interface INDArrayRing<T, R, N> :
    NDRing<T, R, N> where R : Ring<T>, N : INDArrayStructure<T>, N : MutableNDStructure<T> {
    fun INDArray.wrap(): N

    override val zero: N
        get() = Nd4j.zeros(*shape).wrap()

    override val one: N
        get() = Nd4j.ones(*shape).wrap()

    override fun produce(initializer: R.(IntArray) -> T): N {
        val struct = Nd4j.create(*shape).wrap()
        struct.elements().map(Pair<IntArray, T>::first).forEach { struct[it] = elementContext.initializer(it) }
        return struct
    }

    override fun map(arg: N, transform: R.(T) -> T): N {
        val new = Nd4j.create(*shape)
        Nd4j.copy(arg.ndArray, new)
        val newStruct = new.wrap()
        newStruct.elements().forEach { (idx, value) -> newStruct[idx] = elementContext.transform(value) }
        return newStruct
    }

    override fun mapIndexed(arg: N, transform: R.(index: IntArray, T) -> T): N {
        val new = Nd4j.create(*shape).wrap()
        new.elements().forEach { (idx, _) -> new[idx] = elementContext.transform(idx, arg[idx]) }
        return new
    }

    override fun combine(a: N, b: N, transform: R.(T, T) -> T): N {
        val new = Nd4j.create(*shape).wrap()
        new.elements().forEach { (idx, _) -> new[idx] = elementContext.transform(a[idx], b[idx]) }
        return new
    }

    override fun add(a: N, b: N): N = a.ndArray.addi(b.ndArray).wrap()
    override fun N.minus(b: N): N = ndArray.subi(b.ndArray).wrap()
    override fun N.unaryMinus(): N = ndArray.negi().wrap()
    override fun multiply(a: N, b: N): N = a.ndArray.muli(b.ndArray).wrap()
    override fun multiply(a: N, k: Number): N = a.ndArray.muli(k).wrap()
    override fun N.div(k: Number): N = ndArray.divi(k).wrap()
    override fun N.minus(b: Number): N = ndArray.subi(b).wrap()
    override fun N.plus(b: Number): N = ndArray.addi(b).wrap()
    override fun N.times(k: Number): N = ndArray.muli(k).wrap()
}

interface INDArrayField<T, F, N> : NDField<T, F, N>,
    INDArrayRing<T, F, N> where F : Field<T>, N : INDArrayStructure<T>, N : MutableNDStructure<T> {
    override fun divide(a: N, b: N): N = a.ndArray.divi(b.ndArray).wrap()
    override fun Number.div(b: N): N = b.ndArray.rdivi(this).wrap()
}

class RealINDArrayField(override val shape: IntArray, override val elementContext: Field<Double> = RealField) :
     INDArrayField<Double, Field<Double>, INDArrayRealStructure> {
    override fun INDArray.wrap(): INDArrayRealStructure = asRealStructure()
    override fun INDArrayRealStructure.div(arg: Double): INDArrayRealStructure = ndArray.divi(arg).wrap()
    override fun INDArrayRealStructure.plus(arg: Double): INDArrayRealStructure = ndArray.addi(arg).wrap()
    override fun INDArrayRealStructure.minus(arg: Double): INDArrayRealStructure = ndArray.subi(arg).wrap()
    override fun INDArrayRealStructure.times(arg: Double): INDArrayRealStructure = ndArray.muli(arg).wrap()
    override fun Double.div(arg: INDArrayRealStructure): INDArrayRealStructure = arg.ndArray.rdivi(this).wrap()
}

class FloatINDArrayField(override val shape: IntArray, override val elementContext: Field<Float> = FloatField) :
    INDArrayField<Float, Field<Float>, INDArrayFloatStructure> {
    override fun INDArray.wrap(): INDArrayFloatStructure = asFloatStructure()
    override fun INDArrayFloatStructure.div(arg: Float): INDArrayFloatStructure = ndArray.divi(arg).wrap()
    override fun INDArrayFloatStructure.plus(arg: Float): INDArrayFloatStructure = ndArray.addi(arg).wrap()
    override fun INDArrayFloatStructure.minus(arg: Float): INDArrayFloatStructure = ndArray.subi(arg).wrap()
    override fun INDArrayFloatStructure.times(arg: Float): INDArrayFloatStructure = ndArray.muli(arg).wrap()
    override fun Float.div(arg: INDArrayFloatStructure): INDArrayFloatStructure = arg.ndArray.rdivi(this).wrap()
}

class IntINDArrayRing(override val shape: IntArray, override val elementContext: Ring<Int> = IntRing) :
    INDArrayRing<Int, Ring<Int>, INDArrayIntStructure> {
    override fun INDArray.wrap(): INDArrayIntStructure = asIntStructure()
    override fun INDArrayIntStructure.plus(arg: Int): INDArrayIntStructure = ndArray.addi(arg).wrap()
    override fun INDArrayIntStructure.minus(arg: Int): INDArrayIntStructure = ndArray.subi(arg).wrap()
    override fun INDArrayIntStructure.times(arg: Int): INDArrayIntStructure = ndArray.muli(arg).wrap()
}
