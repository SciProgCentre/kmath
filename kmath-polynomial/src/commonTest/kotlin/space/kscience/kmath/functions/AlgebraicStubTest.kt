/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.functions


import space.kscience.kmath.operations.invoke
import space.kscience.kmath.operations.Field
import kotlin.jvm.JvmInline
import kotlin.test.Test
import kotlin.test.assertEquals

@JvmInline
value class Expr(val expr: String)

object ExprRing : Field<Expr> {
    override fun Expr.unaryMinus(): Expr = Expr("-${expr}")
    override fun add(left: Expr, right: Expr): Expr = Expr("(${left.expr} + ${right.expr})")
    override fun multiply(left: Expr, right: Expr): Expr = Expr("(${left.expr} * ${right.expr})")
    override val zero: Expr = Expr("0")
    override val one: Expr = Expr("1")
    override fun divide(left: Expr, right: Expr): Expr = Expr("(${left.expr} / ${right.expr})")
    override fun scale(a: Expr, value: Double): Expr = Expr("(${a.expr} / $value)")
}

class AlgebraicStubTest {
    @Test
    fun test_addMultipliedBySquaring_for_UInt() {
        ExprRing {
            assertEquals(
                "57",
                addMultipliedByDoubling(Expr("57"), Expr("179"), 0u).expr,
                "tried addMultipliedBySquaring(57, 179, 0u)"
            )
            assertEquals(
                "(57 + 179)",
                addMultipliedByDoubling(Expr("57"), Expr("179"), 1u).expr,
                "tried addMultipliedBySquaring(57, 179, 1u)"
            )
            assertEquals(
                "(57 + (179 + 179))",
                addMultipliedByDoubling(Expr("57"), Expr("179"), 2u).expr,
                "tried addMultipliedBySquaring(57, 179, 2u)"
            )
            assertEquals(
                "((57 + 179) + (179 + 179))",
                addMultipliedByDoubling(Expr("57"), Expr("179"), 3u).expr,
                "tried addMultipliedBySquaring(57, 179, 3u)"
            )
            assertEquals(
                "(57 + ((179 + 179) + (179 + 179)))",
                addMultipliedByDoubling(Expr("57"), Expr("179"), 4u).expr,
                "tried addMultipliedBySquaring(57, 179, 4u)"
            )
            assertEquals(
                "((57 + 179) + ((179 + 179) + (179 + 179)))",
                addMultipliedByDoubling(Expr("57"), Expr("179"), 5u).expr,
                "tried addMultipliedBySquaring(57, 179, 5u)"
            )
            assertEquals(
                "((57 + (179 + 179)) + ((179 + 179) + (179 + 179)))",
                addMultipliedByDoubling(Expr("57"), Expr("179"), 6u).expr,
                "tried addMultipliedBySquaring(57, 179, 6u)"
            )
            assertEquals(
                "(((57 + 179) + (179 + 179)) + ((179 + 179) + (179 + 179)))",
                addMultipliedByDoubling(Expr("57"), Expr("179"), 7u).expr,
                "tried addMultipliedBySquaring(57, 179, 7u)"
            )
            assertEquals(
                "(57 + (((179 + 179) + (179 + 179)) + ((179 + 179) + (179 + 179))))",
                addMultipliedByDoubling(Expr("57"), Expr("179"), 8u).expr,
                "tried addMultipliedBySquaring(57, 179, 8u)"
            )
        }
    }
    @Test
    fun test_multiplyBySquaring_for_UInt() {
        ExprRing {
            assertEquals(
                "0",
                multiplyByDoubling(Expr("57"), 0u).expr,
                "tried multiplyBySquaring(57, 0u)"
            )
            assertEquals(
                "57",
                multiplyByDoubling(Expr("57"), 1u).expr,
                "tried multiplyBySquaring(57, 1u)"
            )
            assertEquals(
                "(57 + 57)",
                multiplyByDoubling(Expr("57"), 2u).expr,
                "tried multiplyBySquaring(57, 2u)"
            )
            assertEquals(
                "(57 + (57 + 57))",
                multiplyByDoubling(Expr("57"), 3u).expr,
                "tried multiplyBySquaring(57, 3u)"
            )
            assertEquals(
                "((57 + 57) + (57 + 57))",
                multiplyByDoubling(Expr("57"), 4u).expr,
                "tried multiplyBySquaring(57, 4u)"
            )
            assertEquals(
                "(57 + ((57 + 57) + (57 + 57)))",
                multiplyByDoubling(Expr("57"), 5u).expr,
                "tried multiplyBySquaring(57, 5u)"
            )
            assertEquals(
                "((57 + 57) + ((57 + 57) + (57 + 57)))",
                multiplyByDoubling(Expr("57"), 6u).expr,
                "tried multiplyBySquaring(57, 6u)"
            )
            assertEquals(
                "((57 + (57 + 57)) + ((57 + 57) + (57 + 57)))",
                multiplyByDoubling(Expr("57"), 7u).expr,
                "tried multiplyBySquaring(57, 7u)"
            )
            assertEquals(
                "(((57 + 57) + (57 + 57)) + ((57 + 57) + (57 + 57)))",
                multiplyByDoubling(Expr("57"), 8u).expr,
                "tried multiplyBySquaring(57, 8u)"
            )
        }
    }
    @Test
    fun test_addMultipliedBySquaring_for_Int() {
        ExprRing {
            assertEquals(
                "57",
                addMultipliedByDoubling(Expr("57"), Expr("179"), 0).expr,
                "tried addMultipliedBySquaring(57, 179, 0)"
            )
            assertEquals(
                "(57 + 179)",
                addMultipliedByDoubling(Expr("57"), Expr("179"), 1).expr,
                "tried addMultipliedBySquaring(57, 179, 1)"
            )
            assertEquals(
                "(57 + -179)",
                addMultipliedByDoubling(Expr("57"), Expr("179"), -1).expr,
                "tried addMultipliedBySquaring(57, 179, -1)"
            )
            assertEquals(
                "(57 + (179 + 179))",
                addMultipliedByDoubling(Expr("57"), Expr("179"), 2).expr,
                "tried addMultipliedBySquaring(57, 179, 2)"
            )
            assertEquals(
                "(57 + (-179 + -179))",
                addMultipliedByDoubling(Expr("57"), Expr("179"), -2).expr,
                "tried addMultipliedBySquaring(57, 179, -2)"
            )
            assertEquals(
                "((57 + 179) + (179 + 179))",
                addMultipliedByDoubling(Expr("57"), Expr("179"), 3).expr,
                "tried addMultipliedBySquaring(57, 179, 3)"
            )
            assertEquals(
                "((57 + -179) + (-179 + -179))",
                addMultipliedByDoubling(Expr("57"), Expr("179"), -3).expr,
                "tried addMultipliedBySquaring(57, 179, -3)"
            )
            assertEquals(
                "(57 + ((179 + 179) + (179 + 179)))",
                addMultipliedByDoubling(Expr("57"), Expr("179"), 4).expr,
                "tried addMultipliedBySquaring(57, 179, 4)"
            )
            assertEquals(
                "(57 + ((-179 + -179) + (-179 + -179)))",
                addMultipliedByDoubling(Expr("57"), Expr("179"), -4).expr,
                "tried addMultipliedBySquaring(57, 179, -4)"
            )
            assertEquals(
                "((57 + 179) + ((179 + 179) + (179 + 179)))",
                addMultipliedByDoubling(Expr("57"), Expr("179"), 5).expr,
                "tried addMultipliedBySquaring(57, 179, 5)"
            )
            assertEquals(
                "((57 + -179) + ((-179 + -179) + (-179 + -179)))",
                addMultipliedByDoubling(Expr("57"), Expr("179"), -5).expr,
                "tried addMultipliedBySquaring(57, 179, -5)"
            )
            assertEquals(
                "((57 + (179 + 179)) + ((179 + 179) + (179 + 179)))",
                addMultipliedByDoubling(Expr("57"), Expr("179"), 6).expr,
                "tried addMultipliedBySquaring(57, 179, 6)"
            )
            assertEquals(
                "((57 + (-179 + -179)) + ((-179 + -179) + (-179 + -179)))",
                addMultipliedByDoubling(Expr("57"), Expr("179"), -6).expr,
                "tried addMultipliedBySquaring(57, 179, -6)"
            )
            assertEquals(
                "(((57 + 179) + (179 + 179)) + ((179 + 179) + (179 + 179)))",
                addMultipliedByDoubling(Expr("57"), Expr("179"), 7).expr,
                "tried addMultipliedBySquaring(57, 179, 7)"
            )
            assertEquals(
                "(((57 + -179) + (-179 + -179)) + ((-179 + -179) + (-179 + -179)))",
                addMultipliedByDoubling(Expr("57"), Expr("179"), -7).expr,
                "tried addMultipliedBySquaring(57, 179, -7)"
            )
            assertEquals(
                "(57 + (((179 + 179) + (179 + 179)) + ((179 + 179) + (179 + 179))))",
                addMultipliedByDoubling(Expr("57"), Expr("179"), 8).expr,
                "tried addMultipliedBySquaring(57, 179, 8)"
            )
            assertEquals(
                "(57 + (((-179 + -179) + (-179 + -179)) + ((-179 + -179) + (-179 + -179))))",
                addMultipliedByDoubling(Expr("57"), Expr("179"), -8).expr,
                "tried addMultipliedBySquaring(57, 179, -8)"
            )
        }
    }
    @Test
    fun test_multiplyBySquaring_for_Int() {
        ExprRing {
            assertEquals(
                "0",
                multiplyByDoubling(Expr("57"), 0).expr,
                "tried multiplyBySquaring(57, 0)"
            )
            assertEquals(
                "57",
                multiplyByDoubling(Expr("57"), 1).expr,
                "tried multiplyBySquaring(57, 1)"
            )
            assertEquals(
                "-57",
                multiplyByDoubling(Expr("57"), -1).expr,
                "tried multiplyBySquaring(57, -1)"
            )
            assertEquals(
                "(57 + 57)",
                multiplyByDoubling(Expr("57"), 2).expr,
                "tried multiplyBySquaring(57, 2)"
            )
            assertEquals(
                "(-57 + -57)",
                multiplyByDoubling(Expr("57"), -2).expr,
                "tried multiplyBySquaring(57, -2)"
            )
            assertEquals(
                "(57 + (57 + 57))",
                multiplyByDoubling(Expr("57"), 3).expr,
                "tried multiplyBySquaring(57, 3)"
            )
            assertEquals(
                "(-57 + (-57 + -57))",
                multiplyByDoubling(Expr("57"), -3).expr,
                "tried multiplyBySquaring(57, -3)"
            )
            assertEquals(
                "((57 + 57) + (57 + 57))",
                multiplyByDoubling(Expr("57"), 4).expr,
                "tried multiplyBySquaring(57, 4)"
            )
            assertEquals(
                "((-57 + -57) + (-57 + -57))",
                multiplyByDoubling(Expr("57"), -4).expr,
                "tried multiplyBySquaring(57, -4)"
            )
            assertEquals(
                "(57 + ((57 + 57) + (57 + 57)))",
                multiplyByDoubling(Expr("57"), 5).expr,
                "tried multiplyBySquaring(57, 5)"
            )
            assertEquals(
                "(-57 + ((-57 + -57) + (-57 + -57)))",
                multiplyByDoubling(Expr("57"), -5).expr,
                "tried multiplyBySquaring(57, -5)"
            )
            assertEquals(
                "((57 + 57) + ((57 + 57) + (57 + 57)))",
                multiplyByDoubling(Expr("57"), 6).expr,
                "tried multiplyBySquaring(57, 6)"
            )
            assertEquals(
                "((-57 + -57) + ((-57 + -57) + (-57 + -57)))",
                multiplyByDoubling(Expr("57"), -6).expr,
                "tried multiplyBySquaring(57, -6)"
            )
            assertEquals(
                "((57 + (57 + 57)) + ((57 + 57) + (57 + 57)))",
                multiplyByDoubling(Expr("57"), 7).expr,
                "tried multiplyBySquaring(57, 7)"
            )
            assertEquals(
                "((-57 + (-57 + -57)) + ((-57 + -57) + (-57 + -57)))",
                multiplyByDoubling(Expr("57"), -7).expr,
                "tried multiplyBySquaring(57, -7)"
            )
            assertEquals(
                "(((57 + 57) + (57 + 57)) + ((57 + 57) + (57 + 57)))",
                multiplyByDoubling(Expr("57"), 8).expr,
                "tried multiplyBySquaring(57, 8)"
            )
            assertEquals(
                "(((-57 + -57) + (-57 + -57)) + ((-57 + -57) + (-57 + -57)))",
                multiplyByDoubling(Expr("57"), -8).expr,
                "tried multiplyBySquaring(57, -8)"
            )
        }
    }
    @Test
    fun test_multiplyExponentiationBySquaring_for_UInt() {
        ExprRing {
            assertEquals(
                "57",
                multiplyExponentiatedBySquaring(Expr("57"), Expr("179"), 0u).expr,
                "tried multiplyExponentiationBySquaring(57, 179, 0u)"
            )
            assertEquals(
                "(57 * 179)",
                multiplyExponentiatedBySquaring(Expr("57"), Expr("179"), 1u).expr,
                "tried multiplyExponentiationBySquaring(57, 179, 1u)"
            )
            assertEquals(
                "(57 * (179 * 179))",
                multiplyExponentiatedBySquaring(Expr("57"), Expr("179"), 2u).expr,
                "tried multiplyExponentiationBySquaring(57, 179, 2u)"
            )
            assertEquals(
                "((57 * 179) * (179 * 179))",
                multiplyExponentiatedBySquaring(Expr("57"), Expr("179"), 3u).expr,
                "tried multiplyExponentiationBySquaring(57, 179, 3u)"
            )
            assertEquals(
                "(57 * ((179 * 179) * (179 * 179)))",
                multiplyExponentiatedBySquaring(Expr("57"), Expr("179"), 4u).expr,
                "tried multiplyExponentiationBySquaring(57, 179, 4u)"
            )
            assertEquals(
                "((57 * 179) * ((179 * 179) * (179 * 179)))",
                multiplyExponentiatedBySquaring(Expr("57"), Expr("179"), 5u).expr,
                "tried multiplyExponentiationBySquaring(57, 179, 5u)"
            )
            assertEquals(
                "((57 * (179 * 179)) * ((179 * 179) * (179 * 179)))",
                multiplyExponentiatedBySquaring(Expr("57"), Expr("179"), 6u).expr,
                "tried multiplyExponentiationBySquaring(57, 179, 6u)"
            )
            assertEquals(
                "(((57 * 179) * (179 * 179)) * ((179 * 179) * (179 * 179)))",
                multiplyExponentiatedBySquaring(Expr("57"), Expr("179"), 7u).expr,
                "tried multiplyExponentiationBySquaring(57, 179, 7u)"
            )
            assertEquals(
                "(57 * (((179 * 179) * (179 * 179)) * ((179 * 179) * (179 * 179))))",
                multiplyExponentiatedBySquaring(Expr("57"), Expr("179"), 8u).expr,
                "tried multiplyExponentiationBySquaring(57, 179, 8u)"
            )
        }
    }
    @Test
    fun test_exponentiationBySquaring_for_UInt() {
        ExprRing {
            assertEquals(
                "0",
                exponentiateBySquaring(Expr("57"), 0u).expr,
                "tried exponentiationBySquaring(57, 0u)"
            )
            assertEquals(
                "57",
                exponentiateBySquaring(Expr("57"), 1u).expr,
                "tried exponentiationBySquaring(57, 1u)"
            )
            assertEquals(
                "(57 * 57)",
                exponentiateBySquaring(Expr("57"), 2u).expr,
                "tried exponentiationBySquaring(57, 2u)"
            )
            assertEquals(
                "(57 * (57 * 57))",
                exponentiateBySquaring(Expr("57"), 3u).expr,
                "tried exponentiationBySquaring(57, 3u)"
            )
            assertEquals(
                "((57 * 57) * (57 * 57))",
                exponentiateBySquaring(Expr("57"), 4u).expr,
                "tried exponentiationBySquaring(57, 4u)"
            )
            assertEquals(
                "(57 * ((57 * 57) * (57 * 57)))",
                exponentiateBySquaring(Expr("57"), 5u).expr,
                "tried exponentiationBySquaring(57, 5u)"
            )
            assertEquals(
                "((57 * 57) * ((57 * 57) * (57 * 57)))",
                exponentiateBySquaring(Expr("57"), 6u).expr,
                "tried exponentiationBySquaring(57, 6u)"
            )
            assertEquals(
                "((57 * (57 * 57)) * ((57 * 57) * (57 * 57)))",
                exponentiateBySquaring(Expr("57"), 7u).expr,
                "tried exponentiationBySquaring(57, 7u)"
            )
            assertEquals(
                "(((57 * 57) * (57 * 57)) * ((57 * 57) * (57 * 57)))",
                exponentiateBySquaring(Expr("57"), 8u).expr,
                "tried exponentiationBySquaring(57, 8u)"
            )
        }
    }
    @Test
    fun test_multiplyExponentiationBySquaring_for_Int() {
        ExprRing {
            assertEquals(
                "57",
                multiplyExponentiatedBySquaring(Expr("57"), Expr("179"), 0).expr,
                "tried multiplyExponentiationBySquaring(57, 179, 0)"
            )
            assertEquals(
                "(57 * 179)",
                multiplyExponentiatedBySquaring(Expr("57"), Expr("179"), 1).expr,
                "tried multiplyExponentiationBySquaring(57, 179, 1)"
            )
            assertEquals(
                "(57 * (1 / 179))",
                multiplyExponentiatedBySquaring(Expr("57"), Expr("179"), -1).expr,
                "tried multiplyExponentiationBySquaring(57, 179, -1)"
            )
            assertEquals(
                "(57 * (179 * 179))",
                multiplyExponentiatedBySquaring(Expr("57"), Expr("179"), 2).expr,
                "tried multiplyExponentiationBySquaring(57, 179, 2)"
            )
            assertEquals(
                "(57 * ((1 / 179) * (1 / 179)))",
                multiplyExponentiatedBySquaring(Expr("57"), Expr("179"), -2).expr,
                "tried multiplyExponentiationBySquaring(57, 179, -2)"
            )
            assertEquals(
                "((57 * 179) * (179 * 179))",
                multiplyExponentiatedBySquaring(Expr("57"), Expr("179"), 3).expr,
                "tried multiplyExponentiationBySquaring(57, 179, 3)"
            )
            assertEquals(
                "((57 * (1 / 179)) * ((1 / 179) * (1 / 179)))",
                multiplyExponentiatedBySquaring(Expr("57"), Expr("179"), -3).expr,
                "tried multiplyExponentiationBySquaring(57, 179, -3)"
            )
            assertEquals(
                "(57 * ((179 * 179) * (179 * 179)))",
                multiplyExponentiatedBySquaring(Expr("57"), Expr("179"), 4).expr,
                "tried multiplyExponentiationBySquaring(57, 179, 4)"
            )
            assertEquals(
                "(57 * (((1 / 179) * (1 / 179)) * ((1 / 179) * (1 / 179))))",
                multiplyExponentiatedBySquaring(Expr("57"), Expr("179"), -4).expr,
                "tried multiplyExponentiationBySquaring(57, 179, -4)"
            )
            assertEquals(
                "((57 * 179) * ((179 * 179) * (179 * 179)))",
                multiplyExponentiatedBySquaring(Expr("57"), Expr("179"), 5).expr,
                "tried multiplyExponentiationBySquaring(57, 179, 5)"
            )
            assertEquals(
                "((57 * (1 / 179)) * (((1 / 179) * (1 / 179)) * ((1 / 179) * (1 / 179))))",
                multiplyExponentiatedBySquaring(Expr("57"), Expr("179"), -5).expr,
                "tried multiplyExponentiationBySquaring(57, 179, -5)"
            )
            assertEquals(
                "((57 * (179 * 179)) * ((179 * 179) * (179 * 179)))",
                multiplyExponentiatedBySquaring(Expr("57"), Expr("179"), 6).expr,
                "tried multiplyExponentiationBySquaring(57, 179, 6)"
            )
            assertEquals(
                "((57 * ((1 / 179) * (1 / 179))) * (((1 / 179) * (1 / 179)) * ((1 / 179) * (1 / 179))))",
                multiplyExponentiatedBySquaring(Expr("57"), Expr("179"), -6).expr,
                "tried multiplyExponentiationBySquaring(57, 179, -6)"
            )
            assertEquals(
                "(((57 * 179) * (179 * 179)) * ((179 * 179) * (179 * 179)))",
                multiplyExponentiatedBySquaring(Expr("57"), Expr("179"), 7).expr,
                "tried multiplyExponentiationBySquaring(57, 179, 7)"
            )
            assertEquals(
                "(((57 * (1 / 179)) * ((1 / 179) * (1 / 179))) * (((1 / 179) * (1 / 179)) * ((1 / 179) * (1 / 179))))",
                multiplyExponentiatedBySquaring(Expr("57"), Expr("179"), -7).expr,
                "tried multiplyExponentiationBySquaring(57, 179, -7)"
            )
            assertEquals(
                "(57 * (((179 * 179) * (179 * 179)) * ((179 * 179) * (179 * 179))))",
                multiplyExponentiatedBySquaring(Expr("57"), Expr("179"), 8).expr,
                "tried multiplyExponentiationBySquaring(57, 179, 8)"
            )
            assertEquals(
                "(57 * ((((1 / 179) * (1 / 179)) * ((1 / 179) * (1 / 179))) * (((1 / 179) * (1 / 179)) * ((1 / 179) * (1 / 179)))))",
                multiplyExponentiatedBySquaring(Expr("57"), Expr("179"), -8).expr,
                "tried multiplyExponentiationBySquaring(57, 179, -8)"
            )
        }
    }
    @Test
    fun test_exponentiationBySquaring_for_Int() {
        ExprRing {
            assertEquals(
                "0",
                exponentiateBySquaring(Expr("57"), 0).expr,
                "tried exponentiationBySquaring(57, 0)"
            )
            assertEquals(
                "57",
                exponentiateBySquaring(Expr("57"), 1).expr,
                "tried exponentiationBySquaring(57, 1)"
            )
            assertEquals(
                "(1 / 57)",
                exponentiateBySquaring(Expr("57"), -1).expr,
                "tried exponentiationBySquaring(57, -1)"
            )
            assertEquals(
                "(57 * 57)",
                exponentiateBySquaring(Expr("57"), 2).expr,
                "tried exponentiationBySquaring(57, 2)"
            )
            assertEquals(
                "((1 / 57) * (1 / 57))",
                exponentiateBySquaring(Expr("57"), -2).expr,
                "tried exponentiationBySquaring(57, -2)"
            )
            assertEquals(
                "(57 * (57 * 57))",
                exponentiateBySquaring(Expr("57"), 3).expr,
                "tried exponentiationBySquaring(57, 3)"
            )
            assertEquals(
                "((1 / 57) * ((1 / 57) * (1 / 57)))",
                exponentiateBySquaring(Expr("57"), -3).expr,
                "tried exponentiationBySquaring(57, -3)"
            )
            assertEquals(
                "((57 * 57) * (57 * 57))",
                exponentiateBySquaring(Expr("57"), 4).expr,
                "tried exponentiationBySquaring(57, 4)"
            )
            assertEquals(
                "(((1 / 57) * (1 / 57)) * ((1 / 57) * (1 / 57)))",
                exponentiateBySquaring(Expr("57"), -4).expr,
                "tried exponentiationBySquaring(57, -4)"
            )
            assertEquals(
                "(57 * ((57 * 57) * (57 * 57)))",
                exponentiateBySquaring(Expr("57"), 5).expr,
                "tried exponentiationBySquaring(57, 5)"
            )
            assertEquals(
                "((1 / 57) * (((1 / 57) * (1 / 57)) * ((1 / 57) * (1 / 57))))",
                exponentiateBySquaring(Expr("57"), -5).expr,
                "tried exponentiationBySquaring(57, -5)"
            )
            assertEquals(
                "((57 * 57) * ((57 * 57) * (57 * 57)))",
                exponentiateBySquaring(Expr("57"), 6).expr,
                "tried exponentiationBySquaring(57, 6)"
            )
            assertEquals(
                "(((1 / 57) * (1 / 57)) * (((1 / 57) * (1 / 57)) * ((1 / 57) * (1 / 57))))",
                exponentiateBySquaring(Expr("57"), -6).expr,
                "tried exponentiationBySquaring(57, -6)"
            )
            assertEquals(
                "((57 * (57 * 57)) * ((57 * 57) * (57 * 57)))",
                exponentiateBySquaring(Expr("57"), 7).expr,
                "tried exponentiationBySquaring(57, 7)"
            )
            assertEquals(
                "(((1 / 57) * ((1 / 57) * (1 / 57))) * (((1 / 57) * (1 / 57)) * ((1 / 57) * (1 / 57))))",
                exponentiateBySquaring(Expr("57"), -7).expr,
                "tried exponentiationBySquaring(57, -7)"
            )
            assertEquals(
                "(((57 * 57) * (57 * 57)) * ((57 * 57) * (57 * 57)))",
                exponentiateBySquaring(Expr("57"), 8).expr,
                "tried exponentiationBySquaring(57, 8)"
            )
            assertEquals(
                "((((1 / 57) * (1 / 57)) * ((1 / 57) * (1 / 57))) * (((1 / 57) * (1 / 57)) * ((1 / 57) * (1 / 57))))",
                exponentiateBySquaring(Expr("57"), -8).expr,
                "tried exponentiationBySquaring(57, -8)"
            )
        }
    }
}