package scientifik.kmath.dimensions

import kotlin.reflect.KClass

private val dimensionMap = hashMapOf<UInt, Dimension>(
    1u to D1,
    2u to D2,
    3u to D3
)

@Suppress("UNCHECKED_CAST")
actual fun <D : Dimension> Dimension.Companion.resolve(type: KClass<D>): D {
    return dimensionMap.entries.find { it.value::class == type }?.value as? D ?: error("Can't resolve dimension $type")
}

actual fun Dimension.Companion.of(dim: UInt): Dimension {
    return dimensionMap.getOrPut(dim) {
        object : Dimension {
            override val dim: UInt get() = dim
        }
    }
}