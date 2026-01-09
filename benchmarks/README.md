# Module KMath-Benchmarks

# BenchmarksResult

## Report for benchmark configuration <code>main</code>

* Run on OpenJDK 64-Bit Server VM (build 21.0.9+10-LTS) with Java process:

```
C:\Users\altavir\.gradle\jdks\eclipse_adoptium-21-amd64-windows.2\bin\java.exe -Dfile.encoding=UTF-8 -Duser.country=US -Duser.language=en -Duser.variant
```
* JMH 1.37 was used in `thrpt` mode with 5 warmup iterations by 10 s and 5 measurement iterations by 10 s.
### [ArrayBenchmark](src/jvmMain/kotlin/space/kscience/kmath/benchmarks/ArrayBenchmark.kt)

| Benchmark | Score |
|:---------:|:-----:|
|`benchmarkArrayRead`|3.9E+06 &plusmn; 1.1E+06 ops/s|
|`benchmarkBufferRead`|4.0E+06 &plusmn; 2.2E+05 ops/s|
|`nativeBufferRead`|4.0E+06 &plusmn; 1.7E+05 ops/s|
### [BigIntBenchmark](src/jvmMain/kotlin/space/kscience/kmath/benchmarks/BigIntBenchmark.kt)

| Benchmark | Score |
|:---------:|:-----:|
|`jvmAdd`|2.9E+07 &plusmn; 2.8E+06 ops/s|
|`jvmAddLarge`|3.8E+04 &plusmn; 6.4E+03 ops/s|
|`jvmMultiply`|5.3E+07 &plusmn; 6.1E+06 ops/s|
|`jvmMultiplyLarge`|2.2E+02 &plusmn; 1.9 ops/s|
|`jvmParsing10`|3.9E+06 &plusmn; 4.7E+05 ops/s|
|`jvmParsing16`|3.1E+06 &plusmn; 4.6E+05 ops/s|
|`jvmPower`|24 &plusmn; 1.7 ops/s|
|`jvmSmallAdd`|4.7E+07 &plusmn; 4.6E+06 ops/s|
|`kmAdd`|2.3E+07 &plusmn; 5.1E+06 ops/s|
|`kmAddLarge`|2.6E+04 &plusmn; 3.0E+02 ops/s|
|`kmMultiply`|3.7E+07 &plusmn; 2.9E+06 ops/s|
|`kmMultiplyLarge`|34 &plusmn; 2.8 ops/s|
|`kmParsing10`|2.5E+06 &plusmn; 1.5E+05 ops/s|
|`kmParsing16`|4.0E+06 &plusmn; 2.4E+05 ops/s|
|`kmPower`|6.5 &plusmn; 0.69 ops/s|
|`kmSmallAdd`|1.6E+07 &plusmn; 8.0E+05 ops/s|
### [BufferBenchmark](src/jvmMain/kotlin/space/kscience/kmath/benchmarks/BufferBenchmark.kt)

| Benchmark | Score |
|:---------:|:-----:|
|`bufferViewReadWrite`|5.4E+06 &plusmn; 3.8E+05 ops/s|
|`bufferViewReadWriteSpecialized`|5.0E+06 &plusmn; 1.2E+06 ops/s|
|`complexBufferReadWrite`|2.2E+06 &plusmn; 5.7E+04 ops/s|
|`doubleArrayReadWrite`|6.9E+06 &plusmn; 1.2E+06 ops/s|
|`doubleBufferReadWrite`|6.6E+06 &plusmn; 1.1E+06 ops/s|
### [DotBenchmark](src/jvmMain/kotlin/space/kscience/kmath/benchmarks/DotBenchmark.kt)

| Benchmark | Score |
|:---------:|:-----:|
|`bufferedDot`|1.2 &plusmn; 0.20 ops/s|
|`cmDot`|0.36 &plusmn; 0.14 ops/s|
|`cmDotWithConversion`|0.80 &plusmn; 0.092 ops/s|
|`ejmlDot`|2.9 &plusmn; 0.61 ops/s|
|`ejmlDotWithConversion`|2.7 &plusmn; 0.15 ops/s|
|`multikDot`|23 &plusmn; 2.4 ops/s|
|`ojalgoDot`|11 &plusmn; 0.79 ops/s|
|`parallelDot`|9.4 &plusmn; 1.3 ops/s|
|`tensorDot`|1.0 &plusmn; 0.15 ops/s|
|`tfDot`|3.9 &plusmn; 0.90 ops/s|
### [ExpressionsInterpretersBenchmark](src/jvmMain/kotlin/space/kscience/kmath/benchmarks/ExpressionsInterpretersBenchmark.kt)

