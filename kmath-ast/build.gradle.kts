import ru.mipt.npm.gradle.Maturity

plugins {
    id("ru.mipt.npm.mpp")
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
    commonMain {
        dependencies {
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
            implementation(npm("astring", "1.4.3"))
        }
    }

    jvmMain {
        dependencies {
            api("com.github.h0tk3y.betterParse:better-parse:0.4.0")
            implementation("org.ow2.asm:asm:9.0")
            implementation("org.ow2.asm:asm-commons:9.0")
        }
    }
}

readme {
    maturity = Maturity.PROTOTYPE
    propertyByTemplate("artifact", rootProject.file("docs/templates/ARTIFACT-TEMPLATE.md"))

    feature(
        id = "expression-language",
        description = "Expression language and its parser",
        ref = "src/jvmMain/kotlin/kscience/kmath/ast/parser.kt"
    )

    feature(
        id = "mst",
        description = "MST (Mathematical Syntax Tree) as expression language's syntax intermediate representation",
        ref = "src/commonMain/kotlin/kscience/kmath/ast/MST.kt"
    )

    feature(
        id = "mst-building",
        description = "MST building algebraic structure",
        ref = "src/commonMain/kotlin/kscience/kmath/ast/MstAlgebra.kt"
    )

    feature(
        id = "mst-interpreter",
        description = "MST interpreter",
        ref = "src/commonMain/kotlin/kscience/kmath/ast/MST.kt"
    )

    feature(
        id = "mst-jvm-codegen",
        description = "Dynamic MST to JVM bytecode compiler",
        ref = "src/jvmMain/kotlin/kscience/kmath/asm/asm.kt"
    )

    feature(
        id = "mst-js-codegen",
        description = "Dynamic MST to JS compiler",
        ref = "src/jsMain/kotlin/kscience/kmath/estree/estree.kt"
    )
}
