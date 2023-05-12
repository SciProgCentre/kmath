/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.wasm.internal

import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.ast.TypedMst
import space.kscience.kmath.expressions.*
import space.kscience.kmath.internal.binaryen.*
import space.kscience.kmath.internal.webassembly.Instance
import space.kscience.kmath.operations.*
import space.kscience.kmath.internal.binaryen.Module as BinaryenModule
import space.kscience.kmath.internal.webassembly.Module as WasmModule

private val spreader = eval("(obj, args) => obj(...args)")

@OptIn(UnstableKMathAPI::class)
@Suppress("UnsafeCastFromDynamic")
internal sealed class WasmBuilder<T : Number, out E : Expression<T>>(
    protected val binaryenType: Type,
    protected val algebra: Algebra<T>,
    protected val target: TypedMst<T>,
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

    protected abstract fun visitNumber(number: Number): ExpressionRef

    protected open fun visitVariable(node: TypedMst.Variable<T>): ExpressionRef {
        var idx = keys.indexOf(node.symbol)

        if (idx == -1) {
            keys += node.symbol
            idx = keys.lastIndex
        }

        return ctx.local.get(idx, binaryenType)
    }

    protected open fun visitUnary(node: TypedMst.Unary<T>): ExpressionRef =
        error("Unary operation ${node.operation} not defined in $this")

    protected open fun visitBinary(mst: TypedMst.Binary<T>): ExpressionRef =
        error("Binary operation ${mst.operation} not defined in $this")

    protected open fun createModule(): BinaryenModule = space.kscience.kmath.internal.binaryen.Module()

    protected fun visit(node: TypedMst<T>): ExpressionRef = when (node) {
        is TypedMst.Constant -> visitNumber(
            node.number ?: error("Object constants are not supported by pritimive ASM builder"),
        )

        is TypedMst.Variable -> visitVariable(node)
        is TypedMst.Unary -> visitUnary(node)
        is TypedMst.Binary -> visitBinary(node)
    }
}

@UnstableKMathAPI
internal class DoubleWasmBuilder(target: TypedMst<Double>) :
    WasmBuilder<Double, DoubleExpression>(f64, DoubleField, target) {
    override val instance by lazy {
        object : DoubleExpression {
            override val indexer = SimpleSymbolIndexer(keys)

            override fun invoke(arguments: DoubleArray) = spreader(executable, arguments).unsafeCast<Double>()
        }
    }

    override fun createModule() = readBinary(f64StandardFunctions)

    override fun visitNumber(number: Number) = ctx.f64.const(number.toDouble())

    override fun visitUnary(node: TypedMst.Unary<Double>): ExpressionRef = when (node.operation) {
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

    override fun visitBinary(mst: TypedMst.Binary<Double>): ExpressionRef = when (mst.operation) {
        GroupOps.PLUS_OPERATION -> ctx.f64.add(visit(mst.left), visit(mst.right))
        GroupOps.MINUS_OPERATION -> ctx.f64.sub(visit(mst.left), visit(mst.right))
        RingOps.TIMES_OPERATION -> ctx.f64.mul(visit(mst.left), visit(mst.right))
        FieldOps.DIV_OPERATION -> ctx.f64.div(visit(mst.left), visit(mst.right))
        PowerOperations.POW_OPERATION -> ctx.call("pow", arrayOf(visit(mst.left), visit(mst.right)), f64)
        else -> super.visitBinary(mst)
    }
}

@UnstableKMathAPI
internal class IntWasmBuilder(target: TypedMst<Int>) : WasmBuilder<Int, IntExpression>(i32, IntRing, target) {
    override val instance by lazy {
        object : IntExpression {
            override val indexer = SimpleSymbolIndexer(keys)

            override fun invoke(arguments: IntArray) = spreader(executable, arguments).unsafeCast<Int>()
        }
    }

    override fun visitNumber(number: Number) = ctx.i32.const(number.toInt())

    override fun visitUnary(node: TypedMst.Unary<Int>): ExpressionRef = when (node.operation) {
        GroupOps.MINUS_OPERATION -> ctx.i32.sub(ctx.i32.const(0), visit(node.value))
        GroupOps.PLUS_OPERATION -> visit(node.value)
        else -> super.visitUnary(node)
    }

    override fun visitBinary(mst: TypedMst.Binary<Int>): ExpressionRef = when (mst.operation) {
        GroupOps.PLUS_OPERATION -> ctx.i32.add(visit(mst.left), visit(mst.right))
        GroupOps.MINUS_OPERATION -> ctx.i32.sub(visit(mst.left), visit(mst.right))
        RingOps.TIMES_OPERATION -> ctx.i32.mul(visit(mst.left), visit(mst.right))
        else -> super.visitBinary(mst)
    }
}
