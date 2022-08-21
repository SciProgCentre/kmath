/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("LocalVariableName")

package space.kscience.kmath.functions

import space.kscience.kmath.expressions.Symbol
import space.kscience.kmath.expressions.symbol
import space.kscience.kmath.operations.algebra
import space.kscience.kmath.operations.invoke


/**
 * Shows [ListPolynomial]s' and [ListRationalFunction]s' capabilities.
 */
fun listPolynomialsExample() {
    // [ListPolynomial] is a representation of a univariate polynomial as a list of coefficients from the least term to
    // the greatest term. For example,
    val polynomial1: ListPolynomial<Int> = ListPolynomial(listOf(2, -3, 1))
    // represents polynomial 2 + (-3) x + x^2

    // There are also shortcut fabrics:
    val polynomial2: ListPolynomial<Int> = ListPolynomial(2, -3, 1)
    println(polynomial1 == polynomial2) // true
    // and even
    val polynomial3: ListPolynomial<Int> = 57.asListPolynomial()
    val polynomial4: ListPolynomial<Int> = ListPolynomial(listOf(57))
    println(polynomial3 == polynomial4) // true

    val polynomial5: ListPolynomial<Int> = ListPolynomial(3, -1)
    // For every ring there can be provided a polynomial ring:
    Int.algebra.listPolynomialSpace {
        println(-polynomial5 == ListPolynomial(-3, 1)) // true
        println(polynomial1 + polynomial5 == ListPolynomial(5, -4, 1)) // true
        println(polynomial1 - polynomial5 == ListPolynomial(-1, -2, 1)) // true
        println(polynomial1 * polynomial5 == ListPolynomial(6, -11, 6, -1)) // true
    }
    // You can even write
    val x: ListPolynomial<Double> = ListPolynomial(0.0, 1.0)
    val polynomial6: ListPolynomial<Double> = ListPolynomial(2.0, -3.0, 1.0)
    Double.algebra.listPolynomialSpace {
        println(2 - 3 * x + x * x == polynomial6)
        println(2.0 - 3.0 * x + x * x == polynomial6)
    }

    // Also there are some utilities for polynomials:
    println(polynomial1.substitute(Int.algebra, 1) == 0) // true, because 2 + (-3) * 1 + 1^2 = 0
    println(polynomial1.substitute(Int.algebra, polynomial5) == polynomial1) // true, because 2 + (-3) * (3-x) + (3-x)^2 = 2 - 3x + x^2
    println(polynomial1.derivative(Int.algebra) == ListPolynomial(-3, 2)) // true, (2 - 3x + x^2)' = -3 + 2x
    println(polynomial1.nthDerivative(Int.algebra, 2) == 2.asListPolynomial()) // true, (2 - 3x + x^2)'' = 2

    // Lastly, there are rational functions and some other utilities:
    Double.algebra.listRationalFunctionSpace {
        val rationalFunction1: ListRationalFunction<Double> = ListRationalFunction(listOf(2.0, -3.0, 1.0), listOf(3.0, -1.0))
        // It's just (2 - 3x + x^2)/(3 - x)

        val rationalFunction2 : ListRationalFunction<Double> = ListRationalFunction(listOf(5.0, -4.0, 1.0), listOf(3.0, -1.0))
        // It's just (5 - 4x + x^2)/(3 - x)

        println(rationalFunction1 + 1 == rationalFunction2)
    }
}

/**
 * Shows [NumberedPolynomial]s' and [NumberedRationalFunction]s' capabilities.
 */
