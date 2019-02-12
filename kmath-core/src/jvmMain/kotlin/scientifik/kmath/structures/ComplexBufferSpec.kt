package scientifik.kmath.structures

import scientifik.kmath.operations.Complex
import scientifik.kmath.operations.ComplexField
import java.nio.ByteBuffer

/**
 * A serialization specification for complex numbers
 */
object ComplexBufferSpec : FixedSizeBufferSpec<Complex> {

    override val unitSize: Int = 16

    override fun ByteBuffer.readObject(): Complex {
        val re = double
        val im = double
        return Complex(re, im)
    }

    override fun ByteBuffer.writeObject(value: Complex) {
        putDouble(value.re)
        putDouble(value.im)
    }
}

/**
 * Create a read-only/mutable buffer which ignores boxing
 */
fun Buffer.Companion.complex(size: Int, initializer: ((Int) -> Complex)? = null): Buffer<Complex> =
    ObjectBuffer.create(ComplexBufferSpec, size, initializer)

fun MutableBuffer.Companion.complex(size: Int, initializer: ((Int) -> Complex)? = null) =
    ObjectBuffer.create(ComplexBufferSpec, size, initializer)

fun NDField.Companion.complex(shape: IntArray) = ComplexNDField(shape)

fun NDElement.Companion.complex(shape: IntArray, initializer: ComplexField.(IntArray) -> Complex) =
    NDField.complex(shape).produce(initializer)


