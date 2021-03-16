/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.complex

import space.kscience.kmath.operations.JBigDecimalField
import space.kscience.kmath.operations.JBigIntegerRing
import java.math.BigDecimal
import java.math.BigInteger

/**
 * [ComplexRing] instance for [JBigIntegerRing].
 */
public val ComplexJBigIntegerRing: ComplexRing<BigInteger, JBigIntegerRing> = ComplexRing(JBigIntegerRing)

/**
 * [ComplexRing] instance for [JBigDecimalField].
 */
public val ComplexJBigDecimalField: ComplexField<BigDecimal, JBigDecimalField.Companion> =
    ComplexField(JBigDecimalField)
