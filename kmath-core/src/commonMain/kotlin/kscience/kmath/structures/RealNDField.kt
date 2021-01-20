package kscience.kmath.structures

import kscience.kmath.misc.UnstableKMathAPI
import kscience.kmath.operations.FieldElement
import kscience.kmath.operations.RealField
import kscience.kmath.operations.RingWithNumbers

public typealias RealNDElement = BufferedNDFieldElement<Double, RealField>

@OptIn(UnstableKMathAPI::class)
public class RealNDField(override val shape: IntArray) :
    BufferedNDField<Double, RealField>,
    ExtendedNDField<Double, RealField, NDBuffer<Double>>,
    RingWithNumbers<NDBuffer<Double>> {

    override val strides: Strides = DefaultStrides(shape)

    override val elementContext: RealField get() = RealField
    override val zero: RealNDElement by lazy { produce { zero } }
    override val one: RealNDElement by lazy { produce { one } }

    override fun number(value: Number): NDBuffer<Double> {
        val d = value.toDouble()
        return produce { d }
    }

    @Suppress("OVERRIDE_BY_INLINE")
    override inline fun map(
        arg: NDBuffer<Double>,
        transform: RealField.(Double) -> Double,
    ): RealNDElement {
        check(arg)
        val array = RealBuffer(arg.strides.linearSize) { offset -> RealField.transform(arg.buffer[offset]) }
        return BufferedNDFieldElement(this, array)
    }

    @Suppress("OVERRIDE_BY_INLINE")
    override inline fun produce(initializer: RealField.(IntArray) -> Double): RealNDElement {
        val array = RealBuffer(strides.linearSize) { offset -> elementContext.initializer(strides.index(offset)) }
        return BufferedNDFieldElement(this, array)
    }

    @Suppress("OVERRIDE_BY_INLINE")
    override inline fun mapIndexed(
        arg: NDBuffer<Double>,
        transform: RealField.(index: IntArray, Double) -> Double,
    ): RealNDElement {
        check(arg)
        return BufferedNDFieldElement(
            this,
            RealBuffer(arg.strides.linearSize) { offset ->
                elementContext.transform(
                    arg.strides.index(offset),
                    arg.buffer[offset]
                )
            })
    }

    @Suppress("OVERRIDE_BY_INLINE")
    override inline fun combine(
        a: NDBuffer<Double>,
        b: NDBuffer<Double>,
        transform: RealField.(Double, Double) -> Double,
    ): RealNDElement {
        check(a, b)
        val buffer = RealBuffer(strides.linearSize) { offset ->
            elementContext.transform(a.buffer[offset], b.buffer[offset])
        }
        return BufferedNDFieldElement(this, buffer)
    }

    override fun NDBuffer<Double>.toElement(): FieldElement<NDBuffer<Double>, *, out BufferedNDField<Double, RealField>> =
        BufferedNDFieldElement(this@RealNDField, buffer)

    override fun power(arg: NDBuffer<Double>, pow: Number): RealNDElement = map(arg) { power(it, pow) }

    override fun exp(arg: NDBuffer<Double>): RealNDElement = map(arg) { exp(it) }

    override fun ln(arg: NDBuffer<Double>): RealNDElement = map(arg) { ln(it) }

    override fun sin(arg: NDBuffer<Double>): RealNDElement = map(arg) { sin(it) }
    override fun cos(arg: NDBuffer<Double>): RealNDElement = map(arg) { cos(it) }
    override fun tan(arg: NDBuffer<Double>): RealNDElement = map(arg) { tan(it) }
    override fun asin(arg: NDBuffer<Double>): RealNDElement = map(arg) { asin(it) }
    override fun acos(arg: NDBuffer<Double>): RealNDElement = map(arg) { acos(it) }
    override fun atan(arg: NDBuffer<Double>): RealNDElement = map(arg) { atan(it) }

    override fun sinh(arg: NDBuffer<Double>): RealNDElement = map(arg) { sinh(it) }
    override fun cosh(arg: NDBuffer<Double>): RealNDElement = map(arg) { cosh(it) }
    override fun tanh(arg: NDBuffer<Double>): RealNDElement = map(arg) { tanh(it) }
    override fun asinh(arg: NDBuffer<Double>): RealNDElement = map(arg) { asinh(it) }
    override fun acosh(arg: NDBuffer<Double>): RealNDElement = map(arg) { acosh(it) }
    override fun atanh(arg: NDBuffer<Double>): RealNDElement = map(arg) { atanh(it) }
}


/**
 * Fast element production using function inlining
 */
public inline fun BufferedNDField<Double, RealField>.produceInline(crossinline initializer: RealField.(Int) -> Double): RealNDElement {
    val array = DoubleArray(strides.linearSize) { offset -> RealField.initializer(offset) }
    return BufferedNDFieldElement(this, RealBuffer(array))
}

/**
 * Map one [RealNDElement] using function with indices.
 */
public inline fun RealNDElement.mapIndexed(crossinline transform: RealField.(index: IntArray, Double) -> Double): RealNDElement =
    context.produceInline { offset -> transform(strides.index(offset), buffer[offset]) }

/**
 * Map one [RealNDElement] using function without indices.
 */
public inline fun RealNDElement.map(crossinline transform: RealField.(Double) -> Double): RealNDElement {
    val array = DoubleArray(strides.linearSize) { offset -> RealField.transform(buffer[offset]) }
    return BufferedNDFieldElement(context, RealBuffer(array))
}

/**
 * Element by element application of any operation on elements to the whole array. Just like in numpy.
 */
public operator fun Function1<Double, Double>.invoke(ndElement: RealNDElement): RealNDElement =
    ndElement.map { this@invoke(it) }

/* plus and minus */

/**
 * Summation operation for [BufferedNDElement] and single element
 */
public operator fun RealNDElement.plus(arg: Double): RealNDElement = map { it + arg }

/**
 * Subtraction operation between [BufferedNDElement] and single element
 */
public operator fun RealNDElement.minus(arg: Double): RealNDElement = map { it - arg }

/**
 * Produce a context for n-dimensional operations inside this real field
 */
public inline fun <R> RealField.nd(vararg shape: Int, action: RealNDField.() -> R): R = NDField.real(*shape).run(action)
