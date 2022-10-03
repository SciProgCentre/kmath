/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.functions.testUtils

import space.kscience.kmath.functions.ListPolynomial
import space.kscience.kmath.functions.ListPolynomialSpace
import space.kscience.kmath.functions.PolynomialSpaceOverRing


fun ListPolynomialSpace<IntModulo, IntModuloRing>.ListPolynomial(vararg coefs: Int): ListPolynomial<IntModulo> =
    ListPolynomial(coefs.map { IntModulo(it, ring.modulus) })
fun IntModuloRing.ListPolynomial(vararg coefs: Int): ListPolynomial<IntModulo> =
    ListPolynomial(coefs.map { IntModulo(it, modulus) })

fun IntModuloRing.m(arg: Int): IntModulo = IntModulo(arg, modulus)
fun PolynomialSpaceOverRing<IntModulo, *, IntModuloRing>.m(arg: Int): IntModulo = IntModulo(arg, ring.modulus)