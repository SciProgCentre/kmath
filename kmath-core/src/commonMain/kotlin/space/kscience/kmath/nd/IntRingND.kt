/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.nd

import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.operations.IntRing
import space.kscience.kmath.operations.NumbersAddOps
import space.kscience.kmath.operations.bufferAlgebra
import space.kscience.kmath.structures.IntBuffer
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public class IntBufferND(
    indexes: ShapeIndexer,
    override val buffer: IntBuffer,
) : MutableBufferND<Int>(indexes, buffer)

public sealed class IntRingOpsND : BufferedRingOpsND<Int, IntRing>(IntRing.bufferAlgebra) {

    override fun structureND(shape: ShapeND, initializer: IntRing.(IntArray) -> Int): IntBufferND {
        val indexer = indexerBuilder(shape)
        return IntBufferND(
            indexer,
            IntBuffer(indexer.linearSize) { offset ->
                elementAlgebra.initializer(indexer.index(offset))
            }
        )
    }

    public companion object : IntRingOpsND()
}

@OptIn(UnstableKMathAPI::class)
public class IntRingND(
    override val shape: ShapeND
) : IntRingOpsND(), RingND<Int, IntRing>, NumbersAddOps<StructureND<Int>> {

    override fun number(value: Number): BufferND<Int> {
        val int = value.toInt() // minimize conversions
        return structureND(shape) { int }
    }
}

public inline fun <R> IntRing.withNdAlgebra(vararg shape: Int, action: IntRingND.() -> R): R {
    contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }
    return IntRingND(ShapeND(shape)).run(action)
}
