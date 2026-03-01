# WanAndroidClean

WanAndroidClean 是一款基于 **Clean Architecture** 架构、**Jetpack Compose** 声明式 UI 以及 **多模块化 (Multi-module)** 思路开发的现代 Android 应用。数据来源自 [玩Android](https://www.wanandroid.com/) 开放 API。

## 🚀 项目亮点

- **教科书级的 Clean Architecture**: 严格划分 `Domain`、`Data` 和 `Feature` 层，实现高内聚低耦合。
- **现代化技术栈**: 采用 Kotlin 2.0.21, Jetpack Compose, Koin, Retrofit, DataStore 等主流技术。
- **按功能模块化**: 每个核心功能（首页、体系、公众号、项目、我的、详情）均为独立模块，支持按需插拔。
- **极致的交互体验**:
    - 全局平滑的左右滑动页面转场动画。
    - 自定义悬浮圆角底部导航栏，带弹性 Q 弹缩放动画。
    - 适配官方 SplashScreen API，解决冷启动白屏问题。
- **健壮的网络层**: 封装 `safeApiCall`，精准区分网络异常、HTTP 错误与业务错误，并实现基于 DataStore 的异步 Cookie 持久化。

## 🛠 技术栈

| 类别 | 采用方案 |
| :--- | :--- |
| **语言** | Kotlin 2.0.21 (Strongly Typed) |
| **UI 框架** | Jetpack Compose (Material 3) |
| **架构** | Clean Architecture + MVVM + Multi-module |
| **依赖注入** | Koin 3.5.3 |
| **网络请求** | Retrofit 2.11.0 + OkHttp 4.12.0 |
| **数据持久化** | Preferences DataStore (用于 Cookie 和配置存储) |
| **图片加载** | Coil 2.7.0 |
| **异步处理** | Kotlin Coroutines & Flow |
| **导航** | Navigation Compose |

## 📦 模块结构

```text
├── app                 # 壳工程：负责初始化 (Koin, App主题) 与全局导航配置
├── domain              # 领域层：纯 Kotlin 模块，包含 Entity, Repository 接口与 UseCase
├── data                # 数据层：Repository 实现、API 定义、数据模型 (DTO) 及持久化逻辑
└── feature             # 功能模块层
    ├── home            # 首页模块：文章列表、Banner 轮播、自动滚动逻辑
    ├── system          # 体系模块：二级分类树、常用导航 Tab
    ├── wxarticle       # 公众号模块：支持多公众号子 Tab 切换
    ├── project         # 项目模块：完整开源项目分类展示
    ├── mine            # 个人中心：用户信息展示、登录逻辑实现
    └── web             # 详情页模块：基于 WebView 的沉浸式阅读体验
```

## 📸 功能展示

- **首页**: 动态 Banner 与置顶文章置顶展示。
- **体系/导航**: 灵活的流式布局展示知识体系。
- **沉浸式阅读**: 点击任意文章自动隐藏导航栏，进入全屏 WebView 浏览。
- **自动登录**: 登录成功后 Cookie 自动持久化，支持一个月有效期。

## 📝 开发规范

1. **响应式 UI**: 所有的 UI 状态均通过 `UiState` 数据类在 ViewModel 中统一管理。
2. **单向数据流**: UI -> Intent -> ViewModel -> State -> UI。
3. **安全请求**: 所有 API 调用必须经过 `safeApiCall` 包装，返回 `Result<T>` 类型。
4. **组件复用**: 提取通用的 `ArticlesList` 等组件以减少代码冗余。

---
*本项目持续更新中，旨在打造 Android 纯 Compose 架构的最佳实践。*
