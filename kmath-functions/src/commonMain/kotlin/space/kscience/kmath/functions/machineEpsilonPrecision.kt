/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.functions

import space.kscience.kmath.UnstableKMathAPI

private var cachedMachineEpsilonPrecision: Double? = null

@UnstableKMathAPI
public val machineEpsilonPrecision: Double
    get() = cachedMachineEpsilonPrecision ?: run {
        var eps = 1.0
        while (1 + eps > 1) {
            eps /= 2.0
        }
        cachedMachineEpsilonPrecision = eps

        eps
    }
