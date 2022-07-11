/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.functions


/**
 * Constructs a [Polynomial] instance with provided [coefficients]. The collection of coefficients will be reversed
 * if [reverse] parameter is true.
 */
@Suppress("FunctionName")
public fun <C> ListPolynomial(coefficients: List<C>, reverse: Boolean = false): Polynomial<C> =
    Polynomial(with(coefficients) { if (reverse) reversed() else this })

/**
 * Constructs a [Polynomial] instance with provided [coefficients]. The collection of coefficients will be reversed
 * if [reverse] parameter is true.
 */
@Suppress("FunctionName")
public fun <C> ListPolynomial(vararg coefficients: C, reverse: Boolean = false): Polynomial<C> =
    Polynomial(with(coefficients) { if (reverse) reversed() else toList() })

/**
 * Represents [this] constant as a [Polynomial].
 */
public fun <C> C.asListPolynomial() : Polynomial<C> = Polynomial(listOf(this))