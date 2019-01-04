package scientifik.kmath.structures

import scientifik.kmath.operations.DoubleField

typealias RealNDElement = BufferNDElement<Double, DoubleField>

class RealNDField(shape: IntArray) :
    StridedNDField<Double, DoubleField>(shape, DoubleField),
    ExtendedNDField<Double, DoubleField, NDBuffer<Double>> {

    override val bufferFactory: BufferFactory<Double>
        get() = DoubleBufferFactory

    /**
     * Inline map an NDStructure to
     */
    @Suppress("OVERRIDE_BY_INLINE")
    override inline fun NDBuffer<Double>.map(crossinline transform: DoubleField.(Double) -> Double): RealNDElement {
        check(this)
        val array = DoubleArray(strides.linearSize) { offset -> DoubleField.transform(buffer[offset]) }
        return BufferNDElement(this@RealNDField, DoubleBuffer(array))
    }

    @Suppress("OVERRIDE_BY_INLINE")
    override inline fun produce(crossinline initializer: DoubleField.(IntArray) -> Double): RealNDElement {
        val array = DoubleArray(strides.linearSize) { offset -> elementField.initializer(strides.index(offset)) }
        return BufferNDElement(this, DoubleBuffer(array))
    }

    @Suppress("OVERRIDE_BY_INLINE")
    override inline fun NDBuffer<Double>.mapIndexed(crossinline transform: DoubleField.(index: IntArray, Double) -> Double): BufferNDElement<Double, DoubleField> {
        check(this)
        return BufferNDElement(
            this@RealNDField,
            bufferFactory(strides.linearSize) { offset ->
                elementField.transform(
                    strides.index(offset),
                    buffer[offset]
                )
            })
    }

    @Suppress("OVERRIDE_BY_INLINE")
    override inline fun combine(
        a: NDBuffer<Double>,
        b: NDBuffer<Double>,
        crossinline transform: DoubleField.(Double, Double) -> Double
    ): BufferNDElement<Double, DoubleField> {
        check(a, b)
        return BufferNDElement(
            this,
            bufferFactory(strides.linearSize) { offset -> elementField.transform(a.buffer[offset], b.buffer[offset]) })
    }

    override fun power(arg: NDBuffer<Double>, pow: Double) = arg.map { power(it, pow) }

    override fun exp(arg: NDBuffer<Double>) = arg.map { exp(it) }

    override fun ln(arg: NDBuffer<Double>) = arg.map { ln(it) }

    override fun sin(arg: NDBuffer<Double>) = arg.map { sin(it) }

    override fun cos(arg: NDBuffer<Double>) = arg.map { cos(it) }
//
//    override fun NDBuffer<Double>.times(k: Number) = mapInline { value -> value * k.toDouble() }
//
//    override fun NDBuffer<Double>.div(k: Number) = mapInline { value -> value / k.toDouble() }
//
//    override fun Number.times(b: NDBuffer<Double>) = b * this
//
//    override fun Number.div(b: NDBuffer<Double>) = b * (1.0 / this.toDouble())
}


/**
 * Fast element production using function inlining
 */
inline fun StridedNDField<Double, DoubleField>.produceInline(crossinline initializer: DoubleField.(Int) -> Double): RealNDElement {
    val array = DoubleArray(strides.linearSize) { offset -> elementField.initializer(offset) }
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
