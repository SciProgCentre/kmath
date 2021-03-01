package space.kscience.kmath.torch

import space.kscience.kmath.memory.DeferScope
import space.kscience.kmath.memory.withDeferScope

public sealed class TorchTensorAlgebraJVM<
        T,
        PrimitiveArrayType,
        TorchTensorType : TorchTensorJVM<T>> constructor(
    internal val scope: DeferScope
) : TorchTensorAlgebra<T, PrimitiveArrayType, TorchTensorType> {
    override fun getNumThreads(): Int {
        return space.kscience.kmath.torch.JTorch.getNumThreads()
    }

    override fun setNumThreads(numThreads: Int): Unit {
        space.kscience.kmath.torch.JTorch.setNumThreads(numThreads)
    }

    override fun cudaAvailable(): Boolean {
        return space.kscience.kmath.torch.JTorch.cudaIsAvailable()
    }

    override fun setSeed(seed: Int): Unit {
        space.kscience.kmath.torch.JTorch.setSeed(seed)
    }

    override var checks: Boolean = false

    internal abstract fun wrap(tensorHandle: Long): TorchTensorType

    override operator fun TorchTensorType.times(other: TorchTensorType): TorchTensorType {
        if (checks) checkLinearOperation(this, other)
        return wrap(space.kscience.kmath.torch.JTorch.timesTensor(this.tensorHandle, other.tensorHandle))
    }

    override operator fun TorchTensorType.timesAssign(other: TorchTensorType): Unit {
        if (checks) checkLinearOperation(this, other)
        space.kscience.kmath.torch.JTorch.timesTensorAssign(this.tensorHandle, other.tensorHandle)
    }

    override operator fun TorchTensorType.plus(other: TorchTensorType): TorchTensorType {
        if (checks) checkLinearOperation(this, other)
        return wrap(space.kscience.kmath.torch.JTorch.plusTensor(this.tensorHandle, other.tensorHandle))
    }

    override operator fun TorchTensorType.plusAssign(other: TorchTensorType): Unit {
        if (checks) checkLinearOperation(this, other)
        space.kscience.kmath.torch.JTorch.plusTensorAssign(this.tensorHandle, other.tensorHandle)
    }

    override operator fun TorchTensorType.minus(other: TorchTensorType): TorchTensorType {
        if (checks) checkLinearOperation(this, other)
        return wrap(space.kscience.kmath.torch.JTorch.minusTensor(this.tensorHandle, other.tensorHandle))
    }

    override operator fun TorchTensorType.minusAssign(other: TorchTensorType): Unit {
        if (checks) checkLinearOperation(this, other)
        space.kscience.kmath.torch.JTorch.minusTensorAssign(this.tensorHandle, other.tensorHandle)
    }

    override operator fun TorchTensorType.unaryMinus(): TorchTensorType =
        wrap(space.kscience.kmath.torch.JTorch.unaryMinus(this.tensorHandle))

    override infix fun TorchTensorType.dot(other: TorchTensorType): TorchTensorType {
        if (checks) checkDotOperation(this, other)
        return wrap(space.kscience.kmath.torch.JTorch.matmul(this.tensorHandle, other.tensorHandle))
    }

    override infix fun TorchTensorType.dotAssign(other: TorchTensorType): Unit {
        if (checks) checkDotOperation(this, other)
        space.kscience.kmath.torch.JTorch.matmulAssign(this.tensorHandle, other.tensorHandle)
    }

    override infix fun TorchTensorType.dotRightAssign(other: TorchTensorType): Unit {
        if (checks) checkDotOperation(this, other)
        space.kscience.kmath.torch.JTorch.matmulRightAssign(this.tensorHandle, other.tensorHandle)
    }

    override fun diagonalEmbedding(
        diagonalEntries: TorchTensorType, offset: Int, dim1: Int, dim2: Int
    ): TorchTensorType =
        wrap(space.kscience.kmath.torch.JTorch.diagEmbed(diagonalEntries.tensorHandle, offset, dim1, dim2))

    override fun TorchTensorType.transpose(i: Int, j: Int): TorchTensorType {
        if (checks) checkTranspose(this.dimension, i, j)
        return wrap(space.kscience.kmath.torch.JTorch.transposeTensor(tensorHandle, i, j))
    }

    override fun TorchTensorType.transposeAssign(i: Int, j: Int): Unit {
        if (checks) checkTranspose(this.dimension, i, j)
        space.kscience.kmath.torch.JTorch.transposeTensorAssign(tensorHandle, i, j)
    }

    override fun TorchTensorType.view(shape: IntArray): TorchTensorType {
        if (checks) checkView(this, shape)
        return wrap(space.kscience.kmath.torch.JTorch.viewTensor(this.tensorHandle, shape))
    }

    override fun TorchTensorType.abs(): TorchTensorType = wrap(space.kscience.kmath.torch.JTorch.absTensor(tensorHandle))
    override fun TorchTensorType.absAssign(): Unit = space.kscience.kmath.torch.JTorch.absTensorAssign(tensorHandle)

    override fun TorchTensorType.sum(): TorchTensorType = wrap(space.kscience.kmath.torch.JTorch.sumTensor(tensorHandle))
    override fun TorchTensorType.sumAssign(): Unit = space.kscience.kmath.torch.JTorch.sumTensorAssign(tensorHandle)

    override fun TorchTensorType.randIntegral(low: Long, high: Long): TorchTensorType =
        wrap(space.kscience.kmath.torch.JTorch.randintLike(this.tensorHandle, low, high))

    override fun TorchTensorType.randIntegralAssign(low: Long, high: Long): Unit =
        space.kscience.kmath.torch.JTorch.randintLikeAssign(this.tensorHandle, low, high)

    override fun TorchTensorType.copy(): TorchTensorType =
        wrap(space.kscience.kmath.torch.JTorch.copyTensor(this.tensorHandle))

    override fun TorchTensorType.copyToDevice(device: space.kscience.kmath.torch.Device): TorchTensorType =
        wrap(space.kscience.kmath.torch.JTorch.copyToDevice(this.tensorHandle, device.toInt()))

    override infix fun TorchTensorType.swap(other: TorchTensorType): Unit =
        space.kscience.kmath.torch.JTorch.swapTensors(this.tensorHandle, other.tensorHandle)
}

