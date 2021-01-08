package kscience.kmath.torch

import kscience.kmath.structures.MutableBuffer

import kotlinx.cinterop.*
import ctorch.*

public sealed class TorchTensorBuffer<T> constructor(
    scope: DeferScope,
    tensorHandle: COpaquePointer?
) : MutableBuffer<T>, TorchMemoryHolder(scope, tensorHandle) {

    override val size: Int
        get(){
            return get_numel(tensorHandle!!)
        }

    internal abstract fun wrap(outScope: DeferScope, outTensorHandle: COpaquePointer): TorchTensorBuffer<T>

    override fun copy(): TorchTensorBuffer<T> = wrap(
        outScope = scope,
        outTensorHandle = copy_tensor(tensorHandle!!)!!
    )
}

public class TorchTensorBufferFloat internal constructor(
    scope: DeferScope,
    tensorHandle: COpaquePointer
) : TorchTensorBuffer<Float>(scope, tensorHandle) {

    private val tensorData: CPointer<FloatVar>
        get(){
            return get_data_float(tensorHandle!!)!!
        }

    override operator fun get(index: Int): Float = tensorData[index]

    override operator fun set(index: Int, value: Float) {
        tensorData[index] = value
    }

    override operator fun iterator(): Iterator<Float> = (1..size).map { tensorData[it - 1] }.iterator()

    override fun wrap(outScope: DeferScope, outTensorHandle: COpaquePointer) = TorchTensorBufferFloat(
        scope = outScope,
        tensorHandle = outTensorHandle
    )
}


public class TorchTensorBufferInt internal constructor(
    scope: DeferScope,
    tensorHandle: COpaquePointer
) : TorchTensorBuffer<Int>(scope, tensorHandle) {

    private val tensorData: CPointer<IntVar>
        get(){
            return get_data_int(tensorHandle!!)!!
        }

    override operator fun get(index: Int): Int = tensorData[index]

    override operator fun set(index: Int, value: Int) {
        tensorData[index] = value
    }

    override operator fun iterator(): Iterator<Int> = (1..size).map { tensorData[it - 1] }.iterator()

    override fun wrap(outScope: DeferScope, outTensorHandle: COpaquePointer) = TorchTensorBufferInt(
        scope = outScope,
        tensorHandle = outTensorHandle
    )
}

public class TorchTensorBufferFloatGPU internal constructor(
    scope: DeferScope,
    tensorHandle: COpaquePointer
) : TorchTensorBuffer<Float>(scope, tensorHandle) {

    override operator fun get(index: Int): Float = get_at_offset_float(tensorHandle!!, index)

    override operator fun set(index: Int, value: Float) {
        set_at_offset_float(tensorHandle!!, index, value)
    }

    override operator fun iterator(): Iterator<Float> {
        val cpuCopy = copy_to_cpu(tensorHandle!!)!!
        val tensorCpuData = get_data_float(cpuCopy)!!
        val iteratorResult = (1..size).map { tensorCpuData[it - 1] }.iterator()
        dispose_tensor(cpuCopy)
        return iteratorResult
    }

    override fun wrap(outScope: DeferScope, outTensorHandle: COpaquePointer) = TorchTensorBufferFloatGPU(
        scope = outScope,
        tensorHandle = outTensorHandle
    )
}
