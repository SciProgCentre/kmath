package scientifik.kmath.structures

import scientifik.kmath.operations.ExponentialOperations
import scientifik.kmath.operations.ExtendedField
import scientifik.kmath.operations.PowerOperations
import scientifik.kmath.operations.TrigonometricOperations


interface ExtendedNDField<T : Any, F : ExtendedField<T>, N : NDStructure<T>> :
    NDField<T, F, N>,
    TrigonometricOperations<N>,
    PowerOperations<N>,
    ExponentialOperations<N>


/**
 * NDField that supports [ExtendedField] operations on its elements
 */
class ExtendedNDFieldWrapper<T : Any, F : ExtendedField<T>, N : NDStructure<T>>(private val ndField: NDField<T, F, N>) :
    ExtendedNDField<T, F, N>, NDField<T, F, N> by ndField {

    override val shape: IntArray get() = ndField.shape
    override val elementField: F get() = ndField.elementField

    override fun produce(initializer: F.(IntArray) -> T) = ndField.produce(initializer)

    override fun power(arg: N, pow: Double): N {
        return produce { with(elementField) { power(arg[it], pow) } }
    }

    override fun exp(arg: N): N {
        return produce { with(elementField) { exp(arg[it]) } }
    }

    override fun ln(arg: N): N {
        return produce { with(elementField) { ln(arg[it]) } }
    }

    override fun sin(arg: N): N {
        return produce { with(elementField) { sin(arg[it]) } }
    }

    override fun cos(arg: N): N {
        return produce { with(elementField) { cos(arg[it]) } }
    }
}


