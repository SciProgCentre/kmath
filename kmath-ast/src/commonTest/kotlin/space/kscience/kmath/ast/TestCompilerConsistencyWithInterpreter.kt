/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.ast

import space.kscience.kmath.expressions.MstField
import space.kscience.kmath.expressions.MstRing
import space.kscience.kmath.expressions.Symbol.Companion.x
import space.kscience.kmath.expressions.interpret
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.operations.IntRing
import space.kscience.kmath.operations.invoke
import kotlin.test.Test
import kotlin.test.assertEquals

internal class TestCompilerConsistencyWithInterpreter {
    @Test
    fun intRing() = runCompilerTest {
        val mst = MstRing {
            binaryOperationFunction("+")(
                unaryOperationFunction("+")(
                    (x - (2.toByte() + (scale(
                        add(number(1), number(1)),
                        2.0,
                    ) + 1.toByte()))) * 3.0 - 1.toByte()
                ),

                number(1),
            ) * number(2)
        }

        assertEquals(
            mst.interpret(IntRing, x to 3),
            mst.compile(IntRing, x to 3),
        )
    }

    @Test
    fun doubleField() = runCompilerTest {
        val mst = MstField {
            +(3 - 2 + 2 * number(1) + 1.0) + binaryOperationFunction("+")(
                (3.0 - (x + (scale(add(number(1.0), number(1.0)), 2.0) + 1.0))) * 3 - 1.0
                        + number(1),
                number(1) / 2 + number(2.0) * one,
            ) + zero
        }

        assertEquals(
            mst.interpret(DoubleField, x to 2.0),
            mst.compile(DoubleField, x to 2.0),
        )
    }
}
