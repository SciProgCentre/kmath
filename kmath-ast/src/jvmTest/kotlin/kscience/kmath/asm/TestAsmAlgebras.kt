package kscience.kmath.asm

import kscience.kmath.ast.mstInField
import kscience.kmath.ast.mstInRing
import kscience.kmath.ast.mstInSpace
import kscience.kmath.expressions.invoke
import kscience.kmath.operations.ByteRing
import kscience.kmath.operations.RealField
import kotlin.test.Test
import kotlin.test.assertEquals

internal class TestAsmAlgebras {

    @Test
    fun space() {
        val res1 = ByteRing.mstInSpace {
            binaryOperation(
                "+",

                unaryOperation(
                    "+",
                    number(3.toByte()) - (number(2.toByte()) + (multiply(
                        add(number(1), number(1)),
                        2
                    ) + number(1.toByte()) * 3.toByte() - number(1.toByte())))
                ),

                number(1)
            ) + symbol("x") + zero
        }("x" to 2.toByte())

        val res2 = ByteRing.mstInSpace {
            binaryOperation(
                "+",

                unaryOperation(
                    "+",
                    number(3.toByte()) - (number(2.toByte()) + (multiply(
                        add(number(1), number(1)),
                        2
                    ) + number(1.toByte()) * 3.toByte() - number(1.toByte())))
                ),

                number(1)
            ) + symbol("x") + zero
        }.compile()("x" to 2.toByte())

        assertEquals(res1, res2)
    }

    @Test
    fun ring() {
        val res1 = ByteRing.mstInRing {
            binaryOperation(
                "+",

                unaryOperation(
                    "+",
                    (symbol("x") - (2.toByte() + (multiply(
                        add(number(1), number(1)),
                        2
                    ) + 1.toByte()))) * 3.0 - 1.toByte()
                ),

                number(1)
            ) * number(2)
        }("x" to 3.toByte())

        val res2 = ByteRing.mstInRing {
            binaryOperation(
                "+",

                unaryOperation(
                    "+",
                    (symbol("x") - (2.toByte() + (multiply(
                        add(number(1), number(1)),
                        2
                    ) + 1.toByte()))) * 3.0 - 1.toByte()
                ),

                number(1)
            ) * number(2)
        }.compile()("x" to 3.toByte())

        assertEquals(res1, res2)
    }

    @Test
    fun field() {
        val res1 = RealField.mstInField {
            +(3 - 2 + 2 * number(1) + 1.0) + binaryOperation(
                "+",
                (3.0 - (symbol("x") + (multiply(add(number(1.0), number(1.0)), 2) + 1.0))) * 3 - 1.0
                        + number(1),
                number(1) / 2 + number(2.0) * one
            ) + zero
        }("x" to 2.0)

        val res2 = RealField.mstInField {
            +(3 - 2 + 2 * number(1) + 1.0) + binaryOperation(
                "+",
                (3.0 - (symbol("x") + (multiply(add(number(1.0), number(1.0)), 2) + 1.0))) * 3 - 1.0
                        + number(1),
                number(1) / 2 + number(2.0) * one
            ) + zero
        }.compile()("x" to 2.0)

        assertEquals(res1, res2)
    }
}
