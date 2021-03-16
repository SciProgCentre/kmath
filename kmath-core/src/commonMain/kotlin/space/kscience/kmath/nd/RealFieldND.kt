package space.kscience.kmath.nd

import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.operations.ExtendedField
import space.kscience.kmath.operations.NumbersAddOperations
import space.kscience.kmath.operations.RealField
import space.kscience.kmath.operations.ScaleOperations
import space.kscience.kmath.structures.RealBuffer
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@OptIn(UnstableKMathAPI::class)
public class RealFieldND(
    shape: IntArray,
) : BufferedFieldND<Double, RealField>(shape, RealField, ::RealBuffer),
    NumbersAddOperations<StructureND<Double>>,
    ScaleOperations<StructureND<Double>>,
    ExtendedField<StructureND<Double>> {

    override val zero: NDBuffer<Double> by lazy { produce { zero } }
    override val one: NDBuffer<Double> by lazy { produce { one } }

    override fun number(value: Number): NDBuffer<Double> {
        val d = value.toDouble() // minimize conversions
        return produce { d }
    }

    override val StructureND<Double>.buffer: RealBuffer
        get() = when {
            !shape.contentEquals(this@RealFieldND.shape) -> throw ShapeMismatchException(
                this@RealFieldND.shape,
                shape
            )
            this is NDBuffer && this.strides == this@RealFieldND.strides -> this.buffer as RealBuffer
            else -> RealBuffer(strides.linearSize) { offset -> get(strides.index(offset)) }
        }

    @Suppress("OVERRIDE_BY_INLINE")
    override inline fun StructureND<Double>.map(
        transform: RealField.(Double) -> Double,
    ): NDBuffer<Double> {
        val buffer = RealBuffer(strides.linearSize) { offset -> RealField.transform(buffer.array[offset]) }
        return NDBuffer(strides, buffer)
    }

    @Suppress("OVERRIDE_BY_INLINE")
    override inline fun produce(initializer: RealField.(IntArray) -> Double): NDBuffer<Double> {
        val array = DoubleArray(strides.linearSize) { offset ->
            val index = strides.index(offset)
            RealField.initializer(index)
        }
        return NDBuffer(strides, RealBuffer(array))
    }

    @Suppress("OVERRIDE_BY_INLINE")
    override inline fun StructureND<Double>.mapIndexed(
        transform: RealField.(index: IntArray, Double) -> Double,
    ): NDBuffer<Double> = NDBuffer(
        strides,
        buffer = RealBuffer(strides.linearSize) { offset ->
            RealField.transform(
                strides.index(offset),
                buffer.array[offset]
            )
        })

    @Suppress("OVERRIDE_BY_INLINE")
    override inline fun combine(
        a: StructureND<Double>,
        b: StructureND<Double>,
        transform: RealField.(Double, Double) -> Double,
    ): NDBuffer<Double> {
        val buffer = RealBuffer(strides.linearSize) { offset ->
            RealField.transform(a.buffer.array[offset], b.buffer.array[offset])
        }
        return NDBuffer(strides, buffer)
    }

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

public fun AlgebraND.Companion.real(vararg shape: Int): RealFieldND = RealFieldND(shape)

/**
 * Produce a context for n-dimensional operations inside this real field
 */
public inline fun <R> RealField.nd(vararg shape: Int, action: RealFieldND.() -> R): R {
    contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }
    return RealFieldND(shape).run(action)
}
