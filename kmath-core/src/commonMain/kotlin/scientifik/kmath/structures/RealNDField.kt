package scientifik.kmath.structures

import scientifik.kmath.operations.DoubleField

typealias RealNDElement = BufferNDElement<Double, DoubleField>

class RealNDField(shape: IntArray) : BufferNDField<Double, DoubleField>(shape, DoubleField, DoubleBufferFactory), ExtendedNDField<Double, DoubleField> {

    /**
     * Inline map an NDStructure to
     */
    private inline fun NDStructure<Double>.mapInline(crossinline operation: DoubleField.(Double) -> Double): RealNDElement =
            if (this is BufferNDElement<Double, *>) {
                val array = DoubleArray(strides.linearSize) { offset -> DoubleField.operation(buffer[offset]) }
                BufferNDElement(this@RealNDField, DoubleBuffer(array))
            } else {
                produce { index -> DoubleField.operation(get(index)) }
            }


    @Suppress("OVERRIDE_BY_INLINE")
    override inline fun produce(initializer: DoubleField.(IntArray) -> Double): RealNDElement {
        val array = DoubleArray(strides.linearSize) { offset -> field.initializer(strides.index(offset)) }
        return BufferNDElement(this, DoubleBuffer(array))
    }

    override fun power(arg: NDStructure<Double>, pow: Double) = arg.mapInline { power(it, pow) }

    override fun exp(arg: NDStructure<Double>) = arg.mapInline { exp(it) }

    override fun ln(arg: NDStructure<Double>) = arg.mapInline { ln(it) }

    override fun sin(arg: NDStructure<Double>) = arg.mapInline { sin(it) }

    override fun cos(arg: NDStructure<Double>) = arg.mapInline { cos(it) }

    override fun NDStructure<Double>.times(k: Number) = mapInline { value -> value * k.toDouble() }

    override fun NDStructure<Double>.div(k: Number) = mapInline { value -> value / k.toDouble() }

    override fun Number.times(b: NDStructure<Double>) = b * this

    override fun Number.div(b: NDStructure<Double>) = b * (1.0 / this.toDouble())
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

/**
 * Summation operation for [BufferNDElement] and single element
 */
operator fun RealNDElement.plus(arg: Double) = context.produceInline { i -> buffer[i] + arg }

/**
 * Subtraction operation between [BufferNDElement] and single element
 */
operator fun RealNDElement.minus(arg: Double) = context.produceInline { i -> buffer[i] - arg }