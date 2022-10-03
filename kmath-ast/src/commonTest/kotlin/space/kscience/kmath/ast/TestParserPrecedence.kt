/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.ast

import space.kscience.kmath.expressions.interpret
import space.kscience.kmath.operations.DoubleField
import kotlin.test.Test
import kotlin.test.assertEquals

internal class TestParserPrecedence {
    @Test
    fun test1(): Unit = assertEquals(6.0, "2*2+2".parseMath().interpret(f))

    @Test
    fun test2(): Unit = assertEquals(6.0, "2+2*2".parseMath().interpret(f))

    @Test
    fun test3(): Unit = assertEquals(10.0, "2^3+2".parseMath().interpret(f))

    @Test
    fun test4(): Unit = assertEquals(10.0, "2+2^3".parseMath().interpret(f))

    @Test
    fun test5(): Unit = assertEquals(16.0, "2^3*2".parseMath().interpret(f))

    @Test
    fun test6(): Unit = assertEquals(16.0, "2*2^3".parseMath().interpret(f))

    @Test
    fun test7(): Unit = assertEquals(18.0, "2+2^3*2".parseMath().interpret(f))

    @Test
    fun test8(): Unit = assertEquals(18.0, "2*2^3+2".parseMath().interpret(f))

    private companion object {
        private val f = DoubleField
    }
}
