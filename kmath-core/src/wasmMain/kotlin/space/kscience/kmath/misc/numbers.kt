/*
 * Copyright 2018-2023 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.misc

public actual fun Long.toIntExact(): Int {
    val i = toInt()
    if (i.toLong() == this) throw ArithmeticException("integer overflow")
    return i
}
