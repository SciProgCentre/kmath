package scientifik.kmath.asm.internal

import scientifik.kmath.ast.MST

/**
 * Creates a class name for [AsmCompiledExpression] subclassed to implement [mst] provided.
 *
 * This methods helps to avoid collisions of class name to prevent loading several classes with the same name. If there
 * is a colliding class, change [collision] parameter or leave it `0` to check existing classes recursively.
 */
internal tailrec fun buildName(mst: MST, collision: Int = 0): String {
    val name = "scientifik.kmath.asm.generated.AsmCompiledExpression_${mst.hashCode()}_$collision"

    try {
        Class.forName(name)
    } catch (ignored: ClassNotFoundException) {
        return name
    }

    return buildName(mst, collision + 1)
}
