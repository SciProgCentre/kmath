# Module kmath-ast

Extensions to MST API: transformations, dynamic compilation and visualization.

 - [expression-language](src/commonMain/kotlin/space/kscience/kmath/ast/parser.kt) : Expression language and its parser
 - [mst-jvm-codegen](src/jvmMain/kotlin/space/kscience/kmath/asm/asm.kt) : Dynamic MST to JVM bytecode compiler
 - [mst-js-codegen](src/jsMain/kotlin/space/kscience/kmath/estree/estree.kt) : Dynamic MST to JS compiler
 - [rendering](src/commonMain/kotlin/space/kscience/kmath/ast/rendering/MathRenderer.kt) : Extendable MST rendering


## Artifact:

The Maven coordinates of this project are `space.kscience:kmath-ast:0.3.0`.

**Gradle Groovy:**
```groovy
repositories {
    maven { url 'https://repo.kotlin.link' }
    mavenCentral()
}

dependencies {
    implementation 'space.kscience:kmath-ast:0.3.0'
}
```
**Gradle Kotlin DSL:**
```kotlin
repositories {
    maven("https://repo.kotlin.link")
    mavenCentral()
}

dependencies {
    implementation("space.kscience:kmath-ast:0.3.0")
}
```

## Parsing expressions

In this module there is a parser from human-readable strings like `"x^3-x+3"` (in the more specific [grammar](reference/ArithmeticsEvaluator.g4)) to MST instances.

Supported literals:
1. Constants and variables (consist of latin letters, digits and underscores, can't start with digit): `x`, `_Abc2`.
2. Numbers: `123`, `1.02`, `1e10`, `1e-10`, `1.0e+3`&mdash;all parsed either as `kotlin.Long` or `kotlin.Double`.

Supported binary operators (from the highest precedence to the lowest one):
1. `^`
2. `*`, `/`
3. `+`, `-`

Supported unary operator:
1. `-`, e.&nbsp;g. `-x`

Arbitrary unary and binary functions are also supported: names consist of latin letters, digits and underscores, can't start with digit. Examples:
1. `sin(x)`
2. `add(x, y)`

## Dynamic expression code generation

### On JVM

`kmath-ast` JVM module supports runtime code generation to eliminate overhead of tree traversal. Code generator builds a
special implementation of `Expression<T>` with implemented `invoke` function.

For example, the following code:

```kotlin
import space.kscience.kmath.asm.compileToExpression
import space.kscience.kmath.operations.DoubleField

"x^3-x+3".parseMath().compileToExpression(DoubleField)
```

&mldr; leads to generation of bytecode, which can be decompiled to the following Java class:

```java
import java.util.*;
import kotlin.jvm.functions.*;
import space.kscience.kmath.asm.internal.*;
import space.kscience.kmath.complex.*;
import space.kscience.kmath.expressions.*;

public final class CompiledExpression_45045_0 implements Expression<Complex> {
    private final Object[] constants;

    public Complex invoke(Map<Symbol, ? extends Complex> arguments) {
        Complex var2 = (Complex)MapIntrinsics.getOrFail(arguments, "x");
        return (Complex)((Function2)this.constants[0]).invoke(var2, (Complex)this.constants[1]);
    }
}
```

For `LongRing`, `IntRing`, and `DoubleField` specialization is supported for better performance:

```java
import java.util.*;
import space.kscience.kmath.asm.internal.*;
import space.kscience.kmath.expressions.*;

public final class CompiledExpression_-386104628_0 implements DoubleExpression {
    private final SymbolIndexer indexer;

    public SymbolIndexer getIndexer() {
        return this.indexer;
    }

    public double invoke(double[] arguments) {
        double var2 = arguments[0];
        return Math.pow(var2, 3.0D) - var2 + 3.0D;
    }

    public final Double invoke(Map<Symbol, ? extends Double> arguments) {
        double var2 = ((Double)MapIntrinsics.getOrFail(arguments, "x")).doubleValue();
        return Math.pow(var2, 3.0D) - var2 + 3.0D;
    }
}
```

Setting JVM system property `space.kscience.kmath.ast.dump.generated.classes` to `1` makes the translator dump class files to program's working directory, so they can be reviewed manually.

#### Limitations

- The same classes may be generated and loaded twice, so it is recommended to cache compiled expressions to avoid class loading overhead.
- This API is not supported by non-dynamic JVM implementations like TeaVM or GraalVM Native Image because they may not support class loaders.

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

JS also supports experimental expression optimization with [WebAssembly](https://webassembly.org/) IR generation.
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
(func \$executable (param \$0 f64) (result f64)
  (f64.add
    (local.get \$0)
    (f64.const 2)
  )
)
```

#### Limitations

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

<div style="background-color:white;">

![](https://latex.codecogs.com/gif.latex?%5Coperatorname{exp}%5C,%5Cleft(%5Csqrt{x}%5Cright)-%5Cfrac{%5Cfrac{%5Coperatorname{arcsin}%5C,%5Cleft(2%5C,x%5Cright)}{2%5Ctimes10^{10}%2Bx^{3}}}{12}+x^{2/3})
</div>

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
