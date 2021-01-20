package kscience.kmath.torch

import kscience.kmath.memory.DeferScope
import kscience.kmath.memory.withDeferScope

public sealed class TorchTensorAlgebraJVM<
        T,
        PrimitiveArrayType,
        TorchTensorType : TorchTensorJVM<T>> constructor(
    internal val scope: DeferScope
) : TorchTensorAlgebra<T, PrimitiveArrayType, TorchTensorType> {
    override fun getNumThreads(): Int {
        return JTorch.getNumThreads()
    }

    override fun setNumThreads(numThreads: Int): Unit {
        JTorch.setNumThreads(numThreads)
    }

    override fun cudaAvailable(): Boolean {
        return JTorch.cudaIsAvailable()
    }

    override fun setSeed(seed: Int): Unit {
        JTorch.setSeed(seed)
    }

    override var checks: Boolean = false

    internal abstract fun wrap(tensorHandle: Long): TorchTensorType

    override operator fun TorchTensorType.times(other: TorchTensorType): TorchTensorType {
        if (checks) checkLinearOperation(this, other)
        return wrap(JTorch.timesTensor(this.tensorHandle, other.tensorHandle))
    }

    override operator fun TorchTensorType.timesAssign(other: TorchTensorType): Unit {
        if (checks) checkLinearOperation(this, other)
        JTorch.timesTensorAssign(this.tensorHandle, other.tensorHandle)
    }

    override operator fun TorchTensorType.plus(other: TorchTensorType): TorchTensorType {
        if (checks) checkLinearOperation(this, other)
        return wrap(JTorch.plusTensor(this.tensorHandle, other.tensorHandle))
    }

    override operator fun TorchTensorType.plusAssign(other: TorchTensorType): Unit {
        if (checks) checkLinearOperation(this, other)
        JTorch.plusTensorAssign(this.tensorHandle, other.tensorHandle)
    }

    override operator fun TorchTensorType.minus(other: TorchTensorType): TorchTensorType {
        if (checks) checkLinearOperation(this, other)
        return wrap(JTorch.minusTensor(this.tensorHandle, other.tensorHandle))
    }

    override operator fun TorchTensorType.minusAssign(other: TorchTensorType): Unit {
        if (checks) checkLinearOperation(this, other)
        JTorch.minusTensorAssign(this.tensorHandle, other.tensorHandle)
    }

    override operator fun TorchTensorType.unaryMinus(): TorchTensorType =
        wrap(JTorch.unaryMinus(this.tensorHandle))

    override infix fun TorchTensorType.dot(other: TorchTensorType): TorchTensorType {
        if (checks) checkDotOperation(this, other)
        return wrap(JTorch.matmul(this.tensorHandle, other.tensorHandle))
    }

    override infix fun TorchTensorType.dotAssign(other: TorchTensorType): Unit {
        if (checks) checkDotOperation(this, other)
        JTorch.matmulAssign(this.tensorHandle, other.tensorHandle)
    }

    override infix fun TorchTensorType.dotRightAssign(other: TorchTensorType): Unit {
        if (checks) checkDotOperation(this, other)
        JTorch.matmulRightAssign(this.tensorHandle, other.tensorHandle)
    }

    override fun diagonalEmbedding(
        diagonalEntries: TorchTensorType, offset: Int, dim1: Int, dim2: Int
    ): TorchTensorType =
        wrap(JTorch.diagEmbed(diagonalEntries.tensorHandle, offset, dim1, dim2))

    override fun TorchTensorType.transpose(i: Int, j: Int): TorchTensorType {
        if (checks) checkTranspose(this.dimension, i, j)
        return wrap(JTorch.transposeTensor(tensorHandle, i, j))
    }

    override fun TorchTensorType.transposeAssign(i: Int, j: Int): Unit {
        if (checks) checkTranspose(this.dimension, i, j)
        JTorch.transposeTensorAssign(tensorHandle, i, j)
    }

    override fun TorchTensorType.view(shape: IntArray): TorchTensorType {
        if (checks) checkView(this, shape)
        return wrap(JTorch.viewTensor(this.tensorHandle, shape))
    }

    override fun TorchTensorType.abs(): TorchTensorType = wrap(JTorch.absTensor(tensorHandle))
    override fun TorchTensorType.absAssign(): Unit = JTorch.absTensorAssign(tensorHandle)

    override fun TorchTensorType.sum(): TorchTensorType = wrap(JTorch.sumTensor(tensorHandle))
    override fun TorchTensorType.sumAssign(): Unit = JTorch.sumTensorAssign(tensorHandle)

    override fun TorchTensorType.randIntegral(low: Long, high: Long): TorchTensorType =
        wrap(JTorch.randintLike(this.tensorHandle, low, high))

    override fun TorchTensorType.randIntegralAssign(low: Long, high: Long): Unit =
        JTorch.randintLikeAssign(this.tensorHandle, low, high)

    override fun TorchTensorType.copy(): TorchTensorType =
        wrap(JTorch.copyTensor(this.tensorHandle))

    override fun TorchTensorType.copyToDevice(device: Device): TorchTensorType =
        wrap(JTorch.copyToDevice(this.tensorHandle, device.toInt()))

    override infix fun TorchTensorType.swap(other: TorchTensorType): Unit =
        JTorch.swapTensors(this.tensorHandle, other.tensorHandle)
}

