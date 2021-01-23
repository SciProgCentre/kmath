package kscience.kmath.nd

import kscience.kmath.misc.UnstableKMathAPI
import kscience.kmath.operations.ExtendedField
import kscience.kmath.operations.RealField
import kscience.kmath.operations.RingWithNumbers
import kscience.kmath.structures.Buffer
import kscience.kmath.structures.RealBuffer
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@OptIn(UnstableKMathAPI::class)
public open class RealNDField(
    shape: IntArray,
) : BufferedNDField<Double, RealField>(shape, RealField, Buffer.Companion::real),
    RingWithNumbers<NDStructure<Double>>,
    ExtendedField<NDStructure<Double>> {

    override val zero: NDBuffer<Double> by lazy { produce { zero } }
    override val one: NDBuffer<Double> by lazy { produce { one } }

    override fun number(value: Number): NDBuffer<Double> {
        val d = value.toDouble() // minimize conversions
        return produce { d }
    }
//
//    @Suppress("OVERRIDE_BY_INLINE")
//    override inline fun map(
//        arg: AbstractNDBuffer<Double>,
//        transform: RealField.(Double) -> Double,
//    ): RealNDElement {
//        check(arg)
//        val array = RealBuffer(arg.strides.linearSize) { offset -> RealField.transform(arg.buffer[offset]) }
//        return BufferedNDFieldElement(this, array)
//    }
//
//    @Suppress("OVERRIDE_BY_INLINE")
//    override inline fun produce(initializer: RealField.(IntArray) -> Double): RealNDElement {
//        val array = RealBuffer(strides.linearSize) { offset -> elementContext.initializer(strides.index(offset)) }
//        return BufferedNDFieldElement(this, array)
//    }
//
//    @Suppress("OVERRIDE_BY_INLINE")
//    override inline fun mapIndexed(
//        arg: AbstractNDBuffer<Double>,
//        transform: RealField.(index: IntArray, Double) -> Double,
//    ): RealNDElement {
//        check(arg)
//        return BufferedNDFieldElement(
//            this,
//            RealBuffer(arg.strides.linearSize) { offset ->
//                elementContext.transform(
//                    arg.strides.index(offset),
//                    arg.buffer[offset]
//                )
//            })
//    }
//
//    @Suppress("OVERRIDE_BY_INLINE")
//    override inline fun combine(
//        a: AbstractNDBuffer<Double>,
//        b: AbstractNDBuffer<Double>,
//        transform: RealField.(Double, Double) -> Double,
//    ): RealNDElement {
//        check(a, b)
//        val buffer = RealBuffer(strides.linearSize) { offset ->
//            elementContext.transform(a.buffer[offset], b.buffer[offset])
//        }
//        return BufferedNDFieldElement(this, buffer)
//    }

    override fun power(arg: NDStructure<Double>, pow: Number): NDBuffer<Double> = map(arg) { power(it, pow) }

    override fun exp(arg: NDStructure<Double>): NDBuffer<Double> = map(arg) { exp(it) }

    override fun ln(arg: NDStructure<Double>): NDBuffer<Double> = map(arg) { ln(it) }

    override fun sin(arg: NDStructure<Double>): NDBuffer<Double> = map(arg) { sin(it) }
    override fun cos(arg: NDStructure<Double>): NDBuffer<Double> = map(arg) { cos(it) }
    override fun tan(arg: NDStructure<Double>): NDBuffer<Double> = map(arg) { tan(it) }
    override fun asin(arg: NDStructure<Double>): NDBuffer<Double> = map(arg) { asin(it) }
    override fun acos(arg: NDStructure<Double>): NDBuffer<Double> = map(arg) { acos(it) }
    override fun atan(arg: NDStructure<Double>): NDBuffer<Double> = map(arg) { atan(it) }

    override fun sinh(arg: NDStructure<Double>): NDBuffer<Double> = map(arg) { sinh(it) }
    override fun cosh(arg: NDStructure<Double>): NDBuffer<Double> = map(arg) { cosh(it) }
    override fun tanh(arg: NDStructure<Double>): NDBuffer<Double> = map(arg) { tanh(it) }
    override fun asinh(arg: NDStructure<Double>): NDBuffer<Double> = map(arg) { asinh(it) }
    override fun acosh(arg: NDStructure<Double>): NDBuffer<Double> = map(arg) { acosh(it) }
    override fun atanh(arg: NDStructure<Double>): NDBuffer<Double> = map(arg) { atanh(it) }
}


/**
 * Fast element production using function inlining
 */
public inline fun BufferedNDField<Double, RealField>.produceInline(crossinline initializer: RealField.(IntArray) -> Double): NDBuffer<Double> {
    contract { callsInPlace(initializer, InvocationKind.EXACTLY_ONCE) }
    val array = DoubleArray(strides.linearSize) { offset ->
        val index = strides.index(offset)
        RealField.initializer(index)
    }
    return NDBuffer(strides, RealBuffer(array))
}

public fun NDAlgebra.Companion.real(vararg shape: Int): RealNDField = RealNDField(shape)

/**
 * Produce a context for n-dimensional operations inside this real field
 */
public inline fun <R> RealField.nd(vararg shape: Int, action: RealNDField.() -> R): R {
    contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }
    return RealNDField(shape).run(action)
}
