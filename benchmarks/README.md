# BenchmarksResult

## Report for benchmark configuration <code>main</code>

* Run on Java HotSpot(TM) 64-Bit Server VM (build 21.0.4+8-LTS-jvmci-23.1-b41) with Java process:

```
C:\Users\altavir\scoop\apps\graalvm-oracle-21jdk\current\bin\java.exe -XX:ThreadPriorityPolicy=1 -XX:+UnlockExperimentalVMOptions -XX:+EnableJVMCIProduct -XX:-UnlockExperimentalVMOptions -Dfile.encoding=UTF-8 -Duser.country=US -Duser.language=en -Duser.variant
```
* JMH 1.21 was used in `thrpt` mode with 5 warmup iterations by 10 s and 5 measurement iterations by 10 s.
### [ArrayBenchmark](src/jvmMain/kotlin/space/kscience/kmath/benchmarks/ArrayBenchmark.kt)

| Benchmark | Score |
|:---------:|:-----:|
|`benchmarkArrayRead`|1.9E+07 &plusmn; 2.3E+05 ops/s|
|`benchmarkBufferRead`|1.4E+07 &plusmn; 8.7E+05 ops/s|
|`nativeBufferRead`|1.4E+07 &plusmn; 1.3E+06 ops/s|
### [BigIntBenchmark](src/jvmMain/kotlin/space/kscience/kmath/benchmarks/BigIntBenchmark.kt)

| Benchmark | Score |
|:---------:|:-----:|
|`jvmAdd`|5.1E+07 &plusmn; 1.3E+06 ops/s|
|`jvmAddLarge`|5.1E+04 &plusmn; 8.2E+02 ops/s|
|`jvmMultiply`|8.5E+07 &plusmn; 9.7E+06 ops/s|
|`jvmMultiplyLarge`|2.5E+02 &plusmn; 15 ops/s|
|`jvmParsing10`|8.7E+06 &plusmn; 5.1E+05 ops/s|
|`jvmParsing16`|6.4E+06 &plusmn; 1.8E+05 ops/s|
|`jvmPower`|28 &plusmn; 0.79 ops/s|
|`jvmSmallAdd`|7.0E+07 &plusmn; 4.3E+06 ops/s|
|`kmAdd`|4.8E+07 &plusmn; 2.2E+06 ops/s|
|`kmAddLarge`|3.5E+04 &plusmn; 3.7E+03 ops/s|
|`kmMultiply`|6.7E+07 &plusmn; 1.5E+07 ops/s|
|`kmMultiplyLarge`|54 &plusmn; 4.2 ops/s|
|`kmParsing10`|4.5E+06 &plusmn; 8.3E+04 ops/s|
|`kmParsing16`|4.9E+06 &plusmn; 1.1E+05 ops/s|
|`kmPower`|10 &plusmn; 0.96 ops/s|
|`kmSmallAdd`|4.1E+07 &plusmn; 5.9E+05 ops/s|
### [BufferBenchmark](src/jvmMain/kotlin/space/kscience/kmath/benchmarks/BufferBenchmark.kt)

| Benchmark | Score |
|:---------:|:-----:|
|`bufferViewReadWrite`|5.8E+06 &plusmn; 1.6E+05 ops/s|
|`bufferViewReadWriteSpecialized`|5.6E+06 &plusmn; 2.6E+05 ops/s|
|`complexBufferReadWrite`|6.6E+06 &plusmn; 2.7E+05 ops/s|
|`doubleArrayReadWrite`|7.5E+06 &plusmn; 1.0E+06 ops/s|
|`doubleBufferReadWrite`|8.0E+06 &plusmn; 6.7E+05 ops/s|
### [DotBenchmark](src/jvmMain/kotlin/space/kscience/kmath/benchmarks/DotBenchmark.kt)

