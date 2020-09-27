package kscience.kmath.structures

import kscience.kmath.operations.RingElement
import kscience.kmath.operations.ShortRing

public typealias ShortNDElement = BufferedNDRingElement<Short, ShortRing>

public class ShortNDRing(override val shape: IntArray) :
    BufferedNDRing<Short, ShortRing> {

    override val strides: Strides = DefaultStrides(shape)
    override val elementContext: ShortRing get() = ShortRing
    override val zero: ShortNDElement by lazy { produce { zero } }
    override val one: ShortNDElement by lazy { produce { one } }

    public inline fun buildBuffer(size: Int, crossinline initializer: (Int) -> Short): Buffer<Short> =
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
 * Fast element production using function inlining.
 */
public inline fun BufferedNDRing<Short, ShortRing>.produceInline(crossinline initializer: ShortRing.(Int) -> Short): ShortNDElement =
    BufferedNDRingElement(this, ShortBuffer(ShortArray(strides.linearSize) { offset -> ShortRing.initializer(offset) }))

/**
 * Element by element application of any operation on elements to the whole array.
 */
public operator fun Function1<Short, Short>.invoke(ndElement: ShortNDElement): ShortNDElement =
    ndElement.context.produceInline { i -> invoke(ndElement.buffer[i]) }


/* plus and minus */

/**
 * Summation operation for [ShortNDElement] and single element.
 */
public operator fun ShortNDElement.plus(arg: Short): ShortNDElement =
    context.produceInline { i -> (buffer[i] + arg).toShort() }

/**
 * Subtraction operation between [ShortNDElement] and single element.
 */
public operator fun ShortNDElement.minus(arg: Short): ShortNDElement =
    context.produceInline { i -> (buffer[i] - arg).toShort() }
