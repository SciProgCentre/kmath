# Module kmath-jafama

Integration with [Jafama](https://github.com/jeffhain/jafama).

 - [jafama-double](src/main/kotlin/space/kscience/kmath/jafama/) : Double ExtendedField implementations based on Jafama


## Artifact:

The Maven coordinates of this project are `space.kscience:kmath-jafama:0.3.0-dev-14`.

**Gradle:**
```gradle
repositories {
    maven { url 'https://repo.kotlin.link' }
    mavenCentral()
}

dependencies {
    implementation 'space.kscience:kmath-jafama:0.3.0-dev-14'
}
```
**Gradle Kotlin DSL:**
```kotlin
repositories {
    maven("https://repo.kotlin.link")
    mavenCentral()
}

dependencies {
    implementation("space.kscience:kmath-jafama:0.3.0-dev-14")
}
```

## Example usage

All the `DoubleField` uses can be replaced with `JafamaDoubleField` or `StrictJafamaDoubleField`.

```kotlin
import space.kscience.kmath.jafama.*
import space.kscience.kmath.operations.*

fun main() {
    val a = 2.0
    val b = StrictJafamaDoubleField { exp(a) }
    println(JafamaDoubleField { b + a })
    println(StrictJafamaDoubleField { ln(b) })
}
```

## Performance

According to KMath benchmarks on GraalVM, Jafama functions are slower than JDK math; however, there are indications that on Hotspot Jafama is a bit faster.

<details>
<summary>
Report for benchmark configuration <code>jafamaDouble</code>
</summary>

* Run on OpenJDK 64-Bit Server VM (build 11.0.11+8-jvmci-21.1-b05) with Java process:

```
/home/commandertvis/graalvm-ce-java11/bin/java -XX:+UnlockExperimentalVMOptions -XX:+EnableJVMCIProduct -XX:-UnlockExperimentalVMOptions -XX:ThreadPriorityPolicy=1 -javaagent:/home/commandertvis/.gradle/caches/modules-2/files-2.1/org.jetbrains.kotlinx/kotlinx-coroutines-core-jvm/1.5.0/d8cebccdcddd029022aa8646a5a953ff88b13ac8/kotlinx-coroutines-core-jvm-1.5.0.jar -Dfile.encoding=UTF-8 -Duser.country=US -Duser.language=en -Duser.variant -ea
```
* JMH 1.21 was used in `thrpt` mode with 1 warmup iteration by 1000 ms and 5 measurement iterations by 1000 ms.

| Benchmark | Score |
|:---------:|:-----:|
|`space.kscience.kmath.benchmarks.JafamaBenchmark.core`|14.35014650168397 &plusmn; 0.9200669832937576 ops/s|
|`space.kscience.kmath.benchmarks.JafamaBenchmark.jafama`|12.048429204455887 &plusmn; 1.2882929181842269 ops/s|
|`space.kscience.kmath.benchmarks.JafamaBenchmark.strictJafama`|12.977653357239152 &plusmn; 1.4122819627470866 ops/s|
</details>

