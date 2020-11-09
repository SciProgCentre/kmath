
import kscience.kmath.ast.MstExtendedField
import kscience.kmath.ast.compileMstToWasmF64
import kscience.kmath.operations.invoke
import kotlin.test.Test

internal class Test {
    @Test
    fun c() {
        compileMstToWasmF64(MstExtendedField { sin(symbol("x")) })
    }
}