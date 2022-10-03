/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("NOTHING_TO_INLINE", "KotlinRedundantDiagnosticSuppress")

package space.kscience.kmath.functions.testUtils

import space.kscience.kmath.operations.Ring
import space.kscience.kmath.operations.ScaleOperations


class IntModulo {
    val residue: Int
    val modulus: Int

    @PublishedApi
    internal constructor(residue: Int, modulus: Int, toCheckInput: Boolean = true) {
        if (toCheckInput) {
            require(modulus != 0) { "modulus can not be zero" }
            this.modulus = if (modulus < 0) -modulus else modulus
            this.residue = residue.mod(this.modulus)
        } else {
            this.residue = residue
            this.modulus = modulus
        }
    }

    constructor(residue: Int, modulus: Int) : this(residue, modulus, true)

    operator fun unaryPlus(): IntModulo = this
    operator fun unaryMinus(): IntModulo =
        IntModulo(
            if (residue == 0) 0 else modulus - residue,
            modulus,
            toCheckInput = false
        )
    operator fun plus(other: IntModulo): IntModulo {
        require(modulus == other.modulus) { "can not add two residue different modulo" }
        return IntModulo(
            (residue + other.residue) % modulus,
            modulus,
            toCheckInput = false
        )
    }
    operator fun plus(other: Int): IntModulo =
        IntModulo(
            (residue + other) % modulus,
            modulus,
            toCheckInput = false
        )
    operator fun minus(other: IntModulo): IntModulo {
        require(modulus == other.modulus) { "can not subtract two residue different modulo" }
        return IntModulo(
            (residue - other.residue) % modulus,
            modulus,
            toCheckInput = false
        )
    }
    operator fun minus(other: Int): IntModulo =
        IntModulo(
            (residue - other) % modulus,
            modulus,
            toCheckInput = false
        )
    operator fun times(other: IntModulo): IntModulo {
        require(modulus == other.modulus) { "can not multiply two residue different modulo" }
        return IntModulo(
            (residue * other.residue) % modulus,
            modulus,
            toCheckInput = false
        )
    }
    operator fun times(other: Int): IntModulo =
        IntModulo(
            (residue * other) % modulus,
            modulus,
            toCheckInput = false
        )
    operator fun div(other: IntModulo): IntModulo {
        require(modulus == other.modulus) { "can not divide two residue different modulo" }
        val (reciprocalCandidate, gcdOfOtherResidueAndModulus) = bezoutIdentityWithGCD(other.residue, modulus)
        require(gcdOfOtherResidueAndModulus == 1) { "can not divide to residue that has non-trivial GCD with modulo" }
        return IntModulo(
            (residue * reciprocalCandidate) % modulus,
            modulus,
            toCheckInput = false
        )
    }
    operator fun div(other: Int): IntModulo {
        val (reciprocalCandidate, gcdOfOtherResidueAndModulus) = bezoutIdentityWithGCD(other, modulus)
        require(gcdOfOtherResidueAndModulus == 1) { "can not divide to residue that has non-trivial GCD with modulo" }
        return IntModulo(
            (residue * reciprocalCandidate) % modulus,
            modulus,
            toCheckInput = false
        )
    }
    override fun equals(other: Any?): Boolean =
        when (other) {
            is IntModulo -> residue == other.residue && modulus == other.modulus
            else -> false
        }

    override fun hashCode(): Int = residue.hashCode()

    override fun toString(): String = "$residue mod $modulus"
}

@Suppress("EXTENSION_SHADOWED_BY_MEMBER", "OVERRIDE_BY_INLINE")
class IntModuloRing : Ring<IntModulo>, ScaleOperations<IntModulo> {

    val modulus: Int

    constructor(modulus: Int) {
        require(modulus != 0) { "modulus can not be zero" }
        this.modulus = if (modulus < 0) -modulus else modulus
    }

    override inline val zero: IntModulo get() = IntModulo(0, modulus, toCheckInput = false)
    override inline val one: IntModulo get() = IntModulo(1, modulus, toCheckInput = false)

    fun number(arg: Int): IntModulo = IntModulo(arg, modulus, toCheckInput = false)

    override inline fun add(left: IntModulo, right: IntModulo): IntModulo = left + right
    override inline fun multiply(left: IntModulo, right: IntModulo): IntModulo = left * right

    override inline fun IntModulo.unaryMinus(): IntModulo = -this
    override inline fun IntModulo.plus(arg: IntModulo): IntModulo = this + arg
    override inline fun IntModulo.minus(arg: IntModulo): IntModulo = this - arg
    override inline fun IntModulo.times(arg: IntModulo): IntModulo = this * arg
    inline fun IntModulo.div(arg: IntModulo): IntModulo = this / arg

    override fun scale(a: IntModulo, value: Double): IntModulo = a * value.toInt()
}