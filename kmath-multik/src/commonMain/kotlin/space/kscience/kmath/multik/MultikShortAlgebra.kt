/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.multik

import org.jetbrains.kotlinx.multik.api.Engine
import org.jetbrains.kotlinx.multik.api.Multik
import org.jetbrains.kotlinx.multik.api.ndarrayOf
import org.jetbrains.kotlinx.multik.ndarray.data.DataType
import space.kscience.kmath.operations.Int16Ring

public class MultikShortAlgebra(
    multikEngine: Engine,
) : MultikTensorAlgebra<Short, Int16Ring>(multikEngine) {
    override val elementAlgebra: Int16Ring get() = Int16Ring
    override val dataType: DataType get() = DataType.ShortDataType
    override fun scalar(value: Short): MultikTensor<Short> = Multik.ndarrayOf(value).wrap()
}

//public val Short.Companion.multikAlgebra: MultikTensorAlgebra<Short, ShortRing> get() = MultikShortAlgebra
//public val ShortRing.multikAlgebra: MultikTensorAlgebra<Short, ShortRing> get() = MultikShortAlgebra