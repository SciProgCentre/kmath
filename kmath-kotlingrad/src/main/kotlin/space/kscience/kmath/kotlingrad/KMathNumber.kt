/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.kotlingrad

import ai.hypergraph.kotlingrad.api.SConst
import space.kscience.kmath.operations.NumericAlgebra

/**
 * Implements [SConst] by delegating its functionality to [NumericAlgebra].
 *
 * @param T The type of number.
 * @param A The [NumericAlgebra] over [T].
 * @property algebra The algebra.
 * @property value The value of this number.
 */
public class KMathNumber<T, A>(public val algebra: A, override val value: T) :
    SConst<KMathNumber<T, A>>(value) where T : Number, A : NumericAlgebra<T> {
    /**
     * Returns a string representation of the [value].
     */
    override fun toString(): String = value.toString()

    /**
     * Wraps [Number] to [KMathNumber].
     */
    override fun wrap(number: Number): KMathNumber<T, A> = KMathNumber(algebra, algebra.number(number))
}
