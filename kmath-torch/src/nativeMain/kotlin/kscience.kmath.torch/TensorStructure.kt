package kscience.kmath.torch

import kscience.kmath.structures.MutableNDStructure

public abstract class TensorStructure<T>: MutableNDStructure<T> {

    // A tensor can have empty shape, in which case it represents just a value
    public abstract fun value(): T

    // Tensors might hold shared resources
    override fun equals(other: Any?): Boolean = false
    override fun hashCode(): Int = 0
}