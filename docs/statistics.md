# Statistics

Mathematically speaking, a statistic is a measurable numerical function of sample data.
In KMath, a statistic is a function that operates on a [Buffer](buffers.md) and is implemented as the `evaluate` method
of the `Statistic` interface.
There are two subinterfaces of the `Statistic` interface:

* `BlockingStatistic` – A statistic that is computed in a synchronous blocking mode
* `ComposableStatistic` – A statistic tha could be computed separately on different blocks of data and then composed


## Common statistics and Implementation Status

| Category         | Statistic         | Description                         | Implementation Status          |
|------------------|-------------------|-------------------------------------|--------------------------------|
| **Basic**        | Min               | Minimum value                       | ✅ `ComposableStatistic`        |
|                  | Max               | Maximum value                       | ✅ `ComposableStatistic`        |
|                  | Mean              | Arithmetic mean                     | ✅ `ComposableStatistic`        |
|                  | Sum               | Sum of all values                   | 🚧 Not yet implemented         |
|                  | Product           | Product of all values               | 🚧 Not yet implemented         |
| **Distribution** | Median            | Median (50th percentile)            | ✅ `BlockingStatistic`          |
|                  | Quantile          | Arbitrary percentile (e.g., Q1, Q3) | 🚧 Not yet implemented         |
|                  | Variance          | Unbiased sample variance            | 🚧 *(Requires `SumOfSquares`)* |
|                  | StandardDeviation | Population standard deviation (σ)   | 🚧 *(Depends on `Variance`)*   |
|                  | Skewness          | Measure of distribution asymmetry   | 🚧 *(Requires `ThirdMoment`)*  |
|                  | Kurtosis          | Measure of distribution tailedness  | 🚧 *(Requires `FourthMoment`)* |
| **Advanced**     | GeometricMean     | Nth root of product of values       | 🚧 *(Requires `SumOfLogs`)*    |
|                  | SumOfLogs         | Sum of natural logarithms           | 🚧 Not yet implemented         |
|                  | SumOfSquares      | Sum of squared values               | 🚧 *(Blocks `Variance`)*       |
| **Moments**      | FirstMoment       | Mean (same as `Mean`)               | ✅ *(Alias for `Mean`)*         |
|                  | SecondMoment      | Variance (same as `Variance`)       | 🚧 *(Alias for `Variance`)*    |
|                  | ThirdMoment       | Used in skewness calculation        | 🚧 Not yet implemented         |
|                  | FourthMoment      | Used in kurtosis calculation        | 🚧 Not yet implemented         |
| **Risk Metrics** | SemiVariance      | Downside variance                   | 🚧 *(Depends on `Variance`)*   |