public sealed class TorchTensorPartialDivisionAlgebraJVM<T, PrimitiveArrayType,
        TorchTensorType : TorchTensorOverFieldJVM<T>>(scope: DeferScope) :
    TorchTensorAlgebraJVM<T, PrimitiveArrayType, TorchTensorType>(scope),
    TorchTensorPartialDivisionAlgebra<T, PrimitiveArrayType, TorchTensorType> {

    override operator fun TorchTensorType.div(other: TorchTensorType): TorchTensorType {
        if (checks) checkLinearOperation(this, other)
        return wrap(JTorch.divTensor(this.tensorHandle, other.tensorHandle))
    }

    override operator fun TorchTensorType.divAssign(other: TorchTensorType): Unit {
        if (checks) checkLinearOperation(this, other)
        JTorch.divTensorAssign(this.tensorHandle, other.tensorHandle)
    }

    override fun TorchTensorType.randUniform(): TorchTensorType =
        wrap(JTorch.randLike(this.tensorHandle))

    override fun TorchTensorType.randUniformAssign(): Unit =
        JTorch.randLikeAssign(this.tensorHandle)

    override fun TorchTensorType.randNormal(): TorchTensorType =
        wrap(JTorch.randnLike(this.tensorHandle))

    override fun TorchTensorType.randNormalAssign(): Unit =
        JTorch.randnLikeAssign(this.tensorHandle)

    override fun TorchTensorType.exp(): TorchTensorType = wrap(JTorch.expTensor(tensorHandle))
    override fun TorchTensorType.expAssign(): Unit = JTorch.expTensorAssign(tensorHandle)
    override fun TorchTensorType.log(): TorchTensorType = wrap(JTorch.logTensor(tensorHandle))
    override fun TorchTensorType.logAssign(): Unit = JTorch.logTensorAssign(tensorHandle)

    override fun TorchTensorType.svd(): Triple<TorchTensorType, TorchTensorType, TorchTensorType> {
        val U = JTorch.emptyTensor()
        val V = JTorch.emptyTensor()
        val S = JTorch.emptyTensor()
        JTorch.svdTensor(this.tensorHandle, U, S, V)
        return Triple(wrap(U), wrap(S), wrap(V))
    }

    override fun TorchTensorType.symEig(eigenvectors: Boolean): Pair<TorchTensorType, TorchTensorType> {
        val V = JTorch.emptyTensor()
        val S = JTorch.emptyTensor()
        JTorch.symeigTensor(this.tensorHandle, S, V, eigenvectors)
        return Pair(wrap(S), wrap(V))
    }

    override fun TorchTensorType.grad(variable: TorchTensorType, retainGraph: Boolean): TorchTensorType {
        if (checks) this.checkIsValue()
        return wrap(JTorch.autogradTensor(this.tensorHandle, variable.tensorHandle, retainGraph))
    }

    override infix fun TorchTensorType.hess(variable: TorchTensorType): TorchTensorType {
        if (checks) this.checkIsValue()
        return wrap(JTorch.autohessTensor(this.tensorHandle, variable.tensorHandle))
    }

    override fun TorchTensorType.detachFromGraph(): TorchTensorType =
        wrap(JTorch.detachFromGraph(this.tensorHandle))

}

