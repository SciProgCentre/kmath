package kscience.kmath.estree.internal

import astring.generate
import estree.*
import kscience.kmath.expressions.Expression
import kscience.kmath.expressions.Symbol

internal class JSBuilder<T>(val bodyCallback: JSBuilder<T>.() -> BaseExpression) {
    private class GeneratedExpression<T>(val executable: dynamic, val constants: Array<dynamic>) : Expression<T> {
        @Suppress("UNUSED_VARIABLE", "PARAMETER_NAME_CHANGED_ON_OVERRIDE")
        override fun invoke(map: Map<Symbol, T>): T {
            val e = executable
            val c = constants
            val a = js("{}")
            map.forEach { (key, value) -> a[key.identity] = value }
            return js("e(c, a)").unsafeCast<T>()
        }
    }

    val instance: Expression<T> by lazy {
        val node = Program(
            sourceType = "script",
            VariableDeclaration(
                kind = "var",
                VariableDeclarator(
                    id = Identifier("executable"),
                    init = FunctionExpression(
                        params = arrayOf(Identifier("constants"), Identifier("arguments")),
                        body = BlockStatement(ReturnStatement(bodyCallback())),
                    ),
                ),
            ),
        )

        eval(generate(node))
        GeneratedExpression(js("executable"), constants.toTypedArray())
    }

    private val constants = mutableListOf<Any>()
    private val keys = mutableListOf<String>()

    fun constant(value: Any?) = when {
        value == null || jsTypeOf(value) == "number" || jsTypeOf(value) == "string" || jsTypeOf(value) == "boolean" -> {
            SimpleLiteral(value)
        }

        else -> {
            val idx = if (value in constants) constants.indexOf(value) else constants.also { it += value }.lastIndex

            MemberExpression(
                computed = true,
                optional = false,
                `object` = Identifier("constants"),
                property = SimpleLiteral(idx),
            )
        }
    }

    fun variable(name: String): BaseExpression {
        return MemberExpression(
            computed = true,
            optional = false,
            `object` = Identifier("arguments"),
            property = SimpleLiteral(name),
        )
    }

    fun call(function: Function<T>, vararg args: BaseExpression): BaseExpression = SimpleCallExpression(
        optional = false,
        callee = constant(function),
        *args,
    )
}
