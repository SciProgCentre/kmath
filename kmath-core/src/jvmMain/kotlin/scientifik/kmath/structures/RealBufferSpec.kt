package scientifik.kmath.structures

import scientifik.kmath.operations.Real
import java.nio.ByteBuffer

object RealBufferSpec : FixedSizeBufferSpec<Real> {
    override val unitSize: Int = 8

    override fun ByteBuffer.readObject(): Real = Real(double)

    override fun ByteBuffer.writeObject(value: Real) {
        putDouble(value.value)
    }
}

object DoubleBufferSpec : FixedSizeBufferSpec<Double> {
    override val unitSize: Int = 8

    override fun ByteBuffer.readObject() = double

    override fun ByteBuffer.writeObject(value: Double) {
        putDouble(value)
    }

}

fun Double.Companion.createBuffer(size: Int) = ObjectBuffer.create(DoubleBufferSpec, size)
fun Real.Companion.createBuffer(size: Int) = ObjectBuffer.create(RealBufferSpec, size)