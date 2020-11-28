package kscience.kmath.real

import kscience.kmath.linear.BufferVectorSpace
import kscience.kmath.linear.Point
import kscience.kmath.linear.VectorSpace
import kscience.kmath.operations.Norm
import kscience.kmath.operations.RealField
import kscience.kmath.operations.SpaceElement
import kscience.kmath.structures.Buffer
import kscience.kmath.structures.RealBuffer
import kscience.kmath.structures.asBuffer
import kscience.kmath.structures.asIterable
import kotlin.math.sqrt

public typealias RealPoint = Point<Double>

public fun RealPoint.asVector(): RealVector = RealVector(this)
public fun DoubleArray.asVector(): RealVector = asBuffer().asVector()
public fun List<Double>.asVector(): RealVector = asBuffer().asVector()

public object VectorL2Norm : Norm<Point<out Number>, Double> {
    override fun norm(arg: Point<out Number>): Double = sqrt(arg.asIterable().sumByDouble(Number::toDouble))
}

public inline class RealVector(private val point: Point<Double>) :
    SpaceElement<RealPoint, RealVector, VectorSpace<Double, RealField>>, RealPoint {
    public override val size: Int get() = point.size
    public override val context: VectorSpace<Double, RealField> get() = space(point.size)

    public override fun unwrap(): RealPoint = point
    public override fun RealPoint.wrap(): RealVector = RealVector(this)
    public override operator fun get(index: Int): Double = point[index]
    public override operator fun iterator(): Iterator<Double> = point.iterator()

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
