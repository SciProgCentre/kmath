# KMath

## [Unreleased]
### Added

### Changed

### Deprecated

### Removed

### Fixed

### Security

## [0.3.0]
### Added
- `ScaleOperations` interface
- `Field` extends `ScaleOperations`
- Basic integration API
- Basic MPP distributions and samplers
- `bindSymbolOrNull`
- Blocking chains and Statistics
- Multiplatform integration
- Integration for any Field element
- Extended operations for ND4J fields
- Jupyter Notebook integration module (kmath-jupyter)
- `@PerformancePitfall` annotation to mark possibly slow API
- Unified architecture for Integration and Optimization using features.
- `BigInt` operation performance improvement and fixes by @zhelenskiy (#328)
- Integration between `MST` and Symja `IExpr`
- Complex power
- Separate methods for UInt, Int and Number powers. NaN safety.
- Tensorflow prototype
- `ValueAndErrorField`
- MST compilation to WASM: #286
- Jafama integration: #176
- `contentEquals` with tolerance: #364
- Compilation to TeX for MST: #254


### Changed
- Exponential operations merged with hyperbolic functions
- Space is replaced by Group. Space is reserved for vector spaces.
- VectorSpace is now a vector space
- Buffer factories for primitives moved to MutableBuffer.Companion
- Rename `NDStructure` and `NDAlgebra` to `StructureND` and `AlgebraND` respectively
- `Real` -> `Double`
- DataSets are moved from functions to core
- Redesign advanced Chain API
- Redesign `MST`. Remove `MstExpression`.
- Move `MST` to core
- Separated benchmarks and examples
- Rewrite `kmath-ejml` without `ejml-simple` artifact, support sparse matrices
- Promote stability of kmath-ast and kmath-kotlingrad to EXPERIMENTAL.
- ColumnarData returns nullable column
- `MST` is made sealed interface
- Replace `MST.Symbolic` by `Symbol`, `Symbol` now implements MST
- Remove Any restriction on polynomials
- Add `out` variance to type parameters of `StructureND` and its implementations where possible
- Rename `DifferentiableMstExpression` to `KotlingradExpression`
- `FeatureSet` now accepts only `Feature`. It is possible to override keys and use interfaces.
- Use `Symbol` factory function instead of `StringSymbol`
- New discoverability pattern: `<Type>.algebra.<nd/etc>`
- Adjusted commons-math API for linear solvers to match conventions.
- Buffer algebra does not require size anymore
- Operations -> Ops
- Default Buffer and ND algebras are now Ops and lack neutral elements (0, 1) as well as algebra-level shapes.
- Tensor algebra takes read-only structures as input and inherits AlgebraND
- `UnivariateDistribution` renamed to `Distribution1D`
- Rework of histograms.
- `UnivariateFunction` -> `Function1D`, `MultivariateFunction` -> `FunctionND`


### Deprecated
- Specialized `DoubleBufferAlgebra`


### Removed
- Nearest in Domain. To be implemented in geometry package.
- Number multiplication and division in main Algebra chain
- `contentEquals` from Buffer. It moved to the companion.
- MSTExpression
- Expression algebra builders
- Complex and Quaternion no longer are elements.
- Second generic from DifferentiableExpression
- Algebra elements are completely removed. Use algebra contexts instead.


### Fixed
- Ring inherits RingOperations, not GroupOperations
- Univariate histogram filling


### Security

## [0.2.0]
### Added
- `fun` annotation for SAM interfaces in library
- Explicit `public` visibility for all public APIs
- Better trigonometric and hyperbolic functions for `AutoDiffField` (https://github.com/mipt-npm/kmath/pull/140)
- Automatic README generation for features (#139)
- Native support for `memory`, `core` and `dimensions`
- `kmath-ejml` to supply EJML SimpleMatrix wrapper (https://github.com/mipt-npm/kmath/pull/136)
- A separate `Symbol` entity, which is used for global unbound symbol.
- A `Symbol` indexing scope.
- Basic optimization API for Commons-math.
- Chi squared optimization for array-like data in CM
- `Fitting` utility object in prob/stat
- ND4J support module submitting `NDStructure` and `NDAlgebra` over `INDArray`
- Coroutine-deterministic Monte-Carlo scope with a random number generator
- Some minor utilities to `kmath-for-real`
- Generic operation result parameter to `MatrixContext`
- New `MatrixFeature` interfaces for matrix decompositions
- Basic Quaternion vector support in `kmath-complex`.


### Changed
- Package changed from `scientifik` to `space.kscience`
- Gradle version: 6.6 -> 6.8.2
- Minor exceptions refactor (throwing `IllegalArgumentException` by argument checks instead of `IllegalStateException`)
- `Polynomial` secondary constructor made function
- Kotlin version: 1.3.72 -> 1.4.30
- `kmath-ast` doesn't depend on heavy `kotlin-reflect` library
- Full autodiff refactoring based on `Symbol`
- `kmath-prob` renamed to `kmath-stat`
- Grid generators moved to `kmath-for-real`
- Use `Point<Double>` instead of specialized type in `kmath-for-real`
- Optimized dot product for buffer matrices moved to `kmath-for-real`
- EjmlMatrix context is an object
- Matrix LUP `inverse` renamed to `inverseWithLup`
- `NumericAlgebra` moved outside of regular algebra chain (`Ring` no longer implements it).
- Features moved to NDStructure and became transparent.
- Capitalization of LUP in many names changed to Lup.
- Refactored `NDStructure` algebra to be more simple, preferring under-the-hood conversion to explicit NDStructure types
- Refactor histograms. They are marked as prototype
- `Complex` and related features moved to a separate module `kmath-complex`
- Refactor AlgebraElement
- `symbol` method in `Algebra` renamed to `bindSymbol` to avoid ambiguity
- Add `out` projection to `Buffer` generic


### Removed
- `kmath-koma` module because it doesn't support Kotlin 1.4.
- Support of `legacy` JS backend (we will support only IR)
- `toGrid` method.
- Public visibility of `BufferAccessor2D`
- `Real` class
- StructureND identity and equals


### Fixed
- `symbol` method in `MstExtendedField` (https://github.com/mipt-npm/kmath/pull/140)

## [0.1.4]
### Added
- Functional Expressions API
- Mathematical Syntax Tree, its interpreter and API
- String to MST parser (https://github.com/mipt-npm/kmath/pull/120)
- MST to JVM bytecode translator (https://github.com/mipt-npm/kmath/pull/94)
- FloatBuffer (specialized MutableBuffer over FloatArray)
- FlaggedBuffer to associate primitive numbers buffer with flags (to mark values infinite or missing, etc.)
- Specialized builder functions for all primitive buffers like `IntBuffer(25) { it + 1 }` (https://github.com/mipt-npm/kmath/pull/125)
- Interface `NumericAlgebra` where `number` operation is available to convert numbers to algebraic elements
- Inverse trigonometric functions support in ExtendedField (`asin`, `acos`, `atan`) (https://github.com/mipt-npm/kmath/pull/114)
- New space extensions: `average` and `averageWith`
- Local coding conventions
- Geometric Domains API in `kmath-core`
- Blocking chains in `kmath-coroutines`
- Full hyperbolic functions support and default implementations within `ExtendedField`
- Norm support for `Complex`


### Changed
- `readAsMemory` now has `throws IOException` in JVM signature.
- Several functions taking functional types were made `inline`.
- Several functions taking functional types now have `callsInPlace` contracts.
- BigInteger and BigDecimal algebra: JBigDecimalField has companion object with default math context; minor optimizations
- `power(T, Int)` extension function has preconditions and supports `Field<T>`
- Memory objects have more preconditions (overflow checking)
- `tg` function is renamed to `tan` (https://github.com/mipt-npm/kmath/pull/114)
- Gradle version: 6.3 -> 6.6
- Moved probability distributions to commons-rng and to `kmath-prob`


### Fixed
- Missing copy method in Memory implementation on JS (https://github.com/mipt-npm/kmath/pull/106)
- D3.dim value in `kmath-dimensions`
- Multiplication in integer rings in `kmath-core` (https://github.com/mipt-npm/kmath/pull/101)
- Commons RNG compatibility (https://github.com/mipt-npm/kmath/issues/93)
- Multiplication of BigInt by scalar