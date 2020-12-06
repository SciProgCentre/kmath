package kscience.kmath.wasm

import kscience.kmath.ast.MST
import kscience.kmath.ast.MstExpression
import kscience.kmath.expressions.Expression
import kscience.kmath.operations.IntRing
import kscience.kmath.operations.RealField
import kscience.kmath.wasm.internal.IntWasmBuilder
import kscience.kmath.wasm.internal.RealWasmBuilder

/**
 * Compiles an [MST] to WASM in the context of reals.
 *
 * @author Iaroslav Postovalov.
 */
public fun RealField.expression(mst: MST): Expression<Double> =
    RealWasmBuilder(mst).instance

/**
 * Compiles an [MST] to WASM in the context of integers.
 *
 * @author Iaroslav Postovalov.
 */
public fun IntRing.expression(mst: MST): Expression<Int> =
    IntWasmBuilder(mst).instance

/**
 * Optimizes performance of an [MstExpression] using WASM codegen in the context of reals.
 *
 * @author Iaroslav Postovalov.
 */
public fun MstExpression<Double, RealField>.compile(): Expression<Double> =
    RealWasmBuilder(mst).instance

/**
 * Optimizes performance of an [MstExpression] using WASM codegen in the context of integers.
 *
 * @author Iaroslav Postovalov.
 */
public fun MstExpression<Int, IntRing>.compile(): Expression<Int> =
    IntWasmBuilder(mst).instance