public sealed class TorchTensorPartialDivisionAlgebraJVM<T, PrimitiveArrayType,
        TorchTensorType : TorchTensorOverFieldJVM<T>>(scope: DeferScope) :
    TorchTensorAlgebraJVM<T, PrimitiveArrayType, TorchTensorType>(scope),
    TorchTensorPartialDivisionAlgebra<T, PrimitiveArrayType, TorchTensorType> {

    override operator fun TorchTensorType.div(other: TorchTensorType): TorchTensorType {
        if (checks) checkLinearOperation(this, other)
        return wrap(space.kscience.kmath.torch.JTorch.divTensor(this.tensorHandle, other.tensorHandle))
    }

    override operator fun TorchTensorType.divAssign(other: TorchTensorType): Unit {
        if (checks) checkLinearOperation(this, other)
        space.kscience.kmath.torch.JTorch.divTensorAssign(this.tensorHandle, other.tensorHandle)
    }

    override fun TorchTensorType.randUniform(): TorchTensorType =
        wrap(space.kscience.kmath.torch.JTorch.randLike(this.tensorHandle))

    override fun TorchTensorType.randUniformAssign(): Unit =
        space.kscience.kmath.torch.JTorch.randLikeAssign(this.tensorHandle)

    override fun TorchTensorType.randNormal(): TorchTensorType =
        wrap(space.kscience.kmath.torch.JTorch.randnLike(this.tensorHandle))

    override fun TorchTensorType.randNormalAssign(): Unit =
        space.kscience.kmath.torch.JTorch.randnLikeAssign(this.tensorHandle)

    override fun TorchTensorType.exp(): TorchTensorType = wrap(space.kscience.kmath.torch.JTorch.expTensor(tensorHandle))
    override fun TorchTensorType.expAssign(): Unit = space.kscience.kmath.torch.JTorch.expTensorAssign(tensorHandle)
    override fun TorchTensorType.log(): TorchTensorType = wrap(space.kscience.kmath.torch.JTorch.logTensor(tensorHandle))
    override fun TorchTensorType.logAssign(): Unit = space.kscience.kmath.torch.JTorch.logTensorAssign(tensorHandle)

    override fun TorchTensorType.svd(): Triple<TorchTensorType, TorchTensorType, TorchTensorType> {
        val U = space.kscience.kmath.torch.JTorch.emptyTensor()
        val V = space.kscience.kmath.torch.JTorch.emptyTensor()
        val S = space.kscience.kmath.torch.JTorch.emptyTensor()
        space.kscience.kmath.torch.JTorch.svdTensor(this.tensorHandle, U, S, V)
        return Triple(wrap(U), wrap(S), wrap(V))
    }

    override fun TorchTensorType.symEig(eigenvectors: Boolean): Pair<TorchTensorType, TorchTensorType> {
        val V = space.kscience.kmath.torch.JTorch.emptyTensor()
        val S = space.kscience.kmath.torch.JTorch.emptyTensor()
        space.kscience.kmath.torch.JTorch.symeigTensor(this.tensorHandle, S, V, eigenvectors)
        return Pair(wrap(S), wrap(V))
    }

    override fun TorchTensorType.grad(variable: TorchTensorType, retainGraph: Boolean): TorchTensorType {
        if (checks) this.checkIsValue()
        return wrap(space.kscience.kmath.torch.JTorch.autogradTensor(this.tensorHandle, variable.tensorHandle, retainGraph))
    }

    override infix fun TorchTensorType.hess(variable: TorchTensorType): TorchTensorType {
        if (checks) this.checkIsValue()
        return wrap(space.kscience.kmath.torch.JTorch.autohessTensor(this.tensorHandle, variable.tensorHandle))
    }

    override fun TorchTensorType.detachFromGraph(): TorchTensorType =
        wrap(space.kscience.kmath.torch.JTorch.detachFromGraph(this.tensorHandle))

}

