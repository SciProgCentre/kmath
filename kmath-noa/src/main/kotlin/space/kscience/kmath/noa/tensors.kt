/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.noa

import space.kscience.kmath.misc.PerformancePitfall
import space.kscience.kmath.noa.memory.NoaResource
import space.kscience.kmath.noa.memory.NoaScope
import space.kscience.kmath.tensors.api.Tensor
import space.kscience.kmath.tensors.core.TensorLinearStructure

internal typealias TensorHandle = Long

public sealed class NoaTensor<T>
constructor(scope: NoaScope, internal val tensorHandle: TensorHandle) :
    NoaResource(scope), Tensor<T> {

    override fun dispose(): Unit = JNoa.disposeTensor(tensorHandle)

    internal abstract fun item(): T

    override val dimension: Int get() = JNoa.getDim(tensorHandle)

    override val shape: IntArray
        get() = (1..dimension).map { JNoa.getShapeAt(tensorHandle, it - 1) }.toIntArray()

    public val strides: IntArray
        get() = (1..dimension).map { JNoa.getStrideAt(tensorHandle, it - 1) }.toIntArray()

    public val numElements: Int get() = JNoa.getNumel(tensorHandle)

    public val device: Device get() = Device.fromInt(JNoa.getDevice(tensorHandle))

    override fun toString(): String = JNoa.tensorToString(tensorHandle)

    @PerformancePitfall
    override fun elements(): Sequence<Pair<IntArray, T>> {
        if (dimension == 0) {
            return emptySequence()
        }
        val indices = (1..numElements).asSequence().map {
            TensorLinearStructure.indexFromOffset(it - 1, strides, dimension)
        }
        return indices.map { it to get(it) }
    }


    public fun asDouble(): NoaDoubleTensor = NoaDoubleTensor(
        scope = scope,
        tensorHandle = JNoa.copyToDouble(this.tensorHandle)
    )

    public fun asFloat(): NoaFloatTensor = NoaFloatTensor(
        scope = scope,
        tensorHandle = JNoa.copyToFloat(this.tensorHandle)
    )

    public fun asLong(): NoaLongTensor = NoaLongTensor(
        scope = scope,
        tensorHandle = JNoa.copyToLong(this.tensorHandle)
    )

    public fun asInt(): NoaIntTensor = NoaIntTensor(
        scope = scope,
        tensorHandle = JNoa.copyToInt(this.tensorHandle)
    )
}

public sealed class NoaTensorOverField<T>
constructor(scope: NoaScope, tensorHandle: Long) :
    NoaTensor<T>(scope, tensorHandle) {
    public var requiresGrad: Boolean
        get() = JNoa.requiresGrad(tensorHandle)
        set(value) = JNoa.setRequiresGrad(tensorHandle, value)
}


public class NoaDoubleTensor
internal constructor(scope: NoaScope, tensorHandle: TensorHandle) :
    NoaTensorOverField<Double>(scope, tensorHandle) {

    override fun item(): Double = JNoa.getItemDouble(tensorHandle)

    @PerformancePitfall
    override fun get(index: IntArray): Double = JNoa.getDouble(tensorHandle, index)

    @PerformancePitfall
    override fun set(index: IntArray, value: Double) {
        JNoa.setDouble(tensorHandle, index, value)
    }
}

public class NoaFloatTensor
internal constructor(scope: NoaScope, tensorHandle: TensorHandle) :
    NoaTensorOverField<Float>(scope, tensorHandle) {

    override fun item(): Float = JNoa.getItemFloat(tensorHandle)

    @PerformancePitfall
    override fun get(index: IntArray): Float = JNoa.getFloat(tensorHandle, index)

    @PerformancePitfall
    override fun set(index: IntArray, value: Float) {
        JNoa.setFloat(tensorHandle, index, value)
    }
}

public class NoaLongTensor
internal constructor(scope: NoaScope, tensorHandle: TensorHandle) :
    NoaTensor<Long>(scope, tensorHandle) {

    override fun item(): Long = JNoa.getItemLong(tensorHandle)

    @PerformancePitfall
    override fun get(index: IntArray): Long = JNoa.getLong(tensorHandle, index)

    @PerformancePitfall
    override fun set(index: IntArray, value: Long) {
        JNoa.setLong(tensorHandle, index, value)
    }
}

public class NoaIntTensor
internal constructor(scope: NoaScope, tensorHandle: TensorHandle) :
    NoaTensor<Int>(scope, tensorHandle) {

    override fun item(): Int = JNoa.getItemInt(tensorHandle)

    @PerformancePitfall
    override fun get(index: IntArray): Int = JNoa.getInt(tensorHandle, index)

    @PerformancePitfall
    override fun set(index: IntArray, value: Int) {
        JNoa.setInt(tensorHandle, index, value)
    }
}

public sealed class Device {
    public object CPU : Device() {
        override fun toString(): String {
            return "CPU"
        }
    }

    public data class CUDA(val index: Int) : Device()

    public fun toInt(): Int {
        when (this) {
            is CPU -> return 0
            is CUDA -> return this.index + 1
        }
    }

    public companion object {
        public fun fromInt(deviceInt: Int): Device {
            return if (deviceInt == 0) CPU else CUDA(
                deviceInt - 1
            )
        }
    }
}
