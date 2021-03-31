package space.kscience.kmath.commons.linear

import org.apache.commons.math3.linear.*
import space.kscience.kmath.linear.*
import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.nd.StructureFeature
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.structures.DoubleBuffer
import kotlin.reflect.KClass
import kotlin.reflect.cast

public inline class CMMatrix(public val origin: RealMatrix) : Matrix<Double> {
    public override val rowNum: Int get() = origin.rowDimension
    public override val colNum: Int get() = origin.columnDimension

    public override operator fun get(i: Int, j: Int): Double = origin.getEntry(i, j)
}

public inline class CMVector(public val origin: RealVector) : Point<Double> {
    public override val size: Int get() = origin.dimension

    public override operator fun get(index: Int): Double = origin.getEntry(index)

    public override operator fun iterator(): Iterator<Double> = origin.toArray().iterator()
}

public fun RealVector.toPoint(): CMVector = CMVector(this)

public object CMLinearSpace : LinearSpace<Double, DoubleField> {
    override val elementAlgebra: DoubleField get() = DoubleField

    public override fun buildMatrix(
        rows: Int,
        columns: Int,
        initializer: DoubleField.(i: Int, j: Int) -> Double,
    ): CMMatrix {
        val array = Array(rows) { i -> DoubleArray(columns) { j -> DoubleField.initializer(i, j) } }
        return CMMatrix(Array2DRowRealMatrix(array))
    }

    @OptIn(UnstableKMathAPI::class)
    public fun Matrix<Double>.toCM(): CMMatrix = when (val matrix = origin) {
        is CMMatrix -> matrix
        else -> {
            //TODO add feature analysis
            val array = Array(rowNum) { i -> DoubleArray(colNum) { j -> get(i, j) } }
            Array2DRowRealMatrix(array).wrap()
        }
    }

    public fun Point<Double>.toCM(): CMVector = if (this is CMVector) this else {
        val array = DoubleArray(size) { this[it] }
        ArrayRealVector(array).wrap()
    }

    internal fun RealMatrix.wrap(): CMMatrix = CMMatrix(this)
    internal fun RealVector.wrap(): CMVector = CMVector(this)

    override fun buildVector(size: Int, initializer: DoubleField.(Int) -> Double): Point<Double> =
        ArrayRealVector(DoubleArray(size) { DoubleField.initializer(it) }).wrap()

    override fun Matrix<Double>.plus(other: Matrix<Double>): CMMatrix =
        toCM().origin.add(other.toCM().origin).wrap()

    override fun Point<Double>.plus(other: Point<Double>): CMVector =
        toCM().origin.add(other.toCM().origin).wrap()

    override fun Point<Double>.minus(other: Point<Double>): CMVector =
        toCM().origin.subtract(other.toCM().origin).wrap()

    public override fun Matrix<Double>.dot(other: Matrix<Double>): CMMatrix =
        toCM().origin.multiply(other.toCM().origin).wrap()

    public override fun Matrix<Double>.dot(vector: Point<Double>): CMVector =
        toCM().origin.preMultiply(vector.toCM().origin).wrap()

    public override operator fun Matrix<Double>.minus(other: Matrix<Double>): CMMatrix =
        toCM().origin.subtract(other.toCM().origin).wrap()

    public override operator fun Matrix<Double>.times(value: Double): CMMatrix =
        toCM().origin.scalarMultiply(value).wrap()

    override fun Double.times(m: Matrix<Double>): CMMatrix =
        m * this

    override fun Point<Double>.times(value: Double): CMVector =
        toCM().origin.mapMultiply(value).wrap()

    override fun Double.times(v: Point<Double>): CMVector =
        v * this

    @UnstableKMathAPI
    override fun <F : StructureFeature> getFeature(structure: Matrix<Double>, type: KClass<out F>): F? {
        //Return the feature if it is intrinsic to the structure
        structure.getFeature(type)?.let { return it }

        val origin = structure.toCM().origin

        return when (type) {
            DiagonalFeature::class -> if (origin is DiagonalMatrix) DiagonalFeature else null

            DeterminantFeature::class, LupDecompositionFeature::class -> object :
                DeterminantFeature<Double>,
                LupDecompositionFeature<Double> {
                private val lup by lazy { LUDecomposition(origin) }
                override val determinant: Double by lazy { lup.determinant }
                override val l: Matrix<Double> by lazy { CMMatrix(lup.l) + LFeature }
                override val u: Matrix<Double> by lazy { CMMatrix(lup.u) + UFeature }
                override val p: Matrix<Double> by lazy { CMMatrix(lup.p) }
            }

            CholeskyDecompositionFeature::class -> object : CholeskyDecompositionFeature<Double> {
                override val l: Matrix<Double> by lazy {
                    val cholesky = CholeskyDecomposition(origin)
                    CMMatrix(cholesky.l) + LFeature
                }
            }

            QRDecompositionFeature::class -> object : QRDecompositionFeature<Double> {
                private val qr by lazy { QRDecomposition(origin) }
                override val q: Matrix<Double> by lazy { CMMatrix(qr.q) + OrthogonalFeature }
                override val r: Matrix<Double> by lazy { CMMatrix(qr.r) + UFeature }
            }

            SingularValueDecompositionFeature::class -> object : SingularValueDecompositionFeature<Double> {
                private val sv by lazy { SingularValueDecomposition(origin) }
                override val u: Matrix<Double> by lazy { CMMatrix(sv.u) }
                override val s: Matrix<Double> by lazy { CMMatrix(sv.s) }
                override val v: Matrix<Double> by lazy { CMMatrix(sv.v) }
                override val singularValues: Point<Double> by lazy { DoubleBuffer(sv.singularValues) }
            }
            else -> null
        }?.let(type::cast)
    }
}

public operator fun CMMatrix.plus(other: CMMatrix): CMMatrix = CMMatrix(origin.add(other.origin))

public operator fun CMMatrix.minus(other: CMMatrix): CMMatrix = CMMatrix(origin.subtract(other.origin))

public infix fun CMMatrix.dot(other: CMMatrix): CMMatrix = CMMatrix(origin.multiply(other.origin))
