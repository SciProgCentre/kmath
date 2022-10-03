/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.asm.internal

import org.objectweb.asm.Type
import org.objectweb.asm.Type.getObjectType
import space.kscience.kmath.expressions.Expression

internal abstract class AsmBuilder {
    /**
     * Internal classloader with alias to define class from byte array.
     */
    class ByteArrayClassLoader(parent: ClassLoader) : ClassLoader(parent) {
        fun defineClass(name: String?, b: ByteArray): Class<*> = defineClass(name, b, 0, b.size)
    }

    protected val classLoader = ByteArrayClassLoader(javaClass.classLoader)

    protected companion object {
        /**
         * ASM type for [Expression].
         */
        val EXPRESSION_TYPE: Type = getObjectType("space/kscience/kmath/expressions/Expression")

        /**
         * ASM type for [java.util.Map].
         */
        val MAP_TYPE: Type = getObjectType("java/util/Map")

        /**
         * ASM type for [java.lang.Object].
         */
        val OBJECT_TYPE: Type = getObjectType("java/lang/Object")

        /**
         * ASM type for [java.lang.String].
         */
        val STRING_TYPE: Type = getObjectType("java/lang/String")

        /**
         * ASM type for MapIntrinsics.
         */
        val MAP_INTRINSICS_TYPE: Type = getObjectType("space/kscience/kmath/asm/internal/MapIntrinsics")

        /**
         * ASM Type for [space.kscience.kmath.expressions.Symbol].
         */
        val SYMBOL_TYPE: Type = getObjectType("space/kscience/kmath/expressions/Symbol")

        const val ARGUMENTS_NAME = "args"
    }
}
