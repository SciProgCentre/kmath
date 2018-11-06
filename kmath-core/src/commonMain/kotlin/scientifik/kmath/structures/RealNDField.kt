package scientifik.kmath.structures

import scientifik.kmath.operations.DoubleField
import scientifik.kmath.operations.ExponentialOperations
import scientifik.kmath.operations.PowerOperations
import scientifik.kmath.operations.TrigonometricOperations
import kotlin.math.*

typealias RealNDArray = NDArray<Double>


class RealNDField(shape: IntArray) : NDField<Double>(shape, DoubleField),
        TrigonometricOperations<RealNDArray>,
        PowerOperations<RealNDArray>,
        ExponentialOperations<RealNDArray> {

    override fun produceStructure(initializer: (IntArray) -> Double): NDStructure<Double> {
        return genericNdStructure(shape, initializer)
    }

    override fun power(arg: RealNDArray, pow: Double): RealNDArray {
        return arg.transform { d -> d.pow(pow) }
    }

    override fun exp(arg: RealNDArray): RealNDArray {
        return arg.transform { d -> exp(d) }
    }

    override fun ln(arg: RealNDArray): RealNDArray {
        return arg.transform { d -> ln(d) }
    }

    override fun sin(arg: RealNDArray): RealNDArray {
        return arg.transform { d -> sin(d) }
    }

    override fun cos(arg: RealNDArray): RealNDArray {
        return arg.transform { d -> cos(d) }
    }

    fun abs(arg: RealNDArray): RealNDArray {
        return arg.transform { d -> abs(d) }
    }
}