public class TorchTensorRealAlgebra(scope: DeferScope) :
    TorchTensorPartialDivisionAlgebraJVM<Double, DoubleArray, TorchTensorReal>(scope) {
    override fun wrap(tensorHandle: Long): TorchTensorReal =
        TorchTensorReal(scope = scope, tensorHandle = tensorHandle)

    override fun TorchTensorReal.copyToArray(): DoubleArray =
        this.elements().map { it.second }.toList().toDoubleArray()

    override fun copyFromArray(array: DoubleArray, shape: IntArray, device: space.kscience.kmath.torch.Device): TorchTensorReal =
        wrap(space.kscience.kmath.torch.JTorch.fromBlobDouble(array, shape, device.toInt()))

    override fun randNormal(shape: IntArray, device: space.kscience.kmath.torch.Device): TorchTensorReal =
        wrap(space.kscience.kmath.torch.JTorch.randnDouble(shape, device.toInt()))

    override fun randUniform(shape: IntArray, device: space.kscience.kmath.torch.Device): TorchTensorReal =
        wrap(space.kscience.kmath.torch.JTorch.randDouble(shape, device.toInt()))

    override fun randIntegral(low: Long, high: Long, shape: IntArray, device: space.kscience.kmath.torch.Device): TorchTensorReal =
        wrap(space.kscience.kmath.torch.JTorch.randintDouble(low, high, shape, device.toInt()))

    override operator fun Double.plus(other: TorchTensorReal): TorchTensorReal =
        wrap(space.kscience.kmath.torch.JTorch.plusDouble(this, other.tensorHandle))

    override fun TorchTensorReal.plus(value: Double): TorchTensorReal =
        wrap(space.kscience.kmath.torch.JTorch.plusDouble(value, this.tensorHandle))

    override fun TorchTensorReal.plusAssign(value: Double): Unit =
        space.kscience.kmath.torch.JTorch.plusDoubleAssign(value, this.tensorHandle)

    override operator fun Double.minus(other: TorchTensorReal): TorchTensorReal =
        wrap(space.kscience.kmath.torch.JTorch.plusDouble(-this, other.tensorHandle))

    override fun TorchTensorReal.minus(value: Double): TorchTensorReal =
        wrap(space.kscience.kmath.torch.JTorch.plusDouble(-value, this.tensorHandle))

    override fun TorchTensorReal.minusAssign(value: Double): Unit =
        space.kscience.kmath.torch.JTorch.plusDoubleAssign(-value, this.tensorHandle)

    override operator fun Double.times(other: TorchTensorReal): TorchTensorReal =
        wrap(space.kscience.kmath.torch.JTorch.timesDouble(this, other.tensorHandle))

    override fun TorchTensorReal.times(value: Double): TorchTensorReal =
        wrap(space.kscience.kmath.torch.JTorch.timesDouble(value, this.tensorHandle))

    override fun TorchTensorReal.timesAssign(value: Double): Unit =
        space.kscience.kmath.torch.JTorch.timesDoubleAssign(value, this.tensorHandle)

    override fun full(value: Double, shape: IntArray, device: space.kscience.kmath.torch.Device): TorchTensorReal =
        wrap(space.kscience.kmath.torch.JTorch.fullDouble(value, shape, device.toInt()))
}

