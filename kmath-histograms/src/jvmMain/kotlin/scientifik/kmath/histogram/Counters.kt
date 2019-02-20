package scientifik.kmath.histogram

import java.util.concurrent.atomic.DoubleAdder
import java.util.concurrent.atomic.LongAdder

actual typealias LongCounter = LongAdder
actual typealias DoubleCounter = DoubleAdder