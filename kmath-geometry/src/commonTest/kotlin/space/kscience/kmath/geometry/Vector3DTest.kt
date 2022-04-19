/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.geometry

import space.kscience.kmath.operations.toList
import kotlin.test.Test
import kotlin.test.assertEquals

internal class Vector3DTest {
    private val vector = Vector3D(1.0, -7.999, 0.001)

    @Test
    fun size() {
        assertEquals(3, vector.size)
    }

    @Test
    fun get() {
        assertEquals(1.0, vector[0])
        assertEquals(-7.999, vector[1])
        assertEquals(0.001, vector[2])
    }

    @Test
    fun iterator() {
        assertEquals(listOf(1.0, -7.999, 0.001), vector.toList())
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
    fun z() {
        assertEquals(0.001, vector.z)
    }

    @Test
    fun equality() {
        val vector2 = AlternativeVector3D(1.0, -7.999, 0.001)

        // reflexive
        assertEquals(vector, vector)
        assertEquals(vector2, vector2)

        // symmetric
        assertEquals(vector, vector2)
        assertEquals(vector2, vector)

        // transitive
        val vector3 = AlternativeVector3D(1.0, -7.999, 0.001)
        assertEquals(vector, vector2)
        assertEquals(vector2, vector3)
        assertEquals(vector3, vector)
    }

    @Test
    fun hash() {
        val vector2 = AlternativeVector3D(1.0, -7.999, 0.001)

        assertEquals(vector, vector2)
        assertEquals(vector.hashCode(), vector2.hashCode())
    }

    private data class AlternativeVector3D(
        override val x: Double,
        override val y: Double,
        override val z: Double,
    ) : Vector3D {

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || other !is Vector3D) return false

            if (x != other.x) return false
            if (y != other.y) return false
            if (z != other.z) return false

            return true
        }

        override fun hashCode(): Int {
            var result = 1
            result = 31 * result + x.hashCode()
            result = 31 * result + y.hashCode()
            result = 31 * result + z.hashCode()
            return result
        }
    }
}
