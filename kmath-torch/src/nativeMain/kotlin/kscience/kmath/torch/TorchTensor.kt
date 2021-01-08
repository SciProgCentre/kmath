package kscience.kmath.torch

import kscience.kmath.structures.*

import kotlinx.cinterop.*
import ctorch.*

public sealed class TorchTensor<T, out TorchTensorBufferImpl : TorchTensorBuffer<T>> :
    MutableNDBufferTrait<T, TorchTensorBufferImpl, TorchTensorStrides>() {

    public fun asBuffer(): MutableBuffer<T> = buffer

    public companion object {
        public fun copyFromFloatArray(scope: DeferScope, array: FloatArray, shape: IntArray): TorchTensorFloat {
            val tensorHandle: COpaquePointer = copy_from_blob_float(
                array.toCValues(), shape.toCValues(), shape.size
            )!!
            return TorchTensorFloat(
                scope = scope,
                tensorHandle = tensorHandle,
                strides = populateStridesFromNative(tensorHandle, rawShape = shape)
            )
        }

        public fun copyFromIntArray(scope: DeferScope, array: IntArray, shape: IntArray): TorchTensorInt {
            val tensorHandle: COpaquePointer = copy_from_blob_int(
                array.toCValues(), shape.toCValues(), shape.size
            )!!
            return TorchTensorInt(
                scope = scope,
                tensorHandle = tensorHandle,
                strides = populateStridesFromNative(tensorHandle, rawShape = shape)
            )
        }

        public fun copyFromFloatArrayToGPU(
            scope: DeferScope,
            array: FloatArray,
            shape: IntArray,
            device: Int
        ): TorchTensorFloatGPU {
            val tensorHandle: COpaquePointer = copy_from_blob_to_gpu_float(
                array.toCValues(), shape.toCValues(), shape.size, device
            )!!
            return TorchTensorFloatGPU(
                scope = scope,
                tensorHandle = tensorHandle,
                strides = populateStridesFromNative(tensorHandle, rawShape = shape)
            )
        }
    }

    override fun toString(): String {
        val nativeStringRepresentation: CPointer<ByteVar> = tensor_to_string(buffer.tensorHandle!!)!!
        val stringRepresentation = nativeStringRepresentation.toKString()
        dispose_char(nativeStringRepresentation)
        return stringRepresentation
    }

    protected abstract fun wrap(
        outScope: DeferScope,
        outTensorHandle: COpaquePointer,
        outStrides: TorchTensorStrides
    ): TorchTensor<T, TorchTensorBufferImpl>

    public fun copy(): TorchTensor<T, TorchTensorBufferImpl> = wrap(
        outScope = buffer.scope,
        outTensorHandle = copy_tensor(buffer.tensorHandle!!)!!,
        outStrides = strides
    )

}

public class TorchTensorFloat internal constructor(
    scope: DeferScope,
    tensorHandle: COpaquePointer,
    override val strides: TorchTensorStrides
) : TorchTensor<Float, TorchTensorBufferFloat>() {
    override val buffer: TorchTensorBufferFloat = TorchTensorBufferFloat(scope, tensorHandle)
    override fun wrap(
        outScope: DeferScope,
        outTensorHandle: COpaquePointer,
        outStrides: TorchTensorStrides
    ): TorchTensorFloat = TorchTensorFloat(
        scope = outScope, tensorHandle = outTensorHandle, strides = outStrides
    )
}

public class TorchTensorInt internal constructor(
    scope: DeferScope,
    tensorHandle: COpaquePointer,
    override val strides: TorchTensorStrides
) : TorchTensor<Int, TorchTensorBufferInt>() {
    override val buffer: TorchTensorBufferInt = TorchTensorBufferInt(scope, tensorHandle)
    override fun wrap(
        outScope: DeferScope,
        outTensorHandle: COpaquePointer,
        outStrides: TorchTensorStrides
    ): TorchTensorInt = TorchTensorInt(
        scope = outScope, tensorHandle = outTensorHandle, strides = outStrides
    )
}

public class TorchTensorFloatGPU internal constructor(
    scope: DeferScope,
    tensorHandle: COpaquePointer,
    override val strides: TorchTensorStrides
) : TorchTensor<Float, TorchTensorBufferFloatGPU>() {
    override val buffer: TorchTensorBufferFloatGPU = TorchTensorBufferFloatGPU(scope, tensorHandle)
    override fun wrap(
        outScope: DeferScope,
        outTensorHandle: COpaquePointer,
        outStrides: TorchTensorStrides
    ): TorchTensorFloatGPU =
        TorchTensorFloatGPU(
            scope = outScope, tensorHandle = outTensorHandle, strides = outStrides
        )
}

