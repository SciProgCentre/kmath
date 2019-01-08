package scientifik.kmath.structures

import scientifik.kmath.operations.RealField

typealias RealNDElement = StridedNDFieldElement<Double, RealField>

class RealNDField(shape: IntArray) :
    StridedNDField<Double, RealField>(shape),
    ExtendedNDField<Double, RealField, NDBuffer<Double>> {

    override val elementContext: RealField get() = RealField
    override val zero by lazy { produce { zero } }
    override val one by lazy { produce { one } }

    @Suppress("OVERRIDE_BY_INLINE")
    override inline fun buildBuffer(size: Int, crossinline initializer: (Int) -> Double): Buffer<Double> =
        DoubleBuffer(DoubleArray(size) { initializer(it) })

    /**
     * Inline transform an NDStructure to
     */
    override fun map(
        arg: NDBuffer<Double>,
        transform: RealField.(Double) -> Double
    ): RealNDElement {
        check(arg)
        val array = buildBuffer(arg.strides.linearSize) { offset -> RealField.transform(arg.buffer[offset]) }
        return StridedNDFieldElement(this, array)
    }

    override fun produce(initializer: RealField.(IntArray) -> Double): RealNDElement {
        val array = buildBuffer(strides.linearSize) { offset -> elementContext.initializer(strides.index(offset)) }
        return StridedNDFieldElement(this, array)
    }

    override fun mapIndexed(
        arg: NDBuffer<Double>,
        transform: RealField.(index: IntArray, Double) -> Double
    ): StridedNDFieldElement<Double, RealField> {
        check(arg)
        return StridedNDFieldElement(
            this,
            buildBuffer(arg.strides.linearSize) { offset ->
                elementContext.transform(
                    arg.strides.index(offset),
                    arg.buffer[offset]
                )
            })
    }

    override fun combine(
        a: NDBuffer<Double>,
        b: NDBuffer<Double>,
        transform: RealField.(Double, Double) -> Double
    ): StridedNDFieldElement<Double, RealField> {
        check(a, b)
        return StridedNDFieldElement(
            this,
            buildBuffer(strides.linearSize) { offset -> elementContext.transform(a.buffer[offset], b.buffer[offset]) })
    }

    override fun power(arg: NDBuffer<Double>, pow: Double) = map(arg) { power(it, pow) }

    override fun exp(arg: NDBuffer<Double>) = map(arg) { exp(it) }

    override fun ln(arg: NDBuffer<Double>) = map(arg) { ln(it) }

    override fun sin(arg: NDBuffer<Double>) = map(arg) { sin(it) }

    override fun cos(arg: NDBuffer<Double>) = map(arg) { cos(it) }

}


/**
 * Fast element production using function inlining
 */
inline fun StridedNDField<Double, RealField>.produceInline(crossinline initializer: RealField.(Int) -> Double): RealNDElement {
    val array = DoubleArray(strides.linearSize) { offset -> RealField.initializer(offset) }
    return StridedNDFieldElement(this, DoubleBuffer(array))
}

/**
 * Element by element application of any operation on elements to the whole array. Just like in numpy
 */
operator fun Function1<Double, Double>.invoke(ndElement: RealNDElement) =
    ndElement.context.produceInline { i -> invoke(ndElement.buffer[i]) }


/* plus and minus */

/**
 * Summation operation for [StridedNDFieldElement] and single element
 */
operator fun RealNDElement.plus(arg: Double) =
    context.produceInline { i -> buffer[i] + arg }

/**
 * Subtraction operation between [StridedNDFieldElement] and single element
 */
operator fun RealNDElement.minus(arg: Double) =
    context.produceInline { i -> buffer[i] - arg }
