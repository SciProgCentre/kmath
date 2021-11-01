package space.kscience.kmath.multik

import org.jetbrains.kotlinx.multik.ndarray.data.DN
import org.jetbrains.kotlinx.multik.ndarray.data.DataType
import space.kscience.kmath.nd.StructureND
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.tensors.api.AnalyticTensorAlgebra
import space.kscience.kmath.tensors.api.LinearOpsTensorAlgebra
import space.kscience.kmath.tensors.api.Tensor

public object MultikDoubleAlgebra : MultikDivisionTensorAlgebra<Double, DoubleField>(),
    AnalyticTensorAlgebra<Double, DoubleField>, LinearOpsTensorAlgebra<Double, DoubleField> {
    override val elementAlgebra: DoubleField get() = DoubleField
    override val type: DataType get() = DataType.DoubleDataType

    override fun StructureND<Double>.mean(): Double = multikStat.mean(asMultik().array)

    override fun StructureND<Double>.mean(dim: Int, keepDim: Boolean): Tensor<Double> =
        multikStat.mean<Double,DN, DN>(asMultik().array, dim).wrap()

    override fun StructureND<Double>.std(): Double {
        TODO("Not yet implemented")
    }

    override fun StructureND<Double>.std(dim: Int, keepDim: Boolean): Tensor<Double> {
        TODO("Not yet implemented")
    }

    override fun StructureND<Double>.variance(): Double {
        TODO("Not yet implemented")
    }

    override fun StructureND<Double>.variance(dim: Int, keepDim: Boolean): Tensor<Double> {
        TODO("Not yet implemented")
    }

    override fun StructureND<Double>.exp(): Tensor<Double> {
        TODO("Not yet implemented")
    }

    override fun StructureND<Double>.ln(): Tensor<Double> {
        TODO("Not yet implemented")
    }

    override fun StructureND<Double>.sqrt(): Tensor<Double> {
        TODO("Not yet implemented")
    }

    override fun StructureND<Double>.cos(): Tensor<Double> {
        TODO("Not yet implemented")
    }

    override fun StructureND<Double>.acos(): Tensor<Double> {
        TODO("Not yet implemented")
    }

    override fun StructureND<Double>.cosh(): Tensor<Double> {
        TODO("Not yet implemented")
    }

    override fun StructureND<Double>.acosh(): Tensor<Double> {
        TODO("Not yet implemented")
    }

    override fun StructureND<Double>.sin(): Tensor<Double> {
        TODO("Not yet implemented")
    }

    override fun StructureND<Double>.asin(): Tensor<Double> {
        TODO("Not yet implemented")
    }

    override fun StructureND<Double>.sinh(): Tensor<Double> {
        TODO("Not yet implemented")
    }

    override fun StructureND<Double>.asinh(): Tensor<Double> {
        TODO("Not yet implemented")
    }

    override fun StructureND<Double>.tan(): Tensor<Double> {
        TODO("Not yet implemented")
    }

    override fun StructureND<Double>.atan(): Tensor<Double> {
        TODO("Not yet implemented")
    }

    override fun StructureND<Double>.tanh(): Tensor<Double> {
        TODO("Not yet implemented")
    }

    override fun StructureND<Double>.atanh(): Tensor<Double> {
        TODO("Not yet implemented")
    }

    override fun StructureND<Double>.ceil(): Tensor<Double> {
        TODO("Not yet implemented")
    }

    override fun StructureND<Double>.floor(): Tensor<Double> {
        TODO("Not yet implemented")
    }

    override fun StructureND<Double>.det(): Tensor<Double> {
        TODO("Not yet implemented")
    }

    override fun StructureND<Double>.inv(): Tensor<Double> {
        TODO("Not yet implemented")
    }

    override fun StructureND<Double>.cholesky(): Tensor<Double> {
        TODO("Not yet implemented")
    }

    override fun StructureND<Double>.qr(): Pair<Tensor<Double>, Tensor<Double>> {
        TODO("Not yet implemented")
    }

    override fun StructureND<Double>.lu(): Triple<Tensor<Double>, Tensor<Double>, Tensor<Double>> {
        TODO("Not yet implemented")
    }

    override fun StructureND<Double>.svd(): Triple<Tensor<Double>, Tensor<Double>, Tensor<Double>> {
        TODO("Not yet implemented")
    }

    override fun StructureND<Double>.symEig(): Pair<Tensor<Double>, Tensor<Double>> {
        TODO("Not yet implemented")
    }
}

public val Double.Companion.multikAlgebra: MultikTensorAlgebra<Double, DoubleField> get() = MultikDoubleAlgebra
public val DoubleField.multikAlgebra: MultikTensorAlgebra<Double, DoubleField> get() = MultikDoubleAlgebra

