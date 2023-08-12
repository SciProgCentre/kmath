/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.ast

import space.kscience.kmath.expressions.Expression
import space.kscience.kmath.expressions.MST
import space.kscience.kmath.expressions.Symbol
import space.kscience.kmath.operations.Float64Field
import space.kscience.kmath.operations.Int32Ring

internal interface CompilerTestContext {
    fun MST.compileToExpression(algebra: Int32Ring): Expression<Int>
    fun MST.compile(algebra: Int32Ring, arguments: Map<Symbol, Int>): Int
    fun MST.compile(algebra: Int32Ring, vararg arguments: Pair<Symbol, Int>): Int = compile(algebra, mapOf(*arguments))
    fun MST.compileToExpression(algebra: Float64Field): Expression<Double>
    fun MST.compile(algebra: Float64Field, arguments: Map<Symbol, Double>): Double

    fun MST.compile(algebra: Float64Field, vararg arguments: Pair<Symbol, Double>): Double =
        compile(algebra, mapOf(*arguments))
}

internal expect inline fun runCompilerTest(action: CompilerTestContext.() -> Unit)
