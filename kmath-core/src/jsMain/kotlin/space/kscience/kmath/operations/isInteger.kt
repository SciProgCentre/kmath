/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.operations

/**
 * Check if number is an integer
 */
public actual fun Number.isInteger(): Boolean = js("Number").isInteger(this) as Boolean