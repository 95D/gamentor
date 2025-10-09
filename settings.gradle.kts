pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
    includeBuild("build-logic")
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "gamentor"
include(":app")
include(":library-modules:chat-domain-api")
include(":library-modules:chat-domain-impl")
include(":library-modules:automation-domain-api")
include(":library-modules:automation-domain-impl")
include(":library-modules:chat-data-source-local-api")
include(":library-modules:chat-data-source-remote-api")
include(":library-modules:chat-data-repository-impl")
include(":library-modules:id-domain-api")
include(":library-modules:id-infra-impl")
