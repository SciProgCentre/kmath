/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.expressions

import kotlin.jvm.JvmInline
import kotlin.properties.ReadOnlyProperty

/**
 * A marker interface for a symbol. A symbol must have an identity with equality relation based on it.
 * Other properties are to store additional, transient data only.
 */
public interface Symbol : MST {
    /**
     * Identity object for the symbol. Two symbols with the same identity are considered to be the same symbol.
     */
    public val identity: String

    public companion object {
        public val x: Symbol = Symbol("x")
        public val xError: Symbol = Symbol("x.error")
        public val y: Symbol = Symbol("y")
        public val yError: Symbol = Symbol("y.error")
        public val z: Symbol = Symbol("z")
        public val zError: Symbol = Symbol("z.error")
    }
}

/**
 * A [Symbol] with a [String] identity
 */
@JvmInline
internal value class StringSymbol(override val identity: String) : Symbol {
    override fun toString(): String = identity
}

/**
 * Create s Symbols with a string identity
 */
public fun Symbol(identity: String): Symbol = StringSymbol(identity)

/**
 * A delegate to create a symbol with a string identity in this scope
 */
public val symbol: ReadOnlyProperty<Any?, Symbol> = ReadOnlyProperty { _, property ->
    StringSymbol(property.name)
}

/**
 * Ger a value from a [String]-keyed map by a [Symbol]
 */
public operator fun <T> Map<String, T>.get(symbol: Symbol): T? = get(symbol.identity)

/**
 * Set a value of [String]-keyed map by a [Symbol]
 */
public operator fun <T> MutableMap<String, T>.set(symbol: Symbol, value: T) {
    set(symbol.identity, value)
}

/**
 * Get a value from a [Symbol]-keyed map by a [String]
 */
public operator fun <T> Map<Symbol, T>.get(string: String): T? = get(StringSymbol(string))

/**
 * Set a value of [String]-keyed map by a [Symbol]
 */
public operator fun <T> MutableMap<Symbol, T>.set(string: String, value: T) {
    set(StringSymbol(string), value)
}