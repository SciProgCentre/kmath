package scientifik.kmath.structures

import scientifik.kmath.operations.ExtendedField

/**
 * [ExtendedField] over [NDStructure].
 *
 * @param T the type of the element contained in ND structure.
 * @param N the type of ND structure.
 * @param F the extended field of structure elements.
 */
interface ExtendedNDField<T : Any, F : ExtendedField<T>, N : NDStructure<T>> : NDField<T, F, N>, ExtendedField<N>

///**
// * NDField that supports [ExtendedField] operations on its elements
// */
//class ExtendedNDFieldWrapper<T : Any, F : ExtendedField<T>, N : NDStructure<T>>(private val ndField: NDField<T, F, N>) :
//    ExtendedNDField<T, F, N>, NDField<T, F, N> by ndField {
//
//    override val shape: IntArray get() = ndField.shape
//    override val elementContext: F get() = ndField.elementContext
//
//    override fun produce(initializer: F.(IntArray) -> T) = ndField.produce(initializer)
//
//    override fun power(arg: N, pow: Double): N {
//        return produce { with(elementContext) { power(arg[it], pow) } }
//    }
//
//    override fun exp(arg: N): N {
//        return produce { with(elementContext) { exp(arg[it]) } }
//    }
//
//    override fun ln(arg: N): N {
//        return produce { with(elementContext) { ln(arg[it]) } }
//    }
//
//    override fun sin(arg: N): N {
//        return produce { with(elementContext) { sin(arg[it]) } }
//    }
//
//    override fun cos(arg: N): N {
//        return produce { with(elementContext) { cos(arg[it]) } }
//    }
//}
