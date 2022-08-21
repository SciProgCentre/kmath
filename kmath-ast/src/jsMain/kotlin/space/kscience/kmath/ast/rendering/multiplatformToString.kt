/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.ast.rendering

internal actual fun Double.multiplatformToString(): String {
    val d = this
    if (d >= 1e7 || d <= -1e7) return js("d.toExponential()") as String
    return toString()
}

internal actual fun Float.multiplatformToString(): String {
    val d = this
    if (d >= 1e7f || d <= -1e7f) return js("d.toExponential()") as String
    return toString()
}
