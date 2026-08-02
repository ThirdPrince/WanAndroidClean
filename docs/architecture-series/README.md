# WanAndroidClean Clean Architecture 架构系列

这组文档以 **Clean Architecture** 为核心主线来分析 WanAndroidClean。Compose、Koin、Retrofit、Room、Paging 这些技术不会被单独当作卖点展开，而是放回 Clean 架构语境中解释：它们分别处在什么层、承担什么职责、为什么不能越界。

系列的目标不是简单介绍“项目用了哪些库”，而是回答这些问题：

- 为什么 `domain` 是项目的架构中心？
- 为什么 `feature` 不应该直接依赖 `data`？
- Repository 接口为什么放在 `domain`，实现为什么放在 `data`？
- UseCase 在这个项目里到底解决什么问题？
- Compose、ViewModel、Room、Retrofit 如何围绕 Clean 边界协作？
- 新增功能时，怎样不破坏现有依赖规则？

## 系列目录

1. [Clean 总览：以 domain 为中心组织 Android 应用](./01-overview.md)
2. [依赖规则：模块边界、依赖反转与 Koin 组装](./02-module-boundaries.md)
3. [领域层：Entity、Repository 接口与 UseCase](./03-data-flow.md)
4. [表现层：Compose + ViewModel 如何遵守 Clean 边界](./04-ui-state.md)
5. [数据层：Room + Paging + Retrofit 如何实现 Repository](./05-offline-first-paging.md)
6. [基础设施：登录、Cookie、收藏为什么属于外层细节](./06-auth-session.md)
7. [架构演进：按 Clean 规则新增一个业务模块](./07-extension-guide.md)

## 项目关键词

- Clean Architecture
- Multi-module
- Jetpack Compose
- MVVM
- Koin
- Retrofit + OkHttp
- Room
- Paging 3 RemoteMediator
- DataStore Cookie 持久化

## 推荐阅读方式

如果要完整理解这个项目的 Clean 架构，建议按顺序读。第 1、2 篇建立架构地图；第 3 篇解释内层业务规则；第 4、5、6 篇分别解释 UI、数据和基础设施这些外层细节如何围绕内层工作；第 7 篇把规则落到新增功能的操作步骤。

## 每篇文章的阅读重点

第 1 篇先建立整体心智模型：这个项目不是“Compose + Retrofit + Room”的简单堆叠，而是以 `domain` 为中心，把 UI、数据、基础设施都放到外层。

第 2 篇重点看依赖规则。只要理解 `feature/* -> domain <- data`，就理解了这个项目最重要的架构约束。

第 3 篇重点看领域层。Entity、Repository 接口和 UseCase 是 Clean 架构的稳定核心，后续所有外层代码都围绕它们展开。

第 4 篇重点看表现层边界。Compose 和 ViewModel 可以处理页面状态和用户事件，但不应该直接接触 Retrofit、Room、Cookie 等数据细节。

第 5 篇重点看数据层如何实现抽象。首页的 Room + Paging + Retrofit 链路是一个很好的 RepositoryImpl 样本。

第 6 篇重点看基础设施归位。登录态、Cookie、收藏接口都是外层细节，应该通过 Repository 和 UseCase 收束，而不是散落在页面里。

第 7 篇重点看实践顺序。新增功能时先定领域能力，再做数据实现，最后接入 UI 和 app 导航。

## 适合继续扩展的方向

这组文档后续还可以继续扩成更完整的系列，例如：

- Clean 架构下的测试策略：UseCase、ViewModel、Repository 分别怎么测。
- Clean 架构下的错误模型：如何区分网络错误、业务错误、未登录错误。
- Clean 架构下的导航设计：中心化 NavHost 和 feature graph 如何取舍。
- Clean 架构下的缓存一致性：收藏状态、分页数据和用户态如何同步。
- Clean 架构下的模块治理：如何避免 feature 之间互相依赖。
