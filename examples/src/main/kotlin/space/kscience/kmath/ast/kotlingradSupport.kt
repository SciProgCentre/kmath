/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.ast

import space.kscience.kmath.expressions.derivative
import space.kscience.kmath.expressions.invoke
import space.kscience.kmath.expressions.Symbol.Companion.x
import space.kscience.kmath.expressions.toExpression
import space.kscience.kmath.kotlingrad.toKotlingradExpression
import space.kscience.kmath.operations.DoubleField

/**
 * In this example, x^2-4*x-44 function is differentiated with Kotlin∇, and the autodiff result is compared with
 * valid derivative in a certain point.
 */
fun main() {
    val actualDerivative = "x^2-4*x-44"
        .parseMath()
        .toKotlingradExpression(DoubleField)
        .derivative(x)

    val expectedDerivative = "2*x-4".parseMath().toExpression(DoubleField)
    check(actualDerivative(x to 123.0) == expectedDerivative(x to 123.0))
}
