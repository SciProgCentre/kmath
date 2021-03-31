package space.kscience.kmath.complex

import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.nd.AlgebraND
import space.kscience.kmath.nd.BufferND
import space.kscience.kmath.nd.BufferedFieldND
import space.kscience.kmath.nd.StructureND
import space.kscience.kmath.operations.ExtendedField
import space.kscience.kmath.operations.NumbersAddOperations
import space.kscience.kmath.structures.Buffer
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


/**
 * An optimized nd-field for complex numbers
 */
@OptIn(UnstableKMathAPI::class)
public class ComplexFieldND(
    shape: IntArray,
) : BufferedFieldND<Complex, ComplexField>(shape, ComplexField, Buffer.Companion::complex),
    NumbersAddOperations<StructureND<Complex>>,
    ExtendedField<StructureND<Complex>> {

    public override val zero: BufferND<Complex> by lazy { produce { zero } }
    public override val one: BufferND<Complex> by lazy { produce { one } }

    public override fun number(value: Number): BufferND<Complex> {
        val d = value.toComplex() // minimize conversions
        return produce { d }
    }

//
//    @Suppress("OVERRIDE_BY_INLINE")
//    override inline fun map(
//        arg: AbstractNDBuffer<Double>,
//        transform: DoubleField.(Double) -> Double,
//    ): RealNDElement {
//        check(arg)
//        val array = RealBuffer(arg.strides.linearSize) { offset -> DoubleField.transform(arg.buffer[offset]) }
//        return BufferedNDFieldElement(this, array)
//    }
//
//    @Suppress("OVERRIDE_BY_INLINE")
//    override inline fun produce(initializer: DoubleField.(IntArray) -> Double): RealNDElement {
//        val array = RealBuffer(strides.linearSize) { offset -> elementContext.initializer(strides.index(offset)) }
//        return BufferedNDFieldElement(this, array)
//    }
//
//    @Suppress("OVERRIDE_BY_INLINE")
//    override inline fun mapIndexed(
//        arg: AbstractNDBuffer<Double>,
//        transform: DoubleField.(index: IntArray, Double) -> Double,
//    ): RealNDElement {
//        check(arg)
//        return BufferedNDFieldElement(
//            this,
//            RealBuffer(arg.strides.linearSize) { offset ->
//                elementContext.transform(
//                    arg.strides.index(offset),
//                    arg.buffer[offset]
//                )
//            })
//    }
//
//    @Suppress("OVERRIDE_BY_INLINE")
//    override inline fun combine(
//        a: AbstractNDBuffer<Double>,
//        b: AbstractNDBuffer<Double>,
//        transform: DoubleField.(Double, Double) -> Double,
//    ): RealNDElement {
//        check(a, b)
//        val buffer = RealBuffer(strides.linearSize) { offset ->
//            elementContext.transform(a.buffer[offset], b.buffer[offset])
//        }
//        return BufferedNDFieldElement(this, buffer)
//    }

    public override fun power(arg: StructureND<Complex>, pow: Number): BufferND<Complex> = arg.map { power(it, pow) }

    public override fun exp(arg: StructureND<Complex>): BufferND<Complex> = arg.map { exp(it) }

    public override fun ln(arg: StructureND<Complex>): BufferND<Complex> = arg.map { ln(it) }

    public override fun sin(arg: StructureND<Complex>): BufferND<Complex> = arg.map { sin(it) }
    public override fun cos(arg: StructureND<Complex>): BufferND<Complex> = arg.map { cos(it) }
    public override fun tan(arg: StructureND<Complex>): BufferND<Complex> = arg.map { tan(it) }
    public override fun asin(arg: StructureND<Complex>): BufferND<Complex> = arg.map { asin(it) }
    public override fun acos(arg: StructureND<Complex>): BufferND<Complex> = arg.map { acos(it) }
    public override fun atan(arg: StructureND<Complex>): BufferND<Complex> = arg.map { atan(it) }

    public override fun sinh(arg: StructureND<Complex>): BufferND<Complex> = arg.map { sinh(it) }
    public override fun cosh(arg: StructureND<Complex>): BufferND<Complex> = arg.map { cosh(it) }
    public override fun tanh(arg: StructureND<Complex>): BufferND<Complex> = arg.map { tanh(it) }
    public override fun asinh(arg: StructureND<Complex>): BufferND<Complex> = arg.map { asinh(it) }
    public override fun acosh(arg: StructureND<Complex>): BufferND<Complex> = arg.map { acosh(it) }
    public override fun atanh(arg: StructureND<Complex>): BufferND<Complex> = arg.map { atanh(it) }
}


/**
 * Fast element production using function inlining
 */
public inline fun BufferedFieldND<Complex, ComplexField>.produceInline(initializer: ComplexField.(Int) -> Complex): BufferND<Complex> {
    contract { callsInPlace(initializer, InvocationKind.EXACTLY_ONCE) }
    val buffer = Buffer.complex(strides.linearSize) { offset -> ComplexField.initializer(offset) }
    return BufferND(strides, buffer)
}


public fun AlgebraND.Companion.complex(vararg shape: Int): ComplexFieldND = ComplexFieldND(shape)

/**
 * Produce a context for n-dimensional operations inside this real field
 */
public inline fun <R> ComplexField.nd(vararg shape: Int, action: ComplexFieldND.() -> R): R {
    contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }
    return ComplexFieldND(shape).action()
}
