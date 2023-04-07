/*
 * Copyright 2018-2023 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.operations

/**
 * Check if number is an integer from platform point of view
 */
public actual fun Number.isInteger(): Boolean =
    (this is Int) || (this is Long) || (this is Short) || (this.toDouble() % 1 == 0.0)