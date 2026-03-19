package template

import jetbrains.buildServer.configs.kotlin.v2019_2.*

/**
 * 通用构建模板
 * 可以被多个项目复用
 */
object CommonBuildTemplate : Template({
    id("CommonBuildTemplate")
    name = "通用构建模板"

    // 通用参数
    params {
        param("env.JAVA_HOME", "%env.JDK_17%")
        param("env.MAVEN_OPTS", "-Xmx1024m")
    }

    // 通用构建步骤
    steps {
        step {
            name = "检出代码"
            type = "vcsTrigger"
        }

        step {
            name = "Maven 构建"
            type = "Maven2"
            mavenVersion = "Maven3"
            mavenGoals = "clean package"
            runnerParameters {
                param("userSdkSelection", "useBundled")
            }
        }

        step {
            name = "运行测试"
            type = "Maven2"
            mavenVersion = "Maven3"
            mavenGoals = "test"
            runnerParameters {
                param("userSdkSelection", "useBundled")
            }
        }
    }

    // 通用触发器
    triggers {
        vcs {
            branchFilter = "+:*"
        }
    }

    // 通用构建失败条件
    failureConditions {
        executionTimeoutMin = 30
    }
})
