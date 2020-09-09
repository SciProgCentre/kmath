package scientifik.kmath.real

import scientifik.kmath.linear.BufferVectorSpace
import scientifik.kmath.linear.Point
import scientifik.kmath.linear.VectorSpace
import scientifik.kmath.operations.Norm
import scientifik.kmath.operations.RealField
import scientifik.kmath.operations.SpaceElement
import scientifik.kmath.structures.Buffer
import scientifik.kmath.structures.RealBuffer
import scientifik.kmath.structures.asBuffer
import scientifik.kmath.structures.asIterable
import kotlin.math.sqrt

public typealias RealPoint = Point<Double>

public fun DoubleArray.asVector(): RealVector = RealVector(this.asBuffer())
public fun List<Double>.asVector(): RealVector = RealVector(this.asBuffer())

public object VectorL2Norm : Norm<Point<out Number>, Double> {
    override fun norm(arg: Point<out Number>): Double = sqrt(arg.asIterable().sumByDouble { it.toDouble() })
}

public inline class RealVector(private val point: Point<Double>) :
    SpaceElement<RealPoint, RealVector, VectorSpace<Double, RealField>>, RealPoint {
    public override val size: Int get() = point.size
    public override val context: VectorSpace<Double, RealField> get() = space(point.size)

    public override fun unwrap(): RealPoint = point

    public override fun RealPoint.wrap(): RealVector = RealVector(this)


    override operator fun get(index: Int): Double = point[index]

    override operator fun iterator(): Iterator<Double> = point.iterator()

    public companion object {
        private val spaceCache: MutableMap<Int, BufferVectorSpace<Double, RealField>> = hashMapOf()

        public inline operator fun invoke(dim: Int, initializer: (Int) -> Double): RealVector =
            RealVector(RealBuffer(dim, initializer))

        public operator fun invoke(vararg values: Double): RealVector = values.asVector()

        public fun space(dim: Int): BufferVectorSpace<Double, RealField> = spaceCache.getOrPut(dim) {
            BufferVectorSpace(dim, RealField) { size, init -> Buffer.real(size, init) }
        }
    }
}
