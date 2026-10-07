# Arbitrary Precision Numbers in kmath-core

`kmath-core` provides pure Kotlin Multiplatform implementations for arbitrary-precision integers (`BigInt`) and fixed-scale decimals (`BigDecimal`), together with their respective algebraic contexts and JVM-specific wrappers.

---

## BigInt (Arbitrary Precision Integers)

`BigInt` represents arbitrary-precision signed integers across all supported Kotlin Multiplatform targets.

### Creation and Parsing

- **From primitive integers**:
  ```kotlin
  import space.kscience.kmath.operations.*

  val a = 123456789.toBigInt()
  val b = 9876543210123456789L.toBigInt()
  val c = 42U.toBigInt()
  ```

- **From strings (within `BigIntField` context)**:
  Decimal (with optional `_` digit separators and signs) and hexadecimal (`0x` prefix) strings can be parsed or converted using unary operators:
  ```kotlin
  BigIntField {
      val dec = "123_456_789_012_345_678_901_234_567_890".parseBigInteger()
      val hex = "0x1A2B_3C4D_5E6F".parseBigInteger()

      // Unary operators for string literals:
      val x = +"1000000000000000000000000"
      val y = -"500000000000000000000000"
  }
  ```

- **Constants**:
  `BigInt.ZERO`, `BigInt.ONE`, `BigInt.TWO`, `BigInt.TEN`, `BigInt.NEGATIVE_ONE`

### Operations

- **Arithmetic & Comparison**:
  `BigInt` implements `Comparable<BigInt>` and operators `+`, `-`, `*`, `/`, `%` (`rem`), unary `-`, and `abs()`:
  ```kotlin
  val x = 1000.toBigInt()
  val y = 300.toBigInt()

  val sum = x + y
  val diff = x - y
  val prod = x * y
  val quot = x / y // integer division
  val rem = x % y  // remainder
  ```

- **Mixed Arithmetic**:
  Direct operations with `Int` and `UInt`:
  ```kotlin
  val res = x * 10
  val div = x / 2U
  ```

- **Bitwise and Exponentiation**:
  ```kotlin
  val shifted = x shl 4
  val masked = x and 0xFF.toBigInt()
  val pow = x.pow(3U)
  val modPow = x.modPow(exponent = 65537.toBigInt(), m = 1000000007.toBigInt())
  ```

- **Conversions**:
  ```kotlin
  val s = x.toDecimalString()
  val l = x.toLong()
  val i = x.toInt()
  val d = x.toDouble()
  val bd = x.toBigDecimal()
  ```

---

## BigDecimal (Arbitrary Precision Decimals)

`BigDecimal` represents arbitrary-precision decimal numbers, defined by an unscaled `BigInt` value and an integer `scale` ($value \times 10^{-scale}$).

### Creation and Parsing

- **From numbers and BigInt**:
  ```kotlin
  import space.kscience.kmath.operations.*

  val a = BigDecimal(12345, scale = 2) // 123.45
  val b = 123.toBigDecimal()
  val c = 456L.toBigDecimal()
  val d = 123.toBigInt().toBigDecimal()
  ```

- **From strings and Double (within `BigDecimalField` context)**:
  ```kotlin
  BigDecimalField {
      val parsed = "123.4567890123456789".parseBigDecimal()
      val sci = "1.23e-4".parseBigDecimal()
      val fromDouble = 123.45.toBigDecimal()

      // Unary operators for string literals:
      val x = +"3.14159265358979323846"
      val y = -"2.71828182845904523536"
  }
  ```

- **Constants**:
  `BigDecimal.ZERO`, `BigDecimal.ONE`, `BigDecimal.TWO`, `BigDecimal.TEN`, `BigDecimal.NEGATIVE_ONE`

### Operations and Precision

- **Arithmetic**:
  ```kotlin
  val a = BigDecimal(1234, 2) // 12.34
  val b = BigDecimal(566, 2)  // 5.66

  val sum = a + b  // 18.00
  val diff = a - b // 6.68
  val prod = a * b // 69.8444
  ```

- **Division and Exponentiation with Decimal Precision**:
  Because decimal division can produce infinite repeating decimals, division accepts a precision parameter (defaulting to 128 decimal places):
  ```kotlin
  val oneThird = 1.toBigDecimal().divide(3.toBigDecimal(), decimalPrecision = 50)
  val power = 2.toBigDecimal().pow(-2, decimalPrecision = 50) // 0.25
  ```

- **Conversions and Formatting**:
  ```kotlin
  val d = a.toDouble()
  val plain = a.toPlainString()
  val sci = a.toScientificString()
  val dec = a.toDecimalString()
  val bigInt = a.toBigInt()
  ```

---

## Algebraic Contexts

`kmath-core` integrates `BigInt` and `BigDecimal` into KMath's algebra hierarchy.

### `BigIntField`

`BigIntField` implements `Field<BigInt>`, `NumbersAddOps<BigInt>`, and `ScaleOperations<BigInt>`:

```kotlin
import space.kscience.kmath.operations.*

val result = BigIntField {
    val a = +"1000000000000000000"
    val b = +"2000000000000000000"
    a + b
}
```

### `BigDecimalField`

`BigDecimalField` implements `ExtendedField<BigDecimal>`, `NumbersAddOps<BigDecimal>`, `ScaleOperations<BigDecimal>`, and `Norm<BigDecimal, BigDecimal>`. It supports elementary and transcendental functions (`sin`, `cos`, `tan`, `asin`, `acos`, `atan`, `sinh`, `cosh`, `tanh`, `exp`, `ln`, `sqrt`, `power`):

```kotlin
import space.kscience.kmath.operations.*

// Default precision (128 decimal places)
val res = BigDecimalField {
    val x = +"2.0"
    sqrt(x) + sin(+"0.5")
}

// Custom precision
val highPrecisionField = BigDecimalField(decimalPrecision = 256)
val piEstimate = highPrecisionField {
    +"355" / +"113"
}
```

### Buffer and ND Support

Both types support KMath buffer factories and N-dimensional arrays:

```kotlin
import space.kscience.kmath.operations.*
import space.kscience.kmath.nd.*

// 1D Buffer
val buffer = BigInt.buffer(10) { index -> index.toBigInt() }

// N-Dimensional operations
val ndField = BigDecimalField.nd
```

---

## JVM Big Numbers Integration

On JVM targets, `kmath-core` provides algebra contexts wrapping Java's `java.math.BigInteger` and `java.math.BigDecimal`:

- **`JBigIntegerField`**: `Ring<java.math.BigInteger>` and `NumericAlgebra<java.math.BigInteger>`.
- **`JBigDecimalField`**: `Field<java.math.BigDecimal>`, `PowerOperations<java.math.BigDecimal>`, and `ScaleOperations<java.math.BigDecimal>` configured with `java.math.MathContext` (defaults to `MathContext.DECIMAL64`).

```kotlin
import space.kscience.kmath.operations.*
import java.math.MathContext

val res = JBigDecimalField(MathContext.DECIMAL128) {
    val a = number(1.5)
    val b = number(2.5)
    a * b + sqrt(a)
}
```
