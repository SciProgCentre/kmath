package kscience.kmath.nd

import kscience.kmath.misc.UnstableKMathAPI
import kscience.kmath.operations.RingWithNumbers
import kscience.kmath.operations.ShortRing
import kscience.kmath.structures.Buffer
import kscience.kmath.structures.ShortBuffer
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@OptIn(UnstableKMathAPI::class)
public class ShortNDRing(
    shape: IntArray,
) : BufferedNDRing<Short, ShortRing>(shape, ShortRing, Buffer.Companion::auto),
    RingWithNumbers<NDStructure<Short>> {

    override val zero: NDBuffer<Short> by lazy { produce { zero } }
    override val one: NDBuffer<Short> by lazy { produce { one } }

    override fun number(value: Number): NDBuffer<Short> {
        val d = value.toShort() // minimize conversions
        return produce { d }
    }
}

/**
 * Fast element production using function inlining.
 */
public inline fun BufferedNDRing<Short, ShortRing>.produceInline(crossinline initializer: ShortRing.(Int) -> Short): NDBuffer<Short> {
    return NDBuffer(strides, ShortBuffer(ShortArray(strides.linearSize) { offset -> ShortRing.initializer(offset) }))
}

public inline fun <R> ShortRing.nd(vararg shape: Int, action: ShortNDRing.() -> R): R {
    contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }
    return ShortNDRing(shape).run(action)
}