import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.XmlReport
import jetbrains.buildServer.configs.kotlin.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.buildFeatures.xmlReport
import jetbrains.buildServer.configs.kotlin.buildSteps.DotnetMsBuildStep
import jetbrains.buildServer.configs.kotlin.buildSteps.dotCover
import jetbrains.buildServer.configs.kotlin.buildSteps.dotnetMsBuild
import jetbrains.buildServer.configs.kotlin.buildSteps.powerShell
import jetbrains.buildServer.configs.kotlin.buildSteps.script
import jetbrains.buildServer.configs.kotlin.triggers.finishBuildTrigger
import jetbrains.buildServer.configs.kotlin.triggers.vcs
import jetbrains.buildServer.configs.kotlin.projectFeatures.VersionedSettings
import jetbrains.buildServer.configs.kotlin.projectFeatures.versionedSettings

version = "2024.03"

project {
    name = "TcDemo Project"

    buildType(DemoPipeline)
    buildType(TcDemo_Build2)

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

    // VCS Root definition - 需要根据实际情况配置
    subProject(TcDemoVcs)
}

object TcDemoVcs : Project({
    name = "VCS Roots"

    vcsRoot(TcDemo)
})

object TcDemo : VcsRoot({
    name = "TcDemo"
    id = VcsRootId("TcDemo")
    url = "https://github.com/your-org/tc2.git"
    branch = "refs/heads/main"
    defaultBranch = "refs/heads/main"
    authMethod = defaultMethod {
        userName = "your-username"
    }
})

object DemoPipeline : BuildType({
    name = "单元测试"
    id = absoluteId("DemoPipeline")

    artifactRules = """
        **\bin\Release\** => demodist\ReleaseLib\
        **\obj\Release\** => demodist\ReleaseObj\
        Report_*.html => demodist\unittest\
        TestResult_*.xml => demodist\unittest\
    """.trimIndent()
    buildNumberPattern = "1.0.%build.counter%+CL%teamcity.build.vcs.number%"
    publishArtifacts = PublishMode.SUCCESSFUL

    params {
        param("DotNetCLI_Path", """C:\Program Files\dotnet\dotnet.exe""")
    }

    vcs {
        root(TcDemo)
        cleanCheckout = true
        showDependenciesChanges = true
    }

    steps {
        script {
            name = "单元测试"
            id = "ce_shi"
            scriptContent = """D:\\NUnit.Console-3.22.0\\bin\\net462\\nunit3-console.exe tc2\\tc2-test\\bin\\Debug\\tc2_test.dll --result:TestResult_CL-%teamcity.build.vcs.number%.xml"""
        }
        powerShell {
            name = "生成单元测试报告"
            id = "bao_gao"
            scriptMode = file {
                path = """C:\TeamCity\scripts\Convert-NUnitXmlToHtml.ps1"""
            }
            scriptArgs = "--XmlPath TestResult_CL-%teamcity.build.vcs.number%.xml --HtmlPath Report_CL-%teamcity.build.vcs.number%.html"
        }
        dotnetMsBuild {
            name = "构建项目"
            id = "dotnet"
            projects = "tc2/tc2.sln"
            version = DotnetMsBuildStep.MSBuildVersion.V4
            args = "-t:Clean;Build;Publish -p:Configuration=Release -v:detailed -property:ConsoleEncoding=utf-8"
            sdk = "4.8"
        }
        dotCover {
            name = "统计覆盖率"
            id = "fu_gai_lv"
            enabled = false
            executable = """C:\\Program Files\\dotnet\\dotnet.exe"""
            commandLineArguments = "test --framework net48"
        }
    }

    triggers {
        vcs {
            branchFilter = "+:*"
        }
        finishBuildTrigger {
            buildType = TcDemo_Build2.id
        }
    }

    failureConditions {
        executionTimeoutMin = 1
    }

    features {
        perfmon {
        }
        xmlReport {
            reportType = XmlReport.XmlReportType.NUNIT
            rules = "TestResult_*.xml"
            verbose = true
        }
    }
})

object TcDemo_Build2 : BuildType({
    name = "Build2"
    id = absoluteId("TcDemo_Build2")

    vcs {
        root(TcDemo)
        cleanCheckout = true
    }

    steps {
        script {
            name = "示例步骤"
            scriptContent = "echo 'Build2 step'"
        }
    }

    triggers {
        vcs {
            branchFilter = "+:*"
        }
    }
})
