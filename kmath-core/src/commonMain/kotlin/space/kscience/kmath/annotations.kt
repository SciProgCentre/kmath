/*
 * Copyright 2018-2023 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath

/**
 * Marks declarations that are still experimental in the KMath APIs, which means that the design of the corresponding
 * declarations has open issues that may (or may not) lead to their changes in the future. Roughly speaking, there is
 * a chance of those declarations will be deprecated in the near future or the semantics of their behavior may change
 * in some way that may break some code.
 */
@MustBeDocumented
@Retention(value = AnnotationRetention.BINARY)
@RequiresOptIn("This API is unstable and could change in future", RequiresOptIn.Level.WARNING)
public annotation class UnstableKMathAPI

/**
 * Marks API that could cause performance problems. The code marked by this API is unnecessary slow but could cause
 * slow-down in some cases. Refer to the documentation and benchmark it to be sure.
 */
@MustBeDocumented
@Retention(value = AnnotationRetention.BINARY)
@RequiresOptIn(
    "Refer to the documentation to use this API in performance-critical code",
    RequiresOptIn.Level.WARNING,
)
public annotation class PerformancePitfall(
    val message: String = "Potential performance problem",
)

/**
 * Marks API that is public, but should not be used without clear understanding what it does.
 */
@MustBeDocumented
@Retention(value = AnnotationRetention.BINARY)
@RequiresOptIn(
    "This API is unsafe and should be used carefully",
    RequiresOptIn.Level.ERROR,
)
public annotation class UnsafeKMathAPI(
    val message: String = "Unsafe API",
)
