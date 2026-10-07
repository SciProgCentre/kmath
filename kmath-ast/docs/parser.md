# Expression Parser and Evaluation in kmath-ast

The `kmath-ast` module provides functionality to parse mathematical expressions from strings into Abstract Syntax Trees
([Mathematical Syntax Tree](https://kscience.space/kmath/docs/expressions.html) / `MST`), render them, compile them
dynamically, and evaluate them across algebraic structures using `MstInterpreterContext`.

---

## Parser Syntax Reference

The expression parser is built on top of `better-parse` using the grammar defined in
`space.kscience.kmath.ast.ArithmeticsEvaluator`.

### Literals

1. **Numbers**:
    - Integers and decimals: `0`, `42`, `3.14159`, `.5`.
    - Scientific notation: `1e10`, `1e-5`, `2.5E+3`.
    - Stored as `MST.Numeric` (`Long` or `Double`).

2. **Identifiers / Symbols**:
    - Variable and constant names: start with a Latin letter or underscore, followed by letters, digits, or underscores
      (e.g., `x`, `alpha`, `_val1`, `var_2`).
    - Transformed into `space.kscience.kmath.expressions.Symbol`.

3. **String Literals**:
    - Single-quoted (`'hello'`) or double-quoted (`"world"`).
    - Parsed into `Symbol` containing the unquoted string content. Useful for passing string identifiers or enum-like
      arguments to external functions.

### Operators and Precedence

Expressions are evaluated according to standard operator precedence and associativity (from highest precedence to
lowest):

| Precedence | Syntax / Operator   | Description                        | Associativity     | Resulting MST Node                                                                       |
|------------|---------------------|------------------------------------|-------------------|------------------------------------------------------------------------------------------|
| 1          | `(...)`             | Grouping / Parentheses             | N/A               | Subtree                                                                                  |
| 1          | `foo(a=1, b="bar")` | Function Call with named arguments | N/A               | `MST.FunctionCall`                                                                       |
| 1          | `add(x, y)`         | Binary function call (positional)  | N/A               | `MST.Binary`                                                                             |
| 1          | `sin(x)`            | Unary function call (positional)   | N/A               | `MST.Unary`                                                                              |
| 2          | `^`                 | Power / Exponentiation             | Left-associative  | `MST.Binary(PowerOperations.POW_OPERATION, ...)`                                         |
| 3          | `-` (prefix)        | Unary Minus / Negation             | Right-associative | `MST.Unary(GroupOps.MINUS_OPERATION, ...)`                                               |
| 4          | `*`, `/`            | Multiplication, Division           | Left-associative  | `MST.Binary(RingOps.TIMES_OPERATION, ...)` / `MST.Binary(FieldOps.DIV_OPERATION, ...)`   |
| 5          | `+`, `-`            | Addition, Subtraction              | Left-associative  | `MST.Binary(GroupOps.PLUS_OPERATION, ...)` / `MST.Binary(GroupOps.MINUS_OPERATION, ...)` |

### Function Call Syntax

The parser distinguishes three forms of function invocations:

1. **Named-argument Function Calls**:
   ```
   functionName(param1 = expr1, param2 = expr2, ...)
   ```
   Transformed into `MST.FunctionCall(name, mapOf(Symbol("param1") to expr1, ...))`.

   Example:
   ```kotlin
   "hypot(x = 3, y = 4)".parseMath()
   "filter(signal = s, cutoff = 100, mode = \"lowpass\")".parseMath()
   ```

2. **Unary Positional Functions**:
   ```
   func(expr)
   ```
   Transformed into `MST.Unary("func", expr)`.

   Example:
   ```kotlin
   "sin(x + 1)".parseMath()
   ```

3. **Binary Positional Functions**:
   ```
   func(leftExpr, rightExpr)
   ```
   Transformed into `MST.Binary("func", leftExpr, rightExpr)`.

   Example:
   ```kotlin
   "atan2(y, x)".parseMath()
   ```

---

## Parsing API

The module exposes two main extension functions on `kotlin.String`:

### `String.parseMath(): MST`

Parses the entire string and returns the root `MST` node. Throws a `com.github.h0tk3y.betterParse.parser.ParseException`
if the input string contains syntax errors or unconsumed trailing tokens.

```kotlin
import space.kscience.kmath.ast.parseMath
import space.kscience.kmath.expressions.MST

val mst: MST = "2 * (x + y)^2 - sin(x)".parseMath()
```

### `String.tryParseMath(): ParseResult<MST>`

Tries to parse the string and returns a `ParseResult<MST>`, which is either `Parsed(value)` or `ErrorResult` with
detailed diagnostic information (e.g., token position and error message).

```kotlin
import com.github.h0tk3y.betterParse.parser.Parsed
import com.github.h0tk3y.betterParse.parser.ErrorResult
import space.kscience.kmath.ast.tryParseMath

when (val result = "x + * 2".tryParseMath()) {
    is Parsed -> println("Parsed AST: ${result.value}")
    is ErrorResult -> println("Parsing error at position: $result")
}
```

---

## Evaluating with Algebras and `MstInterpreterContext`

Once an expression is parsed into an `MST`, it can be evaluated in various mathematical contexts without hardcoding
values or operations.

### Basic Interpretation with `Algebra<T>`

For simple expressions, pass an algebra (such as `Float64Field`, `Int64Ring`, or `ComplexField`) and variable
substitutions:

```kotlin
import space.kscience.kmath.ast.parseMath
import space.kscience.kmath.expressions.Symbol
import space.kscience.kmath.expressions.interpret
import space.kscience.kmath.operations.Float64Field

val mst = "x^2 + 2 * x + 1".parseMath()
val result = mst.interpret(Float64Field, Symbol("x") to 3.0)
// result = 16.0
```

### Advanced Evaluation via `MstInterpreterContext`

`MstInterpreterContext` allows configuring evaluation parameters:

- `algebra`: The base `Algebra<T>` (e.g. `Float64Field`).
- `arguments`: Dynamic variables `Map<Symbol, T>`.
- `constants`: Static constants `Map<Symbol, T>` (takes precedence over `arguments`).
- `unaryOperations`: Custom unary operations `Map<String, (T) -> T>` (overrides algebra unary functions).
- `binaryOperations`: Custom binary operations `Map<String, (T, T) -> T>` (overrides algebra binary functions).
- `functions`: External functions with named arguments `Map<String, Expression<T>>`.

#### Symbol Resolution Order

When resolving symbols in an expression, `MstInterpreterContext` follows this hierarchy:

1. `constants` map
2. `arguments` map
3. `algebra.bindSymbolOrNull(symbol)`
4. If unresolved, throws an error.

---

## Examples

### 1. Custom External Functions with Named Arguments

Define an external function using `Expression<T>` and register it in `functions`:

```kotlin
import space.kscience.kmath.ast.parseMath
import space.kscience.kmath.expressions.Expression
import space.kscience.kmath.expressions.MstInterpreterContext
import space.kscience.kmath.expressions.Symbol
import space.kscience.kmath.expressions.interpret
import space.kscience.kmath.operations.Float64Field
import kotlin.math.sqrt

// Define a 2D Euclidean distance function: hypot(u=..., v=...)
val hypot = Expression(Float64Field.type) { args ->
    val u = args[Symbol("u")] ?: 0.0
    val v = args[Symbol("v")] ?: 0.0
    sqrt(u * u + v * v)
}

// Define a clamping function: clamp(value=..., min=..., max=...)
val clamp = Expression(Float64Field.type) { args ->
    val value = args[Symbol("value")] ?: 0.0
    val min = args[Symbol("min")] ?: Double.NEGATIVE_INFINITY
    val max = args[Symbol("max")] ?: Double.POSITIVE_INFINITY
    value.coerceIn(min, max)
}

val context = MstInterpreterContext(
    algebra = Float64Field,
    arguments = mapOf(Symbol("x") to 3.0, Symbol("y") to 4.0),
    functions = mapOf(
        "hypot" to hypot,
        "clamp" to clamp
    )
)

val mst = "clamp(value = hypot(u = x, v = y), min = 0.0, max = 4.5)".parseMath()

val result = context(context) { mst.interpret() }
// hypot(3.0, 4.0) = 5.0, clamped to max 4.5 -> result = 4.5
```

### 2. Overriding Unary and Binary Operations

Customize or override existing operators (such as `+`, `-`, `*`, `/`, `^`) or implement domain-specific functions:

```kotlin
import space.kscience.kmath.ast.parseMath
import space.kscience.kmath.expressions.MstInterpreterContext
import space.kscience.kmath.expressions.Symbol
import space.kscience.kmath.expressions.interpret
import space.kscience.kmath.operations.Float64Field
import kotlin.math.atan2

val context = MstInterpreterContext(
    algebra = Float64Field,
    arguments = mapOf(Symbol("x") to 10.0, Symbol("y") to 3.0),
    unaryOperations = mapOf(
        // Custom unary operation
        "sqr" to { it * it },
        "inv" to { 1.0 / it }
    ),
    binaryOperations = mapOf(
        // Custom modulo operator and atan2 function
        "mod" to { a, b -> a % b },
        "atan2" to { y, x -> atan2(y, x) }
    )
)

val mst = "sqr(x) + mod(x, y)".parseMath()
// sqr(10) + mod(10, 3) = 100.0 + 1.0 = 101.0

val result = context(context) { mst.interpret() }
```

### 3. Combining Constants, Variables, Operations, and External Functions

```kotlin
import space.kscience.kmath.ast.parseMath
import space.kscience.kmath.expressions.Expression
import space.kscience.kmath.expressions.MstInterpreterContext
import space.kscience.kmath.expressions.Symbol
import space.kscience.kmath.expressions.interpret
import space.kscience.kmath.operations.Float64Field

// Custom Gaussian activation function
val gaussian = Expression(Float64Field.type) { args ->
    val x = args[Symbol("x")] ?: 0.0
    val mu = args[Symbol("mu")] ?: 0.0
    val sigma = args[Symbol("sigma")] ?: 1.0
    kotlin.math.exp(-((x - mu) * (x - mu)) / (2 * sigma * sigma))
}

val context = MstInterpreterContext(
    algebra = Float64Field,
    constants = mapOf(
        Symbol("PI") to kotlin.math.PI,
        Symbol("sigma") to 1.5
    ),
    arguments = mapOf(
        Symbol("input") to 2.0
    ),
    unaryOperations = mapOf(
        "degToRad" to { it * (kotlin.math.PI / 180.0) }
    ),
    functions = mapOf(
        "gauss" to gaussian
    )
)

val parsed = "gauss(x = input, mu = 0.0, sigma = sigma) * degToRad(180)".parseMath()
val evaluated = context(context) { parsed.interpret() }
```

### 4. Dynamic Expression Compilation

Parsed expressions can also be compiled into optimized JVM bytecode or JavaScript functions via `kmath-ast` compilation
extensions:

```kotlin
import space.kscience.kmath.asm.compileToExpression
import space.kscience.kmath.ast.parseMath
import space.kscience.kmath.expressions.MstInterpreterContext
import space.kscience.kmath.expressions.Symbol
import space.kscience.kmath.operations.Float64Field

val context = MstInterpreterContext(
    algebra = Float64Field,
    arguments = mapOf(Symbol("x") to 2.0),
    constants = mapOf(Symbol("k") to 5.0)
)

// Compiles MST directly to high-performance bytecode leveraging context configuration
val expression = context(context) {
    "x^3 + k * x".parseMath().compileToExpression()
}

val value = expression(mapOf(Symbol("x") to 3.0))
// 3^3 + 5 * 3 = 27 + 15 = 42.0
```
