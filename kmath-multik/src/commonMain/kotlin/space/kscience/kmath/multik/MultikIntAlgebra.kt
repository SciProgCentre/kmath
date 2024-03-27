/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.multik

import org.jetbrains.kotlinx.multik.api.Engine
import org.jetbrains.kotlinx.multik.api.Multik
import org.jetbrains.kotlinx.multik.api.ndarrayOf
import org.jetbrains.kotlinx.multik.ndarray.data.DataType
import space.kscience.kmath.operations.Int32Ring

public class MultikIntAlgebra(
    multikEngine: Engine,
) : MultikTensorAlgebra<Int, Int32Ring>(multikEngine) {
    override val elementAlgebra: Int32Ring get() = Int32Ring
    override val dataType: DataType get() = DataType.IntDataType
    override fun scalar(value: Int): MultikTensor<Int> = Multik.ndarrayOf(value).wrap()
}

//public val Int.Companion.multikAlgebra: MultikTensorAlgebra<Int, IntRing> get() = MultikIntAlgebra
//public val IntRing.multikAlgebra: MultikTensorAlgebra<Int, IntRing> get() = MultikIntAlgebra