/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.wasm

import space.kscience.kmath.estree.compileWith
import space.kscience.kmath.expressions.Expression
import space.kscience.kmath.expressions.MST
import space.kscience.kmath.expressions.Symbol
import space.kscience.kmath.expressions.invoke
import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.operations.IntRing
import space.kscience.kmath.wasm.internal.DoubleWasmBuilder
import space.kscience.kmath.wasm.internal.IntWasmBuilder

/**
 * Compiles an [MST] to WASM in the context of reals.
 *
 * @author Iaroslav Postovalov
 */
@UnstableKMathAPI
public fun DoubleField.expression(mst: MST): Expression<Double> =
    DoubleWasmBuilder(mst).instance

/**
 * Compiles an [MST] to WASM in the context of integers.
 *
 * @author Iaroslav Postovalov
 */
@UnstableKMathAPI
public fun IntRing.expression(mst: MST): Expression<Int> =
    IntWasmBuilder(mst).instance

/**
 * Create a compiled expression with given [MST] and given [algebra].
 *
 * @author Iaroslav Postovalov
 */
@UnstableKMathAPI
public fun MST.compileToExpression(algebra: IntRing): Expression<Int> = compileWith(algebra)


/**
 * Compile given MST to expression and evaluate it against [arguments].
 *
 * @author Iaroslav Postovalov
 */
@UnstableKMathAPI
public fun MST.compile(algebra: IntRing, arguments: Map<Symbol, Int>): Int =
    compileToExpression(algebra).invoke(arguments)


/**
 * Compile given MST to expression and evaluate it against [arguments].
 *
 * @author Iaroslav Postovalov
 */
@UnstableKMathAPI
public fun MST.compile(algebra: IntRing, vararg arguments: Pair<Symbol, Int>): Int =
    compileToExpression(algebra)(*arguments)

/**
 * Create a compiled expression with given [MST] and given [algebra].
 *
 * @author Iaroslav Postovalov
 */
@UnstableKMathAPI
public fun MST.compileToExpression(algebra: DoubleField): Expression<Double> = compileWith(algebra)


/**
 * Compile given MST to expression and evaluate it against [arguments].
 *
 * @author Iaroslav Postovalov
 */
@UnstableKMathAPI
public fun MST.compile(algebra: DoubleField, arguments: Map<Symbol, Double>): Double =
    compileToExpression(algebra).invoke(arguments)


/**
 * Compile given MST to expression and evaluate it against [arguments].
 *
 * @author Iaroslav Postovalov
 */
@UnstableKMathAPI
public fun MST.compile(algebra: DoubleField, vararg arguments: Pair<Symbol, Double>): Double =
    compileToExpression(algebra).invoke(*arguments)
