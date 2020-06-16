package scientifik.kmath.asm.internal

import scientifik.kmath.ast.MST

internal fun buildName(mst: MST, collision: Int = 0): String {
    val name = "scientifik.kmath.asm.generated.AsmCompiledExpression_${mst.hashCode()}_$collision"

    try {
        Class.forName(name)
    } catch (ignored: ClassNotFoundException) {
        return name
    }

    return buildName(mst, collision + 1)
}
