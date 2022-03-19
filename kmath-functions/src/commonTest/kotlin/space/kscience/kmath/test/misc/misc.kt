/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.test.misc

import kotlin.math.abs

// TODO: Move to corresponding module "kmath-number-theory"

tailrec fun gcd(a: Long, b: Long): Long = if (a == 0L) abs(b) else gcd(b % a, a)