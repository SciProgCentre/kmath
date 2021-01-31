# Abstract Syntax Tree Expression Representation and Operations (`kmath-ast`)

This subproject implements the following features:

 - [expression-language](src/jvmMain/kotlin/kscience/kmath/ast/parser.kt) : Expression language and its parser
 - [mst](src/commonMain/kotlin/kscience/kmath/ast/MST.kt) : MST (Mathematical Syntax Tree) as expression language's syntax intermediate representation
 - [mst-building](src/commonMain/kotlin/kscience/kmath/ast/MstAlgebra.kt) : MST building algebraic structure
 - [mst-interpreter](src/commonMain/kotlin/kscience/kmath/ast/MST.kt) : MST interpreter
 - [mst-jvm-codegen](src/jvmMain/kotlin/kscience/kmath/asm/asm.kt) : Dynamic MST to JVM bytecode compiler
 - [mst-js-codegen](src/jsMain/kotlin/kscience/kmath/estree/estree.kt) : Dynamic MST to JS compiler


> #### Artifact:
>
> This module artifact: `kscience.kmath:kmath-ast:0.2.0-dev-6`.
>
> Bintray release version:        [ ![Download](https://api.bintray.com/packages/mipt-npm/kscience/kmath-ast/images/download.svg) ](https://bintray.com/mipt-npm/kscience/kmath-ast/_latestVersion)
>
> Bintray development version:    [ ![Download](https://api.bintray.com/packages/mipt-npm/dev/kmath-ast/images/download.svg) ](https://bintray.com/mipt-npm/dev/kmath-ast/_latestVersion)
>
> **Gradle:**
>
> ```gradle
> repositories {
>     maven { url "https://dl.bintray.com/kotlin/kotlin-eap" }
>     maven { url 'https://dl.bintray.com/mipt-npm/kscience' }
>     maven { url 'https://dl.bintray.com/mipt-npm/dev' }
>     maven { url 'https://dl.bintray.com/hotkeytlt/maven' }
> 
> }
> 
> dependencies {
>     implementation 'kscience.kmath:kmath-ast:0.2.0-dev-6'
> }
> ```
> **Gradle Kotlin DSL:**
>
> ```kotlin
> repositories {
>     maven("https://dl.bintray.com/kotlin/kotlin-eap")
>     maven("https://dl.bintray.com/mipt-npm/kscience")
>     maven("https://dl.bintray.com/mipt-npm/dev")
>     maven("https://dl.bintray.com/hotkeytlt/maven")
> }
> 
> dependencies {
>     implementation("kscience.kmath:kmath-ast:0.2.0-dev-6")
> }
> ```

## Dynamic expression code generation

### On JVM

`kmath-ast` JVM module supports runtime code generation to eliminate overhead of tree traversal. Code generator builds
a special implementation of `Expression<T>` with implemented `invoke` function.

For example, the following builder:

```kotlin
RealField.mstInField { symbol("x") + 2 }.compile()
``` 

… leads to generation of bytecode, which can be decompiled to the following Java class:

```java
package kscience.kmath.asm.generated;

import java.util.Map;
import kotlin.jvm.functions.Function2;
import kscience.kmath.asm.internal.MapIntrinsics;
import kscience.kmath.expressions.Expression;
import kscience.kmath.expressions.Symbol;

public final class AsmCompiledExpression_45045_0 implements Expression<Double> {
    private final Object[] constants;

    public final Double invoke(Map<Symbol, Double> arguments) {
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
RealField.mstInField { symbol("x") + 2 }.compile()
RealField.expression("x+2".parseMath())
```

#### Known issues

- The same classes may be generated and loaded twice, so it is recommended to cache compiled expressions to avoid
  class loading overhead.
- This API is not supported by non-dynamic JVM implementations (like TeaVM and GraalVM) because of using class loaders.

### On JS

A similar feature is also available on JS.

```kotlin
RealField.mstInField { symbol("x") + 2 }.compile()
``` 

The code above returns expression implemented with such a JS function:

```js
var executable = function (constants, arguments) {
  return constants[1](constants[0](arguments, "x"), 2);
};
```

#### Known issues

- This feature uses `eval` which can be unavailable in several environments.
