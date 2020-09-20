package kscience.kmath.asm.internal

import kscience.kmath.ast.MST

/**
 * Represents types known in [MST], numbers and general values.
 */
internal enum class MstType {
    GENERAL,
    NUMBER;

    companion object {
        fun fromMst(mst: MST): MstType {
            if (mst is MST.Numeric)
                return NUMBER

            return GENERAL
        }
    }
}
