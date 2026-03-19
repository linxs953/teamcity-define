package template

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.dockerSupport
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.dockerCommand

/**
 * Java 微服务项目模板
 * 适用于 Spring Boot / Maven 项目
 */
object JavaServiceTemplate : Template({
    id("JavaServiceTemplate")
    name = "Java 微服务模板"

    // 默认参数
    params {
        param("docker.registry", "your-registry.com")
        param("docker.image.name", "service-name")
        param("java.version", "17")
    }

    steps {
        // 1. 检出代码（自动）

        // 2. 代码质量检查
        step {
            name = "代码格式检查"
            type = "Maven2"
            mavenVersion = "Maven3"
            mavenGoals = "spotless:check"
        }

        // 3. 编译
        step(SharedSteps.mavenBuild("clean compile"))

        // 4. 单元测试
        step {
            name = "单元测试"
            type = "Maven2"
            mavenVersion = "Maven3"
            mavenGoals = "test"
        }

        // 5. 打包
        step(SharedSteps.mavenBuild("package -DskipTests"))

        // 6. Docker 镜像构建
        step {
            name = "构建 Docker 镜像"
            type = "DockerCommand"
            commandType = build {
                namesAndTags = "%docker.registry%/%docker.image.name%:%build.number%"
                namesAndTags += ",%docker.registry%/%docker.image.name%:latest"
                dockerfilePath = "Dockerfile"
            }
        }

        // 7. Docker 镜像推送
        step {
            name = "推送 Docker 镜像"
            type = "DockerCommand"
            commandType = push {
                namesAndTags = "%docker.registry%/%docker.image.name%:%build.number%"
                namesAndTags += ",%docker.registry%/%docker.image.name%:latest"
            }
        }
    }

    // 构建完成后生成报告
    features {
        dockerSupport {
            cleanupPushedImages = true
        }
    }

    // 构建失败条件
    failureConditions {
        errorMessage = true
        executionTimeoutMin = 30
    }

    // 要求
    requirements {
        contains("teamcity.agent.jvm.os.name", "Linux")
    }
})
