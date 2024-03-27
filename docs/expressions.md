# Expressions

Expressions is a feature, which allows constructing lazily or immediately calculated parametric mathematical
expressions.

The potential use-cases for it (so far) are following:

* lazy evaluation (in general simple lambda is better, but there are some border cases);
* automatic differentiation in single-dimension and in multiple dimensions;
* generation of mathematical syntax trees with subsequent code generation for other languages;
* symbolic computations, especially differentiation (and some other actions with `kmath-symja` integration with
  Symja's `IExpr`&mdash;integration, simplification, and more);
* visualization with `kmath-jupyter`.

The workhorse of this API is `Expression` interface, which exposes
single `operator fun invoke(arguments: Map<Symbol, T>): T`
method. `ExpressionAlgebra` is used to generate expressions and introduce variables.

Currently there are two implementations:

* Generic `ExpressionField` in `kmath-core` which allows construction of custom lazy expressions

* Auto-differentiation expression in `kmath-commons` module allows using full power of `DerivativeStructure`
  from commons-math. **TODO: add example**
