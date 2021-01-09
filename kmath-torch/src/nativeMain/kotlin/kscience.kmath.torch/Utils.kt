package kscience.kmath.torch

import kotlinx.cinterop.*
import kscience.kmath.ctorch.*

public fun getNumThreads(): Int {
    return get_num_threads()
}

public fun setNumThreads(numThreads: Int): Unit {
    set_num_threads(numThreads)
}

public fun cudaAvailable(): Boolean {
    return cuda_is_available()
}

public fun setSeed(seed: Int): Unit {
    set_seed(seed)
}
