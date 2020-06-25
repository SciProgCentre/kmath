package scientifik.kmath.asm.internal

import org.objectweb.asm.Label
import org.objectweb.asm.commons.InstructionAdapter

internal fun InstructionAdapter.label(): Label {
    val l = Label()
    visitLabel(l)
    return l
}
