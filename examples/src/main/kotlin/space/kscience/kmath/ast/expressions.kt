/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.ast

import space.kscience.kmath.expressions.MstField
import space.kscience.kmath.expressions.Symbol.Companion.x
import space.kscience.kmath.expressions.interpret
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.operations.invoke

fun main() {
    val expr = MstField {
        x * 2.0 + number(2.0) / x - 16.0
    }

    repeat(10000000) {
        expr.interpret(DoubleField, x to 1.0)
    }
}