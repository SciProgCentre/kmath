package space.kscience.kmath.tensors

public interface ReduceOpsTensorAlgebra<T, TensorType : TensorStructure<T>> :
    TensorAlgebra<T, TensorType> {
    public fun TensorType.value(): T

}