/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.complex

import space.kscience.kmath.expressions.FunctionalExpressionField
import space.kscience.kmath.expressions.invoke
import space.kscience.kmath.expressions.symbol
import space.kscience.kmath.operations.bindSymbol
import kotlin.test.Test
import kotlin.test.assertEquals

internal class ExpressionFieldForComplexTest {
    val x by symbol

    @Test
    fun testComplex() {
        val expression = FunctionalExpressionField(ComplexDoubleField).run {
            val x = bindSymbol(x)
            x * x + 2 * x + one
        }

        assertEquals(Complex(4.0, 0.0), expression(x to Complex(1.0, 0.0)))
    }
}
