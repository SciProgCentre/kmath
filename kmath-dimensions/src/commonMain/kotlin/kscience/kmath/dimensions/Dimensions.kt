package kscience.kmath.dimensions

import kotlin.reflect.KClass

/**
 * An abstract class which is not used in runtime. Designates a size of some structure.
 * Could be replaced later by fully inline constructs
 */
public interface Dimension {
    public val dim: UInt

    public companion object
}

public fun <D : Dimension> KClass<D>.dim(): UInt = Dimension.resolve(this).dim

public expect fun <D : Dimension> Dimension.Companion.resolve(type: KClass<D>): D

public expect fun Dimension.Companion.of(dim: UInt): Dimension

public inline fun <reified D : Dimension> Dimension.Companion.dim(): UInt = D::class.dim()

public object D1 : Dimension {
    override val dim: UInt get() = 1U
}

public object D2 : Dimension {
    override val dim: UInt get() = 2U
}

public object D3 : Dimension {
    override val dim: UInt get() = 3U
}