public class TorchTensorRealAlgebra(scope: DeferScope) :
    TorchTensorPartialDivisionAlgebraJVM<Double, DoubleArray, TorchTensorReal>(scope) {
    override fun wrap(tensorHandle: Long): TorchTensorReal =
        TorchTensorReal(scope = scope, tensorHandle = tensorHandle)

    override fun TorchTensorReal.copyToArray(): DoubleArray =
        this.elements().map { it.second }.toList().toDoubleArray()

    override fun copyFromArray(array: DoubleArray, shape: IntArray, device: Device): TorchTensorReal =
        wrap(JTorch.fromBlobDouble(array, shape, device.toInt()))

    override fun randNormal(shape: IntArray, device: Device): TorchTensorReal =
        wrap(JTorch.randnDouble(shape, device.toInt()))

    override fun randUniform(shape: IntArray, device: Device): TorchTensorReal =
        wrap(JTorch.randDouble(shape, device.toInt()))

    override fun randIntegral(low: Long, high: Long, shape: IntArray, device: Device): TorchTensorReal =
        wrap(JTorch.randintDouble(low, high, shape, device.toInt()))

    override operator fun Double.plus(other: TorchTensorReal): TorchTensorReal =
        wrap(JTorch.plusDouble(this, other.tensorHandle))

    override fun TorchTensorReal.plus(value: Double): TorchTensorReal =
        wrap(JTorch.plusDouble(value, this.tensorHandle))

    override fun TorchTensorReal.plusAssign(value: Double): Unit =
        JTorch.plusDoubleAssign(value, this.tensorHandle)

    override operator fun Double.minus(other: TorchTensorReal): TorchTensorReal =
        wrap(JTorch.plusDouble(-this, other.tensorHandle))

    override fun TorchTensorReal.minus(value: Double): TorchTensorReal =
        wrap(JTorch.plusDouble(-value, this.tensorHandle))

    override fun TorchTensorReal.minusAssign(value: Double): Unit =
        JTorch.plusDoubleAssign(-value, this.tensorHandle)

    override operator fun Double.times(other: TorchTensorReal): TorchTensorReal =
        wrap(JTorch.timesDouble(this, other.tensorHandle))

    override fun TorchTensorReal.times(value: Double): TorchTensorReal =
        wrap(JTorch.timesDouble(value, this.tensorHandle))

    override fun TorchTensorReal.timesAssign(value: Double): Unit =
        JTorch.timesDoubleAssign(value, this.tensorHandle)

    override fun full(value: Double, shape: IntArray, device: Device): TorchTensorReal =
        wrap(JTorch.fullDouble(value, shape, device.toInt()))
}

public class TorchTensorFloatAlgebra(scope: DeferScope) :
    TorchTensorPartialDivisionAlgebraJVM<Float, FloatArray, TorchTensorFloat>(scope) {
    override fun wrap(tensorHandle: Long): TorchTensorFloat =
        TorchTensorFloat(scope = scope, tensorHandle = tensorHandle)

    override fun TorchTensorFloat.copyToArray(): FloatArray =
        this.elements().map { it.second }.toList().toFloatArray()

    override fun copyFromArray(array: FloatArray, shape: IntArray, device: Device): TorchTensorFloat =
        wrap(JTorch.fromBlobFloat(array, shape, device.toInt()))

    override fun randNormal(shape: IntArray, device: Device): TorchTensorFloat =
        wrap(JTorch.randnFloat(shape, device.toInt()))

    override fun randUniform(shape: IntArray, device: Device): TorchTensorFloat =
        wrap(JTorch.randFloat(shape, device.toInt()))

    override fun randIntegral(low: Long, high: Long, shape: IntArray, device: Device): TorchTensorFloat =
        wrap(JTorch.randintFloat(low, high, shape, device.toInt()))

    override operator fun Float.plus(other: TorchTensorFloat): TorchTensorFloat =
        wrap(JTorch.plusFloat(this, other.tensorHandle))

    override fun TorchTensorFloat.plus(value: Float): TorchTensorFloat =
        wrap(JTorch.plusFloat(value, this.tensorHandle))

    override fun TorchTensorFloat.plusAssign(value: Float): Unit =
        JTorch.plusFloatAssign(value, this.tensorHandle)

    override operator fun Float.minus(other: TorchTensorFloat): TorchTensorFloat =
        wrap(JTorch.plusFloat(-this, other.tensorHandle))

    override fun TorchTensorFloat.minus(value: Float): TorchTensorFloat =
        wrap(JTorch.plusFloat(-value, this.tensorHandle))

    override fun TorchTensorFloat.minusAssign(value: Float): Unit =
        JTorch.plusFloatAssign(-value, this.tensorHandle)

    override operator fun Float.times(other: TorchTensorFloat): TorchTensorFloat =
        wrap(JTorch.timesFloat(this, other.tensorHandle))

    override fun TorchTensorFloat.times(value: Float): TorchTensorFloat =
        wrap(JTorch.timesFloat(value, this.tensorHandle))

    override fun TorchTensorFloat.timesAssign(value: Float): Unit =
        JTorch.timesFloatAssign(value, this.tensorHandle)

    override fun full(value: Float, shape: IntArray, device: Device): TorchTensorFloat =
        wrap(JTorch.fullFloat(value, shape, device.toInt()))
}

