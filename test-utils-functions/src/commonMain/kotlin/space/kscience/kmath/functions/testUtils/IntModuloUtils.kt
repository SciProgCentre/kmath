/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.functions.testUtils

import space.kscience.kmath.functions.Polynomial
import space.kscience.kmath.functions.ListPolynomialSpace


public fun ListPolynomialSpace<IntModulo, IntModuloRing>.ListPolynomial(vararg coefs: Int): Polynomial<IntModulo> =
    Polynomial(coefs.map { IntModulo(it, ring.modulus) })
public fun IntModuloRing.ListPolynomial(vararg coefs: Int): Polynomial<IntModulo> =
    Polynomial(coefs.map { IntModulo(it, modulus) })

public fun IntModuloRing.m(arg: Int): IntModulo = IntModulo(arg, modulus)
public fun ListPolynomialSpace<IntModulo, IntModuloRing>.m(arg: Int): IntModulo = IntModulo(arg, ring.modulus)