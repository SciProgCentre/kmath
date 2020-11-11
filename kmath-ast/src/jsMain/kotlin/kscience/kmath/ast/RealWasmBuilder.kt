package kscience.kmath.ast

import kscience.kmath.expressions.Expression
import kscience.kmath.expressions.StringSymbol
import kscience.kmath.operations.*

private val spreader = eval("(obj, args) => obj(...args)")

internal sealed class WasmBuilder<T>(val binaryenType: binaryen.Type, val kmathAlgebra: Algebra<T>) where T : Number {
    val keys: MutableList<String> = mutableListOf()
    lateinit var ctx: binaryen.Module

    open fun visitSymbolic(mst: MST.Symbolic): binaryen.ExpressionRef {
        try {
            kmathAlgebra.symbol(mst.value)
        } catch (ignored: Throwable) {
            null
        }?.let { return visitNumeric(MST.Numeric(it)) }

        var idx = keys.indexOf(mst.value)

        if (idx == -1) {
            keys += mst.value
            idx = keys.lastIndex
        }

        return ctx.local.get(idx, binaryenType)
    }

    abstract fun visitNumeric(mst: MST.Numeric): binaryen.ExpressionRef

    open fun visitUnary(mst: MST.Unary): binaryen.ExpressionRef =
        error("Unary operation ${mst.operation} not defined in $this")

    open fun visitBinary(mst: MST.Binary): binaryen.ExpressionRef =
        error("Binary operation ${mst.operation} not defined in $this")

    open fun createModule(): binaryen.Module = binaryen.Module()

    fun visit(mst: MST): binaryen.ExpressionRef = when (mst) {
        is MST.Symbolic -> visitSymbolic(mst)
        is MST.Numeric -> visitNumeric(mst)
        is MST.Unary -> visitUnary(mst)
        is MST.Binary -> visitBinary(mst)
    }

    fun compile(mst: MST): Expression<T> {
        val keys = mutableListOf<String>()

        val bin = with(createModule()) {
            ctx = this
            val expr = visit(mst)

            addFunction(
                "executable",
                binaryen.createType(Array(keys.size) { binaryenType }),
                binaryenType,
                arrayOf(),
                expr
            )

            binaryen.setOptimizeLevel(3)
//          optimizeFunction("executable")
            addFunctionExport("executable", "executable")
            val res = emitBinary()
            dispose()
            res
        }

        val c = WebAssembly.Module(bin)
        val i = WebAssembly.Instance(c, js("{}") as Any)

        return Expression { args ->
            val params = keys.map { StringSymbol(it) }.map { args.getValue(it) }.toTypedArray()
            keys.clear()
            spreader(i.exports.asDynamic().executable, params) as T
        }
    }
}

internal class RealWasmBuilder : WasmBuilder<Double>(binaryen.f64, RealField) {
    override fun createModule(): binaryen.Module = binaryen.readBinary(f64StandardFunctions)

    override fun visitNumeric(mst: MST.Numeric): binaryen.ExpressionRef = ctx.f64.const(mst.value)

    override fun visitUnary(mst: MST.Unary): binaryen.ExpressionRef = when (mst.operation) {
        SpaceOperations.MINUS_OPERATION -> ctx.f64.neg(visit(mst.value))
        SpaceOperations.PLUS_OPERATION -> visit(mst.value)
        PowerOperations.SQRT_OPERATION -> ctx.f64.sqrt(visit(mst.value))
        TrigonometricOperations.SIN_OPERATION -> ctx.call("sin", arrayOf(visit(mst.value)), binaryen.f64)
        TrigonometricOperations.COS_OPERATION -> ctx.call("cos", arrayOf(visit(mst.value)), binaryen.f64)
        TrigonometricOperations.TAN_OPERATION -> ctx.call("tan", arrayOf(visit(mst.value)), binaryen.f64)
        TrigonometricOperations.ASIN_OPERATION -> ctx.call("asin", arrayOf(visit(mst.value)), binaryen.f64)
        TrigonometricOperations.ACOS_OPERATION -> ctx.call("acos", arrayOf(visit(mst.value)), binaryen.f64)
        TrigonometricOperations.ATAN_OPERATION -> ctx.call("atan", arrayOf(visit(mst.value)), binaryen.f64)
        HyperbolicOperations.SINH_OPERATION -> ctx.call("sinh", arrayOf(visit(mst.value)), binaryen.f64)
        HyperbolicOperations.COSH_OPERATION -> ctx.call("cosh", arrayOf(visit(mst.value)), binaryen.f64)
        HyperbolicOperations.TANH_OPERATION -> ctx.call("tanh", arrayOf(visit(mst.value)), binaryen.f64)
        HyperbolicOperations.ASINH_OPERATION -> ctx.call("asinh", arrayOf(visit(mst.value)), binaryen.f64)
        HyperbolicOperations.ACOSH_OPERATION -> ctx.call("acosh", arrayOf(visit(mst.value)), binaryen.f64)
        HyperbolicOperations.ATANH_OPERATION -> ctx.call("atanh", arrayOf(visit(mst.value)), binaryen.f64)
        ExponentialOperations.EXP_OPERATION -> ctx.call("exp", arrayOf(visit(mst.value)), binaryen.f64)
        ExponentialOperations.LN_OPERATION -> ctx.call("log", arrayOf(visit(mst.value)), binaryen.f64)
        else -> super.visitUnary(mst)
    }

