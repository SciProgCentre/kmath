package space.kscience.kmath.structures

import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.nd.*
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.operations.ExtendedField
import space.kscience.kmath.operations.NumbersAddOperations
import java.util.*
import java.util.stream.IntStream

/**
 * A demonstration implementation of NDField over Real using Java [DoubleStream] for parallel execution
 */
@OptIn(UnstableKMathAPI::class)
class StreamDoubleFieldND(
    override val shape: IntArray,
) : FieldND<Double, DoubleField>,
    NumbersAddOperations<StructureND<Double>>,
    ExtendedField<StructureND<Double>> {

    private val strides = DefaultStrides(shape)
    override val elementContext: DoubleField get() = DoubleField
    override val zero: BufferND<Double> by lazy { produce { zero } }
    override val one: BufferND<Double> by lazy { produce { one } }

    override fun number(value: Number): BufferND<Double> {
        val d = value.toDouble() // minimize conversions
        return produce { d }
    }

    private val StructureND<Double>.buffer: DoubleBuffer
        get() = when {
            !shape.contentEquals(this@StreamDoubleFieldND.shape) -> throw ShapeMismatchException(
                this@StreamDoubleFieldND.shape,
                shape
            )
            this is BufferND && this.strides == this@StreamDoubleFieldND.strides -> this.buffer as DoubleBuffer
            else -> DoubleBuffer(strides.linearSize) { offset -> get(strides.index(offset)) }
        }


    override fun produce(initializer: DoubleField.(IntArray) -> Double): BufferND<Double> {
        val array = IntStream.range(0, strides.linearSize).parallel().mapToDouble { offset ->
            val index = strides.index(offset)
            DoubleField.initializer(index)
        }.toArray()

        return BufferND(strides, array.asBuffer())
    }

    override fun StructureND<Double>.map(
        transform: DoubleField.(Double) -> Double,
    ): BufferND<Double> {
        val array = Arrays.stream(buffer.array).parallel().map { DoubleField.transform(it) }.toArray()
        return BufferND(strides, array.asBuffer())
    }

    override fun StructureND<Double>.mapIndexed(
        transform: DoubleField.(index: IntArray, Double) -> Double,
    ): BufferND<Double> {
        val array = IntStream.range(0, strides.linearSize).parallel().mapToDouble { offset ->
            DoubleField.transform(
                strides.index(offset),
                buffer.array[offset]
            )
        }.toArray()

        return BufferND(strides, array.asBuffer())
    }

    override fun combine(
        a: StructureND<Double>,
        b: StructureND<Double>,
        transform: DoubleField.(Double, Double) -> Double,
    ): BufferND<Double> {
        val array = IntStream.range(0, strides.linearSize).parallel().mapToDouble { offset ->
            DoubleField.transform(a.buffer.array[offset], b.buffer.array[offset])
        }.toArray()
        return BufferND(strides, array.asBuffer())
    }

    override fun StructureND<Double>.unaryMinus(): StructureND<Double> = map { -it }

    override fun scale(a: StructureND<Double>, value: Double): StructureND<Double> = a.map { it * value }

    override fun power(arg: StructureND<Double>, pow: Number): BufferND<Double> = arg.map { power(it, pow) }

    override fun exp(arg: StructureND<Double>): BufferND<Double> = arg.map { exp(it) }

    override fun ln(arg: StructureND<Double>): BufferND<Double> = arg.map { ln(it) }

    override fun sin(arg: StructureND<Double>): BufferND<Double> = arg.map { sin(it) }
    override fun cos(arg: StructureND<Double>): BufferND<Double> = arg.map { cos(it) }
    override fun tan(arg: StructureND<Double>): BufferND<Double> = arg.map { tan(it) }
    override fun asin(arg: StructureND<Double>): BufferND<Double> = arg.map { asin(it) }
    override fun acos(arg: StructureND<Double>): BufferND<Double> = arg.map { acos(it) }
    override fun atan(arg: StructureND<Double>): BufferND<Double> = arg.map { atan(it) }

    override fun sinh(arg: StructureND<Double>): BufferND<Double> = arg.map { sinh(it) }
    override fun cosh(arg: StructureND<Double>): BufferND<Double> = arg.map { cosh(it) }
    override fun tanh(arg: StructureND<Double>): BufferND<Double> = arg.map { tanh(it) }
    override fun asinh(arg: StructureND<Double>): BufferND<Double> = arg.map { asinh(it) }
    override fun acosh(arg: StructureND<Double>): BufferND<Double> = arg.map { acosh(it) }
    override fun atanh(arg: StructureND<Double>): BufferND<Double> = arg.map { atanh(it) }
}

fun AlgebraND.Companion.realWithStream(vararg shape: Int): StreamDoubleFieldND = StreamDoubleFieldND(shape)