# TeamCity Kotlin DSL 模板仓库

## 结构说明

```
.teamcity/
├── project.kt              # 主项目入口
├── settings.kt             # 项目设置
└── template/               # 模板目录
    ├── CommonBuildTemplate.kt    # 通用构建模板
    └── ProjectTemplate.kt        # 示例项目配置
```

## 使用方式

### 方式一：在 TeamCity 中直接关联

1. 在 TeamCity 中创建新项目
2. 进入 **Versioned Settings** 设置
3. 选择 **Sync from repository**
4. 配置仓库信息：
   - Repository URL: `https://github.com/your-org/teamcity-templates.git`
   - Branch: `main`
5. 保存后 TeamCity 会自动读取 Kotlin DSL 配置

### 方式二：创建子项目模板仓库

1. **Fork 此模板仓库** 到新的项目专用仓库

2. **修改 `.teamcity/template/ProjectTemplate.kt`**：
   - 修改项目名称、描述
   - 配置 VCS Root 指向实际代码仓库
   - 调整构建步骤

3. **在 TeamCity 中创建项目**并关联该仓库

### 方式三：共享模板，多项目复用

1. **保持此仓库作为模板中心**
   - 定义通用模板 (`CommonBuildTemplate`)
   - 定义共享构建步骤

2. **各项目仓库引入模板**：

```kotlin
// .teamcity/project.kt
package _Self

import jetbrains.buildServer.configs.kotlin.v2019_2.*
import template.CommonBuildTemplate  // 引入共享模板

project = Project("MyProject", "我的项目") {
    buildType(MyBuild)
}

object MyBuild : BuildType({
    name = "构建"

    // 继承共享模板
    templates(CommonBuildTemplate)

    // 项目特定配置
    params {
        param("project.name", "MyProject")
    }
})
```

## 模板继承

```kotlin
// 定义模板
object MyTemplate : Template({
    id("MyTemplate")
    name = "我的模板"

    steps {
        step { /* 通用步骤 */ }
    }
})

// 使用模板
object MyBuild : BuildType({
    name = "构建"

    templates(MyTemplate)  // 继承模板

    // 可以覆盖或扩展
    steps {
        step { /* 额外步骤 */ }
    }
})
```

## 最佳实践

1. **分层设计**：
   - 顶层：共享模板仓库（通用步骤、通用配置）
   - 项目层：项目专用配置仓库

2. **参数化**：
   ```kotlin
   params {
       param("env.JAVA_HOME", "%env.JDK_%java.version%")
   }
   ```

3. **模块化**：
   - 将常用的构建步骤定义为单独对象
   - 通过 `include` 机制复用

4. **版本管理**：
   - 模板变更通过 PR 审查
   - 使用分支管理不同环境的配置
