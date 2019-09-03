# Workde 框架

## 介绍

　　Workde Core是一个基于SpringBoot封装的基础库，内置丰富的JDK工具，并且自动装配了一系列的基础Bean

## 工程结构

```
workde-core
	├── workde-core  基础库
	│   ├── workde-core-dependencies  父pom
	│   ├── workde-core-base          基础库提供了丰富的Java工具包
	│   ├── workde-starter-boot       基于SpringBoot进行二次封装，同时也自动装配了一系列基础Bean等
	│   ├── workde-starter-redis      基于SpringRedis进行二次封装，更简单灵活
	│   ├── workde-starter-token      提供token创建与解析
	│   ├── workde-starter-secure     提供权限相关的注解，拦截器等
	│   ├── workde-starter-mybatis-tk 基于Mybatis,Tkmybatis进行二次封装，提供了默认的Service，Mapper
	│   ├── workde-starter-admin      通过配置实体类，提供普通的CRUD接口
	│   └── workde-starter-swagger    基于Swagger，knife4j进行二次封装，提供接口Api文档
	├── workde-core-samples  基础库示例   
	└── └── workde-core-boot
```
## 快速使用
maven项目，在pom.xml文件中添加如下一段代码，并将`${version}`替换为对应版本号：[![maven-central](https://img.shields.io/maven-central/v/cn.workde/workde-core-dependencies.svg?label=Maven%20Central)](https://maven-badges.herokuapp.com/maven-central/ai.ylyue/yue-library-dependencies)
```xml
<parent>
	<groupId>cn.workde</groupId>
	<artifactId>workde-core-dependencies</artifactId>
	<version>${version}</version>
</parent>
```
随后引入所需要的模块
```xml
<dependencies>
	<dependency>
		<groupId>cn.workde</groupId>
		<artifactId>workde-starter-boot</artifactId>
	</dependency>
	...
</dependencies>
```

## 模块说明
### workde-core-base
`workde-core-base`提供了丰富的Java工具包，它能够帮助我们简化每一行代码（集成[Hutool](https://hutool.cn)工具包）
- `Result`Http最外层响应对象，更适应Restful风格API
- 基于`validator`扩展IPO增强校验注解，更适合国内校验场景。（如：手机号、身份证号码）

### workde-starter-boot
`workde-starter-boot`基于SpringBoot进行二次封装，同时也自动装配了一系列基础Bean
- 通过`WorkdeApplication.run`启动项目
- 全局统一异常处理，结合`Result`对象，定位异常更轻松，前端显示更贴切
- 控制Json返回字段的 `JsonReturnHandler`

### workde-starter-redis
基于SpringRedis进行二次封装，更简单灵活，提供全局token与登录等特性
- 简化使用并拥有Redis原生常用命令所对应的方法

### workde-starter-token
提供token创建与解析
- 通过`JwtUtil`获取和解析token串
- 集成`WorkdeController`获取Token的用户信息

### workde-starter-secure
提供权限相关的注解，拦截器等
- 默认只对swagger相关的接口放行，可通过配置 `workde.secure.skip` 来设置放行地址
- 通过 `@PreAuth` 来实现功能判断（待实现）

### workde-starter-mybatis-tk
提供权限相关的注解，拦截器等
- 基于Mybatis,Tkmybatis进行二次封装，提供了默认的Service，Mapper
- 默认Service提供 `list`, `page`, `byId`, `one`, `save`, `udpate`, `delete` 方法

### workde-starter-admin
通过配置实体类，提供普通的CRUD接口
- 启动类增加注解 `@EnableWorkdeAdmin`, `workde.admin.contextPath` 配置后台地址，默认为 admin
- `Controller` 继承 `ModuleController`，提供 `list`(列表),`newDefault`(默认值),`create`(保存),`edit`(修改),`update`(更新),`delete`(删除)接口
- 增加注解`@AdminController`, 设置`ModuleDefine` 和 `path` (必须)
- `ModuleDefine` 定义Module的实体类,Service,Logic等
- Logic 默认提供 `beforeInsert`(插入前),`afterInsert`(插入后),`beforeUpdate`(保存前),`afterUpdate`(保存后),`beforeDelete`(删除前),`afterDelete`(删除后),`getNewDefultValue`(默认值)
