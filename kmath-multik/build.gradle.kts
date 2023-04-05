plugins {
    id("space.kscience.gradle.mpp")
}

description = "JetBrains Multik connector"

val multikVersion: String by rootProject.extra

kscience {
    jvm()
    js()
}

kotlin{
    sourceSets{
        commonMain{
            dependencies{
                api(project(":kmath-tensors"))
                api("org.jetbrains.kotlinx:multik-core:$multikVersion")
            }
        }
        commonTest{
            dependencies{
                api("org.jetbrains.kotlinx:multik-default:$multikVersion")
            }
        }
    }
}

readme {
    maturity = space.kscience.gradle.Maturity.PROTOTYPE
}