package scientifik.kmath.structures

import scientifik.kmath.operations.RingElement
import scientifik.kmath.operations.ShortRing


typealias ShortNDElement = BufferedNDRingElement<Short, ShortRing>

class ShortNDRing(override val shape: IntArray) :
    BufferedNDRing<Short, ShortRing> {

    override val strides: Strides = DefaultStrides(shape)

    override val elementContext: ShortRing get() = ShortRing
    override val zero by lazy { produce { ShortRing.zero } }
    override val one by lazy { produce { ShortRing.one } }

    inline fun buildBuffer(size: Int, crossinline initializer: (Int) -> Short): Buffer<Short> =
        ShortBuffer(ShortArray(size) { initializer(it) })

    /**
     * Inline transform an NDStructure to
     */
    override fun map(
        arg: NDBuffer<Short>,
        transform: ShortRing.(Short) -> Short
    ): ShortNDElement {
        check(arg)
        val array = buildBuffer(arg.strides.linearSize) { offset -> ShortRing.transform(arg.buffer[offset]) }
        return BufferedNDRingElement(this, array)
    }

    override fun produce(initializer: ShortRing.(IntArray) -> Short): ShortNDElement {
        val array = buildBuffer(strides.linearSize) { offset -> elementContext.initializer(strides.index(offset)) }
        return BufferedNDRingElement(this, array)
    }

    override fun mapIndexed(
        arg: NDBuffer<Short>,
        transform: ShortRing.(index: IntArray, Short) -> Short
    ): ShortNDElement {
        check(arg)
        return BufferedNDRingElement(
            this,
            buildBuffer(arg.strides.linearSize) { offset ->
                elementContext.transform(
                    arg.strides.index(offset),
                    arg.buffer[offset]
                )
            })
    }

    override fun combine(
        a: NDBuffer<Short>,
        b: NDBuffer<Short>,
        transform: ShortRing.(Short, Short) -> Short
    ): ShortNDElement {
        check(a, b)
        return BufferedNDRingElement(
            this,
            buildBuffer(strides.linearSize) { offset -> elementContext.transform(a.buffer[offset], b.buffer[offset]) })
    }

    override fun NDBuffer<Short>.toElement(): RingElement<NDBuffer<Short>, *, out BufferedNDRing<Short, ShortRing>> =
        BufferedNDRingElement(this@ShortNDRing, buffer)
}


/**
 * Fast element production using function inlining
 */
inline fun BufferedNDRing<Short, ShortRing>.produceInline(crossinline initializer: ShortRing.(Int) -> Short): ShortNDElement {
    val array = ShortArray(strides.linearSize) { offset -> ShortRing.initializer(offset) }
    return BufferedNDRingElement(this, ShortBuffer(array))
}

/**
 * Element by element application of any operation on elements to the whole array. Just like in numpy
 */
operator fun Function1<Short, Short>.invoke(ndElement: ShortNDElement) =
    ndElement.context.produceInline { i -> invoke(ndElement.buffer[i]) }


/* plus and minus */

/**
 * Summation operation for [StridedNDFieldElement] and single element
 */
operator fun ShortNDElement.plus(arg: Short) =
    context.produceInline { i -> (buffer[i] + arg).toShort() }

/**
 * Subtraction operation between [StridedNDFieldElement] and single element
 */
operator fun ShortNDElement.minus(arg: Short) =
    context.produceInline { i -> (buffer[i] - arg).toShort() }