| Benchmark | Score |
|:---------:|:-----:|
|`asmGenericExpression`|15 &plusmn; 1.8 ops/s|
|`asmPrimitiveExpression`|27 &plusmn; 0.98 ops/s|
|`asmPrimitiveExpressionArray`|78 &plusmn; 14 ops/s|
|`functionalExpression`|4.4 &plusmn; 0.25 ops/s|
|`justCalculate`|79 &plusmn; 5.4 ops/s|
|`mstExpression`|4.2 &plusmn; 0.93 ops/s|
|`rawExpression`|25 &plusmn; 5.0 ops/s|
### [IntegrationBenchmark](src/jvmMain/kotlin/space/kscience/kmath/benchmarks/IntegrationBenchmark.kt)

| Benchmark | Score |
|:---------:|:-----:|
|`complexIntegration`|2.2E+03 &plusmn; 3.0E+02 ops/s|
|`doubleIntegration`|2.3E+03 &plusmn; 6.4E+02 ops/s|
### [MatrixInverseBenchmark](src/jvmMain/kotlin/space/kscience/kmath/benchmarks/MatrixInverseBenchmark.kt)

| Benchmark | Score |
|:---------:|:-----:|
|`cmLUPInversion`|2.0E+03 &plusmn; 1.1E+02 ops/s|
|`ejmlInverse`|1.2E+03 &plusmn; 29 ops/s|
|`kmathLupInversion`|3.9E+02 &plusmn; 92 ops/s|
|`kmathParallelLupInversion`|55 &plusmn; 5.0 ops/s|
|`ojalgoInverse`|1.7E+03 &plusmn; 35 ops/s|
### [MinStatisticBenchmark](src/jvmMain/kotlin/space/kscience/kmath/benchmarks/MinStatisticBenchmark.kt)

| Benchmark | Score |
|:---------:|:-----:|
|`kotlinArrayMin`|1.6E+03 &plusmn; 3.0E+02 ops/s|
|`minBlocking`|1.2E+03 &plusmn; 1.2E+02 ops/s|
### [NDFieldBenchmark](src/jvmMain/kotlin/space/kscience/kmath/benchmarks/NDFieldBenchmark.kt)

| Benchmark | Score |
|:---------:|:-----:|
|`boxingFieldAdd`|1.9 &plusmn; 0.089 ops/s|
|`multikAdd`|6.8 &plusmn; 1.0 ops/s|
|`multikInPlaceAdd`|32 &plusmn; 4.7 ops/s|
|`specializedFieldAdd`|6.7 &plusmn; 0.98 ops/s|
|`tensorAdd`|7.9 &plusmn; 1.1 ops/s|
|`tensorInPlaceAdd`|11 &plusmn; 3.4 ops/s|
|`viktorAdd`|6.4 &plusmn; 0.41 ops/s|
### [ViktorBenchmark](src/jvmMain/kotlin/space/kscience/kmath/benchmarks/ViktorBenchmark.kt)

| Benchmark | Score |
|:---------:|:-----:|
|`doubleFieldAddition`|7.3 &plusmn; 1.1 ops/s|
|`rawViktor`|6.0 &plusmn; 0.88 ops/s|
|`viktorFieldAddition`|6.7 &plusmn; 0.47 ops/s|
### [ViktorLogBenchmark](src/jvmMain/kotlin/space/kscience/kmath/benchmarks/ViktorLogBenchmark.kt)

| Benchmark | Score |
|:---------:|:-----:|
|`rawViktorLog`|1.3 &plusmn; 0.40 ops/s|
|`realFieldLog`|1.2 &plusmn; 0.34 ops/s|
|`viktorFieldLog`|1.3 &plusmn; 0.0073 ops/s|



