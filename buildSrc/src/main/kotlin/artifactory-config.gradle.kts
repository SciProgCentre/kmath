import groovy.lang.GroovyObject
import org.jfrog.gradle.plugin.artifactory.dsl.PublisherConfig
import org.jfrog.gradle.plugin.artifactory.dsl.ResolverConfig

plugins {
    id("com.jfrog.artifactory")
}

artifactory {
    val artifactoryUser: String? by project
    val artifactoryPassword: String? by project
    val artifactoryContextUrl = "http://npm.mipt.ru:8081/artifactory"

    setContextUrl(artifactoryContextUrl)//The base Artifactory URL if not overridden by the publisher/resolver
    publish(delegateClosureOf<PublisherConfig> {
        repository(delegateClosureOf<GroovyObject> {
            setProperty("repoKey", "gradle-dev-local")
            setProperty("username", artifactoryUser)
            setProperty("password", artifactoryPassword)
        })

        defaults(delegateClosureOf<GroovyObject>{
            invokeMethod("publications", arrayOf("jvm", "js", "kotlinMultiplatform", "metadata"))
            //TODO: This property is not available for ArtifactoryTask
            //setProperty("publishBuildInfo", false)
            setProperty("publishArtifacts", true)
            setProperty("publishPom", true)
            setProperty("publishIvy", false)
        })
    })
    resolve(delegateClosureOf<ResolverConfig> {
        repository(delegateClosureOf<GroovyObject> {
            setProperty("repoKey", "gradle-dev")
            setProperty("username", artifactoryUser)
            setProperty("password", artifactoryPassword)
        })
    })
}
