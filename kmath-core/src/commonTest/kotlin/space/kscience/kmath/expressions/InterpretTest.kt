/*
 * Copyright 2018-2026 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.expressions

import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.expressions.Symbol.Companion.x
import space.kscience.kmath.expressions.Symbol.Companion.y
import space.kscience.kmath.expressions.Symbol.Companion.z
import space.kscience.kmath.operations.BooleanAlgebra
import space.kscience.kmath.operations.Float64Field
import space.kscience.kmath.operations.Int32Ring
import space.kscience.kmath.operations.invoke
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails


@OptIn(UnstableKMathAPI::class)
internal class InterpretTest {
    private val a by symbol
    private val b by symbol

    @Test
    fun interpretation() {
        val expr = MstField {
            x * 2.0 + number(2.0) / x - 16.0
        }.toExpression(Float64Field)
        assertEquals(-10.69, expr(x to 2.2), 0.02)
    }

    @Test
    fun groupInterpretation() {
        val mst = MstGroup {
            -x + y - zero + scale(x, 3.0) + (+z)
        }
        val expr = mst.toExpression(Float64Field)
        // -2.0 + 5.0 - 0.0 + 3.0 * 2.0 + 10.0 = 19.0
        assertEquals(19.0, expr(x to 2.0, y to 5.0, z to 10.0), 1e-6)

        // Also test direct interpret with Map and with Int32Ring
        val intMst = MstGroup {
            -x + y - z
        }
        val intResult = intMst.interpret(Int32Ring, mapOf(x to 10, y to 25, z to 5))
        assertEquals(10, intResult)
    }

    @Test
    fun ringInterpretation() {
        val mst = MstRing {
            (x + 2.0) * (y - 3.0) + one - scale(z, 2.0)
        }
        val expr = mst.toExpression(Float64Field)
        // (3.0 + 2.0) * (7.0 - 3.0) + 1.0 - (4.0 * 2.0) = 5.0 * 4.0 + 1.0 - 8.0 = 13.0
        assertEquals(13.0, expr(x to 3.0, y to 7.0, z to 4.0), 1e-6)

        // Polynomial in Int32Ring: (x + 1) * (x - 2) * (x + 3)
        val polyMst = MstRing {
            (x + 1) * (x - 2) * (x + 3)
        }
        val polyExpr = polyMst.toExpression(Int32Ring)
        // x = 4 -> (5) * (2) * (7) = 70
        assertEquals(70, polyExpr(x to 4))
    }

    @Test
    fun fieldComplexRationalInterpretation() {
        val mst = MstField {
            ((x + y) / (x - y)) * ((x * x - y * y) / (x + 1.0)) + number(1.0) / (x * y)
        }
        val expr = mst.toExpression(Float64Field)
        // For x = 3.0, y = 2.0:
        // (5.0 / 1.0) * ((9.0 - 4.0) / 4.0) + 1.0 / 6.0 = 5.0 * (5.0 / 4.0) + 1/6 = 6.25 + 0.16666667 = 6.41666667
        val expected = 5.0 * (5.0 / 4.0) + 1.0 / 6.0
        assertEquals(expected, expr(x to 3.0, y to 2.0), 1e-6)

        // Nested continued fraction: 1 / (1 + 1 / (1 + 1 / x))
        val fracMst = MstField {
            number(1.0) / (number(1.0) + number(1.0) / (number(1.0) + number(1.0) / x))
        }
        val fracExpr = fracMst.toExpression(Float64Field)
        // x = 2.0: 1 + 1/2 = 1.5; 1 + 1/1.5 = 1 + 2/3 = 5/3; 1 / (5/3) = 3/5 = 0.6
        assertEquals(0.6, fracExpr(x to 2.0), 1e-6)
    }

    @Test
    fun extendedFieldTrigonometricInterpretation() {
        val trigIdentity = MstExtendedField {
            sin(x) * sin(x) + cos(x) * cos(x)
        }.toExpression(Float64Field)
        assertEquals(1.0, trigIdentity(x to 0.75), 1e-6)
        assertEquals(1.0, trigIdentity(x to -1.25), 1e-6)

        val tanMst = MstExtendedField {
            tan(x) - sin(x) / cos(x)
        }.toExpression(Float64Field)
        assertEquals(0.0, tanMst(x to 0.5), 1e-6)

        val inverseTrig = MstExtendedField {
            asin(sin(x)) + acos(cos(y)) + atan(tan(z))
        }.toExpression(Float64Field)
        assertEquals(0.3 + 0.4 + 0.5, inverseTrig(x to 0.3, y to 0.4, z to 0.5), 1e-6)
    }

    @Test
    fun extendedFieldHyperbolicInterpretation() {
        val hypIdentity = MstExtendedField {
            cosh(x) * cosh(x) - sinh(x) * sinh(x)
        }.toExpression(Float64Field)
        assertEquals(1.0, hypIdentity(x to 1.5), 1e-6)
        assertEquals(1.0, hypIdentity(x to -2.0), 1e-6)

        val tanhMst = MstExtendedField {
            tanh(x) - sinh(x) / cosh(x)
        }.toExpression(Float64Field)
        assertEquals(0.0, tanhMst(x to 0.8), 1e-6)

        val inverseHyp = MstExtendedField {
            asinh(sinh(x)) + acosh(cosh(y)) + atanh(tanh(z))
        }.toExpression(Float64Field)
        assertEquals(0.5 + 1.2 + 0.7, inverseHyp(x to 0.5, y to 1.2, z to 0.7), 1e-6)
    }

    @Test
    fun extendedFieldPowerExpLnInterpretation() {
        val expLn = MstExtendedField {
            exp(ln(x)) + ln(exp(y)) + sqrt(power(z, 2))
        }.toExpression(Float64Field)
        assertEquals(2.5 + 3.5 + 4.5, expLn(x to 2.5, y to 3.5, z to 4.5), 1e-6)

        val powerDouble = MstExtendedField {
            power(x, 3.5)
        }.toExpression(Float64Field)
        assertEquals(2.0.pow(3.5), powerDouble(x to 2.0), 1e-6)

        // Complex composite non-linear formula
        val complexMst = MstExtendedField {
            (exp(-x) * sin(y) + sqrt(x * y + 1.0)) / (ln(x + y) + 1.0)
        }
        val complexExpr = complexMst.toExpression(Float64Field)
        val xVal = 1.5
        val yVal = 2.0
        val expected = (kotlin.math.exp(-xVal) * kotlin.math.sin(yVal) + sqrt(xVal * yVal + 1.0)) / (kotlin.math.ln(xVal + yVal) + 1.0)
        assertEquals(expected, complexExpr(x to xVal, y to yVal), 1e-6)
    }

    @Test
    fun booleanAlgebra() {
        val expr = MstLogicAlgebra {
            x and const(true)
        }.toExpression(BooleanAlgebra)

        assertEquals(true, expr(x to true))
        assertEquals(false, expr(x to false))
    }

    @Test
    fun booleanComplexLogicInterpretation() {
        // Multiplexer: (s and a) or (!s and b)
        val s = Symbol("s")
        val mux = MstLogicAlgebra {
            (s and a) or (!s and b)
        }.toExpression(BooleanAlgebra)

        assertEquals(true, mux(s to true, a to true, b to false))
        assertEquals(false, mux(s to true, a to false, b to true))
        assertEquals(true, mux(s to false, a to false, b to true))
        assertEquals(false, mux(s to false, a to true, b to false))

        // XOR truth table and De Morgan's laws
        val xorExpr = MstLogicAlgebra {
            x xor y
        }.toExpression(BooleanAlgebra)
        assertEquals(false, xorExpr(x to false, y to false))
        assertEquals(true, xorExpr(x to true, y to false))
        assertEquals(true, xorExpr(x to false, y to true))
        assertEquals(false, xorExpr(x to true, y to true))

        val deMorganAnd = MstLogicAlgebra {
            !(x and y)
        }.toExpression(BooleanAlgebra)
        val deMorganOr = MstLogicAlgebra {
            !x or !y
        }.toExpression(BooleanAlgebra)
        for (vx in listOf(true, false)) {
            for (vy in listOf(true, false)) {
                assertEquals(deMorganAnd(x to vx, y to vy), deMorganOr(x to vx, y to vy))
            }
        }
    }

    @Test
    fun directMstInterpretationEdgeCases() {
        // Numeric node alone
        val numMst = MST.Numeric(42.0)
        assertEquals(42.0, numMst.interpret(Float64Field))

        // Constant expression with no variable arguments
        val constMst = MstField {
            number(10.0) / 2.0 + 3.0 * 4.0
        }
        assertEquals(17.0, constMst.interpret(Float64Field))

        // Left numeric, right expression
        val leftNumMst = MstField {
            number(100.0) - (x * 5.0)
        }
        assertEquals(85.0, leftNumMst.interpret(Float64Field, x to 3.0))

        // Right numeric, left expression
        val rightNumMst = MstField {
            (x * 5.0) - number(10.0)
        }
        assertEquals(5.0, rightNumMst.interpret(Float64Field, x to 3.0))

        // Direct Unary node on Numeric
        val unaryNumMst = MST.Unary("-", MST.Numeric(15.0))
        assertEquals(-15.0, unaryNumMst.interpret(Float64Field))

        // Direct Binary node on Numeric
        val binaryNumMst = MST.Binary("+", MST.Numeric(12.0), MST.Numeric(8.0))
        assertEquals(20.0, binaryNumMst.interpret(Float64Field))

        // Missing argument should fail
        val varMst = MstField { x + 1.0 }
        assertFails { varMst.interpret(Float64Field) }
    }
}
