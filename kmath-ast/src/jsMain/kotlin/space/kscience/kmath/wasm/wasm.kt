/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("UNUSED_PARAMETER")

package space.kscience.kmath.wasm

import space.kscience.kmath.estree.compileWith
import space.kscience.kmath.expressions.*
import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.operations.IntRing
import space.kscience.kmath.wasm.internal.DoubleWasmBuilder
import space.kscience.kmath.wasm.internal.IntWasmBuilder

/**
 * Create a compiled expression with given [MST] and given [algebra].
 *
 * @author Iaroslav Postovalov
 */
@UnstableKMathAPI
public fun MST.compileToExpression(algebra: IntRing): IntExpression = IntWasmBuilder(this).instance


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
public fun MST.compileToExpression(algebra: DoubleField): Expression<Double> = DoubleWasmBuilder(this).instance


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
