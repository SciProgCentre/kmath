package scientifik.kmath.structures

import scientifik.kmath.operations.Field


class BufferNDField<T, F : Field<T>>(
    shape: IntArray,
    override val elementContext: F,
    val bufferFactory: BufferFactory<T>
) : StridedNDField<T, F>(shape), NDField<T, F, NDBuffer<T>> {

    override fun buildBuffer(size: Int, initializer: (Int) -> T): Buffer<T> = bufferFactory(size, initializer)

    override fun check(vararg elements: NDBuffer<T>) {
        if (!elements.all { it.strides == this.strides }) error("Element strides are not the same as context strides")
    }

    override val zero by lazy { produce { zero } }
    override val one by lazy { produce { one } }

    override fun produce(initializer: F.(IntArray) -> T): StridedNDFieldElement<T, F> =
        StridedNDFieldElement(
            this,
            buildBuffer(strides.linearSize) { offset -> elementContext.initializer(strides.index(offset)) })

    override fun map(arg: NDBuffer<T>, transform: F.(T) -> T): StridedNDFieldElement<T, F> {
        check(arg)
        return StridedNDFieldElement(
            this,
            buildBuffer(arg.strides.linearSize) { offset -> elementContext.transform(arg.buffer[offset]) })
    }

    override fun mapIndexed(
        arg: NDBuffer<T>,
        transform: F.(index: IntArray, T) -> T
    ): StridedNDFieldElement<T, F> {
        check(arg)
        return StridedNDFieldElement(
            this,
            buildBuffer(arg.strides.linearSize) { offset ->
                elementContext.transform(
                    arg.strides.index(offset),
                    arg.buffer[offset]
                )
            })
    }

    override fun combine(
        a: NDBuffer<T>,
        b: NDBuffer<T>,
        transform: F.(T, T) -> T
    ): StridedNDFieldElement<T, F> {
        check(a, b)
        return StridedNDFieldElement(
            this,
            buildBuffer(strides.linearSize) { offset -> elementContext.transform(a.buffer[offset], b.buffer[offset]) })
    }
}