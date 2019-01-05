package scientifik.kmath.structures

import scientifik.kmath.operations.Complex
import scientifik.kmath.operations.ComplexField
import java.nio.ByteBuffer

object ComplexBufferSpec : FixedSizeBufferSpec<Complex> {
    override val unitSize: Int = 16

    override fun fromBuffer(buffer: ByteBuffer): Complex {
        val re = buffer.getDouble(0)
        val im = buffer.getDouble(8)
        return Complex(re, im)
    }

    override fun toBuffer(value: Complex): ByteBuffer = ByteBuffer.allocate(16).apply {
        putDouble(value.re)
        putDouble(value.im)
    }
}

/**
 * Create a read-only/mutable buffer which ignores boxing
 */
fun Buffer.Companion.complex(size: Int): Buffer<Complex> =
    ObjectBuffer.create(ComplexBufferSpec, size)

fun MutableBuffer.Companion.complex(size: Int) =
    ObjectBuffer.create(ComplexBufferSpec, size)

fun NDField.Companion.complex(shape: IntArray) =
    BufferNDField(shape, ComplexField) { size, init -> ObjectBuffer.create(ComplexBufferSpec, size, init) }

fun NDElement.Companion.complex(shape: IntArray, initializer: ComplexField.(IntArray) -> Complex) =
    NDField.complex(shape).produce(initializer)


