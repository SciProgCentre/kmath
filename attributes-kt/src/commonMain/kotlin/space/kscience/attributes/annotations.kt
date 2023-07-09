/*
 * Copyright 2018-2023 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.attributes

/**
 * Marks declarations that are still experimental in the Attributes-kt APIs, which means that the design of the corresponding
 * declarations has open issues that may (or may not) lead to their changes in the future. Roughly speaking, there is
 * a chance of those declarations will be deprecated in the future or the semantics of their behavior may change
 * in some way that may break some code.
 */
@MustBeDocumented
@Retention(value = AnnotationRetention.BINARY)
@RequiresOptIn("This API is unstable and could change in future", RequiresOptIn.Level.WARNING)
public annotation class UnstableAttributesAPI