package scietifik.kmath.ast

import scientifik.kmath.ast.evaluate
import scientifik.kmath.ast.parseMath
import scientifik.kmath.operations.Field
import scientifik.kmath.operations.RealField
import kotlin.test.Test
import kotlin.test.assertEquals

internal class ParserPrecedenceTest {
    private val f: Field<Double> = RealField

    @Test
    fun test1(): Unit = assertEquals(6.0, f.evaluate("2*2+2".parseMath()))

    @Test
    fun test2(): Unit = assertEquals(6.0, f.evaluate("2+2*2".parseMath()))

    @Test
    fun test3(): Unit = assertEquals(10.0, f.evaluate("2^3+2".parseMath()))

    @Test
    fun test4(): Unit = assertEquals(10.0, f.evaluate("2+2^3".parseMath()))

    @Test
    fun test5(): Unit = assertEquals(16.0, f.evaluate("2^3*2".parseMath()))

    @Test
    fun test6(): Unit = assertEquals(16.0, f.evaluate("2*2^3".parseMath()))

    @Test
    fun test7(): Unit = assertEquals(18.0, f.evaluate("2+2^3*2".parseMath()))

    @Test
    fun test8(): Unit = assertEquals(18.0, f.evaluate("2*2^3+2".parseMath()))
}
