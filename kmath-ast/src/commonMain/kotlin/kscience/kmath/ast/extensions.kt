package kscience.kmath.ast

import kscience.kmath.operations.Algebra
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty

/**
 * Returns [PropertyDelegateProvider] providing [ReadOnlyProperty] of [MST.Symbolic] with its value equal to the name
 * of the property.
 */
public val Algebra<MST>.symbol: PropertyDelegateProvider<Algebra<MST>, ReadOnlyProperty<Algebra<MST>, MST.Symbolic>>
    get() = PropertyDelegateProvider { _, _ -> ReadOnlyProperty { _, p -> MST.Symbolic(p.name) } }
