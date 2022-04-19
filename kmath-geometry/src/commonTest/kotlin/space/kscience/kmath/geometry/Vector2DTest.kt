/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.geometry

import space.kscience.kmath.operations.toList
import kotlin.test.Test
import kotlin.test.assertEquals

internal class Vector2DTest {
    private val vector = Vector2D(1.0, -7.999)

    @Test
    fun size() {
        assertEquals(2, vector.size)
    }

    @Test
    fun get() {
        assertEquals(1.0, vector[0])
        assertEquals(-7.999, vector[1])
    }

    @Test
    fun iterator() {
        assertEquals(listOf(1.0, -7.999), vector.toList())
    }

    @Test
    fun x() {
        assertEquals(1.0, vector.x)
    }

    @Test
    fun y() {
        assertEquals(-7.999, vector.y)
    }

    @Test
    fun equality() {
        val vector2 = AlternativeVector2D(1.0, -7.999)

        // reflexive
        assertEquals(vector, vector)
        assertEquals(vector2, vector2)

        // symmetric
        assertEquals(vector, vector2)
        assertEquals(vector2, vector)

        // transitive
        val vector3 = AlternativeVector2D(1.0, -7.999)
        assertEquals(vector, vector2)
        assertEquals(vector2, vector3)
        assertEquals(vector3, vector)
    }

    @Test
    fun hash() {
        val vector2 = AlternativeVector2D(1.0, -7.999)

        assertEquals(vector, vector2)
        assertEquals(vector.hashCode(), vector2.hashCode())
    }

    private data class AlternativeVector2D(
        override val x: Double,
        override val y: Double,
    ) : Vector2D {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || other !is Vector2D) return false

            if (x != other.x) return false
            if (y != other.y) return false

            return true
        }

        override fun hashCode(): Int {
            var result = 1
            result = 31 * result + x.hashCode()
            result = 31 * result + y.hashCode()
            return result
        }
    }
}
