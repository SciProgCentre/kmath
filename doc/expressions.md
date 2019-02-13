# Expressions

**Experimental: this API is in early stage and could change any time**

Expressions is an experimental feature which allows to construct lazily or immediately calculated parametric mathematical 
expressions.

The potential use-cases for it (so far) are following:

* Lazy evaluation (in general simple lambda is better, but there are some border cases)

* Automatic differentiation in single-dimension and in multiple dimensions

* Generation of mathematical syntax trees with subsequent code generation for other languages

* Maybe symbolic computations (needs additional research)

The workhorse of this API is `Expression` interface which exposes single `operator fun invoke(arguments: Map<String, T>): T`
method. `ExpressionContext` is used to generate expressions and introduce variables.

Currently there are two implementations:

* Generic `ExpressionField` in `kmath-core` which allows construction of custom lazy expressions

* Auto-differentiation expression in `kmath-commons` module allows to use full power of `DerivativeStructure` 
from commons-math. **TODO: add example**
