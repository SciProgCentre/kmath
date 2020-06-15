package scietifik.kmath.asm

import scientifik.kmath.asm.asmField
import scientifik.kmath.asm.asmRing
import scientifik.kmath.asm.asmSpace
import scientifik.kmath.expressions.invoke
import scientifik.kmath.operations.ByteRing
import scientifik.kmath.operations.RealField
import kotlin.test.Test
import kotlin.test.assertEquals

class TestAsmAlgebras {
    @Test
    fun space() {
        val res = ByteRing.asmSpace {
            binaryOperation(
                "+",

                unaryOperation(
                    "+",
                    3.toByte() - (2.toByte() + (multiply(
                        add(const(1), const(1)),
                        2
                    ) + 1.toByte()) * 3.toByte() - 1.toByte())
                ),

                number(1)
            ) + variable("x") + zero
        }("x" to 2.toByte())

        assertEquals(16, res)
    }

    @Test
    fun ring() {
        val res = ByteRing.asmRing {
            binaryOperation(
                "+",

                unaryOperation(
                    "+",
                    (3.toByte() - (2.toByte() + (multiply(
                        add(const(1), const(1)),
                        2
                    ) + 1.toByte()))) * 3.0 - 1.toByte()
                ),

                number(1)
            ) * const(2)
        }()

        assertEquals(24, res)
    }

    @Test
    fun field() {
        val res = RealField.asmField {
            divide(binaryOperation(
                "+",

                unaryOperation(
                    "+",
                    (3.0 - (2.0 + (multiply(
                        add(const(1.0), const(1.0)),
                        2
                    ) + 1.0))) * 3 - 1.0
                ),

                number(1)
            ) / 2, const(2.0)) * one
        }()

        assertEquals(3.0, res)
    }
}
