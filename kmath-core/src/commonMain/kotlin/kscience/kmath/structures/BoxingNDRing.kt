package kscience.kmath.structures

import kscience.kmath.operations.Ring
import kscience.kmath.operations.RingElement

public class BoxingNDRing<T, R : Ring<T>>(
    override val shape: IntArray,
    override val elementContext: R,
    public val bufferFactory: BufferFactory<T>
) : BufferedNDRing<T, R> {
    override val strides: Strides = DefaultStrides(shape)
    override val zero: BufferedNDRingElement<T, R> by lazy { produce { zero } }
    override val one: BufferedNDRingElement<T, R> by lazy { produce { one } }

    public fun buildBuffer(size: Int, initializer: (Int) -> T): Buffer<T> = bufferFactory(size, initializer)

    override fun check(vararg elements: NDBuffer<T>): Array<out NDBuffer<T>> {
        if (!elements.all { it.strides == this.strides }) error("Element strides are not the same as context strides")
        return elements
    }

    override fun produce(initializer: R.(IntArray) -> T): BufferedNDRingElement<T, R> =
        BufferedNDRingElement(
            this,
            buildBuffer(strides.linearSize) { offset -> elementContext.initializer(strides.index(offset)) })

    override fun map(arg: NDBuffer<T>, transform: R.(T) -> T): BufferedNDRingElement<T, R> {
        check(arg)
        return BufferedNDRingElement(
            this,
            buildBuffer(arg.strides.linearSize) { offset -> elementContext.transform(arg.buffer[offset]) })

//        val buffer = arg.buffer.transform { _, value -> elementContext.transform(value) }
//        return BufferedNDFieldElement(this, buffer)

    }

    override fun mapIndexed(
        arg: NDBuffer<T>,
        transform: R.(index: IntArray, T) -> T
    ): BufferedNDRingElement<T, R> {
        check(arg)
        return BufferedNDRingElement(
            this,
            buildBuffer(arg.strides.linearSize) { offset ->
                elementContext.transform(
                    arg.strides.index(offset),
                    arg.buffer[offset]
                )
            })

//        val buffer =
//            arg.buffer.transform { offset, value -> elementContext.transform(arg.strides.index(offset), value) }
//        return BufferedNDFieldElement(this, buffer)
    }

    override fun combine(
        a: NDBuffer<T>,
        b: NDBuffer<T>,
        transform: R.(T, T) -> T
    ): BufferedNDRingElement<T, R> {
        check(a, b)

        return BufferedNDRingElement(
            this,
            buildBuffer(strides.linearSize) { offset -> elementContext.transform(a.buffer[offset], b.buffer[offset]) })
    }

    override fun NDBuffer<T>.toElement(): RingElement<NDBuffer<T>, *, out BufferedNDRing<T, R>> =
        BufferedNDRingElement(this@BoxingNDRing, buffer)
}
