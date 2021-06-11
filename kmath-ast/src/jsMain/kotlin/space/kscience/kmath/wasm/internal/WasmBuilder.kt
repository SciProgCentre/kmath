/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.wasm.internal

import space.kscience.kmath.expressions.Expression
import space.kscience.kmath.expressions.MST
import space.kscience.kmath.expressions.MST.*
import space.kscience.kmath.expressions.Symbol
import space.kscience.kmath.internal.binaryen.*
import space.kscience.kmath.internal.webassembly.Instance
import space.kscience.kmath.operations.*
import space.kscience.kmath.internal.binaryen.Module as BinaryenModule
import space.kscience.kmath.internal.webassembly.Module as WasmModule

private val spreader = eval("(obj, args) => obj(...args)")

@Suppress("UnsafeCastFromDynamic")
internal sealed class WasmBuilder<T>(
    val binaryenType: Type,
    val algebra: Algebra<T>,
    val target: MST,
) where T : Number {
    val keys: MutableList<Symbol> = mutableListOf()
    lateinit var ctx: BinaryenModule

    open fun visitSymbolic(mst: Symbol): ExpressionRef {
        algebra.bindSymbolOrNull(mst)?.let { return visitNumeric(Numeric(it)) }

        var idx = keys.indexOf(mst)

        if (idx == -1) {
            keys += mst
            idx = keys.lastIndex
        }

        return ctx.local.get(idx, binaryenType)
    }

    abstract fun visitNumeric(mst: Numeric): ExpressionRef

    open fun visitUnary(mst: Unary): ExpressionRef =
        error("Unary operation ${mst.operation} not defined in $this")

    open fun visitBinary(mst: Binary): ExpressionRef =
        error("Binary operation ${mst.operation} not defined in $this")

    open fun createModule(): BinaryenModule = js("new \$module\$binaryen.Module()")

    fun visit(mst: MST): ExpressionRef = when (mst) {
        is Symbol -> visitSymbolic(mst)
        is Numeric -> visitNumeric(mst)

        is Unary -> when {
            algebra is NumericAlgebra && mst.value is Numeric -> visitNumeric(
                Numeric(algebra.unaryOperationFunction(mst.operation)(algebra.number((mst.value as Numeric).value))))

            else -> visitUnary(mst)
        }

        is Binary -> when {
            algebra is NumericAlgebra && mst.left is Numeric && mst.right is Numeric -> visitNumeric(Numeric(
                algebra.binaryOperationFunction(mst.operation)
                    .invoke(algebra.number((mst.left as Numeric).value), algebra.number((mst.right as Numeric).value))
            ))

            else -> visitBinary(mst)
        }
    }

    val instance by lazy {
        val c = WasmModule(with(createModule()) {
            ctx = this
            val expr = visit(target)

            addFunction(
                "executable",
                createType(Array(keys.size) { binaryenType }),
                binaryenType,
                arrayOf(),
                expr
            )

            setOptimizeLevel(3)
            optimizeFunction("executable")
            addFunctionExport("executable", "executable")
            val res = emitBinary()
            dispose()
            res
        })

        val i = Instance(c, js("{}") as Any)
        val symbols = keys
        keys.clear()

        Expression<T> { args ->
            val params = symbols.map(args::getValue).toTypedArray()
            spreader(i.exports.asDynamic().executable, params) as T
        }
    }
}

internal class DoubleWasmBuilder(target: MST) : WasmBuilder<Double>(f64, DoubleField, target) {
    override fun createModule(): BinaryenModule = readBinary(f64StandardFunctions)

    override fun visitNumeric(mst: Numeric): ExpressionRef = ctx.f64.const(mst.value)

    override fun visitUnary(mst: Unary): ExpressionRef = when (mst.operation) {
        GroupOperations.MINUS_OPERATION -> ctx.f64.neg(visit(mst.value))
        GroupOperations.PLUS_OPERATION -> visit(mst.value)
        PowerOperations.SQRT_OPERATION -> ctx.f64.sqrt(visit(mst.value))
        TrigonometricOperations.SIN_OPERATION -> ctx.call("sin", arrayOf(visit(mst.value)), f64)
        TrigonometricOperations.COS_OPERATION -> ctx.call("cos", arrayOf(visit(mst.value)), f64)
        TrigonometricOperations.TAN_OPERATION -> ctx.call("tan", arrayOf(visit(mst.value)), f64)
        TrigonometricOperations.ASIN_OPERATION -> ctx.call("asin", arrayOf(visit(mst.value)), f64)
        TrigonometricOperations.ACOS_OPERATION -> ctx.call("acos", arrayOf(visit(mst.value)), f64)
        TrigonometricOperations.ATAN_OPERATION -> ctx.call("atan", arrayOf(visit(mst.value)), f64)
        ExponentialOperations.SINH_OPERATION -> ctx.call("sinh", arrayOf(visit(mst.value)), f64)
        ExponentialOperations.COSH_OPERATION -> ctx.call("cosh", arrayOf(visit(mst.value)), f64)
        ExponentialOperations.TANH_OPERATION -> ctx.call("tanh", arrayOf(visit(mst.value)), f64)
        ExponentialOperations.ASINH_OPERATION -> ctx.call("asinh", arrayOf(visit(mst.value)), f64)
        ExponentialOperations.ACOSH_OPERATION -> ctx.call("acosh", arrayOf(visit(mst.value)), f64)
        ExponentialOperations.ATANH_OPERATION -> ctx.call("atanh", arrayOf(visit(mst.value)), f64)
        ExponentialOperations.EXP_OPERATION -> ctx.call("exp", arrayOf(visit(mst.value)), f64)
        ExponentialOperations.LN_OPERATION -> ctx.call("log", arrayOf(visit(mst.value)), f64)
        else -> super.visitUnary(mst)
    }

    override fun visitBinary(mst: Binary): ExpressionRef = when (mst.operation) {
        GroupOperations.PLUS_OPERATION -> ctx.f64.add(visit(mst.left), visit(mst.right))
        GroupOperations.MINUS_OPERATION -> ctx.f64.sub(visit(mst.left), visit(mst.right))
        RingOperations.TIMES_OPERATION -> ctx.f64.mul(visit(mst.left), visit(mst.right))
        FieldOperations.DIV_OPERATION -> ctx.f64.div(visit(mst.left), visit(mst.right))
        PowerOperations.POW_OPERATION -> ctx.call("pow", arrayOf(visit(mst.left), visit(mst.right)), f64)
        else -> super.visitBinary(mst)
    }
}

internal class IntWasmBuilder(target: MST) : WasmBuilder<Int>(i32, IntRing, target) {
    override fun visitNumeric(mst: Numeric): ExpressionRef = ctx.i32.const(mst.value)

    override fun visitUnary(mst: Unary): ExpressionRef = when (mst.operation) {
        GroupOperations.MINUS_OPERATION -> ctx.i32.sub(ctx.i32.const(0), visit(mst.value))
        GroupOperations.PLUS_OPERATION -> visit(mst.value)
        else -> super.visitUnary(mst)
    }

    override fun visitBinary(mst: Binary): ExpressionRef = when (mst.operation) {
        GroupOperations.PLUS_OPERATION -> ctx.i32.add(visit(mst.left), visit(mst.right))
        GroupOperations.MINUS_OPERATION -> ctx.i32.sub(visit(mst.left), visit(mst.right))
        RingOperations.TIMES_OPERATION -> ctx.i32.mul(visit(mst.left), visit(mst.right))
        else -> super.visitBinary(mst)
    }
}
