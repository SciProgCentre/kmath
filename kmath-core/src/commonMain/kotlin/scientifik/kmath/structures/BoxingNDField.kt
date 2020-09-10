package scientifik.kmath.structures

import scientifik.kmath.operations.Field
import scientifik.kmath.operations.FieldElement

class BoxingNDField<T, F : Field<T>>(
    override val shape: IntArray,
    override val elementContext: F,
    val bufferFactory: BufferFactory<T>
) : BufferedNDField<T, F> {
    override val zero: BufferedNDFieldElement<T, F> by lazy { produce { zero } }
    override val one: BufferedNDFieldElement<T, F> by lazy { produce { one } }
    override val strides: Strides = DefaultStrides(shape)

    fun buildBuffer(size: Int, initializer: (Int) -> T): Buffer<T> =
        bufferFactory(size, initializer)

    override fun check(vararg elements: NDBuffer<T>) {
        check(elements.all { it.strides == strides }) { "Element strides are not the same as context strides" }
    }

    override fun produce(initializer: F.(IntArray) -> T): BufferedNDFieldElement<T, F> =
        BufferedNDFieldElement(
            this,
            buildBuffer(strides.linearSize) { offset -> elementContext.initializer(strides.index(offset)) })

    override fun map(arg: NDBuffer<T>, transform: F.(T) -> T): BufferedNDFieldElement<T, F> {
        check(arg)

        return BufferedNDFieldElement(
            this,
            buildBuffer(arg.strides.linearSize) { offset -> elementContext.transform(arg.buffer[offset]) })

//        val buffer = arg.buffer.transform { _, value -> elementContext.transform(value) }
//        return BufferedNDFieldElement(this, buffer)

    }

    override fun mapIndexed(
        arg: NDBuffer<T>,
        transform: F.(index: IntArray, T) -> T
    ): BufferedNDFieldElement<T, F> {
        check(arg)
        return BufferedNDFieldElement(
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
        transform: F.(T, T) -> T
    ): BufferedNDFieldElement<T, F> {
        check(a, b)
        return BufferedNDFieldElement(
            this,
            buildBuffer(strides.linearSize) { offset -> elementContext.transform(a.buffer[offset], b.buffer[offset]) })
    }

    override fun NDBuffer<T>.toElement(): FieldElement<NDBuffer<T>, *, out BufferedNDField<T, F>> =
        BufferedNDFieldElement(this@BoxingNDField, buffer)
}

inline fun <T : Any, F : Field<T>, R> F.nd(
    noinline bufferFactory: BufferFactory<T>,
    vararg shape: Int,
    action: NDField<T, F, *>.() -> R
): R {
    val ndfield: BoxingNDField<T, F> = NDField.boxing(this, *shape, bufferFactory = bufferFactory)
    return ndfield.action()
}