| Benchmark | Score |
|:---------:|:-----:|
|`bufferedDot`|1.3 &plusmn; 0.020 ops/s|
|`cmDot`|0.47 &plusmn; 0.42 ops/s|
|`cmDotWithConversion`|0.76 &plusmn; 0.13 ops/s|
|`ejmlDot`|6.7 &plusmn; 0.091 ops/s|
|`ejmlDotWithConversion`|6.4 &plusmn; 0.82 ops/s|
|`multikDot`|40 &plusmn; 6.7 ops/s|
|`parallelDot`|12 &plusmn; 1.8 ops/s|
|`tensorDot`|1.2 &plusmn; 0.041 ops/s|
|`tfDot`|5.9 &plusmn; 0.49 ops/s|
### [ExpressionsInterpretersBenchmark](src/jvmMain/kotlin/space/kscience/kmath/benchmarks/ExpressionsInterpretersBenchmark.kt)

| Benchmark | Score |
|:---------:|:-----:|
|`asmGenericExpression`|29 &plusmn; 1.2 ops/s|
|`asmPrimitiveExpression`|43 &plusmn; 1.3 ops/s|
|`asmPrimitiveExpressionArray`|71 &plusmn; 0.38 ops/s|
|`functionalExpression`|5.6 &plusmn; 0.11 ops/s|
|`justCalculate`|69 &plusmn; 9.0 ops/s|
|`mstExpression`|7.1 &plusmn; 0.020 ops/s|
|`rawExpression`|41 &plusmn; 1.5 ops/s|
### [IntegrationBenchmark](src/jvmMain/kotlin/space/kscience/kmath/benchmarks/IntegrationBenchmark.kt)

| Benchmark | Score |
|:---------:|:-----:|
|`complexIntegration`|3.6E+03 &plusmn; 1.9E+02 ops/s|
|`doubleIntegration`|3.7E+03 &plusmn; 12 ops/s|
### [JafamaBenchmark](src/jvmMain/kotlin/space/kscience/kmath/benchmarks/JafamaBenchmark.kt)

| Benchmark | Score |
|:---------:|:-----:|
|`core`|38 &plusmn; 0.64 ops/s|
|`jafama`|52 &plusmn; 0.36 ops/s|
|`strictJafama`|52 &plusmn; 4.0 ops/s|
### [MatrixInverseBenchmark](src/jvmMain/kotlin/space/kscience/kmath/benchmarks/MatrixInverseBenchmark.kt)

| Benchmark | Score |
|:---------:|:-----:|
|`cmLUPInversion`|2.2E+03 &plusmn; 76 ops/s|
|`ejmlInverse`|1.3E+03 &plusmn; 5.7 ops/s|
|`kmathLupInversion`|9.5E+02 &plusmn; 1.8E+02 ops/s|
|`kmathParallelLupInversion`|9.1E+02 &plusmn; 1.4E+02 ops/s|
### [NDFieldBenchmark](src/jvmMain/kotlin/space/kscience/kmath/benchmarks/NDFieldBenchmark.kt)

| Benchmark | Score |
|:---------:|:-----:|
|`boxingFieldAdd`|7.7 &plusmn; 0.79 ops/s|
|`multikAdd`|6.5 &plusmn; 0.33 ops/s|
|`multikInPlaceAdd`|64 &plusmn; 0.79 ops/s|
|`specializedFieldAdd`|8.0 &plusmn; 0.090 ops/s|
|`tensorAdd`|9.2 &plusmn; 0.053 ops/s|
|`tensorInPlaceAdd`|17 &plusmn; 10 ops/s|
|`viktorAdd`|7.6 &plusmn; 1.2 ops/s|
### [ViktorBenchmark](src/jvmMain/kotlin/space/kscience/kmath/benchmarks/ViktorBenchmark.kt)

| Benchmark | Score |
|:---------:|:-----:|
|`doubleFieldAddition`|7.7 &plusmn; 0.34 ops/s|
|`rawViktor`|5.9 &plusmn; 1.1 ops/s|
|`viktorFieldAddition`|7.3 &plusmn; 1.1 ops/s|
### [ViktorLogBenchmark](src/jvmMain/kotlin/space/kscience/kmath/benchmarks/ViktorLogBenchmark.kt)

| Benchmark | Score |
|:---------:|:-----:|
|`rawViktorLog`|1.4 &plusmn; 0.076 ops/s|
|`realFieldLog`|1.3 &plusmn; 0.069 ops/s|
|`viktorFieldLog`|1.3 &plusmn; 0.032 ops/s|



