@file:JvmName("MapIntrinsics")

package kscience.kmath.asm.internal

import kscience.kmath.expressions.StringSymbol
import kscience.kmath.expressions.Symbol

/**
 * Gets value with given [key] or throws [NoSuchElementException] whenever it is not present.
 *
 * @author Iaroslav Postovalov
 */
internal fun <V> Map<Symbol, V>.getOrFail(key: String): V = getValue(StringSymbol(key))
