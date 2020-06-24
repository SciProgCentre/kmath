package scientifik.kmath.asm.internal

import org.objectweb.asm.Type
import kotlin.reflect.KClass

internal val KClass<*>.asm: Type
    get() = Type.getType(java)
