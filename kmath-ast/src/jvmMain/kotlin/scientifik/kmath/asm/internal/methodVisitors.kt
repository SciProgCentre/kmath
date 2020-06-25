package scientifik.kmath.asm.internal

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.commons.InstructionAdapter

internal fun MethodVisitor.instructionAdapter(): InstructionAdapter = InstructionAdapter(this)

internal fun MethodVisitor.instructionAdapter(block: InstructionAdapter.() -> Unit): InstructionAdapter =
    instructionAdapter().apply(block)
