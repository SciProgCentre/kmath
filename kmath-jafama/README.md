# Module kmath-jafama

Jafama based implementation of DoubleField of kmath-operations.

- JafamaDoubleField : DoubleField implementation using FastMath
- StrictJafamaDoubleField - DoubleField implementation using StrictFastMath

## Examples

Different Operations on DoubleField

```kotlin
package space.kscience.kmath.jafama

import net.jafama.FastMath


fun main(){
    val a = JafamaDoubleField.number(2.0)
    val b = StrictJafamaDoubleField.power(FastMath.E,a)
    println(JafamaDoubleField.add(b,a))
    println(StrictJafamaDoubleField.ln(b))
}
```
