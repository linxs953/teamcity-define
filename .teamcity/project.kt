import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.projectFeatures.VersionedSettings
import jetbrains.buildServer.configs.kotlin.v2019_2.projectFeatures.versionedSettings

version = "2024.03"

project {
    name = "TeamCity Define Project"

    buildType(UnitTests)
    buildType(BuildAndPackage)

    features {
        versionedSettings {
            id = "PROJECT_EXT_1"
            mode = VersionedSettings.Mode.ENABLED
            buildSettingsMode = VersionedSettings.BuildSettingsMode.USE_CURRENT_SETTINGS
            rootExtId = "HttpsGithubcomTeamcityDefineGitRefsHeadsMain"
            showChanges = false
            settingsFormat = VersionedSettings.Format.KOTLIN
        }
    }

    params {
        param("system.Gradle_Version", "8.5")
    }
}

object UnitTests : BuildType({
    name = "Unit Tests"
    id = absoluteId("UnitTests")

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        gradle("gradle clean test") {
            name = "Run Unit Tests"
            tasks = "clean test"
            gradleWrapperPath = ""
        }
    }

    triggers {
        vcs {
            branchFilter = "+:*"
        }
    }

    requirements {
        contains("docker.server.osType", "Linux")
    }
})

object BuildAndPackage : BuildType({
    name = "Build and Package"
    id = absoluteId("BuildAndPackage")

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        gradle("gradle clean build") {
            name = "Build Project"
            tasks = "clean build"
            gradleWrapperPath = ""
        }
    }

    triggers {
        vcs {
            branchFilter = "+:*"
        }
    }

    dependencies {
        snapshot(UnitTests) {
            onDependencyFailure = FailureAction.FAIL_TO_START
        }
    }

    requirements {
        contains("docker.server.osType", "Linux")
    }

    artifactRules = "build/libs/*.jar"
})
