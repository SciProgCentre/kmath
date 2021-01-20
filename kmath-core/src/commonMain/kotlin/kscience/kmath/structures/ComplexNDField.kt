package kscience.kmath.structures

import kscience.kmath.misc.UnstableKMathAPI
import kscience.kmath.operations.*
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public typealias ComplexNDElement = BufferedNDFieldElement<Complex, ComplexField>

/**
 * An optimized nd-field for complex numbers
 */
@OptIn(UnstableKMathAPI::class)
public class ComplexNDField(override val shape: IntArray) :
    BufferedNDField<Complex, ComplexField>,
    ExtendedNDField<Complex, ComplexField, NDBuffer<Complex>>,
    RingWithNumbers<NDBuffer<Complex>>{

    override val strides: Strides = DefaultStrides(shape)
    override val elementContext: ComplexField get() = ComplexField
    override val zero: ComplexNDElement by lazy { produce { zero } }
    override val one: ComplexNDElement by lazy { produce { one } }

    override fun number(value: Number): NDBuffer<Complex> {
        val c = value.toComplex()
        return produce { c }
    }

    public inline fun buildBuffer(size: Int, crossinline initializer: (Int) -> Complex): Buffer<Complex> =
        Buffer.complex(size) { initializer(it) }

    /**
     * Inline transform an NDStructure to another structure
     */
    override fun map(
        arg: NDBuffer<Complex>,
        transform: ComplexField.(Complex) -> Complex,
    ): ComplexNDElement {
        check(arg)
        val array = buildBuffer(arg.strides.linearSize) { offset -> ComplexField.transform(arg.buffer[offset]) }
        return BufferedNDFieldElement(this, array)
    }

    override fun produce(initializer: ComplexField.(IntArray) -> Complex): ComplexNDElement {
        val array = buildBuffer(strides.linearSize) { offset -> elementContext.initializer(strides.index(offset)) }
        return BufferedNDFieldElement(this, array)
    }

    override fun mapIndexed(
        arg: NDBuffer<Complex>,
        transform: ComplexField.(index: IntArray, Complex) -> Complex,
    ): ComplexNDElement {
        check(arg)

        return BufferedNDFieldElement(
            this,
            buildBuffer(arg.strides.linearSize) { offset ->
                elementContext.transform(
                    arg.strides.index(offset),
                    arg.buffer[offset]
                )
            })
    }

    override fun combine(
        a: NDBuffer<Complex>,
        b: NDBuffer<Complex>,
        transform: ComplexField.(Complex, Complex) -> Complex,
    ): ComplexNDElement {
        check(a, b)

        return BufferedNDFieldElement(
            this,
            buildBuffer(strides.linearSize) { offset -> elementContext.transform(a.buffer[offset], b.buffer[offset]) })
    }

    override fun NDBuffer<Complex>.toElement(): FieldElement<NDBuffer<Complex>, *, out BufferedNDField<Complex, ComplexField>> =
        BufferedNDFieldElement(this@ComplexNDField, buffer)

    override fun power(arg: NDBuffer<Complex>, pow: Number): ComplexNDElement =
        map(arg) { power(it, pow) }

    override fun exp(arg: NDBuffer<Complex>): ComplexNDElement = map(arg) { exp(it) }
    override fun ln(arg: NDBuffer<Complex>): ComplexNDElement = map(arg) { ln(it) }

    override fun sin(arg: NDBuffer<Complex>): ComplexNDElement = map(arg) { sin(it) }
    override fun cos(arg: NDBuffer<Complex>): ComplexNDElement = map(arg) { cos(it) }
    override fun tan(arg: NDBuffer<Complex>): ComplexNDElement = map(arg) { tan(it) }
    override fun asin(arg: NDBuffer<Complex>): ComplexNDElement = map(arg) { asin(it) }
    override fun acos(arg: NDBuffer<Complex>): ComplexNDElement = map(arg) { acos(it) }
    override fun atan(arg: NDBuffer<Complex>): ComplexNDElement = map(arg) { atan(it) }

    override fun sinh(arg: NDBuffer<Complex>): ComplexNDElement = map(arg) { sinh(it) }
    override fun cosh(arg: NDBuffer<Complex>): ComplexNDElement = map(arg) { cosh(it) }
    override fun tanh(arg: NDBuffer<Complex>): ComplexNDElement = map(arg) { tanh(it) }
    override fun asinh(arg: NDBuffer<Complex>): ComplexNDElement = map(arg) { asinh(it) }
    override fun acosh(arg: NDBuffer<Complex>): ComplexNDElement = map(arg) { acosh(it) }
    override fun atanh(arg: NDBuffer<Complex>): ComplexNDElement = map(arg) { atanh(it) }
}


/**
 * Fast element production using function inlining
 */
public inline fun BufferedNDField<Complex, ComplexField>.produceInline(initializer: ComplexField.(Int) -> Complex): ComplexNDElement {
    val buffer = Buffer.complex(strides.linearSize) { offset -> ComplexField.initializer(offset) }
    return BufferedNDFieldElement(this, buffer)
}

/**
 * Map one [ComplexNDElement] using function with indices.
 */
public inline fun ComplexNDElement.mapIndexed(transform: ComplexField.(index: IntArray, Complex) -> Complex): ComplexNDElement =
    context.produceInline { offset -> transform(strides.index(offset), buffer[offset]) }

/**
 * Map one [ComplexNDElement] using function without indices.
 */
public inline fun ComplexNDElement.map(transform: ComplexField.(Complex) -> Complex): ComplexNDElement {
    val buffer = Buffer.complex(strides.linearSize) { offset -> ComplexField.transform(buffer[offset]) }
    return BufferedNDFieldElement(context, buffer)
}

/**
 * Element by element application of any operation on elements to the whole array. Just like in numpy
 */
public operator fun Function1<Complex, Complex>.invoke(ndElement: ComplexNDElement): ComplexNDElement =
    ndElement.map { this@invoke(it) }

/* plus and minus */

/**
 * Summation operation for [BufferedNDElement] and single element
 */
public operator fun ComplexNDElement.plus(arg: Complex): ComplexNDElement = map { it + arg }

/**
 * Subtraction operation between [BufferedNDElement] and single element
 */
public operator fun ComplexNDElement.minus(arg: Complex): ComplexNDElement = map { it - arg }

public operator fun ComplexNDElement.plus(arg: Double): ComplexNDElement = map { it + arg }
public operator fun ComplexNDElement.minus(arg: Double): ComplexNDElement = map { it - arg }

public fun NDField.Companion.complex(vararg shape: Int): ComplexNDField = ComplexNDField(shape)

public fun NDElement.Companion.complex(
    vararg shape: Int,
    initializer: ComplexField.(IntArray) -> Complex,
): ComplexNDElement = NDField.complex(*shape).produce(initializer)

/**
 * Produce a context for n-dimensional operations inside this real field
 */
public inline fun <R> ComplexField.nd(vararg shape: Int, action: ComplexNDField.() -> R): R {
    contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }
    return NDField.complex(*shape).action()
}
