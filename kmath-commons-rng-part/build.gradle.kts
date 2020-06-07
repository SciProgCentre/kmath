plugins { id("scientifik.mpp") }
kotlin.sourceSets { commonMain.get().dependencies { api(project(":kmath-coroutines")) } }
