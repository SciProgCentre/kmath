# Abstract syntax tree expression representation and operations (`kmath-ast`)

This subproject implements the following features:

- Expression Language and its parser.
- MST (Mathematical Syntax Tree) as expression language's syntax intermediate representation.
- Type-safe builder for MST.
- Evaluating expressions by traversing MST.

> #### Artifact:
> This module is distributed in the artifact `scientifik:kmath-ast:0.1.4-dev-8`.
> 
> **Gradle:**
>
> ```gradle
> repositories {
>     maven { url 'https://dl.bintray.com/mipt-npm/scientifik' }
>     maven { url 'https://dl.bintray.com/mipt-npm/dev' }
>     maven { url https://dl.bintray.com/hotkeytlt/maven' }
> }
> 
> dependencies {
>     implementation 'scientifik:kmath-ast:0.1.4-dev-8'
> }
> ```
> **Gradle Kotlin DSL:**
>
> ```kotlin
> repositories {
>     maven("https://dl.bintray.com/mipt-npm/scientifik")
>     maven("https://dl.bintray.com/mipt-npm/dev")
>     maven("https://dl.bintray.com/hotkeytlt/maven")
> }
> 
> dependencies {
>     implementation("scientifik:kmath-ast:0.1.4-dev-8")
> }
> ```
>

## Dynamic expression code generation with ObjectWeb ASM

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
import scientifik.kmath.asm.internal.MapIntrinsics;
import scientifik.kmath.expressions.Expression;
import scientifik.kmath.operations.RealField;

public final class AsmCompiledExpression_1073786867_0 implements Expression<Double> {
    private final RealField algebra;

    public final Double invoke(Map<String, ? extends Double> arguments) {
        return (Double)this.algebra.add(((Double)MapIntrinsics.getOrFail(arguments, "x")).doubleValue(), 2.0D);
    }

    public AsmCompiledExpression_1073786867_0(RealField algebra) {
        this.algebra = algebra;
    }
}

```

### Example Usage

This API is an extension to MST and MstExpression, so you may optimize as both of them: 

```kotlin
RealField.mstInField { symbol("x") + 2 }.compile()
RealField.expression("x+2".parseMath())
```

### Known issues

- The same classes may be generated and loaded twice, so it is recommended to cache compiled expressions to avoid
class loading overhead. 
- This API is not supported by non-dynamic JVM implementations (like TeaVM and GraalVM) because of using class loaders.

Contributed by [Iaroslav Postovalov](https://github.com/CommanderTvis).
