package space.kscience.kmath.dimensions

import kotlin.reflect.KClass

/**
 * Represents a quantity of dimensions in certain structure.
 *
 * @property dim The number of dimensions.
 */
public interface Dimension {
    public val dim: UInt

    public companion object
}

public fun <D : Dimension> KClass<D>.dim(): UInt = Dimension.resolve(this).dim

public expect fun <D : Dimension> Dimension.Companion.resolve(type: KClass<D>): D

/**
 * Finds or creates [Dimension] with [Dimension.dim] equal to [dim].
 */
public expect fun Dimension.Companion.of(dim: UInt): Dimension

/**
 * Finds [Dimension.dim] of given type [D].
 */
public inline fun <reified D : Dimension> Dimension.Companion.dim(): UInt = D::class.dim()

/**
 * Type representing 1 dimension.
 */
public object D1 : Dimension {
    override val dim: UInt get() = 1U
}

/**
 * Type representing 2 dimensions.
 */
public object D2 : Dimension {
    override val dim: UInt get() = 2U
}

/**
 * Type representing 3 dimensions.
 */
public object D3 : Dimension {
    override val dim: UInt get() = 3U
}