public class TorchTensorLongAlgebra(scope: DeferScope) :
    TorchTensorAlgebraJVM<Long, LongArray, TorchTensorLong>(scope) {
    override fun wrap(tensorHandle: Long): TorchTensorLong =
        TorchTensorLong(scope = scope, tensorHandle = tensorHandle)

    override fun TorchTensorLong.copyToArray(): LongArray =
        this.elements().map { it.second }.toList().toLongArray()

    override fun copyFromArray(array: LongArray, shape: IntArray, device: Device): TorchTensorLong =
        wrap(JTorch.fromBlobLong(array, shape, device.toInt()))

    override fun randIntegral(low: Long, high: Long, shape: IntArray, device: Device): TorchTensorLong =
        wrap(JTorch.randintLong(low, high, shape, device.toInt()))

    override operator fun Long.plus(other: TorchTensorLong): TorchTensorLong =
        wrap(JTorch.plusLong(this, other.tensorHandle))

    override fun TorchTensorLong.plus(value: Long): TorchTensorLong =
        wrap(JTorch.plusLong(value, this.tensorHandle))

    override fun TorchTensorLong.plusAssign(value: Long): Unit =
        JTorch.plusLongAssign(value, this.tensorHandle)

    override operator fun Long.minus(other: TorchTensorLong): TorchTensorLong =
        wrap(JTorch.plusLong(-this, other.tensorHandle))

    override fun TorchTensorLong.minus(value: Long): TorchTensorLong =
        wrap(JTorch.plusLong(-value, this.tensorHandle))

    override fun TorchTensorLong.minusAssign(value: Long): Unit =
        JTorch.plusLongAssign(-value, this.tensorHandle)

    override operator fun Long.times(other: TorchTensorLong): TorchTensorLong =
        wrap(JTorch.timesLong(this, other.tensorHandle))

    override fun TorchTensorLong.times(value: Long): TorchTensorLong =
        wrap(JTorch.timesLong(value, this.tensorHandle))

    override fun TorchTensorLong.timesAssign(value: Long): Unit =
        JTorch.timesLongAssign(value, this.tensorHandle)

    override fun full(value: Long, shape: IntArray, device: Device): TorchTensorLong =
        wrap(JTorch.fullLong(value, shape, device.toInt()))
}

public class TorchTensorIntAlgebra(scope: DeferScope) :
    TorchTensorAlgebraJVM<Int, IntArray, TorchTensorInt>(scope) {
    override fun wrap(tensorHandle: Long): TorchTensorInt =
        TorchTensorInt(scope = scope, tensorHandle = tensorHandle)

    override fun TorchTensorInt.copyToArray(): IntArray =
        this.elements().map { it.second }.toList().toIntArray()

    override fun copyFromArray(array: IntArray, shape: IntArray, device: Device): TorchTensorInt =
        wrap(JTorch.fromBlobInt(array, shape, device.toInt()))

    override fun randIntegral(low: Long, high: Long, shape: IntArray, device: Device): TorchTensorInt =
        wrap(JTorch.randintInt(low, high, shape, device.toInt()))

    override operator fun Int.plus(other: TorchTensorInt): TorchTensorInt =
        wrap(JTorch.plusInt(this, other.tensorHandle))

    override fun TorchTensorInt.plus(value: Int): TorchTensorInt =
        wrap(JTorch.plusInt(value, this.tensorHandle))

    override fun TorchTensorInt.plusAssign(value: Int): Unit =
        JTorch.plusIntAssign(value, this.tensorHandle)

    override operator fun Int.minus(other: TorchTensorInt): TorchTensorInt =
        wrap(JTorch.plusInt(-this, other.tensorHandle))

    override fun TorchTensorInt.minus(value: Int): TorchTensorInt =
        wrap(JTorch.plusInt(-value, this.tensorHandle))

    override fun TorchTensorInt.minusAssign(value: Int): Unit =
        JTorch.plusIntAssign(-value, this.tensorHandle)

    override operator fun Int.times(other: TorchTensorInt): TorchTensorInt =
        wrap(JTorch.timesInt(this, other.tensorHandle))

    override fun TorchTensorInt.times(value: Int): TorchTensorInt =
        wrap(JTorch.timesInt(value, this.tensorHandle))

    override fun TorchTensorInt.timesAssign(value: Int): Unit =
        JTorch.timesIntAssign(value, this.tensorHandle)

    override fun full(value: Int, shape: IntArray, device: Device): TorchTensorInt =
        wrap(JTorch.fullInt(value, shape, device.toInt()))
}

public inline fun <R> TorchTensorRealAlgebra(block: TorchTensorRealAlgebra.() -> R): R =
    withDeferScope { TorchTensorRealAlgebra(this).block() }

public inline fun <R> TorchTensorFloatAlgebra(block: TorchTensorFloatAlgebra.() -> R): R =
    withDeferScope { TorchTensorFloatAlgebra(this).block() }

public inline fun <R> TorchTensorLongAlgebra(block: TorchTensorLongAlgebra.() -> R): R =
    withDeferScope { TorchTensorLongAlgebra(this).block() }

public inline fun <R> TorchTensorIntAlgebra(block: TorchTensorIntAlgebra.() -> R): R =
    withDeferScope { TorchTensorIntAlgebra(this).block() }