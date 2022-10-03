/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.functions.testUtils

import space.kscience.kmath.functions.Polynomial
import space.kscience.kmath.functions.PolynomialSpace


fun PolynomialSpace<IntModulo, IntModuloRing>.Polynomial(vararg coefs: Int): Polynomial<IntModulo> =
    Polynomial(coefs.map { IntModulo(it, ring.modulus) })
fun IntModuloRing.Polynomial(vararg coefs: Int): Polynomial<IntModulo> =
    Polynomial(coefs.map { IntModulo(it, modulus) })

fun IntModuloRing.m(arg: Int): IntModulo = IntModulo(arg, modulus)
fun PolynomialSpace<IntModulo, IntModuloRing>.m(arg: Int): IntModulo = IntModulo(arg, ring.modulus)