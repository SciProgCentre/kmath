# KMath

## [Unreleased]
### Added
- ScaleOperations interface
- Field extends ScaleOperations
- Basic integration API

### Changed
- Exponential operations merged with hyperbolic functions
- Space is replaced by Group. Space is reserved for vector spaces.
- VectorSpace is now a vector space
- Buffer factories for primitives moved to MutableBuffer.Companion
- NDStructure and NDAlgebra to StructureND and AlgebraND respectively
- Real -> Double

### Deprecated

### Removed
- Nearest in Domain. To be implemented in geometry package.
- Number multiplication and division in main Algebra chain
- `contentEquals` from Buffer. It moved to the companion.

### Fixed

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

### Deprecated

### Removed
- `kmath-koma` module because it doesn't support Kotlin 1.4.
- Support of `legacy` JS backend (we will support only IR)
- `toGrid` method.
- Public visibility of `BufferAccessor2D`
- `Real` class
- StructureND identity and equals

### Fixed
- `symbol` method in `MstExtendedField` (https://github.com/mipt-npm/kmath/pull/140)

### Security

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
