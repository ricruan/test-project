## 状态控制re-frame & kee-frame
   re-frame是状态管理的基础，一个500多行代码的小库。为前端提供了中心化数据存储和状态管理。
   类似于MVC中的M层（被称为app-state的db）和C层(事件注册:reg-event、派发:dispatch、注册事件的后续处理:effect，订阅:reg-subscription)
   Kee-frame在re-frame的基础上做了一些扩展，把状态控制交给路由来做。
   增加了专门处理路由的controller, controller在发生路由跳转的时候触发。controller是个map，里面的两个key :params 和:start 分别挂了两个函数
   第一个函数的参数是路由 [:路由名称 {路由参数map}]
   第二个函数以context和第一个函数的value为输入，输入可以是nil:[ctx 上一个函数的value]
   ```clojurescript
     (reg-controller :控制器名称
                     {:params (fn [{:keys [handler route-params]}]
                                (when (= handler :patient-list)
                                  (:id route-params)))
                      :start  (fn [_ id]
                                [:ajax-fetch-patient id])
                      ;; stop 不是必须的
                      :stop nil
                      })
   ```
   start对应的函数是否执行由以下规则决定：
   1. 路由参数和返回值都和上次一样，start函数不执行
   2. 上次是nil，这次不是，start函数执行
   3. 上次不是nil这次是nil，执行:stop函数, :stop函数是可选的，不一定有
   4. 上次和本次都不是nil,且两次结果不一样,  先stop，再start

   根据以上规则，常见场景
   - 启动时候仅仅执行一次的操作
     #+BEGIN_SRC clojurescript
       {:params (constantly true) ;; true, or whatever non-nil value you prefer
        :start  [:call-me-once-then-never-again]}
     #+END_SRC
   - 每次发生路由的时候都做一次的操作，比如logging
     #+BEGIN_SRC clojurescript
       {:params identity
        :start  [:log-user-activity]}
     #+END_SRC
   - 最常见：到特定路由的时候发生的操作，比如请求某个接口

## 路由的管理

    - 路由库是reitit3.0，和后端一样, 由一个vector来定义, 特定路由可以带参数.
      可以中心化配置，也可以分配配置，再conj起来
      ``` clojurescript
        (def routes [["/" :live]
                     ["/league/:id/:tab" :league]])
      ```
    - 在kee-frame.core/start 的时候作为参数传入
      ``` clojurescript
        (k/start!  {:routes         my-routes
                    :app-db-spec    :my-app/db-spec
                    :initial-db     your-blank-db-map
                    :root-component [my-root-reagent-component]
                    :debug?         true})
      ```
    - 触发路由的方式
      1. 超链接
         ``` clojurescript
           (def path
             (k/path-for [:league {:id 14 :tab :fixtures}]))

           ;;path-for会检查路由是否合法，如果合法，返回串 "/league/14/fixtures"
           ;;下面以超链为例说明href的跳转方式

           [:a {:href path }]

         ```

      2. 使用:navigate-to 这个cofx, 可以在这个基础上定义个函数做全局跳转
        ``` clojurescript
           (k/reg-event-fx :jump-to
                           (fn [_ [path]]
                             {:navigate-to path}))
        ```
