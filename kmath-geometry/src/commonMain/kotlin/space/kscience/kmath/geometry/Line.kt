/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package space.kscience.kmath.geometry

public data class Line<out V : Vector>(val base: V, val direction: V)

public typealias Line2D = Line<Vector2D>
public typealias Line3D = Line<Vector3D>
