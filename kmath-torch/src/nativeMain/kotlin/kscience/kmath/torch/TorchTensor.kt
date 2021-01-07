package kscience.kmath.torch

import kscience.kmath.structures.*

import kotlinx.cinterop.*
import ctorch.*

public abstract class TorchTensor<T, out TorchTensorBufferImpl : TorchTensorBuffer<T>> :
    MutableNDBufferTrait<T, TorchTensorBufferImpl, TorchTensorStrides>() {

    public companion object {
        public fun copyFromFloatArray(scope: DeferScope, array: FloatArray, shape: IntArray): TorchTensorFloat {
            val tensorHandle: COpaquePointer = copy_from_blob_float(
                array.toCValues(), shape.toCValues(), shape.size
            )!!
            return TorchTensorFloat(populateStridesFromNative(tensorHandle, rawShape = shape), scope, tensorHandle)
        }

        public fun copyFromIntArray(scope: DeferScope, array: IntArray, shape: IntArray): TorchTensorInt {
            val tensorHandle: COpaquePointer = copy_from_blob_int(
                array.toCValues(), shape.toCValues(), shape.size
            )!!
            return TorchTensorInt(populateStridesFromNative(tensorHandle, rawShape = shape), scope, tensorHandle)
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
            return TorchTensorFloatGPU(populateStridesFromNative(tensorHandle, rawShape = shape), scope, tensorHandle)
        }
    }

    override fun toString(): String {
        val nativeStringRepresentation: CPointer<ByteVar> = tensor_to_string(buffer.tensorHandle!!)!!
        val stringRepresentation = nativeStringRepresentation.toKString()
        dispose_char(nativeStringRepresentation)
        return stringRepresentation
    }

    internal abstract fun wrap(
        outStrides: TorchTensorStrides,
        outScope: DeferScope,
        outTensorHandle: COpaquePointer
    ): TorchTensor<T, TorchTensorBufferImpl>

    public fun copy(): TorchTensor<T, TorchTensorBufferImpl> = wrap(
        outStrides = strides,
        outScope = buffer.scope,
        outTensorHandle = copy_tensor(buffer.tensorHandle!!)!!
    )

}

public class TorchTensorFloat internal constructor(
    override val strides: TorchTensorStrides,
    scope: DeferScope,
    tensorHandle: COpaquePointer
) : TorchTensor<Float, TorchTensorBufferFloat>() {
    override val buffer: TorchTensorBufferFloat = TorchTensorBufferFloat(scope, tensorHandle)
    override fun wrap(outStrides: TorchTensorStrides, outScope: DeferScope, outTensorHandle: COpaquePointer) =
        TorchTensorFloat(
            strides = outStrides, scope = outScope, tensorHandle = outTensorHandle
        )
}

public class TorchTensorInt internal constructor(
    override val strides: TorchTensorStrides,
    scope: DeferScope,
    tensorHandle: COpaquePointer
) : TorchTensor<Int, TorchTensorBufferInt>() {
    override val buffer: TorchTensorBufferInt = TorchTensorBufferInt(scope, tensorHandle)
    override fun wrap(outStrides: TorchTensorStrides, outScope: DeferScope, outTensorHandle: COpaquePointer) =
        TorchTensorInt(
            strides = outStrides, scope = outScope, tensorHandle = outTensorHandle
        )
}

public class TorchTensorFloatGPU internal constructor(
    override val strides: TorchTensorStrides,
    scope: DeferScope,
    tensorHandle: COpaquePointer
) : TorchTensor<Float, TorchTensorBufferFloatGPU>() {
    override val buffer: TorchTensorBufferFloatGPU = TorchTensorBufferFloatGPU(scope, tensorHandle)
    override fun wrap(outStrides: TorchTensorStrides, outScope: DeferScope, outTensorHandle: COpaquePointer) =
        TorchTensorFloatGPU(
            strides = outStrides, scope = outScope, tensorHandle = outTensorHandle
        )
}

