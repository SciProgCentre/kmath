/*
 * Copyright 2018-2025 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.ojalgo

import org.ojalgo.matrix.decomposition.*
import org.ojalgo.matrix.store.PhysicalStore
import org.ojalgo.matrix.store.R064Store
import space.kscience.kmath.operations.Float64Field
import space.kscience.kmath.operations.Ring
import space.kscience.kmath.structures.Float64

public class Ojalgo<T : Comparable<T>, A : Ring<T>>(
    public val elementAlgebra: A,
    public val storeFactory: PhysicalStore.Factory<T, *>,
    public val lu: LU.Factory<T>,
    public val cholesky: Cholesky.Factory<T>,
    public val qr: QR.Factory<T>,
    public val svd: SingularValue.Factory<T>,
    public val eigen: Eigenvalue.Factory<T>
) {
    public companion object {
        public val R064: Ojalgo<Float64, Float64Field> = Ojalgo(
            elementAlgebra = Float64Field,
            storeFactory = R064Store.FACTORY,
            lu = LU.R064,
            cholesky = Cholesky.R064,
            qr = QR.R064,
            svd = SingularValue.R064,
            eigen = Eigenvalue.R064
        )
    }
}