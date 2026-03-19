package _Self

import jetbrains.buildServer.configs.kotlin.v2019_2.*

object ProjectSettings : ProjectSettings({
    // UUID 需要唯一，每个项目可以生成新的
    uuid = "template-project-uuid"
    projectId = "ProjectTemplate"
    projectName = "项目模板"

    // 版本化设置配置
    versionSettings {
        format = kotlin {
            // Kotlin DSL 格式
        }
        baseSettings {
            // 从当前仓库同步
            vcsRoot = "_Root"
        }
    }
})
