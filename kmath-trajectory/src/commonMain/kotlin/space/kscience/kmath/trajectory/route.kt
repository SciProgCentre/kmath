/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.trajectory

public fun interface MaxCurvature {
    public fun compute(startPoint: PhaseVector2D): Double
}

public fun DubinsPath.Companion.shortest(
    start: PhaseVector2D,
    end: PhaseVector2D,
    maxCurvature: MaxCurvature,
): DubinsPath = shortest(start, end, maxCurvature.compute(start))
