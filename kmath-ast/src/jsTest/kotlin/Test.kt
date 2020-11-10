package kscience.kmath.ast

import kscience.kmath.operations.invoke
import kotlin.test.Test

internal class Test {
    @Test
    fun c() {
        compileMstToWasmF64(MstExtendedField { sin(symbol("x")) })
    }
}
