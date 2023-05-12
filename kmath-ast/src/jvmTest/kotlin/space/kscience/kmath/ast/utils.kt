/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.ast

import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.expressions.Expression
import space.kscience.kmath.expressions.MST
import space.kscience.kmath.expressions.Symbol
import space.kscience.kmath.operations.Algebra
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.operations.IntRing
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import space.kscience.kmath.asm.compile as asmCompile
import space.kscience.kmath.asm.compileToExpression as asmCompileToExpression

private object GenericAsmCompilerTestContext : CompilerTestContext {
    override fun MST.compileToExpression(algebra: IntRing): Expression<Int> =
        asmCompileToExpression(algebra as Algebra<Int>)

    override fun MST.compile(algebra: IntRing, arguments: Map<Symbol, Int>): Int =
        asmCompile(algebra as Algebra<Int>, arguments)

    override fun MST.compileToExpression(algebra: DoubleField): Expression<Double> =
        asmCompileToExpression(algebra as Algebra<Double>)

    override fun MST.compile(algebra: DoubleField, arguments: Map<Symbol, Double>): Double =
        asmCompile(algebra as Algebra<Double>, arguments)
}

@OptIn(UnstableKMathAPI::class)
private object PrimitiveAsmCompilerTestContext : CompilerTestContext {
    override fun MST.compileToExpression(algebra: IntRing): Expression<Int> = asmCompileToExpression(algebra)
    override fun MST.compile(algebra: IntRing, arguments: Map<Symbol, Int>): Int = asmCompile(algebra, arguments)
    override fun MST.compileToExpression(algebra: DoubleField): Expression<Double> = asmCompileToExpression(algebra)

    override fun MST.compile(algebra: DoubleField, arguments: Map<Symbol, Double>): Double =
        asmCompile(algebra, arguments)
}


internal actual inline fun runCompilerTest(action: CompilerTestContext.() -> Unit) {
    contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }
    action(GenericAsmCompilerTestContext)
    action(PrimitiveAsmCompilerTestContext)
}
