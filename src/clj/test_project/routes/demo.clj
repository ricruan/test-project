(ns test-project.routes.demo
  (:require
   [clj-http.client :as http]
   [taoensso.carmine :as car :refer (wcar)]
   [test-project.common.result :refer [ok sorry]]
   [test-project.db.redis :as redis]
   [test-project.common.utils :as utils]
   [test-project.db.core :refer [*db*] :as db]
   [test-project.modules.queries.queries-db :as queries-db]
   [test-project.common.file-util :as file]
   [ring.middleware.cookies :as cookies]
   [reitit.ring.middleware.multipart :as multipart]
   [clojure.tools.logging :as log]
   [clojure.java.io :as io]
   [clojure.spec.alpha :as s]
   [clojure.string :as str]
   [spec-tools.core :as st]
   [clojure.data.json :as json]))

(s/def ::name
  (st/spec
   {:spec            string?
    :swagger/default "demo"
    :reason          "姓名"}))

(s/def ::first-name
  (st/spec
   {:spec            string?
    :swagger/default "redcreation"
    :reason          "名字必填"}))

(s/def ::last-name
  (st/spec
   {:spec            string?
    :swagger/default "hc"
    :reason          "姓不能为空"}))

(s/def ::email
  (st/spec
   {:spec            string?
    :swagger/default "demo@redcreation.net"
    :reason          "邮箱是必填项"}))

