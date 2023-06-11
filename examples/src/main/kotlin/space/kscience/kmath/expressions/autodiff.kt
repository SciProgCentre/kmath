/*
 * Copyright 2018-2023 KMath contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package space.kscience.kmath.expressions

import space.kscience.kmath.UnstableKMathAPI
// Only kmath-core is needed.

// Let's declare some variables
val x by symbol
val y by symbol
val z by symbol

@OptIn(UnstableKMathAPI::class)
fun main() {
    // Let's define some random expression.
    val someExpression = Double.autodiff.differentiate {
        // We bind variables `x` and `y` to the builder scope,
        val x = bindSymbol(x)
        val y = bindSymbol(y)

        // Then we use the bindings to define expression `xy + x + y - 1`
        x * y + x + y - 1
    }

    // Then we can evaluate it at any point ((-1, -1) in the case):
    println(someExpression(x to -1.0, y to -1.0))
    // >>> -2.0

    // We can also construct its partial derivatives:
    val dxExpression = someExpression.derivative(x) // ∂/∂x. Must be `y+1`
    val dyExpression = someExpression.derivative(y) // ∂/∂y. Must be `x+1`
    val dxdxExpression = someExpression.derivative(x, x) // ∂^2/∂x^2. Must be `0`

    // We can evaluate them as well
    println(dxExpression(x to 57.0, y to 6.0))
    // >>> 7.0
    println(dyExpression(x to -1.0, y to 179.0))
    // >>> 0.0
    println(dxdxExpression(x to 239.0, y to 30.0))
    // >>> 0.0

    // You can also provide extra arguments that obviously won't affect the result:
    println(dxExpression(x to 57.0, y to 6.0, z to 42.0))
    // >>> 7.0
    println(dyExpression(x to -1.0, y to 179.0, z to 0.0))
    // >>> 0.0
    println(dxdxExpression(x to 239.0, y to 30.0, z to 100_000.0))
    // >>> 0.0

    // But in case you forgot to specify bound symbol's value, exception is thrown:
    println( runCatching { someExpression(z to 4.0) } )
    // >>> Failure(java.lang.IllegalStateException: Symbol 'x' is not supported in ...)

    // The reason is that the expression is evaluated lazily,
    // and each `bindSymbol` operation actually substitutes the provided symbol with the corresponding value.

    // For example, let there be an expression
    val simpleExpression = Double.autodiff.differentiate {
        val x = bindSymbol(x)
        x pow 2
    }
    // When you evaluate it via
    simpleExpression(x to 1.0, y to 57.0, z to 179.0)
    // lambda above has the context of map `{x: 1.0, y: 57.0, z: 179.0}`.
    // When x is bound, you can think of it as substitution `x -> 1.0`.
    // Other values are unused which does not make any problem to us.
    // But in the case the corresponding value is not provided,
    // we cannot bind the variable. Thus, exception is thrown.

    // There is also a function `bindSymbolOrNull` that fixes the problem:
    val fixedExpression = Double.autodiff.differentiate {
        val x = bindSymbolOrNull(x) ?: const(8.0)
        x pow -2
    }
    println(fixedExpression())
    // >>> 0.015625
    // It works!

    // The expression provides a bunch of operations:
    // 1. Constant bindings (via `const` and `number`).
    // 2. Variable bindings (via `bindVariable`, `bindVariableOrNull`).
    // 3. Arithmetic operations (via `+`, `-`, `*`, and `-`).
    // 4. Exponentiation (via `pow` or `power`).
    // 5. `exp` and `ln`.
    // 6. Trigonometrical functions (`sin`, `cos`, `tan`, `cot`).
    // 7. Inverse trigonometrical functions (`asin`, `acos`, `atan`, `acot`).
    // 8. Hyperbolic functions and inverse hyperbolic functions.
}
