package scientifik.kmath.operations

import scientifik.kmath.operations.internal.RingVerifier
import kotlin.test.Test
import kotlin.test.assertEquals

internal class BigIntAlgebraTest {
    @Test
    fun verify() = BigIntField { RingVerifier(this, +"42", +"10", +"-12", 10).verify() }

    @Test
    fun testKBigIntegerRingSum() {
        val res = BigIntField {
            1_000L.toBigInt() * 1_000L.toBigInt()
        }
        assertEquals(res, 1_000_000.toBigInt())
    }

    @Test
    fun testKBigIntegerRingSum_100_000_000__100_000_000() {
        BigIntField {
            val sum = +"100_000_000" + +"100_000_000"
            assertEquals(sum, "200_000_000".parseBigInteger())
        }
    }

    @Test
    fun test_mul_3__4() {
        BigIntField {
            val prod = +"0x3000_0000_0000" * +"0x4000_0000_0000_0000_0000"
            assertEquals(prod, "0xc00_0000_0000_0000_0000_0000_0000_0000".parseBigInteger())
        }
    }

    @Test
    fun test_div_big_1() {
        BigIntField {
            val res = +"1_000_000_000_000_000_000_000_000_000_000_000_000_000_000_000_000_000_000_000_000_000_000" /
                    +"555_000_444_000_333_000_222_000_111_000_999_001"
            assertEquals(res, +"1801800360360432432518919022699")
        }
    }

    @Test
    fun test_rem_big_1() {
        BigIntField {
            val res = +"1_000_000_000_000_000_000_000_000_000_000_000_000_000_000_000_000_000_000_000_000_000_000" %
                    +"555_000_444_000_333_000_222_000_111_000_999_001"
            assertEquals(res, +"324121220440768000291647788404676301")
        }
    }

}
