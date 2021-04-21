package space.kscience.kmath.tensors

// https://proofwiki.org/wiki/Definition:Division_Algebra
public interface TensorPartialDivisionAlgebra<T> :
        TensorAlgebra<T> {
    public operator fun TensorStructure<T>.div(value: T): TensorStructure<T>
    public operator fun TensorStructure<T>.div(other: TensorStructure<T>): TensorStructure<T>
    public operator fun TensorStructure<T>.divAssign(value: T)
    public operator fun TensorStructure<T>.divAssign(other: TensorStructure<T>)
}
