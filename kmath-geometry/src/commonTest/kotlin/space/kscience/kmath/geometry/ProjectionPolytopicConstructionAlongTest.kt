/*
 * Copyright 2018-2025 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.geometry

import space.kscience.kmath.geometry.euclidean3d.Float64Space3D
import space.kscience.kmath.geometry.euclidean3d.PolytopicConstruction3D
import space.kscience.kmath.geometry.euclidean3d.build
import space.kscience.kmath.nd.BufferND
import space.kscience.kmath.nd.ShapeND
import space.kscience.kmath.operations.invoke
import kotlin.test.Test


class ProjectionPolytopicConstructionAlongTest {
    @Test
    fun projectionRemovingZCoordinate() {
        val l = 10
        val m = 10
        val n = 10
        Float64Space3D {
            val normal = vector(0.0, 0.0, 1.0)
            val base = vector(57.0, 179.0, 0.0)
            
            val polytopicConstruction3D = PolytopicConstruction3D.build {
                val vertices = BufferND(ShapeND(l, m, n)) { (x, y, z) -> addVertex(vector(x, y, z)) }
                // ...
            }
            
            val polytopicConstruction2D = projectAlong(polytopicConstruction3D, normal, base)
        }
    }
}