fun numberedPolynomialsExample() {
    // Consider polynomial
    // 3 + 5 x_2 - 7 x_1^2 x_3
    // Consider, for example, its term -7 x_1^2 x_3. -7 is a coefficient of the term, whereas (2, 0, 1, 0, 0, ...) is
    // description of degrees of variables x_1, x_2, ... in the term. Such description with removed leading zeros
    // [2, 0, 1] is called "signature" of the term -7 x_1^2 x_3.

    val polynomial1: NumberedPolynomial<Int>
    with(Int.algebra) {
        // [NumberedPolynomial] is a representation of a multivariate polynomial, that stores terms in a map with terms'
        // signatures as the map's keys and terms' coefficients as corresponding values. For example,
        polynomial1 = NumberedPolynomial(
            mapOf(
                listOf<UInt>() to 3,
                listOf(0u, 1u) to 5,
                listOf(2u, 0u, 1u) to -7,
            )
        )
        // represents polynomial 3 + 5 x_2 - 7 x_1^2 x_3

        // This `NumberedPolynomial` function needs context of either ring of constant (as `Int.algebra` in this example)
        // or space of NumberedPolynomials over it. To understand why it is like this see documentations of functions
        // NumberedPolynomial and NumberedPolynomialWithoutCheck

        // There are also shortcut fabrics:
        val polynomial2: NumberedPolynomial<Int> = NumberedPolynomial(
            listOf<UInt>() to 3,
            listOf(0u, 1u) to 5,
            listOf(2u, 0u, 1u) to -7,
        )
        println(polynomial1 == polynomial2) // true
        // and even
        val polynomial3: NumberedPolynomial<Int> = 57.asNumberedPolynomial() // This one actually does not algebraic context!
        val polynomial4: NumberedPolynomial<Int> = NumberedPolynomial(listOf<UInt>() to 57)
        println(polynomial3 == polynomial4) // true

        numberedPolynomialSpace {
            // Also there is DSL for constructing NumberedPolynomials:
            val polynomial5: NumberedPolynomial<Int> = NumberedPolynomialDSL1 {
                3 {}
                5 { 1 inPowerOf 1u }
                -7 with { 0 pow 2u; 2 pow 1u }
                // `pow` and `inPowerOf` are the same
                // `with` is omittable
            }
            println(polynomial1 == polynomial5) // true

            // Unfortunately the DSL does not work good in bare context of constants' ring, so for now it's disabled and
            // works only in NumberedPolynomialSpace and NumberedRationalFunctionSpace
        }
    }

    val polynomial6: NumberedPolynomial<Int> = Int.algebra {
        NumberedPolynomial(
            listOf<UInt>() to 7,
            listOf(0u, 1u) to -5,
            listOf(2u, 0u, 1u) to 0,
            listOf(0u, 0u, 0u, 4u) to 4,
        )
    }
    // For every ring there can be provided a polynomial ring:
    Int.algebra.numberedPolynomialSpace {
        println(
            -polynomial6 == NumberedPolynomial(
                listOf<UInt>() to -7,
                listOf(0u, 1u) to 5,
                listOf(2u, 0u, 1u) to 0,
                listOf(0u, 0u, 0u, 4u) to (-4),
            )
        ) // true
        println(
            polynomial1 + polynomial6 == NumberedPolynomial(
                listOf<UInt>() to 10,
                listOf(0u, 1u) to 0,
                listOf(2u, 0u, 1u) to -7,
                listOf(0u, 0u, 0u, 4u) to 4,
            )
        ) // true
        println(
            polynomial1 - polynomial6 == NumberedPolynomial(
                listOf<UInt>() to -4,
                listOf(0u, 1u) to 10,
                listOf(2u, 0u, 1u) to -7,
                listOf(0u, 0u, 0u, 4u) to -4,
            )
        ) // true

        polynomial1 * polynomial6 // Multiplication works too
    }

    Double.algebra.numberedPolynomialSpace {
        // You can even write
        val x_1: NumberedPolynomial<Double> = NumberedPolynomial(listOf(1u) to 1.0)
        val x_2: NumberedPolynomial<Double> = NumberedPolynomial(listOf(0u, 1u) to 1.0)
        val x_3: NumberedPolynomial<Double> = NumberedPolynomial(listOf(0u, 0u, 1u) to 1.0)
        val polynomial7: NumberedPolynomial<Double> = NumberedPolynomial(
            listOf<UInt>() to 3.0,
            listOf(0u, 1u) to 5.0,
            listOf(2u, 0u, 1u) to -7.0,
        )
        Double.algebra.listPolynomialSpace {
            println(3 + 5 * x_2 - 7 * x_1 * x_1 * x_3 == polynomial7)
            println(3.0 + 5.0 * x_2 - 7.0 * x_1 * x_1 * x_3 == polynomial7)
        }
    }

    Int.algebra.numberedPolynomialSpace {
        val x_4: NumberedPolynomial<Int> = NumberedPolynomial(listOf(0u, 0u, 0u, 4u) to 1)
        // Also there are some utilities for polynomials:
        println(polynomial1.substitute(mapOf(0 to 1, 1 to -2, 2 to -1)) == 0.asNumberedPolynomial()) // true,
            // because it's substitution x_1 -> 1, x_2 -> -2, x_3 -> -1,
            // so 3 + 5 x_2 - 7 x_1^2 x_3 = 3 + 5 * (-2) - 7 * 1^2 * (-1) = 3 - 10 + 7 = 0
        println(
            polynomial1.substitute(mapOf(1 to x_4)) == NumberedPolynomial(
                listOf<UInt>() to 3,
                listOf(0u, 1u) to 5,
                listOf(2u, 0u, 1u) to -7,
            )
        ) // true, because it's substitution x_2 -> x_4, so result is 3 + 5 x_4 - 7 x_1^2 x_3
        println(
            polynomial1.derivativeWithRespectTo(Int.algebra, 1) ==
                    NumberedPolynomial(listOf<UInt>() to 5)
        ) // true, d/dx_2 (3 + 5 x_2 - 7 x_1^2 x_3) = 5
    }

    // Lastly, there are rational functions and some other utilities:
    Double.algebra.numberedRationalFunctionSpace {
        val rationalFunction1: NumberedRationalFunction<Double> = NumberedRationalFunction(
            NumberedPolynomial(
                listOf<UInt>() to 2.0,
                listOf(1u) to -3.0,
                listOf(2u) to 1.0,
            ),
            NumberedPolynomial(
                listOf<UInt>() to 3.0,
                listOf(1u) to -1.0,
            )
        )
        // It's just (2 - 3x + x^2)/(3 - x) where x = x_1

        val rationalFunction2: NumberedRationalFunction<Double> = NumberedRationalFunction(
            NumberedPolynomial(
                listOf<UInt>() to 5.0,
                listOf(1u) to -4.0,
                listOf(2u) to 1.0,
            ),
            NumberedPolynomial(
                listOf<UInt>() to 3.0,
                listOf(1u) to -1.0,
            )
        )
        // It's just (5 - 4x + x^2)/(3 - x) where x = x_1

        println(rationalFunction1 + 1 == rationalFunction2)
    }
}

