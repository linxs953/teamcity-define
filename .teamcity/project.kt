package _Self

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.Project

version = "2024.03"

project = Project("ProjectTemplate", "项目模板") {
    // 子项目会在这里自动注册
}
