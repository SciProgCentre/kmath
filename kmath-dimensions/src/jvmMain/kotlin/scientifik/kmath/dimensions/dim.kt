package scientifik.kmath.dimensions

actual inline fun <reified D : Dimension> Dimension.Companion.dim(): UInt =
    D::class.objectInstance?.dim ?: error("Dimension object must be a singleton")