    override fun visitBinary(mst: MST.Binary): binaryen.ExpressionRef = when (mst.operation) {
        SpaceOperations.PLUS_OPERATION -> ctx.f64.add(visit(mst.left), visit(mst.right))
        SpaceOperations.MINUS_OPERATION -> ctx.f64.sub(visit(mst.left), visit(mst.right))
        RingOperations.TIMES_OPERATION -> ctx.f64.mul(visit(mst.left), visit(mst.right))
        FieldOperations.DIV_OPERATION -> ctx.f64.div(visit(mst.left), visit(mst.right))
        PowerOperations.POW_OPERATION -> ctx.call("pow", arrayOf(visit(mst.left), visit(mst.right)), binaryen.f64)
        else -> super.visitBinary(mst)
    }
}

internal class IntWasmBuilder : WasmBuilder<Int>(binaryen.i32, IntRing) {
    override fun visitNumeric(mst: MST.Numeric): binaryen.ExpressionRef = ctx.i32.const(mst.value)

    override fun visitUnary(mst: MST.Unary): binaryen.ExpressionRef = when (mst.operation) {
        SpaceOperations.MINUS_OPERATION -> ctx.i32.sub(ctx.i32.const(0), visit(mst.value))
        SpaceOperations.PLUS_OPERATION -> visit(mst.value)
        else -> super.visitUnary(mst)
    }

    override fun visitBinary(mst: MST.Binary): binaryen.ExpressionRef = when (mst.operation) {
        SpaceOperations.PLUS_OPERATION -> ctx.i32.add(visit(mst.left), visit(mst.right))
        SpaceOperations.MINUS_OPERATION -> ctx.i32.sub(visit(mst.left), visit(mst.right))
        RingOperations.TIMES_OPERATION -> ctx.i32.mul(visit(mst.left), visit(mst.right))
        FieldOperations.DIV_OPERATION -> ctx.i32.div_s(visit(mst.left), visit(mst.right))
        else -> super.visitBinary(mst)
    }
}

internal class LongWasmBuilder : WasmBuilder<Long>(binaryen.i64, LongRing) {
    override fun visitNumeric(mst: MST.Numeric): binaryen.ExpressionRef = ctx.i64.const(mst.value)

    override fun visitUnary(mst: MST.Unary): binaryen.ExpressionRef = when (mst.operation) {
        SpaceOperations.MINUS_OPERATION -> ctx.i64.sub(ctx.i64.const(0, 0), visit(mst.value))
        SpaceOperations.PLUS_OPERATION -> visit(mst.value)
        else -> super.visitUnary(mst)
    }

    override fun visitBinary(mst: MST.Binary): binaryen.ExpressionRef = when (mst.operation) {
        SpaceOperations.PLUS_OPERATION -> ctx.i64.add(visit(mst.left), visit(mst.right))
        SpaceOperations.MINUS_OPERATION -> ctx.i64.sub(visit(mst.left), visit(mst.right))
        RingOperations.TIMES_OPERATION -> ctx.i64.mul(visit(mst.left), visit(mst.right))
        FieldOperations.DIV_OPERATION -> ctx.i64.div_s(visit(mst.left), visit(mst.right))
        else -> super.visitBinary(mst)
    }
}
