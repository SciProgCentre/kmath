package scientifik.kmath.structures

import scientifik.kmath.operations.ExponentialOperations
import scientifik.kmath.operations.ExtendedField
import scientifik.kmath.operations.PowerOperations
import scientifik.kmath.operations.TrigonometricOperations


/**
 * NDField that supports [ExtendedField] operations on its elements
 */
class ExtendedNDField<N: Any>(shape: IntArray, override val field: ExtendedField<N>) : NDField<N>(shape, field),
        TrigonometricOperations<NDElement<N>>,
        PowerOperations<NDElement<N>>,
        ExponentialOperations<NDElement<N>> {

    override fun produceStructure(initializer: (IntArray) -> N): NDStructure<N> {
        return genericNdStructure(shape, initializer)
    }

    override fun power(arg: NDElement<N>, pow: Double): NDElement<N> {
        return arg.transform { d -> with(field){power(d,pow)} }
    }

    override fun exp(arg: NDElement<N>): NDElement<N> {
        return arg.transform { d -> with(field){exp(d)} }
    }

    override fun ln(arg: NDElement<N>): NDElement<N> {
        return arg.transform { d -> with(field){ln(d)} }
    }

    override fun sin(arg: NDElement<N>): NDElement<N> {
        return arg.transform { d -> with(field){sin(d)} }
    }

    override fun cos(arg: NDElement<N>): NDElement<N> {
        return arg.transform { d -> with(field){cos(d)} }
    }
}