/**
 * Shows [LabeledPolynomial]s' and [LabeledRationalFunction]s' capabilities.
 */
fun labeledPolynomialsExample() {
    val x by symbol
    val y by symbol
    val z by symbol
    val t by symbol

    // Consider polynomial
    // 3 + 5 y - 7 x^2 z
    // Consider, for example, its term -7 x^2 z. -7 is a coefficient of the term, whereas matching (x -> 2, z -> 3) is
    // description of degrees of variables x_1, x_2, ... in the term. Such description is called "signature" of the
    // term -7 x_1^2 x_3.

    val polynomial1: LabeledPolynomial<Int>
    with(Int.algebra) {
        // [LabeledPolynomial] is a representation of a multivariate polynomial, that stores terms in a map with terms'
        // signatures as the map's keys and terms' coefficients as corresponding values. For example,
        polynomial1 = LabeledPolynomial(
            mapOf(
                mapOf<Symbol, UInt>() to 3,
                mapOf(y to 1u) to 5,
                mapOf(x to 2u, z to 1u) to -7,
            )
        )
        // represents polynomial 3 + 5 y - 7 x^2 z

        // This `LabeledPolynomial` function needs context of either ring of constant (as `Int.algebra` in this example)
        // or space of LabeledPolynomials over it. To understand why it is like this see documentations of functions
        // LabeledPolynomial and LabeledPolynomialWithoutCheck

        // There are also shortcut fabrics:
        val polynomial2: LabeledPolynomial<Int> = LabeledPolynomial(
            mapOf<Symbol, UInt>() to 3,
            mapOf(y to 1u) to 5,
            mapOf(x to 2u, z to 1u) to -7,
        )
        println(polynomial1 == polynomial2) // true
        // and even
        val polynomial3: LabeledPolynomial<Int> = 57.asLabeledPolynomial() // This one actually does not algebraic context!
        val polynomial4: LabeledPolynomial<Int> = LabeledPolynomial(mapOf<Symbol, UInt>() to 57)
        println(polynomial3 == polynomial4) // true

        labeledPolynomialSpace {
            // Also there is DSL for constructing NumberedPolynomials:
            val polynomial5: LabeledPolynomial<Int> = LabeledPolynomialDSL1 {
                3 {}
                5 { y inPowerOf 1u }
                -7 with { x pow 2u; z pow 1u }
                // `pow` and `inPowerOf` are the same
                // `with` is omittable
            }
            println(polynomial1 == polynomial5) // true

            // Unfortunately the DSL does not work good in bare context of constants' ring, so for now it's disabled and
            // works only in NumberedPolynomialSpace and NumberedRationalFunctionSpace
        }
    }

    val polynomial6: LabeledPolynomial<Int> = Int.algebra {
        LabeledPolynomial(
            mapOf<Symbol, UInt>() to 7,
            mapOf(y to 1u) to -5,
            mapOf(x to 2u, z to 1u) to 0,
            mapOf(t to 4u) to 4,
        )
    }
    // For every ring there can be provided a polynomial ring:
    Int.algebra.labeledPolynomialSpace {
        println(
            -polynomial6 == LabeledPolynomial(
                mapOf<Symbol, UInt>() to -7,
                mapOf(y to 1u) to 5,
                mapOf(x to 2u, z to 1u) to 0,
                mapOf(t to 4u) to -4,
            )
        ) // true
        println(
            polynomial1 + polynomial6 == LabeledPolynomial(
                mapOf<Symbol, UInt>() to 10,
                mapOf(y to 1u) to 0,
                mapOf(x to 2u, z to 1u) to -7,
                mapOf(t to 4u) to 4,
            )
        ) // true
        println(
            polynomial1 - polynomial6 == LabeledPolynomial(
                mapOf<Symbol, UInt>() to -4,
                mapOf(y to 1u) to 10,
                mapOf(x to 2u, z to 1u) to -7,
                mapOf(t to 4u) to -4,
            )
        ) // true

        polynomial1 * polynomial6 // Multiplication works too
    }

    Double.algebra.labeledPolynomialSpace {
        // You can even write
        val polynomial7: LabeledPolynomial<Double> = LabeledPolynomial(
            mapOf<Symbol, UInt>() to 3.0,
            mapOf(y to 1u) to 5.0,
            mapOf(x to 2u, z to 1u) to -7.0,
        )
        Double.algebra.listPolynomialSpace {
            println(3 + 5 * y - 7 * x * x * z == polynomial7)
            println(3.0 + 5.0 * y - 7.0 * x * x * z == polynomial7)
        }
    }

    Int.algebra.labeledPolynomialSpace {
        // Also there are some utilities for polynomials:
        println(polynomial1.substitute(mapOf(x to 1, y to -2, z to -1)) == 0.asLabeledPolynomial()) // true,
        // because it's substitution x -> 1, y -> -2, z -> -1,
        // so 3 + 5 y - 7 x^2 z = 3 + 5 * (-2) - 7 * 1^2 * (-1) = 3 - 10 + 7 = 0
        println(
            polynomial1.substitute(mapOf(y to t.asPolynomial())) == LabeledPolynomial(
                mapOf<Symbol, UInt>() to 3,
                mapOf(t to 1u) to 5,
                mapOf(x to 2u, z to 1u) to -7,
                )
        ) // true, because it's substitution y -> t, so result is 3 + 5 t - 7 x^2 z
        println(
            polynomial1.derivativeWithRespectTo(Int.algebra, y) == LabeledPolynomial(mapOf<Symbol, UInt>() to 5)
        ) // true, d/dy (3 + 5 y - 7 x^2 z) = 5
    }

    // Lastly, there are rational functions and some other utilities:
    Double.algebra.labeledRationalFunctionSpace {
        val rationalFunction1: LabeledRationalFunction<Double> = LabeledRationalFunction(
            LabeledPolynomial(
                mapOf<Symbol, UInt>() to 2.0,
                mapOf(x to 1u) to -3.0,
                mapOf(x to 2u) to 1.0,
            ),
            LabeledPolynomial(
                mapOf<Symbol, UInt>() to 3.0,
                mapOf(x to 1u) to -1.0,
            )
        )
        // It's just (2 - 3x + x^2)/(3 - x)

        val rationalFunction2: LabeledRationalFunction<Double> = LabeledRationalFunction(
            LabeledPolynomial(
                mapOf<Symbol, UInt>() to 5.0,
                mapOf(x to 1u) to -4.0,
                mapOf(x to 2u) to 1.0,
            ),
            LabeledPolynomial(
                mapOf<Symbol, UInt>() to 3.0,
                mapOf(x to 1u) to -1.0,
            )
        )
        // It's just (5 - 4x + x^2)/(3 - x)

        println(rationalFunction1 + 1 == rationalFunction2)
    }
}

fun main() {
    println("ListPolynomials:")
    listPolynomialsExample()
    println()

    println("NumberedPolynomials:")
    numberedPolynomialsExample()
    println()

    println("ListPolynomials:")
    labeledPolynomialsExample()
    println()
}
