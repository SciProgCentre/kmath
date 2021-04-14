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
