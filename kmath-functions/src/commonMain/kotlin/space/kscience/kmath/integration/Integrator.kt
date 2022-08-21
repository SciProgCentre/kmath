/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.integration

/**
 * A general interface for all integrators.
 */
public interface Integrator<I : Integrand> {
    /**
     * Runs one integration pass and return a new [Integrand] with a new set of features.
     */
    public fun process(integrand: I): I
}
