package scientifik.kmath.structures

import scientifik.kmath.operations.ExponentialOperations
import scientifik.kmath.operations.ExtendedField
import scientifik.kmath.operations.PowerOperations
import scientifik.kmath.operations.TrigonometricOperations


interface ExtendedNDField<T : Any, F : ExtendedField<T>, N : NDStructure<out T>> :
        NDField<T, F, N>,
        TrigonometricOperations<N>,
        PowerOperations<N>,
        ExponentialOperations<N>


/**
 * NDField that supports [ExtendedField] operations on its elements
 */
class ExtendedNDFieldWrapper<T : Any, F : ExtendedField<T>, N : NDStructure<out T>>(private val ndField: NDField<T, F, N>) : ExtendedNDField<T, F, N>, NDField<T,F,N> by ndField {

    override val shape: IntArray get() = ndField.shape
    override val field: F get() = ndField.field

    override fun produce(initializer: F.(IntArray) -> T) = ndField.produce(initializer)

    override fun power(arg: N, pow: Double): N {
        return produce { with(field) { power(arg[it], pow) } }
    }

    override fun exp(arg: N): N {
        return produce { with(field) { exp(arg[it]) } }
    }

    override fun ln(arg: N): N {
        return produce { with(field) { ln(arg[it]) } }
    }

    override fun sin(arg: N): N {
        return produce { with(field) { sin(arg[it]) } }
    }

    override fun cos(arg: N): N {
        return produce { with(field) { cos(arg[it]) } }
    }
}


