/*
 * Copyright 2018-2023 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.attributes

/**
 * A builder for [Attributes].
 * The builder is not thread safe
 *
 * @param O type marker of an owner object, for which these attributes are made
 */
public class AttributesBuilder<out O> internal constructor() : Attributes {

    private val map = mutableMapOf<Attribute<*>, Any?>()

    override fun toString(): String = "Attributes(value=${map.entries})"
    override fun equals(other: Any?): Boolean = other is Attributes && Attributes.equals(this, other)
    override fun hashCode(): Int = map.hashCode()

    override val content: Map<out Attribute<*>, Any?> get() = map

    public operator fun <T> set(attribute: Attribute<T>, value: T?) {
        if (value == null) {
            map.remove(attribute)
        } else {
            map[attribute] = value
        }
    }

    public operator fun <V> Attribute<V>.invoke(value: V?) {
        set(this, value)
    }

    public infix fun <V> Attribute<V>.put(value: V?) {
        set(this, value)
    }

    /**
     * Put all attributes for given [attributes]
     */
    public fun putAll(attributes: Attributes) {
        map.putAll(attributes.content)
    }

    public infix fun <V> SetAttribute<V>.add(attrValue: V) {
        val currentSet: Set<V> = get(this) ?: emptySet()
        map[this] = currentSet + attrValue
    }

    /**
     * Remove an element from [SetAttribute]
     */
    public infix fun <V> SetAttribute<V>.remove(attrValue: V) {
        val currentSet: Set<V> = get(this) ?: emptySet()
        map[this] = currentSet - attrValue
    }

    public fun build(): Attributes = MapAttributes(map)
}

/**
 * Create [Attributes] with a given [builder]
 * @param O the type for which attributes are built. The type is used only during compilation phase for static extension dispatch
 */
public fun <O> Attributes(builder: AttributesBuilder<O>.() -> Unit): Attributes =
    AttributesBuilder<O>().apply(builder).build()