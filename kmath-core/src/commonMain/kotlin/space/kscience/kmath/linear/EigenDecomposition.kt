/*
 * Copyright 2018-2024 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.linear

import space.kscience.attributes.PolymorphicAttribute
import space.kscience.attributes.safeTypeOf

public interface EigenDecomposition<T> {
    /**
     * Eigenvector matrix.
     */
    public val v: Matrix<T>

    /**
     * A diagonal matrix of eigenvalues. Must have [IsDiagonal]
     */
    public val d: Matrix<T>
}

public class EigenDecompositionAttribute<T> :
    PolymorphicAttribute<EigenDecomposition<T>>(safeTypeOf()),
    MatrixAttribute<EigenDecomposition<T>>

public val <T> MatrixScope<T>.EIG: EigenDecompositionAttribute<T>
    get() = EigenDecompositionAttribute()
