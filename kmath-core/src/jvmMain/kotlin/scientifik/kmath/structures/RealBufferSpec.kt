package scientifik.kmath.structures

import scientifik.kmath.operations.Real
import java.nio.ByteBuffer

object RealBufferSpec : FixedSizeBufferSpec<Real> {
    override val unitSize: Int = 8

    override fun fromBuffer(buffer: ByteBuffer): Real = Real(buffer.double)

    override fun toBuffer(value: Real): ByteBuffer = ByteBuffer.allocate(8).apply { putDouble(value.value) }
}

object DoubleBufferSpec : FixedSizeBufferSpec<Double> {
    override val unitSize: Int = 8

    override fun fromBuffer(buffer: ByteBuffer): Double = buffer.double

    override fun toBuffer(value: Double): ByteBuffer = ByteBuffer.allocate(8).apply { putDouble(value) }
}

fun Double.Companion.createBuffer(size: Int) = ObjectBuffer.create(DoubleBufferSpec, size)
fun Real.Companion.createBuffer(size: Int) = ObjectBuffer.create(RealBufferSpec, size)