/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.multik

import org.junit.jupiter.api.Test
import space.kscience.kmath.nd.one
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.operations.invoke

internal class MultikNDTest {
    @Test
    fun basicAlgebra(): Unit = DoubleField.multikAlgebra{
        one(2,2) + 1.0
    }
}