/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.wasm.internal

import space.kscience.kmath.expressions.*
import space.kscience.kmath.expressions.MST.*
import space.kscience.kmath.internal.binaryen.*
import space.kscience.kmath.internal.webassembly.Instance
import space.kscience.kmath.misc.UnstableKMathAPI
import space.kscience.kmath.operations.*
import space.kscience.kmath.internal.binaryen.Module as BinaryenModule
import space.kscience.kmath.internal.webassembly.Module as WasmModule

private val spreader = eval("(obj, args) => obj(...args)")

@Suppress("UnsafeCastFromDynamic")
internal sealed class WasmBuilder<T : Number, out E : Expression<T>>(
    protected val binaryenType: Type,
    protected val algebra: Algebra<T>,
    protected val target: MST,
) {
    protected val keys: MutableList<Symbol> = mutableListOf()
    protected lateinit var ctx: BinaryenModule

    abstract val instance: E

    protected val executable = run {
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

        Instance(c, js("{}")).exports.executable
    }

    protected open fun visitSymbol(node: Symbol): ExpressionRef {
        algebra.bindSymbolOrNull(node)?.let { return visitNumeric(Numeric(it)) }

        var idx = keys.indexOf(node)

        if (idx == -1) {
            keys += node
            idx = keys.lastIndex
        }

        return ctx.local.get(idx, binaryenType)
    }

    protected abstract fun visitNumeric(node: Numeric): ExpressionRef

    protected open fun visitUnary(node: Unary): ExpressionRef =
        error("Unary operation ${node.operation} not defined in $this")

    protected open fun visitBinary(mst: Binary): ExpressionRef =
        error("Binary operation ${mst.operation} not defined in $this")

    protected open fun createModule(): BinaryenModule = js("new \$module\$binaryen.Module()")

    protected fun visit(node: MST): ExpressionRef = when (node) {
        is Symbol -> visitSymbol(node)
        is Numeric -> visitNumeric(node)

        is Unary -> when {
            algebra is NumericAlgebra && node.value is Numeric -> visitNumeric(
                Numeric(algebra.unaryOperationFunction(node.operation)(algebra.number((node.value as Numeric).value)))
            )

            else -> visitUnary(node)
        }

        is Binary -> when {
            algebra is NumericAlgebra && node.left is Numeric && node.right is Numeric -> visitNumeric(
                Numeric(
                    algebra.binaryOperationFunction(node.operation)
                        .invoke(
                            algebra.number((node.left as Numeric).value),
                            algebra.number((node.right as Numeric).value)
                        )
                )
            )

            else -> visitBinary(node)
        }
    }
}

@UnstableKMathAPI
internal class DoubleWasmBuilder(target: MST) : WasmBuilder<Double, DoubleExpression>(f64, DoubleField, target) {
    override val instance by lazy {
        object : DoubleExpression {
            override val indexer = SimpleSymbolIndexer(keys)

            override fun invoke(arguments: DoubleArray) = spreader(executable, arguments).unsafeCast<Double>()
        }
    }

    override fun createModule() = readBinary(f64StandardFunctions)

    override fun visitNumeric(node: Numeric) = ctx.f64.const(node.value.toDouble())

    override fun visitUnary(node: Unary): ExpressionRef = when (node.operation) {
        GroupOps.MINUS_OPERATION -> ctx.f64.neg(visit(node.value))
        GroupOps.PLUS_OPERATION -> visit(node.value)
        PowerOperations.SQRT_OPERATION -> ctx.f64.sqrt(visit(node.value))
        TrigonometricOperations.SIN_OPERATION -> ctx.call("sin", arrayOf(visit(node.value)), f64)
        TrigonometricOperations.COS_OPERATION -> ctx.call("cos", arrayOf(visit(node.value)), f64)
        TrigonometricOperations.TAN_OPERATION -> ctx.call("tan", arrayOf(visit(node.value)), f64)
        TrigonometricOperations.ASIN_OPERATION -> ctx.call("asin", arrayOf(visit(node.value)), f64)
        TrigonometricOperations.ACOS_OPERATION -> ctx.call("acos", arrayOf(visit(node.value)), f64)
        TrigonometricOperations.ATAN_OPERATION -> ctx.call("atan", arrayOf(visit(node.value)), f64)
        ExponentialOperations.SINH_OPERATION -> ctx.call("sinh", arrayOf(visit(node.value)), f64)
        ExponentialOperations.COSH_OPERATION -> ctx.call("cosh", arrayOf(visit(node.value)), f64)
        ExponentialOperations.TANH_OPERATION -> ctx.call("tanh", arrayOf(visit(node.value)), f64)
        ExponentialOperations.ASINH_OPERATION -> ctx.call("asinh", arrayOf(visit(node.value)), f64)
        ExponentialOperations.ACOSH_OPERATION -> ctx.call("acosh", arrayOf(visit(node.value)), f64)
        ExponentialOperations.ATANH_OPERATION -> ctx.call("atanh", arrayOf(visit(node.value)), f64)
        ExponentialOperations.EXP_OPERATION -> ctx.call("exp", arrayOf(visit(node.value)), f64)
        ExponentialOperations.LN_OPERATION -> ctx.call("log", arrayOf(visit(node.value)), f64)
        else -> super.visitUnary(node)
    }

    override fun visitBinary(mst: Binary): ExpressionRef = when (mst.operation) {
        GroupOps.PLUS_OPERATION -> ctx.f64.add(visit(mst.left), visit(mst.right))
        GroupOps.MINUS_OPERATION -> ctx.f64.sub(visit(mst.left), visit(mst.right))
        RingOps.TIMES_OPERATION -> ctx.f64.mul(visit(mst.left), visit(mst.right))
        FieldOps.DIV_OPERATION -> ctx.f64.div(visit(mst.left), visit(mst.right))
        PowerOperations.POW_OPERATION -> ctx.call("pow", arrayOf(visit(mst.left), visit(mst.right)), f64)
        else -> super.visitBinary(mst)
    }
}

@UnstableKMathAPI
internal class IntWasmBuilder(target: MST) : WasmBuilder<Int, IntExpression>(i32, IntRing, target) {
    override val instance by lazy {
        object : IntExpression {
            override val indexer = SimpleSymbolIndexer(keys)

            override fun invoke(arguments: IntArray) = spreader(executable, arguments).unsafeCast<Int>()
        }
    }

    override fun visitNumeric(node: Numeric) = ctx.i32.const(node.value.toInt())

    override fun visitUnary(node: Unary): ExpressionRef = when (node.operation) {
        GroupOps.MINUS_OPERATION -> ctx.i32.sub(ctx.i32.const(0), visit(node.value))
        GroupOps.PLUS_OPERATION -> visit(node.value)
        else -> super.visitUnary(node)
    }

    override fun visitBinary(mst: Binary): ExpressionRef = when (mst.operation) {
        GroupOps.PLUS_OPERATION -> ctx.i32.add(visit(mst.left), visit(mst.right))
        GroupOps.MINUS_OPERATION -> ctx.i32.sub(visit(mst.left), visit(mst.right))
        RingOps.TIMES_OPERATION -> ctx.i32.mul(visit(mst.left), visit(mst.right))
        else -> super.visitBinary(mst)
    }
}
