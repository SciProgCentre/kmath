package space.kscience.kmath.multik

import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.zeros
import org.jetbrains.kotlinx.multik.ndarray.data.*
import org.jetbrains.kotlinx.multik.ndarray.operations.*
import space.kscience.kmath.nd.FieldOpsND
import space.kscience.kmath.nd.RingOpsND
import space.kscience.kmath.nd.Shape
import space.kscience.kmath.nd.StructureND
import space.kscience.kmath.operations.*

/**
 * A ring algebra for Multik operations
 */
public open class MultikRingOpsND<T, A : Ring<T>> internal constructor(
    public val type: DataType,
    override val elementAlgebra: A
) : RingOpsND<T, A> {

    public fun MutableMultiArray<T, DN>.wrap(): MultikTensor<T> = MultikTensor(this)

    override fun structureND(shape: Shape, initializer: A.(IntArray) -> T): MultikTensor<T> {
        val res = mk.zeros<T, DN>(shape, type).asDNArray()
        for (index in res.multiIndices) {
            res[index] = elementAlgebra.initializer(index)
        }
        return res.wrap()
    }

    public fun StructureND<T>.asMultik(): MultikTensor<T> = if (this is MultikTensor) {
        this
    } else {
        structureND(shape) { get(it) }
    }

    override fun StructureND<T>.map(transform: A.(T) -> T): MultikTensor<T> {
        //taken directly from Multik sources
        val array = asMultik().array
        val data = initMemoryView<T>(array.size, type)
        var count = 0
        for (el in array) data[count++] = elementAlgebra.transform(el)
        return NDArray(data, shape = array.shape, dim = array.dim).wrap()
    }

    override fun StructureND<T>.mapIndexed(transform: A.(index: IntArray, T) -> T): MultikTensor<T> {
        //taken directly from Multik sources
        val array = asMultik().array
        val data = initMemoryView<T>(array.size, type)
        val indexIter = array.multiIndices.iterator()
        var index = 0
        for (item in array) {
            if (indexIter.hasNext()) {
                data[index++] = elementAlgebra.transform(indexIter.next(), item)
            } else {
                throw ArithmeticException("Index overflow has happened.")
            }
        }
        return NDArray(data, shape = array.shape, dim = array.dim).wrap()
    }

    override fun zip(left: StructureND<T>, right: StructureND<T>, transform: A.(T, T) -> T): MultikTensor<T> {
        require(left.shape.contentEquals(right.shape)) { "ND array shape mismatch" } //TODO replace by ShapeMismatchException
        val leftArray = left.asMultik().array
        val rightArray = right.asMultik().array
        val data = initMemoryView<T>(leftArray.size, type)
        var counter = 0
        val leftIterator = leftArray.iterator()
        val rightIterator = rightArray.iterator()
        //iterating them together
        while (leftIterator.hasNext()) {
            data[counter++] = elementAlgebra.transform(leftIterator.next(), rightIterator.next())
        }
        return NDArray(data, shape = leftArray.shape, dim = leftArray.dim).wrap()
    }

    override fun StructureND<T>.unaryMinus(): MultikTensor<T> = asMultik().array.unaryMinus().wrap()

    override fun add(left: StructureND<T>, right: StructureND<T>): MultikTensor<T> =
        (left.asMultik().array + right.asMultik().array).wrap()

    override fun StructureND<T>.plus(arg: T): MultikTensor<T> =
        asMultik().array.plus(arg).wrap()

    override fun StructureND<T>.minus(arg: T): MultikTensor<T> = asMultik().array.minus(arg).wrap()

    override fun T.plus(arg: StructureND<T>): MultikTensor<T> = arg + this

    override fun T.minus(arg: StructureND<T>): MultikTensor<T> = arg.map { this@minus - it }

    override fun multiply(left: StructureND<T>, right: StructureND<T>): MultikTensor<T> =
        left.asMultik().array.times(right.asMultik().array).wrap()

    override fun StructureND<T>.times(arg: T): MultikTensor<T> =
        asMultik().array.times(arg).wrap()

    override fun T.times(arg: StructureND<T>): MultikTensor<T> = arg * this

    override fun StructureND<T>.unaryPlus(): MultikTensor<T> = asMultik()

    override fun StructureND<T>.plus(other: StructureND<T>): MultikTensor<T> =
        asMultik().array.plus(other.asMultik().array).wrap()

    override fun StructureND<T>.minus(other: StructureND<T>): MultikTensor<T> =
        asMultik().array.minus(other.asMultik().array).wrap()

    override fun StructureND<T>.times(other: StructureND<T>): MultikTensor<T> =
        asMultik().array.times(other.asMultik().array).wrap()
}

/**
 * A field algebra for multik operations
 */
public class MultikFieldOpsND<T, A : Field<T>> internal constructor(
    type: DataType,
    elementAlgebra: A
) : MultikRingOpsND<T, A>(type, elementAlgebra), FieldOpsND<T, A> {
    override fun StructureND<T>.div(other: StructureND<T>): StructureND<T> =
        asMultik().array.div(other.asMultik().array).wrap()
}

public val DoubleField.multikND: MultikFieldOpsND<Double, DoubleField>
    get() = MultikFieldOpsND(DataType.DoubleDataType, DoubleField)

public val FloatField.multikND: MultikFieldOpsND<Float, FloatField>
    get() = MultikFieldOpsND(DataType.FloatDataType, FloatField)

public val ShortRing.multikND: MultikRingOpsND<Short, ShortRing>
    get() = MultikRingOpsND(DataType.ShortDataType, ShortRing)

public val IntRing.multikND: MultikRingOpsND<Int, IntRing>
    get() = MultikRingOpsND(DataType.IntDataType, IntRing)

public val LongRing.multikND: MultikRingOpsND<Long, LongRing>
    get() = MultikRingOpsND(DataType.LongDataType, LongRing)