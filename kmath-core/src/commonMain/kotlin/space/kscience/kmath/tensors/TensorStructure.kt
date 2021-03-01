package space.kscience.kmath.tensors

import space.kscience.kmath.nd.MutableNDStructure

public interface TensorStructure<T> : MutableNDStructure<T> {
    public fun item(): T

    // A tensor can have empty shape, in which case it represents just a value
    public fun value(): T {
        checkIsValue()
        return item()
    }
}

public inline fun <T> TensorStructure<T>.isValue(): Boolean {
    return (dimension == 0)
}

public inline fun <T> TensorStructure<T>.isNotValue(): Boolean = !this.isValue()

public inline fun <T> TensorStructure<T>.checkIsValue(): Unit = check(this.isValue()) {
    "This tensor has shape ${shape.toList()}"
}