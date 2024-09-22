/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:OptIn(UnstableKMathAPI::class)

package space.kscience.kmath.ast

import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.expressions.Expression
import space.kscience.kmath.expressions.MST
import space.kscience.kmath.expressions.Symbol
import space.kscience.kmath.operations.Float64Field
import space.kscience.kmath.operations.Int32Ring
import space.kscience.kmath.structures.Float64
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import space.kscience.kmath.estree.compile as estreeCompile
import space.kscience.kmath.estree.compileToExpression as estreeCompileToExpression
import space.kscience.kmath.wasm.compile as wasmCompile
import space.kscience.kmath.wasm.compileToExpression as wasmCompileToExpression

@OptIn(UnstableKMathAPI::class)
private object WasmCompilerTestContext : CompilerTestContext {
    override fun MST.compileToExpression(algebra: Int32Ring): Expression<Int> = wasmCompileToExpression(algebra)
    override fun MST.compile(algebra: Int32Ring, arguments: Map<Symbol, Int>): Int = wasmCompile(algebra, arguments)
    override fun MST.compileToExpression(algebra: Float64Field): Expression<Float64> = wasmCompileToExpression(algebra)

    override fun MST.compile(algebra: Float64Field, arguments: Map<Symbol, Double>): Double =
        wasmCompile(algebra, arguments)
}

private object ESTreeCompilerTestContext : CompilerTestContext {
    override fun MST.compileToExpression(algebra: Int32Ring): Expression<Int> = estreeCompileToExpression(algebra)
    override fun MST.compile(algebra: Int32Ring, arguments: Map<Symbol, Int>): Int = estreeCompile(algebra, arguments)
    override fun MST.compileToExpression(algebra: Float64Field): Expression<Float64> = estreeCompileToExpression(algebra)

    override fun MST.compile(algebra: Float64Field, arguments: Map<Symbol, Double>): Double =
        estreeCompile(algebra, arguments)
}

internal actual inline fun runCompilerTest(action: CompilerTestContext.() -> Unit) {
    contract { callsInPlace(action, InvocationKind.AT_LEAST_ONCE) }
    action(WasmCompilerTestContext)
    action(ESTreeCompilerTestContext)
}
