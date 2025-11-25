/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.ast

import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.expressions.Expression
import space.kscience.kmath.expressions.MST
import space.kscience.kmath.expressions.Symbol
import space.kscience.kmath.operations.Algebra
import space.kscience.kmath.operations.Float64Field
import space.kscience.kmath.operations.Int32Ring
import space.kscience.kmath.structures.Float64
import space.kscience.kmath.asm.compile as asmCompile
import space.kscience.kmath.asm.compileToExpression as asmCompileToExpression

internal object GenericAsmCompilerTestContext : CompilerTestContext {
    override fun MST.compileToExpression(algebra: Int32Ring): Expression<Int> =
        asmCompileToExpression(algebra as Algebra<Int>)

    override fun MST.compile(algebra: Int32Ring, arguments: Map<Symbol, Int>): Int =
        asmCompile(algebra as Algebra<Int>, arguments)

    override fun MST.compileToExpression(algebra: Float64Field): Expression<Float64> =
        asmCompileToExpression(algebra as Algebra<Float64>)

    override fun MST.compile(algebra: Float64Field, arguments: Map<Symbol, Double>): Double =
        asmCompile(algebra as Algebra<Float64>, arguments)
}

@OptIn(UnstableKMathAPI::class)
internal object PrimitiveAsmCompilerTestContext : CompilerTestContext {
    override fun MST.compileToExpression(algebra: Int32Ring): Expression<Int> = asmCompileToExpression(algebra)
    override fun MST.compile(algebra: Int32Ring, arguments: Map<Symbol, Int>): Int = asmCompile(algebra, arguments)
    override fun MST.compileToExpression(algebra: Float64Field): Expression<Float64> = asmCompileToExpression(algebra)

    override fun MST.compile(algebra: Float64Field, arguments: Map<Symbol, Double>): Double =
        asmCompile(algebra, arguments)
}


internal actual inline fun runCompilerTest(action: CompilerTestContext.() -> Unit) {
    action(GenericAsmCompilerTestContext)
    action(PrimitiveAsmCompilerTestContext)
}
