/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.expressions

import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.expressions.Symbol.Companion.x
import space.kscience.kmath.operations.BooleanAlgebra
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.operations.invoke
import kotlin.test.Test
import kotlin.test.assertEquals


internal class InterpretTest {
    @Test
    fun interpretation() {
        val expr = MstField {
            x * 2.0 + number(2.0) / x - 16.0
        }.toExpression(DoubleField)
        assertEquals(-10.69, expr(x to 2.2), 0.02)
    }

    @Test
    @UnstableKMathAPI
    fun booleanAlgebra() {
        val expr = MstLogicAlgebra {
            x and const(true)
        }.toExpression(BooleanAlgebra)

        assertEquals(true, expr(x to true))
        assertEquals(false, expr(x to false))
    }
}
