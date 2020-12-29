package kscience.kmath.torch

import kscience.kmath.structures.MutableBuffer

import kotlinx.cinterop.*
import ctorch.*

public abstract class TorchTensorBuffer<T, TVar : CPrimitiveVar> internal constructor(
    internal val scope: DeferScope,
    internal val tensorHandle: COpaquePointer
) : MutableBuffer<T> {
    init {
        scope.defer(::close)
    }

    internal fun close() {
        dispose_tensor(tensorHandle)
    }

    protected abstract val tensorData: CPointer<TVar>

    override val size: Int
        get() = get_numel(tensorHandle)

}


public class TorchTensorBufferFloat internal constructor(
    scope: DeferScope,
    tensorHandle: COpaquePointer
) : TorchTensorBuffer<Float, FloatVar>(scope, tensorHandle) {
    override val tensorData: CPointer<FloatVar> = get_data_float(tensorHandle)!!

    override operator fun get(index: Int): Float = tensorData[index]

    override operator fun set(index: Int, value: Float) {
        tensorData[index] = value
    }

    override operator fun iterator(): Iterator<Float> = (1..size).map { tensorData[it - 1] }.iterator()

    override fun copy(): TorchTensorBufferFloat = TorchTensorBufferFloat(
        scope = scope,
        tensorHandle = copy_tensor(tensorHandle)!!
    )
}

public class TorchTensorBufferInt internal constructor(
    scope: DeferScope,
    tensorHandle: COpaquePointer
) : TorchTensorBuffer<Int, IntVar>(scope, tensorHandle) {
    override val tensorData: CPointer<IntVar> = get_data_int(tensorHandle)!!

    override operator fun get(index: Int): Int = tensorData[index]

    override operator fun set(index: Int, value: Int) {
        tensorData[index] = value
    }

    override operator fun iterator(): Iterator<Int> = (1..size).map { tensorData[it - 1] }.iterator()

    override fun copy(): TorchTensorBufferInt = TorchTensorBufferInt(
        scope = scope,
        tensorHandle = copy_tensor(tensorHandle)!!
    )
}

