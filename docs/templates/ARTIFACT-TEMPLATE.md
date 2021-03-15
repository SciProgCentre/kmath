> #### Artifact:
>
> This module artifact: `${group}:${name}:${version}`.
>
> Bintray release version:        [ ![Download](https://api.bintray.com/packages/mipt-npm/kscience/${name}/images/download.svg) ](https://bintray.com/mipt-npm/kscience/${name}/_latestVersion)
>
> Bintray development version:    [ ![Download](https://api.bintray.com/packages/mipt-npm/dev/${name}/images/download.svg) ](https://bintray.com/mipt-npm/dev/${name}/_latestVersion)
>
> **Gradle:**
>
> ```gradle
> repositories {
>     maven { url 'https://repo.kotlin.link' }
>     maven { url 'https://dl.bintray.com/hotkeytlt/maven' }
>     maven { url "https://dl.bintray.com/kotlin/kotlin-eap" } // include for builds based on kotlin-eap
> }
> 
> dependencies {
>     implementation '${group}:${name}:${version}'
> }
> ```
> **Gradle Kotlin DSL:**
>
> ```kotlin
> repositories {
>     maven("https://repo.kotlin.link")
>     maven("https://dl.bintray.com/kotlin/kotlin-eap") // include for builds based on kotlin-eap
>     maven("https://dl.bintray.com/hotkeytlt/maven") // required for a
> }
> 
> dependencies {
>     implementation("${group}:${name}:${version}")
> }
> ```