package kscience.kmath.ast

import kscience.kmath.operations.Algebra
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Stores `provideDelegate` method returning property of [MST.Symbolic].
 */
public object MstSymbolDelegateProvider {
    /**
     * Returns [ReadOnlyProperty] of [MST.Symbolic] with its value equal to the name of the property.
     */
    public operator fun provideDelegate(thisRef: Any?, prop: KProperty<*>): ReadOnlyProperty<Any?, MST.Symbolic> =
        ReadOnlyProperty { _, property -> MST.Symbolic(property.name) }
}

/**
 * Returns [MstSymbolDelegateProvider].
 */
public val Algebra<MST>.symbol: MstSymbolDelegateProvider
    get() = MstSymbolDelegateProvider
