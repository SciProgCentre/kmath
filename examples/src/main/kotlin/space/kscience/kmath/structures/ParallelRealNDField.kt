package space.kscience.kmath.structures

import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.nd.*
import space.kscience.kmath.operations.ExtendedField
import space.kscience.kmath.operations.NumbersAddOperations
import space.kscience.kmath.operations.RealField
import java.util.*
import java.util.stream.IntStream

/**
 * A demonstration implementation of NDField over Real using Java [DoubleStream] for parallel execution
 */
@OptIn(UnstableKMathAPI::class)
class StreamRealFieldND(
    override val shape: IntArray,
) : FieldND<Double, RealField>,
    NumbersAddOperations<StructureND<Double>>,
    ExtendedField<StructureND<Double>> {

    private val strides = DefaultStrides(shape)
    override val elementContext: RealField get() = RealField
    override val zero: NDBuffer<Double> by lazy { produce { zero } }
    override val one: NDBuffer<Double> by lazy { produce { one } }

    override fun number(value: Number): NDBuffer<Double> {
        val d = value.toDouble() // minimize conversions
        return produce { d }
    }

    private val StructureND<Double>.buffer: RealBuffer
        get() = when {
            !shape.contentEquals(this@StreamRealFieldND.shape) -> throw ShapeMismatchException(
                this@StreamRealFieldND.shape,
                shape
            )
            this is NDBuffer && this.strides == this@StreamRealFieldND.strides -> this.buffer as RealBuffer
            else -> RealBuffer(strides.linearSize) { offset -> get(strides.index(offset)) }
        }


    override fun produce(initializer: RealField.(IntArray) -> Double): NDBuffer<Double> {
        val array = IntStream.range(0, strides.linearSize).parallel().mapToDouble { offset ->
            val index = strides.index(offset)
            RealField.initializer(index)
        }.toArray()

        return NDBuffer(strides, array.asBuffer())
    }

    override fun StructureND<Double>.map(
        transform: RealField.(Double) -> Double,
    ): NDBuffer<Double> {
        val array = Arrays.stream(buffer.array).parallel().map { RealField.transform(it) }.toArray()
        return NDBuffer(strides, array.asBuffer())
    }

    override fun StructureND<Double>.mapIndexed(
        transform: RealField.(index: IntArray, Double) -> Double,
    ): NDBuffer<Double> {
        val array = IntStream.range(0, strides.linearSize).parallel().mapToDouble { offset ->
            RealField.transform(
                strides.index(offset),
                buffer.array[offset]
            )
        }.toArray()

        return NDBuffer(strides, array.asBuffer())
    }

    override fun combine(
        a: StructureND<Double>,
        b: StructureND<Double>,
        transform: RealField.(Double, Double) -> Double,
    ): NDBuffer<Double> {
        val array = IntStream.range(0, strides.linearSize).parallel().mapToDouble { offset ->
            RealField.transform(a.buffer.array[offset], b.buffer.array[offset])
        }.toArray()
        return NDBuffer(strides, array.asBuffer())
    }

    override fun StructureND<Double>.unaryMinus(): StructureND<Double> = map { -it }

    override fun scale(a: StructureND<Double>, value: Double): StructureND<Double> = a.map { it * value }

    override fun power(arg: StructureND<Double>, pow: Number): NDBuffer<Double> = arg.map { power(it, pow) }

    override fun exp(arg: StructureND<Double>): NDBuffer<Double> = arg.map { exp(it) }

    override fun ln(arg: StructureND<Double>): NDBuffer<Double> = arg.map { ln(it) }

    override fun sin(arg: StructureND<Double>): NDBuffer<Double> = arg.map { sin(it) }
    override fun cos(arg: StructureND<Double>): NDBuffer<Double> = arg.map { cos(it) }
    override fun tan(arg: StructureND<Double>): NDBuffer<Double> = arg.map { tan(it) }
    override fun asin(arg: StructureND<Double>): NDBuffer<Double> = arg.map { asin(it) }
    override fun acos(arg: StructureND<Double>): NDBuffer<Double> = arg.map { acos(it) }
    override fun atan(arg: StructureND<Double>): NDBuffer<Double> = arg.map { atan(it) }

    override fun sinh(arg: StructureND<Double>): NDBuffer<Double> = arg.map { sinh(it) }
    override fun cosh(arg: StructureND<Double>): NDBuffer<Double> = arg.map { cosh(it) }
    override fun tanh(arg: StructureND<Double>): NDBuffer<Double> = arg.map { tanh(it) }
    override fun asinh(arg: StructureND<Double>): NDBuffer<Double> = arg.map { asinh(it) }
    override fun acosh(arg: StructureND<Double>): NDBuffer<Double> = arg.map { acosh(it) }
    override fun atanh(arg: StructureND<Double>): NDBuffer<Double> = arg.map { atanh(it) }
}

fun AlgebraND.Companion.realWithStream(vararg shape: Int): StreamRealFieldND = StreamRealFieldND(shape)