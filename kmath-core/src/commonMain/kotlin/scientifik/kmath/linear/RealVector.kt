package scientifik.kmath.linear

import scientifik.kmath.operations.Field
import scientifik.kmath.operations.Norm
import scientifik.kmath.operations.RealField
import scientifik.kmath.operations.SpaceElement
import scientifik.kmath.structures.Buffer
import scientifik.kmath.structures.DoubleBuffer
import scientifik.kmath.structures.asBuffer
import scientifik.kmath.structures.asSequence

fun DoubleArray.asVector() = RealVector(this.asBuffer())
fun List<Double>.asVector() = RealVector(this.asBuffer())


object VectorL2Norm : Norm<Point<out Number>, Double> {
    override fun norm(arg: Point<out Number>): Double =
        kotlin.math.sqrt(arg.asSequence().sumByDouble { it.toDouble() })
}

inline class RealVector(val point: Point<Double>) :
    SpaceElement<Point<Double>, RealVector, VectorSpace<Double, RealField>>, Point<Double> {
    override val context: VectorSpace<Double, RealField> get() = space(point.size)

    override fun unwrap(): Point<Double> = point

    override fun Point<Double>.wrap(): RealVector = RealVector(this)

    override val size: Int get() = point.size

    override fun get(index: Int): Double = point[index]

    override fun iterator(): Iterator<Double> = point.iterator()

    companion object {

        private val spaceCache = HashMap<Int, BufferVectorSpace<Double, RealField>>()

        inline operator fun invoke(dim:Int, initalizer: (Int)-> Double) = RealVector(DoubleBuffer(dim, initalizer))

        operator fun invoke(vararg values: Double) = values.asVector()

        fun space(dim: Int) =
            spaceCache.getOrPut(dim) {
                BufferVectorSpace(dim, RealField) { size, init -> Buffer.real(size, init) }
            }
    }
}