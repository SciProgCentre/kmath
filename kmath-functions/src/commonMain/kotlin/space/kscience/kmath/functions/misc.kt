/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.functions


/**
 * Marks operations that are going to be optimized reimplementations by reducing number of boxings but currently is
 * under development and is not stable (or even ready to use).
 */
@RequiresOptIn(
    message = "It's copy of operation with optimized boxing. It's currently unstable.",
    level = RequiresOptIn.Level.ERROR
)
internal annotation class UnstablePolynomialBoxingOptimization

/**
 * Marks declarations that give access to internal entities of polynomials delicate structure. Thus, it allows to
 * optimize performance a bit by skipping standard steps, but such skips may cause critical errors if something is
 * implemented badly. Make sure you fully read and understand documentation and don't break internal contracts.
 */
@RequiresOptIn(
    message = "This declaration gives access to delicate internal structure of polynomials. " +
            "It allows to optimize performance by skipping unnecessary arguments check. " +
            "But at the same time makes it easy to make a mistake " +
            "that will cause wrong computation result or even runtime error. " +
            "Make sure you fully read and understand documentation.",
    level = RequiresOptIn.Level.WARNING
)
internal annotation class DelicatePolynomialAPI