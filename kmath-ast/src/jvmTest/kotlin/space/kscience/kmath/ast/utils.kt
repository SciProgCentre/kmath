/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package space.kscience.kmath.ast

import space.kscience.kmath.expressions.Expression
import space.kscience.kmath.expressions.MST
import space.kscience.kmath.expressions.Symbol
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.operations.IntRing
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import space.kscience.kmath.asm.compile as asmCompile
import space.kscience.kmath.asm.compileToExpression as asmCompileToExpression

private object AsmCompilerTestContext : CompilerTestContext {
    override fun MST.compileToExpression(algebra: IntRing): Expression<Int> = asmCompileToExpression(algebra)
    override fun MST.compile(algebra: IntRing, arguments: Map<Symbol, Int>): Int = asmCompile(algebra, arguments)
    override fun MST.compileToExpression(algebra: DoubleField): Expression<Double> = asmCompileToExpression(algebra)

    override fun MST.compile(algebra: DoubleField, arguments: Map<Symbol, Double>): Double =
        asmCompile(algebra, arguments)
}

internal actual inline fun runCompilerTest(action: CompilerTestContext.() -> Unit) {
    contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }
    action(AsmCompilerTestContext)
}
