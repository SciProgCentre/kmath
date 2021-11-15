/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package space.kscience.kmath.ast

import space.kscience.kmath.asm.compileToExpression
import space.kscience.kmath.expressions.MstField
import space.kscience.kmath.expressions.Symbol
import space.kscience.kmath.expressions.Symbol.Companion.x
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.operations.invoke

fun main() {
    val expr = MstField {
        x * 2.0 + number(2.0) / x - 16.0
    }.compileToExpression(DoubleField)

    val m = HashMap<Symbol, Double>()

    repeat(10000000) {
        m[x] = 1.0
        expr(m)
    }
}
