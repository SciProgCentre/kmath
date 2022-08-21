/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.nd4j

import space.kscience.kmath.misc.toIntExact

internal fun LongArray.toIntArray(): IntArray = IntArray(size) { this[it].toIntExact() }
