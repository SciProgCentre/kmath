package scientifik.kmath.structures

import scientifik.kmath.operations.Complex
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
 * Create a mutable buffer which ignores boxing
 */
fun Complex.Companion.createBuffer(size: Int) = ObjectBuffer.create(ComplexBufferSpec, size)

