/*
 * Copyright 2018-2023 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.attributes

/**
 * A safe builder for [Attributes]
 */
public class AttributesBuilder internal constructor(private val map: MutableMap<Attribute<*>, Any> = mutableMapOf()) {

    @Suppress("UNCHECKED_CAST")
    public operator fun <T> get(attribute: Attribute<T>): T? = map[attribute] as? T

    public operator fun <V> Attribute<V>.invoke(value: V?) {
        if (value == null) {
            map.remove(this)
        } else {
            map[this] = value
        }
    }

    public fun from(attributes: Attributes) {
        map.putAll(attributes.content)
    }

    public fun <V> SetAttribute<V>.add(
        attrValue: V,
    ) {
        val currentSet: Set<V> = get(this) ?: emptySet()
        map[this] = currentSet + attrValue
    }

    /**
     * Remove an element from [SetAttribute]
     */
    public fun <V> SetAttribute<V>.remove(
        attrValue: V,
    ) {
        val currentSet: Set<V> = get(this) ?: emptySet()
        map[this] = currentSet - attrValue
    }

    public fun build(): Attributes = Attributes(map)
}

public fun AttributesBuilder(
    attributes: Attributes,
): AttributesBuilder = AttributesBuilder(attributes.content.toMutableMap())

public fun Attributes(builder: AttributesBuilder.() -> Unit): Attributes = AttributesBuilder().apply(builder).build()