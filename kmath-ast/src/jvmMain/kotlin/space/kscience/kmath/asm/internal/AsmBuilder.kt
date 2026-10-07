/*
 * Copyright 2018-2026 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.asm.internal

import org.objectweb.asm.Type
import org.objectweb.asm.Type.getObjectType
import org.objectweb.asm.Type.getType
import space.kscience.kmath.expressions.Expression
import java.lang.invoke.MethodHandles

internal abstract class AsmBuilder {
    protected fun defineHiddenClass(binary: ByteArray): MethodHandles.Lookup =
        MethodHandles.lookup().defineHiddenClass(binary, true)

    companion object {
        /**
         * ASM type [Expression].
         */
        val EXPRESSION_TYPE: Type = getObjectType("space/kscience/kmath/expressions/Expression")

        /**
         * ASM type [java.util.Map].
         */
        val MAP_TYPE: Type = getObjectType("java/util/Map")

        /**
         * ASM type [java.lang.Object].
         */
        val OBJECT_TYPE: Type = getObjectType("java/lang/Object")

        /**
         * ASM type [java.lang.String].
         */
        val STRING_TYPE: Type = getObjectType("java/lang/String")

        /**
         * ASM type MapIntrinsics.
         */
        val MAP_INTRINSICS_TYPE: Type = getObjectType("space/kscience/kmath/asm/internal/MapIntrinsics")

        /**
         * ASM Type [space.kscience.kmath.expressions.Symbol].
         */
        val SYMBOL_TYPE: Type = getObjectType("space/kscience/kmath/expressions/Symbol")

        /**
         * ASM type [java.lang.Number].
         */
        val NUMBER_TYPE: Type = getObjectType("java/lang/Number")

        /**
         * ASM type [kotlin.jvm.functions.Function1].
         */
        val FUNCTION1_TYPE: Type = getObjectType("kotlin/jvm/functions/Function1")

        /**
         * ASM type `java.lang.Object[]`.
         */
        val OBJECT_ARRAY_TYPE: Type = getType("[Ljava/lang/Object;")

        const val ARGUMENTS_NAME = "args"
    }
}
