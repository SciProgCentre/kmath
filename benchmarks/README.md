# BenchmarksResult

## Report for benchmark configuration <code>main</code>

* Run on OpenJDK 64-Bit Server VM (build 17.0.11+9) with Java process:

```
C:\Users\altavir\scoop\apps\gradle\current\.gradle\jdks\eclipse_adoptium-17-amd64-windows.2\bin\java.exe -Dfile.encoding=UTF-8 -Duser.country=US -Duser.language=en -Duser.variant
```
* JMH 1.21 was used in `thrpt` mode with 5 warmup iterations by 10 s and 5 measurement iterations by 10 s.
### [ArrayBenchmark](src/jvmMain/kotlin/space/kscience/kmath/benchmarks/ArrayBenchmark.kt)

| Benchmark | Score |
|:---------:|:-----:|
|`benchmarkArrayRead`|3.9E+06 &plusmn; 3.4E+05 ops/s|
|`benchmarkBufferRead`|4.0E+06 &plusmn; 3.2E+05 ops/s|
|`nativeBufferRead`|3.9E+06 &plusmn; 2.0E+05 ops/s|
### [BigIntBenchmark](src/jvmMain/kotlin/space/kscience/kmath/benchmarks/BigIntBenchmark.kt)

| Benchmark | Score |
|:---------:|:-----:|
|`jvmAdd`|3.1E+07 &plusmn; 1.8E+07 ops/s|
|`jvmAddLarge`|4.5E+04 &plusmn; 5.5E+03 ops/s|
|`jvmMultiply`|3.6E+07 &plusmn; 1.7E+07 ops/s|
|`jvmMultiplyLarge`|1.9E+02 &plusmn; 95 ops/s|
|`jvmParsing10`|4.0E+06 &plusmn; 8.8E+05 ops/s|
|`jvmParsing16`|3.6E+06 &plusmn; 6.5E+05 ops/s|
|`jvmPower`|25 &plusmn; 1.4 ops/s|
|`jvmSmallAdd`|5.7E+07 &plusmn; 9.7E+05 ops/s|
|`kmAdd`|2.6E+07 &plusmn; 8.8E+05 ops/s|
|`kmAddLarge`|2.3E+04 &plusmn; 1.2E+03 ops/s|
|`kmMultiply`|3.8E+07 &plusmn; 5.5E+06 ops/s|
|`kmMultiplyLarge`|36 &plusmn; 3.8 ops/s|
|`kmParsing10`|2.5E+06 &plusmn; 1.4E+05 ops/s|
|`kmParsing16`|3.7E+06 &plusmn; 4.7E+05 ops/s|
|`kmPower`|6.6 &plusmn; 1.0 ops/s|
|`kmSmallAdd`|2.0E+07 &plusmn; 1.7E+06 ops/s|
### [BufferBenchmark](src/jvmMain/kotlin/space/kscience/kmath/benchmarks/BufferBenchmark.kt)

| Benchmark | Score |
|:---------:|:-----:|
|`bufferViewReadWrite`|6.0E+06 &plusmn; 7.4E+05 ops/s|
|`bufferViewReadWriteSpecialized`|7.6E+05 &plusmn; 1.1E+04 ops/s|
|`complexBufferReadWrite`|2.4E+06 &plusmn; 2.7E+05 ops/s|
|`doubleArrayReadWrite`|7.3E+06 &plusmn; 4.3E+05 ops/s|
|`doubleBufferReadWrite`|7.3E+06 &plusmn; 3.4E+05 ops/s|
### [DotBenchmark](src/jvmMain/kotlin/space/kscience/kmath/benchmarks/DotBenchmark.kt)

