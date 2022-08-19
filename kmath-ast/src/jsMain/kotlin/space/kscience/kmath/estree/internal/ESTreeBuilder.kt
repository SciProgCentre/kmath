/*
 * Copyright 2018-2022 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.estree.internal

import space.kscience.kmath.expressions.Expression
import space.kscience.kmath.expressions.Symbol
import space.kscience.kmath.internal.astring.generate
import space.kscience.kmath.internal.estree.*

internal class ESTreeBuilder<T>(val bodyCallback: ESTreeBuilder<T>.() -> BaseExpression) {
    private class GeneratedExpression<T>(val executable: dynamic, val constants: Array<dynamic>) : Expression<T> {
        @Suppress("UNUSED_VARIABLE")
        override fun invoke(arguments: Map<Symbol, T>): T {
            val e = executable
            val c = constants
            val a = js("{}")
            arguments.forEach { (key, value) -> a[key.identity] = value }
            return js("e(c, a)").unsafeCast<T>()
        }
    }

    @Suppress("UNUSED_VARIABLE")
    val instance: Expression<T> by lazy {
        val node = Program(
            sourceType = "script",
            ReturnStatement(bodyCallback())
        )

        val code = generate(node)
        GeneratedExpression(js("new Function('constants', 'arguments_0', code)"), constants.toTypedArray())
    }

    private val constants = mutableListOf<Any>()

    fun constant(value: Any?): BaseExpression = when {
        value == null || jsTypeOf(value) == "number" || jsTypeOf(value) == "string" || jsTypeOf(value) == "boolean" ->
            SimpleLiteral(value)

        jsTypeOf(value) == "undefined" -> Identifier("undefined")

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

    fun variable(name: Symbol): BaseExpression =
        call(getOrFail, Identifier("arguments_0"), SimpleLiteral(name.identity))

    fun call(function: Function<T>, vararg args: BaseExpression): BaseExpression = SimpleCallExpression(
        optional = false,
        callee = constant(function),
        *args,
    )

    private companion object {
        @Suppress("UNUSED_VARIABLE")
        val getOrFail: (`object`: dynamic, key: String) -> dynamic = { `object`, key ->
            val k = key
            val o = `object`

            if (!(js("k in o") as Boolean))
                throw NoSuchElementException("Key $key is missing in the map.")

            js("o[k]")
        }
    }
}
