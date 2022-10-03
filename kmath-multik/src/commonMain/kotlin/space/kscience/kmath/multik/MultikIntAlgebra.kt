/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.multik

import org.jetbrains.kotlinx.multik.api.Engine
import org.jetbrains.kotlinx.multik.api.Multik
import org.jetbrains.kotlinx.multik.api.ndarrayOf
import org.jetbrains.kotlinx.multik.ndarray.data.DataType
import space.kscience.kmath.operations.IntRing

public class MultikIntAlgebra(
    multikEngine: Engine
) : MultikTensorAlgebra<Int, IntRing>(multikEngine) {
    override val elementAlgebra: IntRing get() = IntRing
    override val type: DataType get() = DataType.IntDataType
    override fun scalar(value: Int): MultikTensor<Int>  = Multik.ndarrayOf(value).wrap()
}

//public val Int.Companion.multikAlgebra: MultikTensorAlgebra<Int, IntRing> get() = MultikIntAlgebra
//public val IntRing.multikAlgebra: MultikTensorAlgebra<Int, IntRing> get() = MultikIntAlgebra