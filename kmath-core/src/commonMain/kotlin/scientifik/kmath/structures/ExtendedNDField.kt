package scientifik.kmath.structures

import scientifik.kmath.operations.ExponentialOperations
import scientifik.kmath.operations.ExtendedField
import scientifik.kmath.operations.PowerOperations
import scientifik.kmath.operations.TrigonometricOperations


/**
 * NDField that supports [ExtendedField] operations on its elements
 */
class ExtendedNDField<T : Any, F : ExtendedField<T>>(shape: IntArray, field: F) : NDField<T, F>(shape, field),
        TrigonometricOperations<NDStructure<T>>,
        PowerOperations<NDStructure<T>>,
        ExponentialOperations<NDStructure<T>> {

    override fun produceStructure(initializer: F.(IntArray) -> T): NDStructure<T> {
        return NdStructure(shape, ::boxingBuffer) { field.initializer(it) }
    }

    override fun power(arg: NDStructure<T>, pow: Double): NDElement<T, F> {
        return produce { with(field) { power(arg[it], pow) } }
    }

    override fun exp(arg: NDStructure<T>): NDElement<T, F> {
        return produce { with(field) { exp(arg[it]) } }
    }

    override fun ln(arg: NDStructure<T>): NDElement<T, F> {
        return produce { with(field) { ln(arg[it]) } }
    }

    override fun sin(arg: NDStructure<T>): NDElement<T, F> {
        return produce { with(field) { sin(arg[it]) } }
    }

    override fun cos(arg: NDStructure<T>): NDElement<T, F> {
        return produce { with(field) { cos(arg[it]) } }
    }
}


