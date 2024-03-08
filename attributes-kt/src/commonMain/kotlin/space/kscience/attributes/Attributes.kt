/*
 * Copyright 2018-2023 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.attributes

import kotlin.jvm.JvmInline

/**
 * A set of attributes. The implementation must guarantee that [content] keys correspond to their value types.
 */
public interface Attributes {
    /**
     * Raw content for this [Attributes]
     */
    public val content: Map<out Attribute<*>, Any?>

    /**
     * Attribute keys contained in this [Attributes]
     */
    public val keys: Set<Attribute<*>> get() = content.keys

    /**
     * Provide an attribute value. Return null if attribute is not present or if its value is null.
     */
    @Suppress("UNCHECKED_CAST")
    public operator fun <T> get(attribute: Attribute<T>): T? = content[attribute] as? T

    public companion object {
        public val EMPTY: Attributes = AttributesImpl(emptyMap())
    }
}

@JvmInline
internal value class AttributesImpl(override val content: Map<out Attribute<*>, Any?>) : Attributes {
    override fun toString(): String = "Attributes(value=${content.entries})"
}

public fun Attributes.isEmpty(): Boolean = content.isEmpty()

/**
 * Get attribute value or default
 */
public fun <T> Attributes.getOrDefault(attribute: AttributeWithDefault<T>): T = get(attribute) ?: attribute.default

/**
 * Check if there is an attribute that matches given key by type and adheres to [predicate].
 */
@Suppress("UNCHECKED_CAST")
public inline fun <T, reified A : Attribute<T>> Attributes.hasAny(predicate: (value: T) -> Boolean): Boolean =
    content.any { (mapKey, mapValue) -> mapKey is A && predicate(mapValue as T) }

/**
 * Check if there is an attribute of given type (subtypes included)
 */
public inline fun <reified A : Attribute<*>> Attributes.hasAny(): Boolean =
    content.any { (mapKey, _) -> mapKey is A }

/**
 * Check if [Attributes] contains a flag. Multiple keys that are instances of a flag could be present
 */
public inline fun <reified A : FlagAttribute> Attributes.hasFlag(): Boolean =
    content.keys.any { it is A }

/**
 * Create [Attributes] with an added or replaced attribute key.
 */
public fun <T, A : Attribute<T>> Attributes.withAttribute(
    attribute: A,
    attrValue: T,
): Attributes = AttributesImpl(content + (attribute to attrValue))

public fun <A : Attribute<Unit>> Attributes.withAttribute(attribute: A): Attributes =
    withAttribute(attribute, Unit)

/**
 * Create a new [Attributes] by modifying the current one
 */
public fun <T> Attributes.modify(block: AttributesBuilder<T>.() -> Unit): Attributes = Attributes<T> {
    from(this@modify)
    block()
}

/**
 * Create new [Attributes] by removing [attribute] key
 */
public fun Attributes.withoutAttribute(attribute: Attribute<*>): Attributes = AttributesImpl(content.minus(attribute))

/**
 * Add an element to a [SetAttribute]
 */
public fun <T, A : SetAttribute<T>> Attributes.withAttributeElement(
    attribute: A,
    attrValue: T,
): Attributes {
    val currentSet: Set<T> = get(attribute) ?: emptySet()
    return AttributesImpl(
        content + (attribute to (currentSet + attrValue))
    )
}

/**
 * Remove an element from [SetAttribute]
 */
public fun <T, A : SetAttribute<T>> Attributes.withoutAttributeElement(
    attribute: A,
    attrValue: T,
): Attributes {
    val currentSet: Set<T> = get(attribute) ?: emptySet()
    return AttributesImpl(content + (attribute to (currentSet - attrValue)))
}

/**
 * Create [Attributes] with a single key
 */
public fun <T, A : Attribute<T>> Attributes(
    attribute: A,
    attrValue: T,
): Attributes = AttributesImpl(mapOf(attribute to attrValue))

/**
 * Create Attributes with a single [Unit] valued attribute
 */
public fun <A : Attribute<Unit>> Attributes(
    attribute: A,
): Attributes = AttributesImpl(mapOf(attribute to Unit))

public operator fun Attributes.plus(other: Attributes): Attributes = AttributesImpl(content + other.content)