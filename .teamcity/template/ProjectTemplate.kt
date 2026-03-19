package template

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildFeatures.PullRequests
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.script

/**
 * 示例项目配置
 * 创建新项目时，复制此文件并修改相应参数
 */
object ProjectTemplate : Project({
    name = "MyProject"
    description = "项目描述"

    // VCS 根配置
    vcsRoot(MyVcsRoot)

    // 构建配置
    buildType(MyBuild)

    // 可以添加多个构建类型
    buildType(MyBuildDeploy)
})

object MyVcsRoot : GitVcsRoot({
    id("MyVcsRoot")
    name = "MyProject VCS"
    url = "https://github.com/your-org/your-repo.git"
    branch = "refs/heads/main"
    branchSpec = """
        +:refs/heads/*
        +:refs/pull/(*/merge)
    """.trimIndent()
    authMethod = password {
        userName = "your-username"
        password = "credentialsJSON:placeholder"
    }
})

object MyBuild : BuildType({
    name = "Build"

    // 继承模板
    templates(CommonBuildTemplate)

    // 项目特定参数
    params {
        param("project.version", "1.0.0")
    }

    // VCS 设置
    vcs {
        root(MyVcsRoot)
    }

    // 可以覆盖或添加额外的步骤
    steps {
        // 模板中的步骤会自动包含
        // 这里添加项目特定的步骤
        step {
            name = "项目特定步骤"
            script {
                content = """
                    echo "执行项目特定操作"
                    echo "项目版本: %project.version%"
                """.trimIndent()
            }
        }
    }

    // 构建特征
    features {
        // 代码审查集成
        pullRequests {
            provider = github {
                authType = token {
                    token = "credentialsJSON:github-token"
                }
            }
        }
    }
})

object MyBuildDeploy : BuildType({
    name = "Deploy"

    // 依赖于构建完成
    dependencies {
        dependency(MyBuild) {
            artifacts {
                artifactRules = "+:target/*.jar => target/"
            }
        }
    }

    steps {
        step {
            name = "部署"
            script {
                content = """
                    echo "部署到测试环境"
                """.trimIndent()
            }
        }
    }

    // 只在 main 分支触发
    triggers {
        vcs {
            branchFilter = "+:main"
        }
    }
})