public class TorchTensorFloatAlgebra(scope: DeferScope) :
    TorchTensorPartialDivisionAlgebraJVM<Float, FloatArray, TorchTensorFloat>(scope) {
    override fun wrap(tensorHandle: Long): TorchTensorFloat =
        TorchTensorFloat(scope = scope, tensorHandle = tensorHandle)

    override fun TorchTensorFloat.copyToArray(): FloatArray =
        this.elements().map { it.second }.toList().toFloatArray()

    override fun copyFromArray(array: FloatArray, shape: IntArray, device: space.kscience.kmath.torch.Device): TorchTensorFloat =
        wrap(space.kscience.kmath.torch.JTorch.fromBlobFloat(array, shape, device.toInt()))

    override fun randNormal(shape: IntArray, device: space.kscience.kmath.torch.Device): TorchTensorFloat =
        wrap(space.kscience.kmath.torch.JTorch.randnFloat(shape, device.toInt()))

    override fun randUniform(shape: IntArray, device: space.kscience.kmath.torch.Device): TorchTensorFloat =
        wrap(space.kscience.kmath.torch.JTorch.randFloat(shape, device.toInt()))

    override fun randIntegral(low: Long, high: Long, shape: IntArray, device: space.kscience.kmath.torch.Device): TorchTensorFloat =
        wrap(space.kscience.kmath.torch.JTorch.randintFloat(low, high, shape, device.toInt()))

    override operator fun Float.plus(other: TorchTensorFloat): TorchTensorFloat =
        wrap(space.kscience.kmath.torch.JTorch.plusFloat(this, other.tensorHandle))

    override fun TorchTensorFloat.plus(value: Float): TorchTensorFloat =
        wrap(space.kscience.kmath.torch.JTorch.plusFloat(value, this.tensorHandle))

    override fun TorchTensorFloat.plusAssign(value: Float): Unit =
        space.kscience.kmath.torch.JTorch.plusFloatAssign(value, this.tensorHandle)

    override operator fun Float.minus(other: TorchTensorFloat): TorchTensorFloat =
        wrap(space.kscience.kmath.torch.JTorch.plusFloat(-this, other.tensorHandle))

    override fun TorchTensorFloat.minus(value: Float): TorchTensorFloat =
        wrap(space.kscience.kmath.torch.JTorch.plusFloat(-value, this.tensorHandle))

    override fun TorchTensorFloat.minusAssign(value: Float): Unit =
        space.kscience.kmath.torch.JTorch.plusFloatAssign(-value, this.tensorHandle)

    override operator fun Float.times(other: TorchTensorFloat): TorchTensorFloat =
        wrap(space.kscience.kmath.torch.JTorch.timesFloat(this, other.tensorHandle))

    override fun TorchTensorFloat.times(value: Float): TorchTensorFloat =
        wrap(space.kscience.kmath.torch.JTorch.timesFloat(value, this.tensorHandle))

    override fun TorchTensorFloat.timesAssign(value: Float): Unit =
        space.kscience.kmath.torch.JTorch.timesFloatAssign(value, this.tensorHandle)

    override fun full(value: Float, shape: IntArray, device: space.kscience.kmath.torch.Device): TorchTensorFloat =
        wrap(space.kscience.kmath.torch.JTorch.fullFloat(value, shape, device.toInt()))
}

public class TorchTensorLongAlgebra(scope: DeferScope) :
    TorchTensorAlgebraJVM<Long, LongArray, TorchTensorLong>(scope) {
    override fun wrap(tensorHandle: Long): TorchTensorLong =
        TorchTensorLong(scope = scope, tensorHandle = tensorHandle)

    override fun TorchTensorLong.copyToArray(): LongArray =
        this.elements().map { it.second }.toList().toLongArray()

    override fun copyFromArray(array: LongArray, shape: IntArray, device: space.kscience.kmath.torch.Device): TorchTensorLong =
        wrap(space.kscience.kmath.torch.JTorch.fromBlobLong(array, shape, device.toInt()))

    override fun randIntegral(low: Long, high: Long, shape: IntArray, device: space.kscience.kmath.torch.Device): TorchTensorLong =
        wrap(space.kscience.kmath.torch.JTorch.randintLong(low, high, shape, device.toInt()))

    override operator fun Long.plus(other: TorchTensorLong): TorchTensorLong =
        wrap(space.kscience.kmath.torch.JTorch.plusLong(this, other.tensorHandle))

    override fun TorchTensorLong.plus(value: Long): TorchTensorLong =
        wrap(space.kscience.kmath.torch.JTorch.plusLong(value, this.tensorHandle))

    override fun TorchTensorLong.plusAssign(value: Long): Unit =
        space.kscience.kmath.torch.JTorch.plusLongAssign(value, this.tensorHandle)

    override operator fun Long.minus(other: TorchTensorLong): TorchTensorLong =
        wrap(space.kscience.kmath.torch.JTorch.plusLong(-this, other.tensorHandle))

    override fun TorchTensorLong.minus(value: Long): TorchTensorLong =
        wrap(space.kscience.kmath.torch.JTorch.plusLong(-value, this.tensorHandle))

    override fun TorchTensorLong.minusAssign(value: Long): Unit =
        space.kscience.kmath.torch.JTorch.plusLongAssign(-value, this.tensorHandle)

    override operator fun Long.times(other: TorchTensorLong): TorchTensorLong =
        wrap(space.kscience.kmath.torch.JTorch.timesLong(this, other.tensorHandle))

    override fun TorchTensorLong.times(value: Long): TorchTensorLong =
        wrap(space.kscience.kmath.torch.JTorch.timesLong(value, this.tensorHandle))

    override fun TorchTensorLong.timesAssign(value: Long): Unit =
        space.kscience.kmath.torch.JTorch.timesLongAssign(value, this.tensorHandle)

    override fun full(value: Long, shape: IntArray, device: space.kscience.kmath.torch.Device): TorchTensorLong =
        wrap(space.kscience.kmath.torch.JTorch.fullLong(value, shape, device.toInt()))
}

