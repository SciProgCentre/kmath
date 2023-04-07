/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.operations

import space.kscience.kmath.UnstableKMathAPI
import space.kscience.kmath.expressions.Symbol
import space.kscience.kmath.expressions.symbol

/**
 * An algebra for generic boolean logic
 */
@UnstableKMathAPI
public interface LogicAlgebra<T : Any> : Algebra<T> {

    /**
     * Represent constant [Boolean] as [T]
     */
    public fun const(boolean: Boolean): T

    override fun bindSymbolOrNull(value: String): T? = value.lowercase().toBooleanStrictOrNull()?.let(::const)

    override fun unaryOperation(operation: String, arg: T): T = when (operation) {
        Boolean::not.name -> arg.not()
        else -> super.unaryOperation(operation, arg)
    }

    override fun unaryOperationFunction(operation: String): (arg: T) -> T = { unaryOperation(operation, it) }

    override fun binaryOperation(operation: String, left: T, right: T): T = when (operation) {
        Boolean::and.name -> left.and(right)
        Boolean::or.name -> left.or(right)
        else -> super.binaryOperation(operation, left, right)
    }

    override fun binaryOperationFunction(operation: String): (left: T, right: T) -> T = { l, r ->
        binaryOperation(operation, l, r)
    }

    /**
     * Logic 'not'
     */
    public operator fun T.not(): T

    /**
     * Logic 'and'
     */
    public infix fun T.and(other: T): T

    /**
     * Logic 'or'
     */
    public infix fun T.or(other: T): T

    /**
     * Logic 'xor'
     */
    public infix fun T.xor(other: T): T


    public companion object {
        public val TRUE: Symbol by symbol
        public val FALSE: Symbol by symbol
    }
}

/**
 * An implementation of [LogicAlgebra] for primitive booleans
 */
@UnstableKMathAPI
@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
public object BooleanAlgebra : LogicAlgebra<Boolean> {

    override fun const(boolean: Boolean): Boolean = boolean

    override fun Boolean.not(): Boolean = !this

    override fun Boolean.and(other: Boolean): Boolean = this && other

    override fun Boolean.or(other: Boolean): Boolean = this || other

    override fun Boolean.xor(other: Boolean): Boolean = this xor other
}