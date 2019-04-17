plugins {
    id("multiplatform-config")
}

// We actually don't need this, we define jvm and js targets in multiplatform-config
kotlin {
    jvm()
    js()
}
