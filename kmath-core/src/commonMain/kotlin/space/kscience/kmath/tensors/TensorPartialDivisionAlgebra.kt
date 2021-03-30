package space.kscience.kmath.tensors

// https://proofwiki.org/wiki/Definition:Division_Algebra
public interface TensorPartialDivisionAlgebra<T, TensorType : TensorStructure<T>> :
        TensorAlgebra<T, TensorType> {
    public operator fun TensorType.div(value: T): TensorType
    public operator fun TensorType.div(other: TensorType): TensorType
    public operator fun TensorType.divAssign(value: T)
    public operator fun TensorType.divAssign(other: TensorType)
}
