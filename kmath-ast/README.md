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
import scientifik.kmath.asm.internal.AsmCompiledExpression;
import scientifik.kmath.operations.Algebra;
import scientifik.kmath.operations.RealField;

// The class's name is build with MST's hash-code and collision fixing number. 
public final class AsmCompiledExpression_45045_0 extends AsmCompiledExpression<Double> {
    // Plain constructor
    public AsmCompiledExpression_45045_0(Algebra algebra, Object[] constants) {
        super(algebra, constants);
    }

    // The actual dynamic code: 
    public final Double invoke(Map<String, ? extends Double> arguments) {
        return (Double)((RealField)super.algebra).add((Double)arguments.get("x"), (Double)2.0D);
    }
}
```

### Example Usage

This API is an extension to MST and MSTExpression APIs. You may optimize both MST and MSTExpression: 

```kotlin
RealField.mstInField { symbol("x") + 2 }.compile()
RealField.expression("2+2".parseMath())
```

### Known issues

- Using numeric algebras causes boxing and calling bridge methods. 
- The same classes may be generated and loaded twice, so it is recommended to cache compiled expressions to avoid
class loading overhead. 
- This API is not supported by non-dynamic JVM implementations (like TeaVM and GraalVM) because of using class loaders.

Contributed by [Iaroslav Postovalov](https://github.com/CommanderTvis).
