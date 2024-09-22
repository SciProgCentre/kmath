/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.expressions

import space.kscience.kmath.operations.Float64Field
import space.kscience.kmath.structures.Float64
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class ExpressionFieldTest {
    val x by symbol

    @Test
    fun testExpression() {
        val expression = with(FunctionalExpressionField(Float64Field)) {
            val x by binding
            x * x + 2 * x + one
        }

        assertEquals(expression(x to 1.0), 4.0)
        assertFails { expression() }
    }

    @Test
    fun separateContext() {
        fun <T> FunctionalExpressionField<T, *>.expression(): Expression<T> {
            val x by binding
            return x * x + 2 * x + one
        }

        val expression = FunctionalExpressionField(Float64Field).expression()
        assertEquals(expression(x to 1.0), 4.0)
    }

    @Test
    fun valueExpression() {
        val expressionBuilder: FunctionalExpressionField<Double, *>.() -> Expression<Float64> = {
            val x by binding
            x * x + 2 * x + one
        }

        val expression = FunctionalExpressionField(Float64Field).expressionBuilder()
        assertEquals(expression(x to 1.0), 4.0)
    }
}
