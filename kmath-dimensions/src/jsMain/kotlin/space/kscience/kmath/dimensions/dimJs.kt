package space.kscience.kmath.dimensions

import kotlin.reflect.KClass

private val dimensionMap: MutableMap<UInt, Dimension> = hashMapOf(1u to D1, 2u to D2, 3u to D3)

@Suppress("UNCHECKED_CAST")
public actual fun <D : Dimension> Dimension.Companion.resolve(type: KClass<D>): D = dimensionMap
    .entries
    .map(MutableMap.MutableEntry<UInt, Dimension>::value)
    .find { it::class == type } as? D
    ?: error("Can't resolve dimension $type")

public actual fun Dimension.Companion.of(dim: UInt): Dimension = dimensionMap.getOrPut(dim) {
    object : Dimension {
        override val dim: UInt get() = dim
    }
}
