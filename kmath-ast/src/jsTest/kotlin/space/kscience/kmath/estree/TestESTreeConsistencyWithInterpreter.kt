package space.kscience.kmath.estree

import space.kscience.kmath.complex.ComplexField
import space.kscience.kmath.complex.toComplex
import space.kscience.kmath.expressions.*
import space.kscience.kmath.misc.Symbol
import space.kscience.kmath.operations.ByteRing
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.operations.invoke
import kotlin.test.Test
import kotlin.test.assertEquals

internal class TestESTreeConsistencyWithInterpreter {

    @Test
    fun mstSpace() {

        val mst = MstGroup {
            binaryOperationFunction("+")(
                unaryOperationFunction("+")(
                    number(3.toByte()) - (number(2.toByte()) + (scale(
                        add(number(1), number(1)),
                        2.0
                    ) + number(1.toByte()) * 3.toByte() - number(1.toByte())))
                ),

                number(1)
            ) + bindSymbol("x") + zero
        }

        assertEquals(
            mst.interpret(MstGroup, Symbol.x to MST.Numeric(2)),
            mst.compile(MstGroup, Symbol.x to MST.Numeric(2))
        )
    }

    @Test
    fun byteRing() {
        val mst = MstRing {
            binaryOperationFunction("+")(
                unaryOperationFunction("+")(
                    (bindSymbol("x") - (2.toByte() + (scale(
                        add(number(1), number(1)),
                        2.0
                    ) + 1.toByte()))) * 3.0 - 1.toByte()
                ),

                number(1)
            ) * number(2)
        }

        assertEquals(
            mst.interpret(ByteRing, Symbol.x to 3.toByte()),
            mst.compile(ByteRing, Symbol.x to 3.toByte())
        )
    }

    @Test
    fun realField() {
        val mst = MstField {
            +(3 - 2 + 2 * number(1) + 1.0) + binaryOperationFunction("+")(
                (3.0 - (bindSymbol("x") + (scale(add(number(1.0), number(1.0)), 2.0) + 1.0))) * 3 - 1.0
                        + number(1),
                number(1) / 2 + number(2.0) * one
            ) + zero
        }

        assertEquals(
            mst.interpret(DoubleField, Symbol.x to 2.0),
            mst.compile(DoubleField, Symbol.x to 2.0)
        )
    }

    @Test
    fun complexField() {
        val mst = MstField {
            +(3 - 2 + 2 * number(1) + 1.0) + binaryOperationFunction("+")(
                (3.0 - (bindSymbol("x") + (scale(add(number(1.0), number(1.0)), 2.0) + 1.0))) * 3 - 1.0
                        + number(1),
                number(1) / 2 + number(2.0) * one
            ) + zero
        }

        assertEquals(
            mst.interpret(ComplexField, Symbol.x to 2.0.toComplex()),
            mst.compile(ComplexField, Symbol.x to 2.0.toComplex())
        )
    }
}
