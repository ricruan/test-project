# 红创clojure代码模板使用说明

本模板前后端分别使用clojure和clojurescript开发。

### 主要技术栈
1. 后端:
 * [clojure编程风格指南](https://github.com/geekerzp/clojure-style-guide/blob/master/README-zhCN.md)
 * [后台基础框架luminus](http://www.luminusweb.net/docs)
 * [后台sql支持HugSQL](https://www.hugsql.org/)
 * [后台web框架ring](https://github.com/ring-clojure/ring)
 * [前后端路由框架reitit](https://github.com/metosin/reitit)
 * [clojure 函数定义及demo查询](https://clojuredocs.org/)
 * [spec guide](https://clojure.org/guides/spec)
 * [单元测试](https://cursive-ide.com/userguide/testing.html)

2. 前端
* [Reagent](https://github.com/reagent-project/reagent): Clojurescript的库，最要作用：hiccup -> react 组件
* [Kee-frame](https://github.com/ingesolvoll/kee-frame): Clojurescript的状态管理
* [Shadow-cljs](https://shadow-cljs.github.io/docs/UsersGuide.html): 包管理，集成工具，需要首先安装`npm install -g shadow-cljs`
* [Hiccup](https://github.com/weavejester/hiccup) clojure里书写html的库
* [Re-frame](https://github.com/Day8/re-frame) cljs状态管理，路由
* [AntD](https://ant.design/docs/react/introduce-cn): js库老朋友，不多介绍了


## 环境准备

* 安装好了jdk1.8及以上
* 安装好了Leiningen,安装请参考官网[leiningen](https://leiningen.org/#install)或者公司博客[Mac 下 Clojure 环境搭建](http://blog.3vyd.com/blog/posts-output/2018-10-31-Clojure-%E7%8E%AF%E5%A2%83%E6%90%AD%E5%BB%BA/) 和 [Windows 下 Clojure 环境搭建](http://blog.3vyd.com/blog/posts-output/2018-11-05-windows%E4%B8%8Aclojure%E7%8E%AF%E5%A2%83%E6%90%AD%E5%BB%BA/)
* 如果运行后端项目，需要安装mysql 5.7及以上

## 开发工具

* 首推IDE是emacs，可以直接使用大神的[配置文件](https://github.com/purcell/emacs.d),emacs的常用快捷键参考[Clojure 代码编辑](http://blog.3vyd.com/blog/posts-output/2019-08-03-clojure-with-emacs/)
* 其次推荐Intellij idea，需要安装[cursive](https://plugins.jetbrains.com/plugin/8090-cursive)插件,个人版免费。
* vs code，安装clojure插件

## 运行说明

本模板包括前端clojurescript和后台clojure，因此分两个端运行项目

#####  1. 前端
命令行启动：new出来模板后在项目下分别执行一下命令，
```
yarn
```
```
yarn start
```
emacs启动：
`M-x`选择cider-jack-in-cljs，然后选择shadow-cljs，然后选择shadow，如果需要选择build的话，请选择【app】，然后可以选择自动打开浏览器。

`yarn start`监听了默认的app，shadows提供可视化页面，浏览器打开如下链接，在build里选择【app】，并start watch。
   * clojure编译窗口: http://localhost:9630

项目默认的端口是8000，可以在根目录的`shadow-cljs.edn`里修改dev-http下修改。
   * 实时开发的预览: http://localhost:8000/login

目前管理后台调用的是裁圣测试环境的接口，账号密码是：admin/admin.123

#####  2. 后端
服务端需要在emacs里使用cider-jack-in-clj或者在intellij idea里用local repl运行。
修改根目录的`dev-config.edn`文件
```
:database-url "mysql://localhost:3306/db_name?user=db_user_here&password=db_user_password_here&useSSL=false&characterEncoding=utf-8&serverTimezone=Hongkong"
```

**服务端的其他介绍，详情请移步`resource/clj-readme.md`学习。**

## 推荐学习文章
* [公司博客上的全都推荐😁](http://blog.3vyd.com/blog/archives/)

## 比较luminus模板有下面的修改
#### 后台
1. 修改默认的jdbc驱动，改为log4jdbc,获取jdbc日志, jdbc链接utf8支持，并指定时区
2. 修改logback配置,sql、info、error分文件输入，设置不同环境的保存时间
3. 全局跨域配置
4. 文件上传，保存本地和上传七牛
5. redis
6. 引入常用的jar包
   * spec
   * log4jdbc
   * clj-http
   * com.taoensso/carmine
   * data.json
   * clj.qiniu
7. mysql的db字段下划线转clojure的中线插件
8. 测试环境利用`env/test`做配置文件进行打包
9. 常用的like、in查询，批量插入示例

#### 管理后台页面
1. 除了react，还引入了antd、moment、uuid。
2. 实现页面：
   * 登录页面：http://localhost:8000/login
   * 商品列表
   * 系统用户列表
   * UI组件：[AntD](https://ant.design/docs/react/introduce-cn)

## 需要持续更新
* 后端业务处理示例：
1. data-json互转的例子
2. jwt的token拦截例子

* 前端页面持续更新及对应接口做成模板

## License

Copyright © 2019 By Redcreation.QD
