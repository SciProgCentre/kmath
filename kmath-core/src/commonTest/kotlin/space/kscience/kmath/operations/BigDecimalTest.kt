/*
 * Copyright 2018-2023 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.operations

import space.kscience.kmath.UnstableKMathAPI
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(UnstableKMathAPI::class)
class BigDecimalTest {

    @Test
    fun simpleExpression() = with(BigDecimalField) {
        assertEquals( 1000.0.toDecimal(),22.2.toDecimal().pow10(2) / 1.11)
    }
}