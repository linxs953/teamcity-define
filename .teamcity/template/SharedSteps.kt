package template

import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.*

/**
 * 共享构建步骤库
 * 可以在不同构建中复用
 */
object SharedSteps {

    /**
     * Maven 构建步骤
     */
    fun mavenBuild(goals: String = "clean package") = Maven2 {
        name = "Maven: $goals"
        mavenVersion = "Maven3"
        mavenGoals = goals
        runnerParameters {
            param("userSdkSelection", "useBundled")
        }
    }

    /**
     * Docker 构建步骤
     */
    fun dockerBuild(imageName: String, dockerfilePath: String = "Dockerfile") = DockerCommand {
        name = "Docker Build: $imageName"
        commandType = build {
            namesAndTags = "$imageName:%build.number%"
            dockerfilePath = dockerfilePath
        }
    }

    /**
     * 通用脚本步骤
     */
    fun scriptStep(name: String, content: String) = Script {
        this.name = name
        scriptContent = content
    }

    /**
     * 通知步骤
     */
    fun notifySlack(channel: String) = Script {
        name = "Slack 通知"
        scriptContent = """
            curl -X POST \
              -H 'Content-type: application/json' \
              --data '{"text":"构建 %build.status%: %teamcity.build.branch%" }' \
              %slack.webhook.url%
        """.trimIndent()
    }
}
