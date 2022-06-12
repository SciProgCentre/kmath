/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.functions

import space.kscience.kmath.operations.Ring


/**
 * Returns a [ListPolynomial] instance with given [coefficients]. The collection of coefficients will be reversed if
 * [reverse] parameter is true.
 */
@Suppress("FunctionName")
public fun <C> ListPolynomial(coefficients: List<C>, reverse: Boolean = false): ListPolynomial<C> =
    ListPolynomial(with(coefficients) { if (reverse) reversed() else this })

/**
 * Returns a [ListPolynomial] instance with given [coefficients]. The collection of coefficients will be reversed if
 * [reverse] parameter is true.
 */
@Suppress("FunctionName")
public fun <C> ListPolynomial(vararg coefficients: C, reverse: Boolean = false): ListPolynomial<C> =
    ListPolynomial(with(coefficients) { if (reverse) reversed() else toList() })

public fun <C> C.asListPolynomial() : ListPolynomial<C> = ListPolynomial(listOf(this))


// Waiting for context receivers :( FIXME: Replace with context receivers when they will be available

@Suppress("FunctionName")
public fun <C> ListRationalFunction(numeratorCoefficients: List<C>, denominatorCoefficients: List<C>, reverse: Boolean = false): ListRationalFunction<C> =
    ListRationalFunction<C>(
        ListPolynomial( with(numeratorCoefficients) { if (reverse) reversed() else this } ),
        ListPolynomial( with(denominatorCoefficients) { if (reverse) reversed() else this } )
    )
@Suppress("FunctionName")
public fun <C, A: Ring<C>> ListRationalFunctionSpace<C, A>.ListRationalFunction(numerator: ListPolynomial<C>): ListRationalFunction<C> =
    ListRationalFunction<C>(numerator, polynomialOne)
@Suppress("FunctionName")
public fun <C, A: Ring<C>> A.ListRationalFunction(numerator: ListPolynomial<C>): ListRationalFunction<C> =
    ListRationalFunction<C>(numerator, ListPolynomial(listOf(one)))
@Suppress("FunctionName")
public fun <C, A: Ring<C>> ListRationalFunctionSpace<C, A>.ListRationalFunction(numeratorCoefficients: List<C>, reverse: Boolean = false): ListRationalFunction<C> =
    ListRationalFunction<C>(
        ListPolynomial( with(numeratorCoefficients) { if (reverse) reversed() else this } ),
        polynomialOne
    )
@Suppress("FunctionName")
public fun <C, A: Ring<C>> A.ListRationalFunction(numeratorCoefficients: List<C>, reverse: Boolean = false): ListRationalFunction<C> =
    ListRationalFunction<C>(
        ListPolynomial( with(numeratorCoefficients) { if (reverse) reversed() else this } ),
        ListPolynomial(listOf(one))
    )

//context(A)
//public fun <C, A: Ring<C>> C.asListRationalFunction() : ListRationalFunction<C> = ListRationalFunction(asListPolynomial())
//context(ListRationalFunctionSpace<C, A>)
//public fun <C, A: Ring<C>> C.asListRationalFunction() : ListRationalFunction<C> = ListRationalFunction(asListPolynomial())