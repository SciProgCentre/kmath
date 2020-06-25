# AST-based expression representation and operations (`kmath-ast`)

This subproject implements the following features:

- Expression Language and its parser.
- MST as expression language's syntax intermediate representation.
- Type-safe builder of MST.
- Evaluating expressions by traversing MST.

## Dynamic expression code generation with OW2 ASM

`kmath-ast` JVM module supports runtime code generation to eliminate overhead of tree traversal. Code generator builds 
a special implementation of `Expression<T>` with implemented `invoke` function. 

For example, the following builder: 

```kotlin
    RealField.mstInField { symbol("x") + 2 }.compile()
``` 

… leads to generation of bytecode, which can be decompiled to the following Java class: 

```java
package scientifik.kmath.asm.generated;

import java.util.Map;
import scientifik.kmath.expressions.Expression;
import scientifik.kmath.operations.RealField;

public final class AsmCompiledExpression_1073786867_0 implements Expression<Double> {
    private final RealField algebra;
    private final Object[] constants;

    public AsmCompiledExpression_1073786867_0(RealField algebra, Object[] constants) {
        this.algebra = algebra;
        this.constants = constants;
    }

    public final Double invoke(Map<String, ? extends Double> arguments) {
        return (Double)this.algebra.add(((Double)arguments.get("x")).doubleValue(), 2.0D);
    }
}
```

### Example Usage

This API is an extension to MST and MSTExpression APIs. You may optimize both MST and MSTExpression: 

```kotlin
RealField.mstInField { symbol("x") + 2 }.compile()
RealField.expression("x+2".parseMath())
```

### Known issues

- The same classes may be generated and loaded twice, so it is recommended to cache compiled expressions to avoid
class loading overhead. 
- This API is not supported by non-dynamic JVM implementations (like TeaVM and GraalVM) because of using class loaders.

Contributed by [Iaroslav Postovalov](https://github.com/CommanderTvis).
