# KMath

## [Unreleased]

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
