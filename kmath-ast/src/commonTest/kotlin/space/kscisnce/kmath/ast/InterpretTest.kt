package space.kscisnce.kmath.ast

import space.kscience.kmath.expressions.MstField
import space.kscience.kmath.expressions.invoke
import space.kscience.kmath.expressions.toExpression
import space.kscience.kmath.misc.Symbol.Companion.x
import space.kscience.kmath.operations.DoubleField
import space.kscience.kmath.operations.bindSymbol
import space.kscience.kmath.operations.invoke
import kotlin.test.Test

class InterpretTest {

    @Test
    fun interpretation(){
        val expr = MstField {
            val x = bindSymbol(x)
            x * 2.0 + number(2.0) / x - 16.0
        }.toExpression(DoubleField)
        expr(x to 2.2)
    }
}