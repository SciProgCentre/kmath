/*
 * Copyright 2018-2021 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.misc

import kotlin.jvm.JvmInline
import kotlin.properties.ReadOnlyProperty

/**
 * A marker interface for a symbol. A symbol mus have an identity
 */
public interface Symbol {
    /**
     * Identity object for the symbol. Two symbols with the same identity are considered to be the same symbol.
     */
    public val identity: String

    public companion object{
        public val x: StringSymbol = StringSymbol("x")
        public val y: StringSymbol = StringSymbol("y")
        public val z: StringSymbol = StringSymbol("z")
    }
}

/**
 * A [Symbol] with a [String] identity
 */
@JvmInline
public value class StringSymbol(override val identity: String) : Symbol {
    override fun toString(): String = identity
}

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
public operator fun <T> MutableMap<String, T>.set(symbol: Symbol, value: T){
    set(symbol.identity, value)
}

/**
 * Get a value from a [Symbol]-keyed map by a [String]
 */
public operator fun <T> Map<Symbol, T>.get(string: String): T? = get(StringSymbol(string))

/**
 * Set a value of [String]-keyed map by a [Symbol]
 */
public operator fun <T> MutableMap<Symbol, T>.set(string: String, value: T){
    set(StringSymbol(string), value)
}