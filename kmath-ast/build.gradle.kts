plugins {
    id("space.kscience.gradle.mpp")
}

kscience{
    native()
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
        .forEach { it.optIn("space.kscience.kmath.misc.UnstableKMathAPI") }

    commonMain {
        dependencies {
            api("com.github.h0tk3y.betterParse:better-parse:0.4.4")
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
            implementation(npm("astring", "1.7.5"))
            implementation(npm("binaryen", "101.0.0"))
            implementation(npm("js-base64", "3.6.1"))
        }
    }

    jvmMain {
        dependencies {
            implementation("org.ow2.asm:asm-commons:9.2")
        }
    }
}

//Workaround for https://github.com/Kotlin/dokka/issues/1455
tasks.dokkaHtml {
    dependsOn(tasks.build)
}

if (System.getProperty("space.kscience.kmath.ast.dump.generated.classes") == "1")
    tasks.jvmTest {
        jvmArgs("-Dspace.kscience.kmath.ast.dump.generated.classes=1")
    }

readme {
    maturity = space.kscience.gradle.Maturity.EXPERIMENTAL
    propertyByTemplate("artifact", rootProject.file("docs/templates/ARTIFACT-TEMPLATE.md"))

    feature(
        id = "expression-language",
        ref = "src/commonMain/kotlin/space/kscience/kmath/ast/parser.kt"
    ) { "Expression language and its parser" }

    feature(
        id = "mst-jvm-codegen",
        ref = "src/jvmMain/kotlin/space/kscience/kmath/asm/asm.kt"
    ) { "Dynamic MST to JVM bytecode compiler" }

    feature(
        id = "mst-js-codegen",
        ref = "src/jsMain/kotlin/space/kscience/kmath/estree/estree.kt"
    ) { "Dynamic MST to JS compiler" }

    feature(
        id = "rendering",
        ref = "src/commonMain/kotlin/space/kscience/kmath/ast/rendering/MathRenderer.kt"
    ) { "Extendable MST rendering" }
}
