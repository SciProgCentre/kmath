val kmathVersion by extra("0.1.2-dev-1")

allprojects {
    repositories {
        jcenter()
        maven("https://kotlin.bintray.com/kotlinx")
    }
    group = "scientifik"
    version = kmathVersion
}

subprojects {
    if (name.startsWith("kmath")) {
        // apply bintray configuration
        apply(plugin = "bintray-config")

        //apply artifactory configuration
        apply(plugin = "artifactory-config")

        plugins.withId("org.jetbrains.kotlin.multiplatform") {
            apply(plugin = "multiplatform-config")
        }

    }
    //    dokka {
//        outputFormat = "html"
//        outputDirectory = javadoc.destinationDir
//    }
//
//    task dokkaJar (type: Jar, dependsOn: dokka) {
//           from javadoc . destinationDir
//            classifier = "javadoc"
//    }

}
