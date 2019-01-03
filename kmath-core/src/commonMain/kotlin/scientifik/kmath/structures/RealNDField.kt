package scientifik.kmath.structures

import scientifik.kmath.operations.DoubleField

typealias RealNDElement = BufferNDElement<Double, DoubleField>

class RealNDField(shape: IntArray) :
        BufferNDField<Double, DoubleField>(shape, DoubleField, DoubleBufferFactory),
        ExtendedNDField<Double, DoubleField, NDBuffer<Double>> {

    /**
     * Inline map an NDStructure to
     */
    private inline fun NDBuffer<Double>.mapInline(crossinline operation: DoubleField.(Double) -> Double): RealNDElement {
        val array = DoubleArray(strides.linearSize) { offset -> DoubleField.operation(buffer[offset]) }
        return BufferNDElement(this@RealNDField, DoubleBuffer(array))
    }

    @Suppress("OVERRIDE_BY_INLINE")
    override inline fun produce(initializer: DoubleField.(IntArray) -> Double): RealNDElement {
        val array = DoubleArray(strides.linearSize) { offset -> field.initializer(strides.index(offset)) }
        return BufferNDElement(this, DoubleBuffer(array))
    }

    override fun power(arg: NDBuffer<Double>, pow: Double) = arg.mapInline { power(it, pow) }

    override fun exp(arg: NDBuffer<Double>) = arg.mapInline { exp(it) }

    override fun ln(arg: NDBuffer<Double>) = arg.mapInline { ln(it) }

    override fun sin(arg: NDBuffer<Double>) = arg.mapInline { sin(it) }

    override fun cos(arg: NDBuffer<Double>) = arg.mapInline { cos(it) }

    override fun NDBuffer<Double>.times(k: Number) = mapInline { value -> value * k.toDouble() }

    override fun NDBuffer<Double>.div(k: Number) = mapInline { value -> value / k.toDouble() }

    override fun Number.times(b: NDBuffer<Double>) = b * this

    override fun Number.div(b: NDBuffer<Double>) = b * (1.0 / this.toDouble())
}

/**
 * Fast element production using function inlining
 */
inline fun BufferNDField<Double, DoubleField>.produceInline(crossinline initializer: DoubleField.(Int) -> Double): RealNDElement {
    val array = DoubleArray(strides.linearSize) { offset -> field.initializer(offset) }
    return BufferNDElement(this, DoubleBuffer(array))
}

/**
 * Element by element application of any operation on elements to the whole array. Just like in numpy
 */
operator fun Function1<Double, Double>.invoke(ndElement: RealNDElement) =
        ndElement.context.produceInline { i -> invoke(ndElement.buffer[i]) }

/* plus and minus */

/**
 * Summation operation for [BufferNDElement] and single element
 */
operator fun RealNDElement.plus(arg: Double) =
        context.produceInline { i -> buffer[i] + arg }

/**
 * Subtraction operation between [BufferNDElement] and single element
 */
operator fun RealNDElement.minus(arg: Double) =
        context.produceInline { i -> buffer[i] - arg }