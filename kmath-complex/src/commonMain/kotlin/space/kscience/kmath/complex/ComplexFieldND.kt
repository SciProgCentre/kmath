package space.kscience.kmath.complex

import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.nd.AlgebraND
import space.kscience.kmath.nd.BufferedFieldND
import space.kscience.kmath.nd.NDBuffer
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

    override val zero: NDBuffer<Complex> by lazy { produce { zero } }
    override val one: NDBuffer<Complex> by lazy { produce { one } }

    override fun number(value: Number): NDBuffer<Complex> {
        val d = value.toComplex() // minimize conversions
        return produce { d }
    }

//
//    @Suppress("OVERRIDE_BY_INLINE")
//    override inline fun map(
//        arg: AbstractNDBuffer<Double>,
//        transform: RealField.(Double) -> Double,
//    ): RealNDElement {
//        check(arg)
//        val array = RealBuffer(arg.strides.linearSize) { offset -> RealField.transform(arg.buffer[offset]) }
//        return BufferedNDFieldElement(this, array)
//    }
//
//    @Suppress("OVERRIDE_BY_INLINE")
//    override inline fun produce(initializer: RealField.(IntArray) -> Double): RealNDElement {
//        val array = RealBuffer(strides.linearSize) { offset -> elementContext.initializer(strides.index(offset)) }
//        return BufferedNDFieldElement(this, array)
//    }
//
//    @Suppress("OVERRIDE_BY_INLINE")
//    override inline fun mapIndexed(
//        arg: AbstractNDBuffer<Double>,
//        transform: RealField.(index: IntArray, Double) -> Double,
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
//        transform: RealField.(Double, Double) -> Double,
//    ): RealNDElement {
//        check(a, b)
//        val buffer = RealBuffer(strides.linearSize) { offset ->
//            elementContext.transform(a.buffer[offset], b.buffer[offset])
//        }
//        return BufferedNDFieldElement(this, buffer)
//    }

    override fun power(arg: StructureND<Complex>, pow: Number): NDBuffer<Complex> = arg.map { power(it, pow) }

    override fun exp(arg: StructureND<Complex>): NDBuffer<Complex> = arg.map { exp(it) }

    override fun ln(arg: StructureND<Complex>): NDBuffer<Complex> = arg.map { ln(it) }

    override fun sin(arg: StructureND<Complex>): NDBuffer<Complex> = arg.map { sin(it) }
    override fun cos(arg: StructureND<Complex>): NDBuffer<Complex> = arg.map { cos(it) }
    override fun tan(arg: StructureND<Complex>): NDBuffer<Complex> = arg.map { tan(it) }
    override fun asin(arg: StructureND<Complex>): NDBuffer<Complex> = arg.map { asin(it) }
    override fun acos(arg: StructureND<Complex>): NDBuffer<Complex> = arg.map { acos(it) }
    override fun atan(arg: StructureND<Complex>): NDBuffer<Complex> = arg.map { atan(it) }

    override fun sinh(arg: StructureND<Complex>): NDBuffer<Complex> = arg.map { sinh(it) }
    override fun cosh(arg: StructureND<Complex>): NDBuffer<Complex> = arg.map { cosh(it) }
    override fun tanh(arg: StructureND<Complex>): NDBuffer<Complex> = arg.map { tanh(it) }
    override fun asinh(arg: StructureND<Complex>): NDBuffer<Complex> = arg.map { asinh(it) }
    override fun acosh(arg: StructureND<Complex>): NDBuffer<Complex> = arg.map { acosh(it) }
    override fun atanh(arg: StructureND<Complex>): NDBuffer<Complex> = arg.map { atanh(it) }
}


/**
 * Fast element production using function inlining
 */
public inline fun BufferedFieldND<Complex, ComplexField>.produceInline(initializer: ComplexField.(Int) -> Complex): NDBuffer<Complex> {
    contract { callsInPlace(initializer, InvocationKind.EXACTLY_ONCE) }
    val buffer = Buffer.complex(strides.linearSize) { offset -> ComplexField.initializer(offset) }
    return NDBuffer(strides, buffer)
}


public fun AlgebraND.Companion.complex(vararg shape: Int): ComplexFieldND = ComplexFieldND(shape)

/**
 * Produce a context for n-dimensional operations inside this real field
 */
public inline fun <R> ComplexField.nd(vararg shape: Int, action: ComplexFieldND.() -> R): R {
    contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }
    return ComplexFieldND(shape).action()
}
