package kscience.kmath.torch

import kotlinx.cinterop.*
import ctorch.*


public sealed class TorchTensorAlgebra<
        T,
        TorchTensorBufferImpl : TorchTensorBuffer<T>,
        PrimitiveArrayType>
constructor(
    internal val scope: DeferScope
) {

    protected abstract fun wrap(
        outTensorHandle: COpaquePointer,
        outStrides: TorchTensorStrides
    ): TorchTensor<T, TorchTensorBufferImpl>

    public infix fun TorchTensor<T, TorchTensorBufferImpl>.swap(other: TorchTensor<T, TorchTensorBufferImpl>): Unit {
        check(this.shape contentEquals other.shape) {
            "Attempt to swap tensors with different shapes"
        }
        this.buffer.tensorHandle = other.buffer.tensorHandle.also {
            other.buffer.tensorHandle = this.buffer.tensorHandle
        }
    }

    public abstract fun copyFromArray(array: PrimitiveArrayType, shape: IntArray): TorchTensor<T, TorchTensorBufferImpl>

    public infix fun TorchTensor<T, TorchTensorBufferImpl>.dot(other: TorchTensor<T, TorchTensorBufferImpl>):
            TorchTensor<T, TorchTensorBufferImpl> {
        val resultHandle = matmul(this.buffer.tensorHandle, other.buffer.tensorHandle)!!
        val strides = populateStridesFromNative(tensorHandle = resultHandle)
        return wrap(resultHandle, strides)
    }
}


public sealed class TorchTensorField<T, TorchTensorBufferImpl : TorchTensorBuffer<T>, PrimitiveArrayType>
constructor(scope: DeferScope) : TorchTensorAlgebra<T, TorchTensorBufferImpl, PrimitiveArrayType>(scope) {
    public abstract fun randn(shape: IntArray): TorchTensor<T, TorchTensorBufferImpl>
}


public class TorchTensorFloatAlgebra(scope: DeferScope) :
    TorchTensorField<Float, TorchTensorBufferFloat, FloatArray>(scope) {
    override fun wrap(
        outTensorHandle: COpaquePointer,
        outStrides: TorchTensorStrides
    ): TorchTensorFloat = TorchTensorFloat(scope = scope, tensorHandle = outTensorHandle, strides = outStrides)

    override fun randn(shape: IntArray): TorchTensor<Float, TorchTensorBufferFloat> {
        val tensorHandle = randn_float(shape.toCValues(), shape.size)!!
        val strides = populateStridesFromNative(tensorHandle = tensorHandle, rawShape = shape)
        return wrap(tensorHandle, strides)
    }

    override fun copyFromArray(array: FloatArray, shape: IntArray): TorchTensorFloat =
        TorchTensor.copyFromFloatArray(scope, array, shape)
}


public fun <R> TorchTensorFloatAlgebra(block: TorchTensorFloatAlgebra.() -> R): R =
    memScoped { TorchTensorFloatAlgebra(this).block() }