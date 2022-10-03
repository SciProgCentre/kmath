/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.trajectory

public fun interface TrajectoryCost {
    public fun estimate(trajectory: Trajectory): Double

    public companion object{
        public val length: TrajectoryCost = TrajectoryCost { it.length }
    }
}