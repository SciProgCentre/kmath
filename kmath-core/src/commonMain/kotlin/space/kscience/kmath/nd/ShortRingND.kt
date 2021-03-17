package space.kscience.kmath.nd

import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.operations.NumbersAddOperations
import space.kscience.kmath.operations.ShortRing
import space.kscience.kmath.structures.Buffer
import space.kscience.kmath.structures.ShortBuffer
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@OptIn(UnstableKMathAPI::class)
public class ShortRingND(
    shape: IntArray,
) : BufferedRingND<Short, ShortRing>(shape, ShortRing, Buffer.Companion::auto),
    NumbersAddOperations<StructureND<Short>> {

    override val zero: BufferND<Short> by lazy { produce { zero } }
    override val one: BufferND<Short> by lazy { produce { one } }

    override fun number(value: Number): BufferND<Short> {
        val d = value.toShort() // minimize conversions
        return produce { d }
    }
}

/**
 * Fast element production using function inlining.
 */
public inline fun BufferedRingND<Short, ShortRing>.produceInline(crossinline initializer: ShortRing.(Int) -> Short): BufferND<Short> {
    return BufferND(strides, ShortBuffer(ShortArray(strides.linearSize) { offset -> ShortRing.initializer(offset) }))
}

public inline fun <R> ShortRing.nd(vararg shape: Int, action: ShortRingND.() -> R): R {
    contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }
    return ShortRingND(shape).run(action)
}