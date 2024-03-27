/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.multik

import org.jetbrains.kotlinx.multik.api.Engine
import org.jetbrains.kotlinx.multik.api.Multik
import org.jetbrains.kotlinx.multik.api.ndarrayOf
import org.jetbrains.kotlinx.multik.ndarray.data.DataType
import space.kscience.kmath.operations.Int64Ring

public class MultikLongAlgebra(
    multikEngine: Engine,
) : MultikTensorAlgebra<Long, Int64Ring>(multikEngine) {
    override val elementAlgebra: Int64Ring get() = Int64Ring
    override val dataType: DataType get() = DataType.LongDataType

    override fun scalar(value: Long): MultikTensor<Long> = Multik.ndarrayOf(value).wrap()
}


//public val Long.Companion.multikAlgebra: MultikTensorAlgebra<Long, LongRing> get() = MultikLongAlgebra
//public val LongRing.multikAlgebra: MultikTensorAlgebra<Long, LongRing> get() = MultikLongAlgebra