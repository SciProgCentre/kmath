//package scientifik.kmath.structures
//
//import scientifik.kmath.operations.ShortRing
//
//
////typealias ShortNDElement = StridedNDFieldElement<Short, ShortRing>
//
//class ShortNDRing(shape: IntArray) :
//    NDRing<Short, ShortRing, NDBuffer<Short>> {
//
//    inline fun buildBuffer(size: Int, crossinline initializer: (Int) -> Short): Buffer<Short> =
//        ShortBuffer(ShortArray(size) { initializer(it) })
//
//    /**
//     * Inline transform an NDStructure to
//     */
//    override fun map(
//        arg: NDBuffer<Short>,
//        transform: ShortRing.(Short) -> Short
//    ): ShortNDElement {
//        check(arg)
//        val array = buildBuffer(arg.strides.linearSize) { offset -> ShortRing.transform(arg.buffer[offset]) }
//        return StridedNDFieldElement(this, array)
//    }
//
//    override fun produce(initializer: ShortRing.(IntArray) -> Short): ShortNDElement {
//        val array = buildBuffer(strides.linearSize) { offset -> elementContext.initializer(strides.index(offset)) }
//        return StridedNDFieldElement(this, array)
//    }
//
//    override fun mapIndexed(
//        arg: NDBuffer<Short>,
//        transform: ShortRing.(index: IntArray, Short) -> Short
//    ): StridedNDFieldElement<Short, ShortRing> {
//        check(arg)
//        return StridedNDFieldElement(
//            this,
//            buildBuffer(arg.strides.linearSize) { offset ->
//                elementContext.transform(
//                    arg.strides.index(offset),
//                    arg.buffer[offset]
//                )
//            })
//    }
//
//    override fun combine(
//        a: NDBuffer<Short>,
//        b: NDBuffer<Short>,
//        transform: ShortRing.(Short, Short) -> Short
//    ): StridedNDFieldElement<Short, ShortRing> {
//        check(a, b)
//        return StridedNDFieldElement(
//            this,
//            buildBuffer(strides.linearSize) { offset -> elementContext.transform(a.buffer[offset], b.buffer[offset]) })
//    }
//}
//
//
///**
// * Fast element production using function inlining
// */
//inline fun StridedNDField<Short, ShortRing>.produceInline(crossinline initializer: ShortRing.(Int) -> Short): ShortNDElement {
//    val array = ShortArray(strides.linearSize) { offset -> ShortRing.initializer(offset) }
//    return StridedNDFieldElement(this, ShortBuffer(array))
//}
//
///**
// * Element by element application of any operation on elements to the whole array. Just like in numpy
// */
//operator fun Function1<Short, Short>.invoke(ndElement: ShortNDElement) =
//    ndElement.context.produceInline { i -> invoke(ndElement.buffer[i]) }
//
//
///* plus and minus */
//
///**
// * Summation operation for [StridedNDFieldElement] and single element
// */
//operator fun ShortNDElement.plus(arg: Short) =
//    context.produceInline { i -> buffer[i] + arg }
//
///**
// * Subtraction operation between [StridedNDFieldElement] and single element
// */
//operator fun ShortNDElement.minus(arg: Short) =
//    context.produceInline { i -> buffer[i] - arg }