(s/def ::pass
  (st/spec
   {:spec            (s/and string? #(> (count %) 6))
    :swagger/default "demo"
    :reason          "密码不能为空,且大于6位数"}))

(s/def ::admin #{true, false})

(s/def ::remark coll?)

(s/def ::user-body
  (s/keys :req-un [::first-name ::last-name ::email ::pass ::admin]
          :opt-un [:base/id ::remark]))

(s/def ::list (s/coll-of ::user-body))

(s/def ::batch-body
  (s/keys :req-un [::list :base/id]))

;;定义demo-user的非必填字段默认值
(def default-demo-user
  {:admin     false
   :is_active true
   :remark    nil})

(defn- batch-create-user
  "批量插入的实现：
   函数本身的注释请写在这个位置
   ----可以换行写
   defn 后面减号 - 的含义是该方法只供本namespace调用，不对外
   注意：批量操作的函数，id如果自增，则不需要前面的map assoc"
  [body]
  (when (pos? (count (:list body)))
    (let [list (map #(assoc % :id (utils/uuid)) (:list body))
          funcs [:id
                 :first-name
                 :last-name
                 :email
                 :pass
                 (constantly true)
                 (constantly true)]
          records (map (apply juxt funcs) list)]
      (when (pos? (count records))
        (queries-db/batch-create-user! {:records records})))))

(defn demo-routes []
  ["/demo"
   {:swagger {:tags ["test-api"]}}

   [""
    {:get  {:summary     "根据id获取user--并设置cookies"
            :description "查询某id的user数据"
            :parameters  {:query (s/keys :req-un [:base/id])}
            :handler     (fn [{{{:keys [id]} :query} :parameters}]
                           {:status  200
                            :body    {:data (queries-db/get-user {:id id})}
                            :cookies {:test-cookie "123123123"}})}

     :post {:summary     "新增记录---事务的使用---json使用"
            :description "插入一条新纪录"
            :parameters  {:body ::user-body}
            :handler     (fn [{{:keys [body]} :parameters}]
                           (println "=====打印参数看看:=====" body)
                           (conman.core/with-transaction
                             [*db*]
                             (let [id (utils/uuid)
                                   data (assoc body :id id
                                               :remark (json/write-str (:remark body)))]
                               (queries-db/create-user! (merge default-demo-user data))
                               #_(ex-info {} "抛个异常") ;;如果不加事务，手动抛出这个异常的话，上面的insert是不会回滚的
                               {:status  200
                                :body    {:data
                                          (assoc data
                                                 :remark
                                                 (json/read-str (:remark data)
                                                                :key-fn  keyword))}})))}}]   ;db里的json结构化

   ["/query/like"
    {:get {:summary     "两种like查询写法的例子"
           :parameters  {:query (s/keys :opt-un [::name ::email])}
           :handler     (fn [{{{:keys [name email]} :query} :parameters}]
                          (ok (queries-db/search-user {:last-name (str "%" name "%")
                                               :name name
                                               :email email})))}}]

   ["/query/in/page"
    {:get {:summary     "in条件 + 分页查询"
           :parameters  {:query (s/keys :opt-un [:base/page :base/size ::email])}
           :handler     (fn [{{{:keys [email page size]} :query} :parameters}]
                          (ok {:total-elements (->> (queries-db/search-users-page-in-email
                                                     {:count true
                                                      :email (str/split email #",")})
                                                    (map :total-elements)
                                                    (first))
                               :content (queries-db/search-users-page-in-email {:page (* page size)
                                                                        :size size
                                                                        :email (str/split email #",")})}))}}]


   ["/batch/users"
    {:post {:summary     "批量插入---用spec构造稍微复杂的body体"
            :parameters  {:body ::batch-body}
            :handler     (fn [{{:keys [body]} :parameters}]
                           (clojure.pprint/pprint body)  ;;打印更加美观的log
                           (ok (batch-create-user body)))}}]

   ["/http/get"
    {:get {:summary "一个http client远端调用的例子"
           :handler (fn [_]
                      (ok (:body
                           (http/get "http://cdn.imgs.3vyd.com/xh/admin/test.json" {:as :json}))))}}]

   ["/http/post/json"
    {:get {:summary "以json body提交到远端"
           :handler (fn [_]
                      (ok (:body
                           (http/post "http://localhost:8185/management/public/doctor"
                                      {:form-params
                                       {:mobile   "15092107093"
                                        :nickName "marvin.ma"
                                        :name     "marvin"
                                        :openid   "8"}
                                       :content-type :json}))))}}]
   ["/http/post/form"
    {:get {:summary "以form body提交到远端"
           :handler (fn [_]
                      (ok (:body
                           (http/post "http://localhost:8185/management/oauth2/token"
                                      {:form-params
                                       {:username   "test"
                                        :password   "test123"
                                        :client_id  "management-Client"
                                        :grant_type "password"}}))))}}]

   ["/redis/set"
    {:get {:summary    "向redis设置一个key的值"
           :parameters {:query {:key   string?
                                :value string?}}
           :handler    (fn [{{{:keys [key, value]} :query} :parameters}]
                         {:status 200
                          :body   (redis/set-value key value)})}}]

   ["/redis/get"
    {:get {:summary    "从redis获取某个key的值"
           :parameters {:query {:key string?}}
           :handler    (fn [{{{:keys [key]} :query} :parameters}]
                         {:status 200
                          :body   {:data {key (redis/get-value key)}}})}}]

   ;;api返回结果: {"data": "path params: {:id \"1\"}\n query params: {\"name\" \"2\"}\n body params: {:message \"22\"}"}
   ["/path/bad/:id"
    {:post {:summary    "路径上传参--不推荐此方法获取--query参数key变成了str"
            :parameters {:path  {:id int?}
                         :query {:name string?}
                         :body  {:message string?}}
            :handler    (fn [{:keys [path-params query-params body-params]}]
                          {:status 200
                           :body   {:data (str "path params: " path-params
                                               "\n query params: " query-params
                                               "\n body params: " body-params)}})}}]

   ;;api返回结果:
   ;;{
   ;;  "code": 1,
   ;;  "message": "操作成功",
   ;;  "data": "path params: {:id 1},  query params: {:name \"2\"},  body params: {:message \"22\"} "
   ;;}
   ["/path/good/:id"
    {:post {:summary    "路径上传参--GOOD--获取到3种map"
            :parameters {:path  {:id int?}
                         :query {:name string?}
                         :body  {:message string?}}
            :handler    (fn [{{:keys [body query path]} :parameters}]
                          (ok (format "path params: %s,  query params: %s,  body params: %s " path query body)))}}]
   ;;api返回结果:
   ;;{
   ;;"code": 1,
   ;;"message": "操作成功",
   ;;"data": "path params 'id': 1, query params 'name': 2 , body params: {:message \"22\"} "
   ;;}
   ["/path/good-all-params/:id"
    {:post {:summary    "路径上传参--GOOD--直接得到key的val"
            :parameters {:path  {:id int?}
                         :query {:name string?}
                         :body  {:message string?}}
            :handler    (fn [{{:keys [body]}          :parameters
                              {{:keys [id]} :path}    :parameters
                              {{:keys [name]} :query} :parameters}]
                          (ok (format "path params 'id': %s, query params 'name': %s , body params: %s " id name body)))}}]

   ["/all-params/:id"
    {:post {:summary    "同时获取所有类型的参数"
            :parameters {:path  {:id int?}
                         :query {:name string?}
                         :body  {:message string?}
                         :header {:role string?}}
            :handler    (fn [{{:keys [body]}          :parameters
                              {{:keys [id]} :path}    :parameters
                              {{:keys [name]} :query} :parameters
                              {{:keys [token]} :header} :parameters}]
                          (ok (format "path params 'id': %s, query params 'name': %s , body params: %s " id name body)))}}]


   ["/files"

    ["/upload"
     {:post {:summary    "上传单个文件"
             :parameters {:multipart {:file multipart/temp-file-part
                                      :prefix string?}}
             :responses  {200 {:body {:code int?
                                      :message string?
                                      :data {:file-url string?}}}}
             :handler    (fn [{{{:keys [file prefix]} :multipart} :parameters}]
                           (ok "上传成功"
                               {:file-url (file/upload-file-qiniu prefix file)}))}}]

    ["/upload-mult"
     {:post {:summary    "多文件上传示例---swagger-ui暂不支持---实现参考单个文件上传"
             :parameters {:multipart {:files [multipart/temp-file-part]
                                      :name string?}}
             :handler    (fn [{{{:keys [files]} :multipart} :parameters}]
                           (ok (map #({:name (:filename %)
                                       :size (:size %)}) files)))}}]

    ["/download"
     {:get {:summary "downloads a file"
            :swagger {:produces ["image/png"]}
            :handler (fn [_]
                       {:status  200
                        :headers {"Content-Type" "image/png"}
                        :body    (-> "public/img/warning_clojure.png"
                                     (io/resource)
                                     (io/input-stream))})}}]]])
