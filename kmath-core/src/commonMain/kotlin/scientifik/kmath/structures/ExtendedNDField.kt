package scientifik.kmath.structures

import scientifik.kmath.operations.*


/**
 * NDField that supports [ExtendedField] operations on its elements
 */
class ExtendedNDField<N: Any>(shape: IntArray, override val field: ExtendedField<N>) : NDField<N>(shape, field),
        TrigonometricOperations<NDArray<N>>,
        PowerOperations<NDArray<N>>,
        ExponentialOperations<NDArray<N>> {

    override fun produceStructure(initializer: (IntArray) -> N): NDStructure<N> {
        return genericNdStructure(shape, initializer)
    }

    override fun power(arg: NDArray<N>, pow: Double): NDArray<N> {
        return arg.transform { d -> with(field){power(d,pow)} }
    }

    override fun exp(arg: NDArray<N>): NDArray<N> {
        return arg.transform { d -> with(field){exp(d)} }
    }

    override fun ln(arg: NDArray<N>): NDArray<N> {
        return arg.transform { d -> with(field){ln(d)} }
    }

    override fun sin(arg: NDArray<N>): NDArray<N> {
        return arg.transform { d -> with(field){sin(d)} }
    }

    override fun cos(arg: NDArray<N>): NDArray<N> {
        return arg.transform { d -> with(field){cos(d)} }
    }
}


