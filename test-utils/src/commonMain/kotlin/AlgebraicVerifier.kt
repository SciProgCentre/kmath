/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.testutils

import space.kscience.kmath.operations.Algebra

internal interface AlgebraicVerifier<T, out A> where A : Algebra<T> {
    val algebra: A

    fun verify()
}
