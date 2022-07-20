# Module kmath-polynomial

Polynomial extra utilities and rational functions

## Features

 - [polynomial abstraction](src/commonMain/kotlin/space/kscience/kmath/functions/Polynomial.kt) : Abstraction for polynomial spaces.
 - [rational function abstraction](src/commonMain/kotlin/space/kscience/kmath/functions/RationalFunction.kt) : Abstraction for rational functions spaces.
 - ["list" polynomials](src/commonMain/kotlin/space/kscience/kmath/functions/ListRationalFunction.kt) : List implementation of univariate polynomials.
 - ["list" rational functions](src/commonMain/kotlin/space/kscience/kmath/functions/ListPolynomial.kt) : List implementation of univariate rational functions.
 - ["list" polynomials and rational functions constructors](src/commonMain/kotlin/space/kscience/kmath/functions/listConstructors.kt) : Constructors for list polynomials and rational functions.
 - ["list" polynomials and rational functions utilities](src/commonMain/kotlin/space/kscience/kmath/functions/listUtil.kt) : Utilities for list polynomials and rational functions.
 - ["numbered" polynomials](src/commonMain/kotlin/space/kscience/kmath/functions/NumberedRationalFunction.kt) : Numbered implementation of multivariate polynomials.
 - ["numbered" rational functions](src/commonMain/kotlin/space/kscience/kmath/functions/NumberedPolynomial.kt) : Numbered implementation of multivariate rational functions.
 - ["numbered" polynomials and rational functions constructors](src/commonMain/kotlin/space/kscience/kmath/functions/numberedConstructors.kt) : Constructors for numbered polynomials and rational functions.
 - ["numbered" polynomials and rational functions utilities](src/commonMain/kotlin/space/kscience/kmath/functions/numberedUtil.kt) : Utilities for numbered polynomials and rational functions.
 - ["labeled" polynomials](src/commonMain/kotlin/space/kscience/kmath/functions/LabeledRationalFunction.kt) : Labeled implementation of multivariate polynomials.
 - ["labeled" rational functions](src/commonMain/kotlin/space/kscience/kmath/functions/LabeledPolynomial.kt) : Labeled implementation of multivariate rational functions.
 - ["labeled" polynomials and rational functions constructors](src/commonMain/kotlin/space/kscience/kmath/functions/labeledConstructors.kt) : Constructors for labeled polynomials and rational functions.
 - ["labeled" polynomials and rational functions utilities](src/commonMain/kotlin/space/kscience/kmath/functions/labeledUtil.kt) : Utilities for labeled polynomials and rational functions.


## Usage

## Artifact:

The Maven coordinates of this project are `space.kscience:kmath-polynomial:0.3.1-dev-1`.

**Gradle Groovy:**
```groovy
repositories {
    maven { url 'https://repo.kotlin.link' }
    mavenCentral()
}

dependencies {
    implementation 'space.kscience:kmath-polynomial:0.3.1-dev-1'
}
```
**Gradle Kotlin DSL:**
```kotlin
repositories {
    maven("https://repo.kotlin.link")
    mavenCentral()
}

dependencies {
    implementation("space.kscience:kmath-polynomial:0.3.1-dev-1")
}
```
