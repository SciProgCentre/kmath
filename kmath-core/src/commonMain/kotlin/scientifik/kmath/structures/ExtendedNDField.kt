package scientifik.kmath.structures

import scientifik.kmath.operations.ExponentialOperations
import scientifik.kmath.operations.ExtendedField
import scientifik.kmath.operations.PowerOperations
import scientifik.kmath.operations.TrigonometricOperations


/**
 * NDField that supports [ExtendedField] operations on its elements
 */
class ExtendedNDField<N : Any, F : ExtendedField<N>>(shape: IntArray, field: F) : NDField<N, F>(shape, field),
        TrigonometricOperations<NDElement<N, F>>,
        PowerOperations<NDElement<N, F>>,
        ExponentialOperations<NDElement<N, F>> {

    override fun produceStructure(initializer: F.(IntArray) -> N): NDStructure<N> {
        return ndStructure(shape) { field.initializer(it) }
    }

    override fun power(arg: NDElement<N, F>, pow: Double): NDElement<N, F> {
        return arg.transform { d -> with(field) { power(d, pow) } }
    }

    override fun exp(arg: NDElement<N, F>): NDElement<N, F> {
        return arg.transform { d -> with(field) { exp(d) } }
    }

    override fun ln(arg: NDElement<N, F>): NDElement<N, F> {
        return arg.transform { d -> with(field) { ln(d) } }
    }

    override fun sin(arg: NDElement<N, F>): NDElement<N, F> {
        return arg.transform { d -> with(field) { sin(d) } }
    }

    override fun cos(arg: NDElement<N, F>): NDElement<N, F> {
        return arg.transform { d -> with(field) { cos(d) } }
    }
}