public class TorchTensorIntAlgebra(scope: DeferScope) :
    TorchTensorAlgebraJVM<Int, IntArray, TorchTensorInt>(scope) {
    override fun wrap(tensorHandle: Long): TorchTensorInt =
        TorchTensorInt(scope = scope, tensorHandle = tensorHandle)

    override fun TorchTensorInt.copyToArray(): IntArray =
        this.elements().map { it.second }.toList().toIntArray()

    override fun copyFromArray(array: IntArray, shape: IntArray, device: space.kscience.kmath.torch.Device): TorchTensorInt =
        wrap(space.kscience.kmath.torch.JTorch.fromBlobInt(array, shape, device.toInt()))

    override fun randIntegral(low: Long, high: Long, shape: IntArray, device: space.kscience.kmath.torch.Device): TorchTensorInt =
        wrap(space.kscience.kmath.torch.JTorch.randintInt(low, high, shape, device.toInt()))

    override operator fun Int.plus(other: TorchTensorInt): TorchTensorInt =
        wrap(space.kscience.kmath.torch.JTorch.plusInt(this, other.tensorHandle))

    override fun TorchTensorInt.plus(value: Int): TorchTensorInt =
        wrap(space.kscience.kmath.torch.JTorch.plusInt(value, this.tensorHandle))

    override fun TorchTensorInt.plusAssign(value: Int): Unit =
        space.kscience.kmath.torch.JTorch.plusIntAssign(value, this.tensorHandle)

    override operator fun Int.minus(other: TorchTensorInt): TorchTensorInt =
        wrap(space.kscience.kmath.torch.JTorch.plusInt(-this, other.tensorHandle))

    override fun TorchTensorInt.minus(value: Int): TorchTensorInt =
        wrap(space.kscience.kmath.torch.JTorch.plusInt(-value, this.tensorHandle))

    override fun TorchTensorInt.minusAssign(value: Int): Unit =
        space.kscience.kmath.torch.JTorch.plusIntAssign(-value, this.tensorHandle)

    override operator fun Int.times(other: TorchTensorInt): TorchTensorInt =
        wrap(space.kscience.kmath.torch.JTorch.timesInt(this, other.tensorHandle))

    override fun TorchTensorInt.times(value: Int): TorchTensorInt =
        wrap(space.kscience.kmath.torch.JTorch.timesInt(value, this.tensorHandle))

    override fun TorchTensorInt.timesAssign(value: Int): Unit =
        space.kscience.kmath.torch.JTorch.timesIntAssign(value, this.tensorHandle)

    override fun full(value: Int, shape: IntArray, device: space.kscience.kmath.torch.Device): TorchTensorInt =
        wrap(space.kscience.kmath.torch.JTorch.fullInt(value, shape, device.toInt()))
}

public inline fun <R> TorchTensorRealAlgebra(block: TorchTensorRealAlgebra.() -> R): R =
    withDeferScope { TorchTensorRealAlgebra(this).block() }

public inline fun <R> TorchTensorFloatAlgebra(block: TorchTensorFloatAlgebra.() -> R): R =
    withDeferScope { TorchTensorFloatAlgebra(this).block() }

public inline fun <R> TorchTensorLongAlgebra(block: TorchTensorLongAlgebra.() -> R): R =
    withDeferScope { TorchTensorLongAlgebra(this).block() }

public inline fun <R> TorchTensorIntAlgebra(block: TorchTensorIntAlgebra.() -> R): R =
    withDeferScope { TorchTensorIntAlgebra(this).block() }