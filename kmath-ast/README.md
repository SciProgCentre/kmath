# Module kmath-ast

Performance and visualization extensions to MST API.

 - [expression-language](src/commonMain/kotlin/space/kscience/kmath/ast/parser.kt) : Expression language and its parser
 - [mst-jvm-codegen](src/jvmMain/kotlin/space/kscience/kmath/asm/asm.kt) : Dynamic MST to JVM bytecode compiler
 - [mst-js-codegen](src/jsMain/kotlin/space/kscience/kmath/estree/estree.kt) : Dynamic MST to JS compiler
 - [rendering](src/commonMain/kotlin/space/kscience/kmath/ast/rendering/MathRenderer.kt) : Extendable MST rendering


## Artifact:

The Maven coordinates of this project are `space.kscience:kmath-ast:0.3.0-dev-13`.

**Gradle:**
```gradle
repositories {
    maven { url 'https://repo.kotlin.link' }
    mavenCentral()
}

dependencies {
    implementation 'space.kscience:kmath-ast:0.3.0-dev-13'
}
```
**Gradle Kotlin DSL:**
```kotlin
repositories {
    maven("https://repo.kotlin.link")
    mavenCentral()
}

dependencies {
    implementation("space.kscience:kmath-ast:0.3.0-dev-13")
}
```

## Dynamic expression code generation

### On JVM

`kmath-ast` JVM module supports runtime code generation to eliminate overhead of tree traversal. Code generator builds a
special implementation of `Expression<T>` with implemented `invoke` function.

For example, the following builder:

```kotlin
import space.kscience.kmath.expressions.Symbol.Companion.x
import space.kscience.kmath.expressions.*
import space.kscience.kmath.operations.*
import space.kscience.kmath.asm.*

MstField { x + 2 }.compileToExpression(DoubleField)
``` 

... leads to generation of bytecode, which can be decompiled to the following Java class:

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
        return (Double) ((Function2) this.constants[0]).invoke((Double) MapIntrinsics.getOrFail(arguments, "x"), 2);
    }

    public AsmCompiledExpression_45045_0(Object[] constants) {
        this.constants = constants;
    }
}

```

#### Known issues

- The same classes may be generated and loaded twice, so it is recommended to cache compiled expressions to avoid class
  loading overhead.
- This API is not supported by non-dynamic JVM implementations (like TeaVM and GraalVM) because of using class loaders.

### On JS

A similar feature is also available on JS.

```kotlin
import space.kscience.kmath.expressions.Symbol.Companion.x
import space.kscience.kmath.expressions.*
import space.kscience.kmath.operations.*
import space.kscience.kmath.estree.*

MstField { x + 2 }.compileToExpression(DoubleField)
``` 

The code above returns expression implemented with such a JS function:

```js
var executable = function (constants, arguments) {
    return constants[1](constants[0](arguments, "x"), 2);
};
```

JS also supports very experimental expression optimization with [WebAssembly](https://webassembly.org/) IR generation.
Currently, only expressions inside `DoubleField` and `IntRing` are supported.

```kotlin
import space.kscience.kmath.expressions.Symbol.Companion.x
import space.kscience.kmath.expressions.*
import space.kscience.kmath.operations.*
import space.kscience.kmath.wasm.*

MstField { x + 2 }.compileToExpression(DoubleField)
```

An example of emitted Wasm IR in the form of WAT:

```lisp
(func $executable (param $0 f64) (result f64)
  (f64.add
    (local.get $0)
    (f64.const 2)
  )
)
```

#### Known issues

- ESTree expression compilation uses `eval` which can be unavailable in several environments.
- WebAssembly isn't supported by old versions of browsers (see https://webassembly.org/roadmap/).

## Rendering expressions

kmath-ast also includes an extensible engine to display expressions in LaTeX or MathML syntax.

Example usage:

```kotlin
import space.kscience.kmath.ast.*
import space.kscience.kmath.ast.rendering.*
import space.kscience.kmath.misc.*

@OptIn(UnstableKMathAPI::class)
public fun main() {
    val mst = "exp(sqrt(x))-asin(2*x)/(2e10+x^3)/(12)+x^(2/3)".parseMath()
    val syntax = FeaturedMathRendererWithPostProcess.Default.render(mst)
    val latex = LatexSyntaxRenderer.renderWithStringBuilder(syntax)
    println("LaTeX:")
    println(latex)
    println()
    val mathML = MathMLSyntaxRenderer.renderWithStringBuilder(syntax)
    println("MathML:")
    println(mathML)
}
```

Result LaTeX:

![](https://latex.codecogs.com/gif.latex?%5Coperatorname{exp}%5C,%5Cleft(%5Csqrt{x}%5Cright)-%5Cfrac{%5Cfrac{%5Coperatorname{arcsin}%5C,%5Cleft(2%5C,x%5Cright)}{2%5Ctimes10^{10}%2Bx^{3}}}{12}+x^{2/3})

Result MathML (can be used with MathJax or other renderers):

<details>

```html
<math xmlns="https://www.w3.org/1998/Math/MathML">
    <mrow>
        <mo>exp</mo>
        <mspace width="0.167em"></mspace>
        <mfenced open="(" close=")" separators="">
            <msqrt>
                <mi>x</mi>
            </msqrt>
        </mfenced>
        <mo>-</mo>
        <mfrac>
            <mrow>
                <mfrac>
                    <mrow>
                        <mo>arcsin</mo>
                        <mspace width="0.167em"></mspace>
                        <mfenced open="(" close=")" separators="">
                            <mn>2</mn>
                            <mspace width="0.167em"></mspace>
                            <mi>x</mi>
                        </mfenced>
                    </mrow>
                    <mrow>
                        <mn>2</mn>
                        <mo>&times;</mo>
                        <msup>
                            <mrow>
                                <mn>10</mn>
                            </mrow>
                            <mrow>
                                <mn>10</mn>
                            </mrow>
                        </msup>
                        <mo>+</mo>
                        <msup>
                            <mrow>
                                <mi>x</mi>
                            </mrow>
                            <mrow>
                                <mn>3</mn>
                            </mrow>
                        </msup>
                    </mrow>
                </mfrac>
            </mrow>
            <mrow>
                <mn>12</mn>
            </mrow>
        </mfrac>
        <mo>+</mo>
        <msup>
            <mrow>
                <mi>x</mi>
            </mrow>
            <mrow>
                <mn>2</mn>
                <mo>/</mo>
                <mn>3</mn>
            </mrow>
        </msup>
    </mrow>
</math>
```

</details>

It is also possible to create custom algorithms of render, and even add support of other markup languages
(see API reference).
