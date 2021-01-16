package kscience.kmath.torch


public fun getNumThreads(): Int {
    return JTorch.getNumThreads()
}

public fun setNumThreads(numThreads: Int): Unit {
    JTorch.setNumThreads(numThreads)
}

public fun cudaAvailable(): Boolean {
    TODO("Implementation not available yet")
}

public fun setSeed(seed: Int): Unit {
    TODO("Implementation not available yet")
}


public fun runCPD(): Unit {
    val tensorHandle = JTorch.createTensor()
    JTorch.printTensor(tensorHandle)
    JTorch.disposeTensor(tensorHandle)
}