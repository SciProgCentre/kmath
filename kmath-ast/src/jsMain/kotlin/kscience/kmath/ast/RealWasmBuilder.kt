package kscience.kmath.ast

import kscience.kmath.operations.*

private const val ARGS_PTR = 0
private const val ARGS_SIZE = 1

public fun compileMstToWasmF64(mst: MST) {
    val keys = mutableListOf<String>()

    val bin = with(binaryen.parseText(INITIAL)) {
        fun MST.visit(): binaryen.ExpressionRef = when (this) {
            is MST.Symbolic -> {
                var idx = keys.indexOf(value)

                if (idx == -1) {
                    keys += value
                    idx = keys.lastIndex
                }

                f64.load(idx * Double.SIZE_BYTES, 0, local.get(ARGS_PTR, binaryen.i32))
            }

            is MST.Numeric -> f64.const(value)

            is MST.Unary -> when (operation) {
                SpaceOperations.MINUS_OPERATION -> f64.neg(value.visit())
                SpaceOperations.PLUS_OPERATION -> value.visit()
                PowerOperations.SQRT_OPERATION -> f64.sqrt(value.visit())
                TrigonometricOperations.SIN_OPERATION -> call("sin", arrayOf(value.visit()), binaryen.f64)
                else -> throw UnsupportedOperationException()
            }

            is MST.Binary -> when (operation) {
                SpaceOperations.PLUS_OPERATION -> f64.add(left.visit(), right.visit())
                RingOperations.TIMES_OPERATION -> f64.mul(left.visit(), right.visit())
                FieldOperations.DIV_OPERATION -> f64.div(left.visit(), right.visit())
                else -> throw UnsupportedOperationException()
            }
        }

        addFunction(
            "executable",
            binaryen.createType(arrayOf(binaryen.i32, binaryen.i32)),
            binaryen.f64,
            arrayOf(),
            mst.visit()
        )

//        setMemory(0, 10000)
        addFunctionExport("executable", "executable")
        optimize()

        if (!validate().unsafeCast<Boolean>())
            error("Invalid module produced.")

        println(emitText())
        emitBinary()
    }


}
