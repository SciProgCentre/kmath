plugins {
    kotlin("multiplatform")
    id("ru.mipt.npm.gradle.common")
}

kotlin.js {
    nodejs {
        testTask {
            useMocha().timeout = "0"
        }
    }

    browser {
        testTask {
            useMocha().timeout = "0"
        }
    }
}

kotlin.sourceSets {
    filter { it.name.contains("test", true) }
        .map(org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet::languageSettings)
        .forEach { it.useExperimentalAnnotation("space.kscience.kmath.misc.UnstableKMathAPI") }

    commonMain {
        dependencies {
            api("com.github.h0tk3y.betterParse:better-parse:0.4.2")
            api(project(":kmath-core"))
        }
    }

    commonTest {
        dependencies {
            implementation(project(":kmath-complex"))
        }
    }

    jsMain {
        dependencies {
            implementation(npm("astring", "1.7.4"))
            implementation(npm("binaryen", "100.0"))
            implementation(npm("js-base64", "3.6.0"))
            implementation(npm("webassembly", "0.11.0"))
        }
    }

    jvmMain {
        dependencies {
            implementation("org.ow2.asm:asm:9.1")
            implementation("org.ow2.asm:asm-commons:9.1")
        }
    }
}

//Workaround for https://github.com/Kotlin/dokka/issues/1455
tasks.dokkaHtml {
    dependsOn(tasks.build)
}

readme {
    maturity = ru.mipt.npm.gradle.Maturity.EXPERIMENTAL
    propertyByTemplate("artifact", rootProject.file("docs/templates/ARTIFACT-TEMPLATE.md"))

    feature(
        id = "expression-language",
        description = "Expression language and its parser",
        ref = "src/commonMain/kotlin/space/kscience/kmath/ast/parser.kt"
    )

    feature(
        id = "mst-jvm-codegen",
        description = "Dynamic MST to JVM bytecode compiler",
        ref = "src/jvmMain/kotlin/space/kscience/kmath/asm/asm.kt"
    )

    feature(
        id = "mst-js-codegen",
        description = "Dynamic MST to JS compiler",
        ref = "src/jsMain/kotlin/space/kscience/kmath/estree/estree.kt"
    )

    feature(
        id = "rendering",
        description = "Extendable MST rendering",
        ref = "src/commonMain/kotlin/space/kscience/kmath/ast/rendering/MathRenderer.kt"
    )
}
