package scietifik.kmath.asm

//
//class TestAsmAlgebras {
//    @Test
//    fun space() {
//        val res = ByteRing.asmInRing {
//            binaryOperation(
//                "+",
//
//                unaryOperation(
//                    "+",
//                    3.toByte() - (2.toByte() + (multiply(
//                        add(number(1), number(1)),
//                        2
//                    ) + 1.toByte()) * 3.toByte() - 1.toByte())
//                ),
//
//                number(1)
//            ) + symbol("x") + zero
//        }("x" to 2.toByte())
//
//        assertEquals(16, res)
//    }
//
//    @Test
//    fun ring() {
//        val res = ByteRing.asmInRing {
//            binaryOperation(
//                "+",
//
//                unaryOperation(
//                    "+",
//                    (3.toByte() - (2.toByte() + (multiply(
//                        add(const(1), const(1)),
//                        2
//                    ) + 1.toByte()))) * 3.0 - 1.toByte()
//                ),
//
//                number(1)
//            ) * const(2)
//        }()
//
//        assertEquals(24, res)
//    }
//
//    @Test
//    fun field() {
//        val res = RealField.asmInField {
//            +(3 - 2 + 2*(number(1)+1.0)
//
//                unaryOperation(
//                    "+",
//                    (3.0 - (2.0 + (multiply(
//                        add((1.0), const(1.0)),
//                        2
//                    ) + 1.0))) * 3 - 1.0
//                )+
//
//                number(1)
//            ) / 2, const(2.0)) * one
//        }()
//
//        assertEquals(3.0, res)
//    }
//}
