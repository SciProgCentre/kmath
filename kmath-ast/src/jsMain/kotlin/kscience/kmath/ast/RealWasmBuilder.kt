package kscience.kmath.ast

import kscience.kmath.expressions.Expression
import kscience.kmath.expressions.StringSymbol
import kscience.kmath.operations.*

public fun compileMstToWasmF64(mst: MST): Expression<Double> {
    val keys = mutableListOf<String>()

    val bin = with(binaryen.readBinary(INITIAL)) {
        fun MST.visit(): binaryen.ExpressionRef = when (this) {
            is MST.Symbolic -> {
                var idx = keys.indexOf(value)

                if (idx == -1) {
                    keys += value
                    idx = keys.lastIndex
                }

                local.get(idx, binaryen.f64)
            }

            is MST.Numeric -> f64.const(value)

            is MST.Unary -> when (operation) {
                SpaceOperations.MINUS_OPERATION -> f64.neg(value.visit())
                SpaceOperations.PLUS_OPERATION -> value.visit()
                PowerOperations.SQRT_OPERATION -> f64.sqrt(value.visit())
                TrigonometricOperations.SIN_OPERATION -> call("sin", arrayOf(value.visit()), binaryen.f64)
                TrigonometricOperations.COS_OPERATION -> call("cos", arrayOf(value.visit()), binaryen.f64)
                TrigonometricOperations.TAN_OPERATION -> call("tan", arrayOf(value.visit()), binaryen.f64)
                TrigonometricOperations.ASIN_OPERATION -> call("asin", arrayOf(value.visit()), binaryen.f64)
                TrigonometricOperations.ACOS_OPERATION -> call("acos", arrayOf(value.visit()), binaryen.f64)
                TrigonometricOperations.ATAN_OPERATION -> call("atan", arrayOf(value.visit()), binaryen.f64)
                HyperbolicOperations.SINH_OPERATION -> call("sinh", arrayOf(value.visit()), binaryen.f64)
                HyperbolicOperations.COSH_OPERATION -> call("cosh", arrayOf(value.visit()), binaryen.f64)
                HyperbolicOperations.TANH_OPERATION -> call("tanh", arrayOf(value.visit()), binaryen.f64)
                HyperbolicOperations.ASINH_OPERATION -> call("asinh", arrayOf(value.visit()), binaryen.f64)
                HyperbolicOperations.ACOSH_OPERATION -> call("acosh", arrayOf(value.visit()), binaryen.f64)
                HyperbolicOperations.ATANH_OPERATION -> call("atanh", arrayOf(value.visit()), binaryen.f64)
                ExponentialOperations.EXP_OPERATION -> call("exp", arrayOf(value.visit()), binaryen.f64)
                ExponentialOperations.LN_OPERATION -> call("log", arrayOf(value.visit()), binaryen.f64)
                else -> throw UnsupportedOperationException()
            }

            is MST.Binary -> when (operation) {
                SpaceOperations.PLUS_OPERATION -> f64.add(left.visit(), right.visit())
                RingOperations.TIMES_OPERATION -> f64.mul(left.visit(), right.visit())
                FieldOperations.DIV_OPERATION -> f64.div(left.visit(), right.visit())
                PowerOperations.POW_OPERATION -> call("pow", arrayOf(left.visit(), right.visit()), binaryen.f64)
                else -> throw UnsupportedOperationException()
            }
        }

        val expr = mst.visit()

        addFunction(
            "executable",
            binaryen.createType(Array(keys.size) { binaryen.f64 }),
            binaryen.f64,
            arrayOf(),
            expr
        )

        binaryen.setOptimizeLevel(3)
//        optimizeFunction("executable")
        addFunctionExport("executable", "executable")
        val res = emitBinary()
        dispose()
        res
    }

    val c = WebAssembly.Module(bin)
    val i = WebAssembly.Instance(c, js("{}") as Any)

    return Expression { args ->
        val params = keys.map { StringSymbol(it) }.map { args.getValue(it) }.toTypedArray()
        val spreader = eval("(obj, args) => obj(...args)")
        spreader(i.exports.asDynamic().executable, params) as Double
    }
}
