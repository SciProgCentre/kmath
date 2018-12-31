package scientifik.kmath.structures

import scientifik.kmath.operations.ExponentialOperations
import scientifik.kmath.operations.ExtendedField
import scientifik.kmath.operations.PowerOperations
import scientifik.kmath.operations.TrigonometricOperations


interface ExtendedNDField<T : Any, F : ExtendedField<T>> :
        NDField<T, F>,
        TrigonometricOperations<NDStructure<T>>,
        PowerOperations<NDStructure<T>>,
        ExponentialOperations<NDStructure<T>>


/**
 * NDField that supports [ExtendedField] operations on its elements
 */
inline class ExtendedNDFieldWrapper<T : Any, F : ExtendedField<T>>(private val ndField: NDField<T, F>) : ExtendedNDField<T, F> {

    override val shape: IntArray get() = ndField.shape
    override val field: F get() = ndField.field

    override fun produce(initializer: F.(IntArray) -> T): NDElement<T, F> = ndField.produce(initializer)

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


