package scientifik.kmath.dimensions

import kotlin.reflect.KClass

actual fun <D:Dimension> Dimension.Companion.resolve(type: KClass<D>): D{
    return type.objectInstance ?: error("No object instance for dimension class")
}

actual fun Dimension.Companion.of(dim: UInt): Dimension{
    return when(dim){
        1u -> D1
        2u -> D2
        3u -> D3
        else -> object : Dimension {
            override val dim: UInt get() = dim
        }
    }
}