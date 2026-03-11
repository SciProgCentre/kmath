plugins {
    id("space.kscience.gradle.mpp")
    alias(spclibs.plugins.kotlin.jupyter.api)
}

kscience {
    jvm()

    jvmMain {
        api(spclibs.kotlinx.html)
        api(project(":kmath-ast"))
        api(project(":kmath-complex"))
        api(project(":kmath-for-real"))
    }
}

readme {
    maturity = space.kscience.gradle.Maturity.PROTOTYPE
}

kotlinJupyter {
    integrations {
        producer("space.kscience.kmath.jupyter.KMathJupyter")
    }
}
