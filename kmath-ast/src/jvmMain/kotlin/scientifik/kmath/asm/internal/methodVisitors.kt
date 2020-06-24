package scientifik.kmath.asm.internal

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.commons.InstructionAdapter

fun MethodVisitor.instructionAdapter(): InstructionAdapter = InstructionAdapter(this)

fun MethodVisitor.instructionAdapter(block: InstructionAdapter.() -> Unit): InstructionAdapter =
    instructionAdapter().apply(block)
