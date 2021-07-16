
### 相关文档
 * [后台基础框架luminus](http://www.luminusweb.net/docs)
 * [后台sql支持HugSQL](https://www.hugsql.org/)
 * [后台web框架](https://github.com/ring-clojure/ring)
 * [前后端路由框架](https://github.com/metosin/reitit)
 * [前端状态管理框架](https://github.com/Day8/re-frame)
 * [前端PC版UI框架-antizer](https://github.com/priornix/antizer)
 * [clojure 函数定义及demo查询](https://clojuredocs.org/)
 * [clojure编程风格指南](https://github.com/geekerzp/clojure-style-guide/blob/master/README-zhCN.md)
 * [spec guide](https://clojure.org/guides/spec)
 * [单元测试](https://cursive-ide.com/userguide/testing.html)

### 项目运行
在命令行工具中启动用lein启动一个repl，lein没有安装的需要自行百度。
```
➜  
➜  ~ lein repl
nREPL server started on port 50529 on host 127.0.0.1 - nrepl://127.0.0.1:50529
REPL-y 0.4.3, nREPL 0.6.0
Clojure 1.10.0
Java HotSpot(TM) 64-Bit Server VM 1.8.0_192-b12
    Docs: (doc function-name-here)
          (find-doc "part-of-name-here")
  Source: (source function-name-here)
 Javadoc: (javadoc java-object-or-class-here)
    Exit: Control+D or (exit) or (quit)
 Results: Stored in vars *1, *2, *3, an exception in *e

user=>
```
然后在Intellij Idea中远程连接

![ideaconfig](../public/img/ide.png)

run这个配置，然后在下面的repl环境中执行`(start)`即启动server。


### 常见问题及解决方案
#### 1、处理request
实际项目开发中经常需要打印request内容，这部分在springMVC中一般用aop来解决。
clojure中没有对象，更别提aop了，但是没有框架的束缚，处理起request和response反而更加灵活，是用clojure的[middleware](http://clojure-doc.org/articles/cookbooks/middleware.html)
处理的，比如一个打印出入参的middleware如下：
```clojure
(require '[clojure.tools.logging :as log])

(defn log-wrap [handler]
  (fn [request]
    (let [request-id (java.util.UUID/randomUUID)]
      (log/info (str "\n================================ REQUEST START ================================"
                     "\n request-id:" request-id
                     "\n request-uri: " (:uri request)
                     "\n request-method: " (:request-method request)
                     "\n request-query: " (:query (:parameters request))
                     "\n request-body: " (:body (:parameters request))))
      (let [res (handler request)]
        (log/info (str "response: " (:body res)
                       "\n request-id:" request-id))
        (log/info (str "\n================================ response END ================================"))
        res))))
```
#### 2、在handler中使用request里自定义的对象

[猛戳这儿](https://blog.3vyd.com/blog/posts-output/2019-08-05-clojure-luminus-middleware&handler/)

#### 3、hendler获取body，path，query的参数

跟后端同学熟悉的spring一下，一般场景的三种形式的参数请参考[clojure luminus开发之handler里的参数获取](https://blog.3vyd.com/blog/posts-output/2019-08-05-clojure-luminus-params/)

#### 4、分页，动态hugsql
 
关于hugsql的使用，参考[clojure luminus开发之HugSQL](https://blog.3vyd.com/blog/posts-output/2019-08-10-clojure-luminus-HugSQL/)

#### 5、mysql中的字段表名和字段下划线在clojure中用中线连接的统一处理
[druids](https://github.com/druids/hugsql-adapter-case)提供了几个adapter，用来处理转换关系，比如有驼峰，中线等，我们使用连接符转换，即创建connection时加入kebab-case：

```clojure
(defstate ^:dynamic *db*
          :start (do (Class/forName "net.sf.log4jdbc.DriverSpy")
                     (if-let [jdbc-url (env :database-url)]
                       (conman/connect! {:jdbc-url jdbc-url})
                       (do
                         (log/warn "database connection URL was not found, please set :database-url in your config, e.g: dev-config.edn")
                         *db*)))
          :stop (conman/disconnect! *db*))
(conman/bind-connection *db* {:adapter (kebab-adapter)} "sql/queries.sql")
```

这个adapter在init项目时已经引入了，就看使用不使用。
#### 5、获取环境变量内容
环境变量比较好获取，比如微信的配置和获取

```clojure
{:weixin           {:app-id "wx9258d165932dad73"
                    :secret "my-secret"}
```
在dev/test/prod中配置结构相同，

```clojure
(require '[project.config :refer [env]])
(defn get-weixin-access-token [code]
  (let [url (format "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code"
                    (-> env
                        :weixin
                        :app-id)
                    (-> env
                        :weixin
                        :secret)
                    code)]
    (log/info "请求微信access-token, url: %s" url) url))
```
如果配置是一层，使用也只需写一层key。
**特别说明**：
在将redis的connetion从clj修改成从环境变量中获取时，也是一样的配置和获取，但是碰到了问题，在request里查看env中的redis的各项都有值，但是调用redis的地方却提示无法创建connection，

```clojure
(ns project.db.redis
  (:require [taoensso.carmine :as car :refer (wcar)]
            [project.config :refer [env]]
            [mount.core :refer [defstate]]))

(def server1-conn
          :start
          {:pool {}
           :spec {:host       (-> env :redis-host)
                  :port       (-> env :redis-port)
                  :password   (-> env :redis-password)
                  :timeout-ms (-> env :redis-timeout-ms)
                  :db         (-> env :redis-db)}})

(defmacro wcar* [& body] `(car/wcar server1-conn ~@body))
```
最后得知是因为env被定义了个state，

```clojure
(ns project.config
  (:require [cprop.core :refer [load-config]]
            [cprop.source :as source]
            [mount.core :refer [args defstate]]))

(defstate env
  :start
  (load-config
    :merge
    [(args)
     (source/from-system-props)
     (source/from-env)]))
```
但是按照[说明文档](https://github.com/ptaoussanis/carmine)redis的conn是个常规的def定义的函数，但是它下面的使用是个宏`defmacro `，宏是在编译的执行的，因此在初始化时evn环没有ready，所以无法创建出connection。需要将server1-conn改成一个state，state有依赖状态，会等到env完成后才产生。

```
(defstate server1-conn 
 ...
)
```
#### 6、jar引入及依赖冲突解决：
* lein deps :tree  查看包依赖。
* 引入新的jar时在`project.clj`的`:dependencies`按说明引入，跟maven一样，分groupId、artifactId、version。
* 排除某sdk里的某些冲突包

```
[com.baidu.aip/java-sdk "4.11.0"
 :exclusions [org.slf4j/slf4j-simple]]
```
#### 7、spec使用

spec是一个进行参数校验的库，校验不通过会返回400，一般的使用场景参考[clojure luminus开发之常用spec](https://blog.3vyd.com/blog/posts-output/2019-08-11-clojure-luminus-spec/)

#### 8、新增接口加入route
创建一个新的namespace，参考[官网说明](http://www.luminusweb.net/docs)定义出一个routes函数，然后将其加入到handle.clj中即可.

#### 9、文件上传接口
接口定义

```clojure
(defn format-date-time [timestamp]
  (-> "yyyyMMddHHmmss"
      (java.text.SimpleDateFormat.)
      (.format timestamp)))

;;上传到本地
(defn upload-file-local [type file]
  (let [file-path (str (-> env :file-path) type
                       "/" (format-date-time (java.util.Date.))
                       "/" (:filename file))]
    (io/make-parents file-path)
    (with-open [writer (io/output-stream file-path)]
      (io/copy (:tempfile file) writer))
    (get-image-data file-path)
    file-path))

(defn common-routes []
  ["/common"
   {:swagger    {:tags ["文件接口"]}
    :parameters {:header (s/keys :req-un [::token ::role])}
    :middleware [token-wrap]}

   ["/files"
    {:post {:summary    "附件上传接口"
            :parameters {:multipart {:file multipart/temp-file-part
                                     :type (st/spec
                                             {:spec        string?
                                              :description "类型"
                                              :reason      "类型必填"})}}

            :responses  {200 {:body {:code int?, :data {:file-url string?}}}}
            :handler    (fn [{{{:keys [type file]} :multipart} :parameters}]
                          {:status 200
                           :body   {:code    1
                                    :message "上传成功"
                                    :data    {:file-url (:url (upload-file-local type file))}}})}}]])

```
如果要将图片上传至七牛等有CDN能力的云存储空间，可以使用别人的轮子，或者自己需要造轮子，我这里使用了一个[别人造的上传七牛的轮子](https://github.com/killme2008/clj.qiniu),先在:dependencies里加入依赖
```
[clj.qiniu "0.2.1"]
```
调用api

```clojure
(require '[clj.qiniu :as qiniu])
;;上传到七牛配置
(defn set-qiniu-config []
  (qiniu/set-config! :access-key "my-key"
                     :secret-key "my-secret"))

(def qiniu-config
  {:bucket "medical"
   :domain "http://prfmkg8tt.bkt.clouddn.com/"
   :prefix "alk/weixin/"})

(defn qiniu-upload-path [type filename]
  (str (-> qiniu-config :prefix)
       type "/"
       (utils/format-date-time (java.util.Date.))
       "/"
       filename))

;;七牛云上传，返回上传后地址
(defn upload-file-qiniu [type file]
  (set-qiniu-config)
  (let [filename (:filename file)
        bucket (-> qiniu-config :bucket)
        key (qiniu-upload-path type filename)
        res (qiniu/upload-bucket bucket
                                 key
                                 (:tempfile file))]
    (log/info "上传七牛云结果：" res)
    (if-not (= 200 (-> res :status))
      (throw (Exception. " 附件上传失败 ")))
    (str (-> qiniu-config :domain) key)))

```
使用的时候将上传local改成upload-file-qiniu即可。

#### 10、跨域配置

luminus里配置origin还是很简单的，使用说明参考[clojure ring 跨域访问](https://blog.3vyd.com/blog/posts-output/2019-08-04-ring-cors/)

#### 11、增加打包环境
比如增加pre环境，在project.clj中配置uberjar即可，在:profiles里增加,可以参考test环境，比如增加的uberjar-test环境：

```
   :uberjar-test  {:omit-source    true
                   :aot            :all
                   :uberjar-name   "project-test.jar"
                   :source-paths   ["env/test/clj"]
                   :resource-paths ["env/test/resources"]
                   :jvm-opts       ["-Dconf=test-config.edn"]}
```
打包:
```
➜  project git:(master) ✗ lein with-profiles uberjar-test uberjar
Compiling project.common.utils
Compiling project.config
Compiling project.core
Compiling project.db.core
Compiling project.db.db-dicts
Compiling project.db.db-doctor
Compiling project.db.db-guestbook
Compiling project.db.db-hospital
Compiling project.db.db-patient
Compiling project.db.redis
Compiling project.env
Compiling project.handler
Compiling project.middleware
Compiling project.middleware.exception
Compiling project.middleware.formats
Compiling project.middleware.interceptor
Compiling project.middleware.log-interceptor
Compiling project.middleware.token-interceptor
Compiling project.nrepl
Compiling project.routes.base
Compiling project.routes.dicts
Compiling project.routes.doctor
Compiling project.routes.file
Compiling project.routes.guestbook
Compiling project.routes.hospital
Compiling project.routes.patient
Compiling project.routes.patient-cost
Compiling project.routes.patient-examine
Compiling project.routes.public
Compiling project.routes.user
Compiling project.routes.weixin
Compiling project.validation
Warning: skipped duplicate file: config.edn
Warning: skipped duplicate file: logback.xml
Created /Users/mahaiqiang/git/redcreation/project/target/uberjar+uberjar-test/project-0.1.0-SNAPSHOT.jar
Created /Users/mahaiqiang/git/redcreation/project/target/uberjar/project-test.jar
➜  project git:(master) ✗
```

#### 12、事务
发起事务使用`conman.core/with-transaction`，一个例子：

```clojure
(let [timestamp (java.util.Date.)
      id (utils/uuid)]
  (conman.core/with-transaction 
    [*db*]
    (db/create-guestbook! (assoc body-params
                            :timestamp timestamp
                            :id id))
    (db/get-guestbook {:id id})
    (throw (ex-info (str "异常，事务回滚，列表中查看该id的数据是否存在，id:" id) {}))))
```
注意：只有在transaction中的exception发生，事务的机制才会生效，我测试时就正好稀里糊涂把throw放到了with-transaction里面，导致总是不会回滚。

#### 13、修改db连接池配置

参考[Luminus 数据库连接池配置](https://blog.3vyd.com/blog/posts-output/2019-08-07-clojure-%E6%95%B0%E6%8D%AE%E5%BA%93%E8%BF%9E%E6%8E%A5%E6%B1%A0%E9%85%8D%E7%BD%AE/)

#### 14、定时任务
有个比较重量级的[http://clojurequartz.info/articles/guides.html](http://clojurequartz.info/articles/guides.html)库，quartz与在java里的一样，只不过是clojure的实现。
我们项目里没有很复杂的需要动态修改的定时任务，因此选择了一个轻量级的库:[chime](https://github.com/jarohen/chime)，api参考github。下面是项目中的一个demo

```clojure
(ns project.common.scheduler
  (:require [chime :refer [chime-ch]]
            [clj-time.core :as t]
            [clj-time.periodic :refer [periodic-seq]]
            [clojure.core.async :as a :refer [<! go-loop]]
            [clojure.tools.logging :as log])
  (:import org.joda.time.DateTimeZone))

;; FIXME 定时功能应该还没有做    (^_^)

(defn times []
  (rest (periodic-seq (.. (t/now)
                          (withZone (DateTimeZone/getDefault))
                          #_(withTime 0 0 0 0))
                      (t/minutes 10))))
(defn channel []
  (a/chan))

(defn chime []
  (chime-ch (times) {:ch (channel)}))

(defn start-scheduler []
  (let [chime-channle (chime)]
    (go-loop []
      (when-let [msg (<! chime-channle)]
        (log/error (format "亲爱的 %s, Clojure repl搞一个小时了，休息一下？"
                           (System/getProperty "user.name")))
        (recur)))
    chime-channle))
```
该定时任务项目启动后一个小时执行一次，执行只是简单打个log.

#### 15、优雅地打印jdbc的执行sql
项目中默认的jdbc驱动是mysql自身的启动，所以默认的databaseurl也许是这样的

```clojure
:database-url "mysql://localhost:3306/demo?user=root&password=password
```
然而，这样的配置是不会打印出jdbc执行的真正sql的，而我们有时候很需要这些sql，因为他们代表着逻辑，有时候debug也会需要。
那么怎么配置才能达到目的呢？
我们使用的是log4jdbc，因此需要在project.clj中引入该库，

```clojure
[com.googlecode.log4jdbc/log4jdbc "1.2"]
```
引入以后修改需要查看sql的profile里的edn配置文件，比如本地dev-config.edn

```clojure
:database-url "jdbc:log4jdbc:mysql://localhost:3306/demo?user=root&password=password
```
然后jdbc连接处自然也得变,routes/db/core.clj

```clojure
(defstate ^:dynamic *db*
          :start (do (Class/forName "net.sf.log4jdbc.DriverSpy")
                     (if-let [jdbc-url (env :database-url)]
                       (conman/connect! {:jdbc-url jdbc-url})
                       (do
                         (log/warn "database connection URL was not found, please set :database-url in your config, e.g: dev-config.edn")
                         *db*)))
          :stop (conman/disconnect! *db*))
```
默认的log配置，使用logback是配置的方式。
这样会在log控制台看到很多jdbc的log，因为默认这些日志都是info的，需要调整logback里日志级别。
