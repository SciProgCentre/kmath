/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.ast

import space.kscience.kmath.asm.compileToExpression
import space.kscience.kmath.expressions.MstExtendedField
import space.kscience.kmath.expressions.Symbol.Companion.x
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.operations.invoke

fun main() {
    val expr = MstExtendedField {
        x * 2.0 + number(2.0) / x - number(16.0) + asinh(x) / sin(x)
    }.compileToExpression(DoubleField)

    val m = DoubleArray(expr.indexer.symbols.size)
    val xIdx = expr.indexer.indexOf(x)

    repeat(10000000) {
        m[xIdx] = 1.0
        expr(m)
    }
}
