/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:OptIn(UnstableKMathAPI::class)

package space.kscience.kmath.ast

import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.expressions.Expression
import space.kscience.kmath.expressions.MST
import space.kscience.kmath.expressions.Symbol
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.operations.IntRing
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import space.kscience.kmath.estree.compile as estreeCompile
import space.kscience.kmath.estree.compileToExpression as estreeCompileToExpression
import space.kscience.kmath.wasm.compile as wasmCompile
import space.kscience.kmath.wasm.compileToExpression as wasmCompileToExpression

private object WasmCompilerTestContext : CompilerTestContext {
    override fun MST.compileToExpression(algebra: IntRing): Expression<Int> = wasmCompileToExpression(algebra)
    override fun MST.compile(algebra: IntRing, arguments: Map<Symbol, Int>): Int = wasmCompile(algebra, arguments)
    override fun MST.compileToExpression(algebra: DoubleField): Expression<Double> = wasmCompileToExpression(algebra)

    override fun MST.compile(algebra: DoubleField, arguments: Map<Symbol, Double>): Double =
        wasmCompile(algebra, arguments)
}

private object ESTreeCompilerTestContext : CompilerTestContext {
    override fun MST.compileToExpression(algebra: IntRing): Expression<Int> = estreeCompileToExpression(algebra)
    override fun MST.compile(algebra: IntRing, arguments: Map<Symbol, Int>): Int = estreeCompile(algebra, arguments)
    override fun MST.compileToExpression(algebra: DoubleField): Expression<Double> = estreeCompileToExpression(algebra)

    override fun MST.compile(algebra: DoubleField, arguments: Map<Symbol, Double>): Double =
        estreeCompile(algebra, arguments)
}

internal actual inline fun runCompilerTest(action: CompilerTestContext.() -> Unit) {
    contract { callsInPlace(action, InvocationKind.AT_LEAST_ONCE) }
    action(WasmCompilerTestContext)
    action(ESTreeCompilerTestContext)
}
