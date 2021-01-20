package kscience.kmath.structures

import kscience.kmath.operations.Field
import kscience.kmath.operations.FieldElement

public class BoxingNDField<T, F : Field<T>>(
    public override val shape: IntArray,
    public override val elementContext: F,
    public val bufferFactory: BufferFactory<T>
) : BufferedNDField<T, F> {
    public override val zero: BufferedNDFieldElement<T, F> by lazy { produce { zero } }
    public override val one: BufferedNDFieldElement<T, F> by lazy { produce { one } }
    public override val strides: Strides = DefaultStrides(shape)

    public fun buildBuffer(size: Int, initializer: (Int) -> T): Buffer<T> =
        bufferFactory(size, initializer)

    public override fun check(vararg elements: NDBuffer<T>): Array<out NDBuffer<T>> {
        require(elements.all { it.strides == strides }) { "Element strides are not the same as context strides" }
        return elements
    }

    public override fun produce(initializer: F.(IntArray) -> T): BufferedNDFieldElement<T, F> =
        BufferedNDFieldElement(
            this,
            buildBuffer(strides.linearSize) { offset -> elementContext.initializer(strides.index(offset)) })

    public override fun map(arg: NDBuffer<T>, transform: F.(T) -> T): BufferedNDFieldElement<T, F> {
        check(arg)

        return BufferedNDFieldElement(
            this,
            buildBuffer(arg.strides.linearSize) { offset -> elementContext.transform(arg.buffer[offset]) })

//        val buffer = arg.buffer.transform { _, value -> elementContext.transform(value) }
//        return BufferedNDFieldElement(this, buffer)

    }

    public override fun mapIndexed(
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

    public override fun combine(
        a: NDBuffer<T>,
        b: NDBuffer<T>,
        transform: F.(T, T) -> T
    ): BufferedNDFieldElement<T, F> {
        check(a, b)
        return BufferedNDFieldElement(
            this,
            buildBuffer(strides.linearSize) { offset -> elementContext.transform(a.buffer[offset], b.buffer[offset]) })
    }

    public override fun NDBuffer<T>.toElement(): FieldElement<NDBuffer<T>, *, out BufferedNDField<T, F>> =
        BufferedNDFieldElement(this@BoxingNDField, buffer)
}

public inline fun <T : Any, F : Field<T>, R> F.nd(
    noinline bufferFactory: BufferFactory<T>,
    vararg shape: Int,
    action: NDField<T, F, *>.() -> R
): R {
    val ndfield = NDField.boxing(this, *shape, bufferFactory = bufferFactory)
    return ndfield.action()
}
