val kmathVersion by extra("0.1.2-dev-4")

allprojects {
    repositories {
        jcenter()
        maven("https://kotlin.bintray.com/kotlinx")
    }
    
    group = "scientifik"
    version = kmathVersion
}

subprojects {
    // Actually, probably we should apply it to plugins explicitly
    // We also can merge them to single kmath-publish plugin
    if (name.startsWith("kmath")) {
        apply(plugin = "bintray-config")
        apply(plugin = "artifactory-config")
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
