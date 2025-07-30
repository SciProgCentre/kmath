/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.series

import space.kscience.kmath.operations.algebra
import space.kscience.kmath.operations.bufferAlgebra
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
fun SeriesAlgebra.Companion.time(zero: Instant, step: Duration) = MonotonicSeriesAlgebra(
    bufferAlgebra = Double.algebra.bufferAlgebra,
    offsetToLabel = { zero + step * it },
    labelToOffset = { (it - zero) / step }
)