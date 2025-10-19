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
include(":library-modules:app-di")
include(":library-modules:multi-lang-resources")
include(":library-modules:design-system")
include(":library-modules:chat-domain-api")
include(":library-modules:automation-domain-api")
include(":library-modules:automation-business-logic-impl")
include(":library-modules:chat-data-source-local-api")
include(":library-modules:chat-data-source-local-impl")
include(":library-modules:chat-data-source-remote-api")
include(":library-modules:chat-data-source-remote-impl")
include(":library-modules:chat-data-repository-impl")
include(":library-modules:id-domain-api")
include(":library-modules:id-infra-impl")
include(":library-modules:chat-ui-entry-api")
include(":library-modules:chat-ui-impl")
include(":library-modules:setting-domain-api")
include(":library-modules:setting-data-repository-impl")
include(":library-modules:setting-ui-entry-api")
include(":library-modules:setting-ui-impl")
include(":library-modules:setting-android-impl")
include(":library-modules:setting-data-source-local-api")
include(":library-modules:setting-data-source-local-impl")
include(":library-modules:ui-core-compose")
