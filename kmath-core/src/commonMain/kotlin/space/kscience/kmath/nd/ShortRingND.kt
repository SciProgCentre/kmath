/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.nd

import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.operations.Int16Ring
import space.kscience.kmath.operations.NumbersAddOps
import space.kscience.kmath.operations.bufferAlgebra
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public sealed class ShortRingOpsND : BufferedRingOpsND<Short, Int16Ring>(Int16Ring.bufferAlgebra) {
    public companion object : ShortRingOpsND()
}

@OptIn(UnstableKMathAPI::class)
public class ShortRingND(
    override val shape: ShapeND
) : ShortRingOpsND(), RingND<Short, Int16Ring>, NumbersAddOps<StructureND<Short>> {

    override fun number(value: Number): BufferND<Short> {
        val short
        = value.toShort() // minimize conversions
        return structureND(shape) { short }
    }
}

public inline fun <R> Int16Ring.withNdAlgebra(vararg shape: Int, action: ShortRingND.() -> R): R {
    contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }
    return ShortRingND(ShapeND(shape)).run(action)
}
