package kscience.kmath.torch

import kscience.kmath.structures.*

import kotlinx.cinterop.*
import ctorch.*

public abstract class TorchTensor<T,
        TVar : CPrimitiveVar,
        TorchTensorBufferImpl : TorchTensorBuffer<T, TVar>> :
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
    }

    override fun toString(): String {
        val nativeStringRepresentation: CPointer<ByteVar> = tensor_to_string(buffer.tensorHandle)!!
        val stringRepresentation = nativeStringRepresentation.toKString()
        dispose_char(nativeStringRepresentation)
        return stringRepresentation
    }

}

public class TorchTensorFloat internal constructor(
    override val strides: TorchTensorStrides,
    scope: DeferScope,
    tensorHandle: COpaquePointer
): TorchTensor<Float, FloatVar, TorchTensorBufferFloat>() {
    override val buffer: TorchTensorBufferFloat = TorchTensorBufferFloat(scope, tensorHandle)
}

public class TorchTensorInt internal constructor(
    override val strides: TorchTensorStrides,
    scope: DeferScope,
    tensorHandle: COpaquePointer
): TorchTensor<Int, IntVar, TorchTensorBufferInt>() {
    override val buffer: TorchTensorBufferInt = TorchTensorBufferInt(scope, tensorHandle)
}

