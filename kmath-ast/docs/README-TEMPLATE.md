# Abstract Syntax Tree Expression Representation and Operations (`kmath-ast`)

This subproject implements the following features:

${features}

${artifact}

## Dynamic expression code generation

### On JVM

`kmath-ast` JVM module supports runtime code generation to eliminate overhead of tree traversal. Code generator builds
a special implementation of `Expression<T>` with implemented `invoke` function.

For example, the following builder:

```kotlin
DoubleField.mstInField { symbol("x") + 2 }.compile()
``` 

… leads to generation of bytecode, which can be decompiled to the following Java class:

```java
package space.kscience.kmath.asm.generated;

import java.util.Map;
import kotlin.jvm.functions.Function2;
import space.kscience.kmath.asm.internal.MapIntrinsics;
import space.kscience.kmath.expressions.Expression;
import space.kscience.kmath.expressions.Symbol;

public final class AsmCompiledExpression_45045_0 implements Expression<Double> {
    private final Object[] constants;

    public final Double invoke(Map<Symbol, ? extends Double> arguments) {
        return (Double)((Function2)this.constants[0]).invoke((Double)MapIntrinsics.getOrFail(arguments, "x"), 2);
    }

    public AsmCompiledExpression_45045_0(Object[] constants) {
        this.constants = constants;
    }
}

```

### Example Usage

This API extends MST and MstExpression, so you may optimize as both of them:

```kotlin
DoubleField.mstInField { symbol("x") + 2 }.compile()
DoubleField.expression("x+2".parseMath())
```

#### Known issues

- The same classes may be generated and loaded twice, so it is recommended to cache compiled expressions to avoid
  class loading overhead.
- This API is not supported by non-dynamic JVM implementations (like TeaVM and GraalVM) because of using class loaders.

### On JS

A similar feature is also available on JS.

```kotlin
DoubleField.mstInField { symbol("x") + 2 }.compile()
``` 

The code above returns expression implemented with such a JS function:

```js
var executable = function (constants, arguments) {
  return constants[1](constants[0](arguments, "x"), 2);
};
```

#### Known issues

- This feature uses `eval` which can be unavailable in several environments.
