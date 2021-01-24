package kscience.kmath.structures

import kscience.kmath.misc.UnstableKMathAPI
import kscience.kmath.nd.*
import kscience.kmath.operations.ExtendedField
import kscience.kmath.operations.RealField
import kscience.kmath.operations.RingWithNumbers
import java.util.*
import java.util.stream.IntStream

/**
 * A demonstration implementation of NDField over Real using Java [DoubleStream] for parallel execution
 */
@OptIn(UnstableKMathAPI::class)
public class StreamRealNDField(
    shape: IntArray,
) : BufferedNDField<Double, RealField>(shape, RealField, Buffer.Companion::real),
    RingWithNumbers<NDStructure<Double>>,
    ExtendedField<NDStructure<Double>> {

    override val zero: NDBuffer<Double> by lazy { produce { zero } }
    override val one: NDBuffer<Double> by lazy { produce { one } }

    override fun number(value: Number): NDBuffer<Double> {
        val d = value.toDouble() // minimize conversions
        return produce { d }
    }

    override val NDStructure<Double>.buffer: RealBuffer
        get() = when {
            !shape.contentEquals(this@StreamRealNDField.shape) -> throw ShapeMismatchException(
                this@StreamRealNDField.shape,
                shape
            )
            this is NDBuffer && this.strides == this@StreamRealNDField.strides -> this.buffer as RealBuffer
            else -> RealBuffer(strides.linearSize) { offset -> get(strides.index(offset)) }
        }


    override fun produce(initializer: RealField.(IntArray) -> Double): NDBuffer<Double> {
        val array = IntStream.range(0, strides.linearSize).parallel().mapToDouble { offset ->
            val index = strides.index(offset)
            RealField.initializer(index)
        }.toArray()

        return NDBuffer(strides, array.asBuffer())
    }

    override fun map(
        arg: NDStructure<Double>,
        transform: RealField.(Double) -> Double,
    ): NDBuffer<Double> {
        val array = Arrays.stream(arg.buffer.array).parallel().map { RealField.transform(it) }.toArray()
        return NDBuffer(strides, array.asBuffer())
    }

    override fun mapIndexed(
        arg: NDStructure<Double>,
        transform: RealField.(index: IntArray, Double) -> Double,
    ): NDBuffer<Double> {
        val array = IntStream.range(0, strides.linearSize).parallel().mapToDouble { offset ->
            RealField.transform(
                strides.index(offset),
                arg.buffer.array[offset]
            )
        }.toArray()

        return NDBuffer(strides, array.asBuffer())
    }

    override fun combine(
        a: NDStructure<Double>,
        b: NDStructure<Double>,
        transform: RealField.(Double, Double) -> Double,
    ): NDBuffer<Double> {
        val array = IntStream.range(0, strides.linearSize).parallel().mapToDouble { offset ->
            RealField.transform(a.buffer.array[offset], b.buffer.array[offset])
        }.toArray()
        return NDBuffer(strides, array.asBuffer())
    }

    override fun power(arg: NDStructure<Double>, pow: Number): NDBuffer<Double> = map(arg) { power(it, pow) }

    override fun exp(arg: NDStructure<Double>): NDBuffer<Double> = map(arg) { exp(it) }

    override fun ln(arg: NDStructure<Double>): NDBuffer<Double> = map(arg) { ln(it) }

    override fun sin(arg: NDStructure<Double>): NDBuffer<Double> = map(arg) { sin(it) }
    override fun cos(arg: NDStructure<Double>): NDBuffer<Double> = map(arg) { cos(it) }
    override fun tan(arg: NDStructure<Double>): NDBuffer<Double> = map(arg) { tan(it) }
    override fun asin(arg: NDStructure<Double>): NDBuffer<Double> = map(arg) { asin(it) }
    override fun acos(arg: NDStructure<Double>): NDBuffer<Double> = map(arg) { acos(it) }
    override fun atan(arg: NDStructure<Double>): NDBuffer<Double> = map(arg) { atan(it) }

    override fun sinh(arg: NDStructure<Double>): NDBuffer<Double> = map(arg) { sinh(it) }
    override fun cosh(arg: NDStructure<Double>): NDBuffer<Double> = map(arg) { cosh(it) }
    override fun tanh(arg: NDStructure<Double>): NDBuffer<Double> = map(arg) { tanh(it) }
    override fun asinh(arg: NDStructure<Double>): NDBuffer<Double> = map(arg) { asinh(it) }
    override fun acosh(arg: NDStructure<Double>): NDBuffer<Double> = map(arg) { acosh(it) }
    override fun atanh(arg: NDStructure<Double>): NDBuffer<Double> = map(arg) { atanh(it) }
}

fun NDAlgebra.Companion.realWithStream(vararg shape: Int): StreamRealNDField = StreamRealNDField(shape)