| Benchmark | Score |
|:---------:|:-----:|
|`bufferedDot`|1.3 &plusmn; 0.032 ops/s|
|`cmDot`|0.42 &plusmn; 0.20 ops/s|
|`cmDotWithConversion`|0.83 &plusmn; 0.12 ops/s|
|`ejmlDot`|2.6 &plusmn; 0.049 ops/s|
|`ejmlDotWithConversion`|2.5 &plusmn; 0.075 ops/s|
|`multikDot`|25 &plusmn; 0.52 ops/s|
|`ojalgoDot`|11 &plusmn; 1.3 ops/s|
|`parallelDot`|11 &plusmn; 0.17 ops/s|
|`tensorDot`|1.1 &plusmn; 0.028 ops/s|
|`tfDot`|4.7 &plusmn; 0.14 ops/s|
### [ExpressionsInterpretersBenchmark](src/jvmMain/kotlin/space/kscience/kmath/benchmarks/ExpressionsInterpretersBenchmark.kt)

| Benchmark | Score |
|:---------:|:-----:|
|`asmGenericExpression`|12 &plusmn; 0.099 ops/s|
|`asmPrimitiveExpression`|26 &plusmn; 0.57 ops/s|
|`asmPrimitiveExpressionArray`|74 &plusmn; 1.7 ops/s|
|`functionalExpression`|5.3 &plusmn; 0.015 ops/s|
|`justCalculate`|74 &plusmn; 0.85 ops/s|
|`mstExpression`|4.2 &plusmn; 0.10 ops/s|
|`rawExpression`|25 &plusmn; 0.74 ops/s|
### [IntegrationBenchmark](src/jvmMain/kotlin/space/kscience/kmath/benchmarks/IntegrationBenchmark.kt)

| Benchmark | Score |
|:---------:|:-----:|
|`complexIntegration`|2.6E+03 &plusmn; 46 ops/s|
|`doubleIntegration`|2.8E+03 &plusmn; 1.1E+02 ops/s|
### [MatrixInverseBenchmark](src/jvmMain/kotlin/space/kscience/kmath/benchmarks/MatrixInverseBenchmark.kt)

| Benchmark | Score |
|:---------:|:-----:|
|`cmLUPInversion`|2.1E+03 &plusmn; 35 ops/s|
|`ejmlInverse`|1.2E+03 &plusmn; 27 ops/s|
|`kmathLupInversion`|4.0E+02 &plusmn; 52 ops/s|
|`kmathParallelLupInversion`|4.0E+02 &plusmn; 9.6 ops/s|
|`ojalgoInverse`|2.1E+03 &plusmn; 3.3E+02 ops/s|
### [NDFieldBenchmark](src/jvmMain/kotlin/space/kscience/kmath/benchmarks/NDFieldBenchmark.kt)

| Benchmark | Score |
|:---------:|:-----:|
|`boxingFieldAdd`|1.7 &plusmn; 0.11 ops/s|
|`multikAdd`|7.0 &plusmn; 0.41 ops/s|
|`multikInPlaceAdd`|34 &plusmn; 1.7 ops/s|
|`specializedFieldAdd`|7.2 &plusmn; 1.2 ops/s|
|`tensorAdd`|7.2 &plusmn; 1.6 ops/s|
|`tensorInPlaceAdd`|7.4 &plusmn; 4.9 ops/s|
|`viktorAdd`|5.8 &plusmn; 0.65 ops/s|
### [ViktorBenchmark](src/jvmMain/kotlin/space/kscience/kmath/benchmarks/ViktorBenchmark.kt)

| Benchmark | Score |
|:---------:|:-----:|
|`doubleFieldAddition`|7.1 &plusmn; 2.0 ops/s|
|`rawViktor`|6.2 &plusmn; 1.0 ops/s|
|`viktorFieldAddition`|6.4 &plusmn; 0.29 ops/s|
### [ViktorLogBenchmark](src/jvmMain/kotlin/space/kscience/kmath/benchmarks/ViktorLogBenchmark.kt)

| Benchmark | Score |
|:---------:|:-----:|
|`rawViktorLog`|1.3 &plusmn; 0.016 ops/s|
|`realFieldLog`|1.3 &plusmn; 0.019 ops/s|
|`viktorFieldLog`|1.3 &plusmn; 0.020 ops/s|



