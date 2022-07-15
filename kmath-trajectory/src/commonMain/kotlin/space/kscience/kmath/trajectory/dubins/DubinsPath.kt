/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.trajectory.dubins

import space.kscience.kmath.trajectory.segments.Arc
import space.kscience.kmath.trajectory.segments.Segment

public class DubinsPath(
    public val a: Arc,
    public val b: Segment,
    public val c: Arc,
) {

    public val type: TYPE = TYPE.valueOf(
        arrayOf(
            a.direction.name[0],
            if (b is Arc) b.direction.name[0] else 'S',
            c.direction.name[0]
        ).toCharArray().concatToString()
    )

    public val length: Double = a.length + b.length + c.length

    public enum class TYPE {
        RLR, LRL, RSR, LSL, RSL, LSR
    }
}
