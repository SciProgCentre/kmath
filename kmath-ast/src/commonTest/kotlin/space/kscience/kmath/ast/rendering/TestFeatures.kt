/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.ast.rendering

import space.kscience.kmath.ast.rendering.TestUtils.testLatex
import space.kscience.kmath.expressions.MST.Numeric
import kotlin.test.Test

internal class TestFeatures {
    @Test
    fun printSymbolic() = testLatex("x", "x")

    @Test
    fun printNumeric() {
        val num = object : Number() {
            override fun toByte(): Byte = throw UnsupportedOperationException()
            override fun toChar(): Char = throw UnsupportedOperationException()
            override fun toDouble(): Double = throw UnsupportedOperationException()
            override fun toFloat(): Float = throw UnsupportedOperationException()
            override fun toInt(): Int = throw UnsupportedOperationException()
            override fun toLong(): Long = throw UnsupportedOperationException()
            override fun toShort(): Short = throw UnsupportedOperationException()
            override fun toString(): String = "foo"
        }

        testLatex(Numeric(num), "foo")
    }

    @Test
    fun prettyPrintFloats() {
        testLatex(Numeric(Double.NaN), "NaN")
        testLatex(Numeric(Double.POSITIVE_INFINITY), "\\infty")
        testLatex(Numeric(Double.NEGATIVE_INFINITY), "-\\infty")
        testLatex(Numeric(1.0), "1")
        testLatex(Numeric(-1.0), "-1")
        testLatex(Numeric(1.42), "1.42")
        testLatex(Numeric(-1.42), "-1.42")
        testLatex(Numeric(1.1e10), "1.1\\times10^{10}")
        testLatex(Numeric(1.1e-10), "1.1\\times10^{-10}")
        testLatex(Numeric(-1.1e-10), "-1.1\\times10^{-10}")
        testLatex(Numeric(-1.1e10), "-1.1\\times10^{10}")
        testLatex(Numeric(0.001), "0.001")
        testLatex(Numeric(0.0000001), "1\\times10^{-7}")

        testLatex(Numeric(Float.NaN), "NaN")
        testLatex(Numeric(Float.POSITIVE_INFINITY), "\\infty")
        testLatex(Numeric(Float.NEGATIVE_INFINITY), "-\\infty")
        testLatex(Numeric(1.0f), "1")
        testLatex(Numeric(-1.0f), "-1")
        testLatex(Numeric(1.42f), "1.42")
        testLatex(Numeric(-1.42f), "-1.42")
        testLatex(Numeric(1e10f), "1\\times10^{10}")
        testLatex(Numeric(1e-10f), "1\\times10^{-10}")
        testLatex(Numeric(-1e-10f), "-1\\times10^{-10}")
        testLatex(Numeric(-1e10f), "-1\\times10^{10}")
        testLatex(Numeric(0.001f), "0.001")
        testLatex(Numeric(0.0000001f), "1\\times10^{-7}")
    }

    @Test
    fun prettyPrintIntegers() {
        testLatex(Numeric(42), "42")
        testLatex(Numeric(-42), "-42")
    }

    @Test
    fun prettyPrintPi() {
        testLatex("pi", "\\pi")
    }

    @Test
    fun binaryPlus() = testLatex("2+2", "2+2")

    @Test
    fun binaryMinus() = testLatex("2-2", "2-2")

    @Test
    fun fraction() = testLatex("2/2", "\\frac{2}{2}")

    @Test
    fun binaryOperator() = testLatex("f(x, y)", "\\operatorname{f}\\left(x,y\\right)")

    @Test
    fun unaryOperator() = testLatex("f(x)", "\\operatorname{f}\\,\\left(x\\right)")

    @Test
    fun power() = testLatex("x^y", "x^{y}")

    @Test
    fun squareRoot() = testLatex("sqrt(x)", "\\sqrt{x}")

    @Test
    fun exponential() = testLatex("exp(x)", "e^{x}")

    @Test
    fun multiplication() = testLatex("x*1", "x\\times1")

    @Test
    fun inverseTrigonometric() {
        testLatex("asin(x)", "\\operatorname{arcsin}\\,\\left(x\\right)")
        testLatex("acos(x)", "\\operatorname{arccos}\\,\\left(x\\right)")
        testLatex("atan(x)", "\\operatorname{arctan}\\,\\left(x\\right)")
    }

    @Test
    fun inverseHyperbolic() {
        testLatex("asinh(x)", "\\operatorname{arsinh}\\,\\left(x\\right)")
        testLatex("acosh(x)", "\\operatorname{arcosh}\\,\\left(x\\right)")
        testLatex("atanh(x)", "\\operatorname{artanh}\\,\\left(x\\right)")
    }

//    @Test
//    fun unaryPlus() {
//        testLatex("+1", "+1")
//        testLatex("+1", "++1")
//    }
}
