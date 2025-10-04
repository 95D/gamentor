dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
    versionCatalogs {
        create("libs") {
            from(files("${rootDir.parentFile}/gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "build-logic"
include(":convention")
