(ns test-project.routes.base
  (:require
   [reitit.swagger :as swagger]
   [reitit.swagger-ui :as swagger-ui]
   [reitit.ring.coercion :as coercion]
   [reitit.coercion.spec :as spec-coercion]
   [reitit.ring.middleware.muuntaja :as muuntaja]
   [reitit.ring.middleware.multipart :as multipart]
   [reitit.ring.middleware.parameters :as parameters]
   [test-project.middleware.formats :as formats]
   [test-project.middleware.exception :as exception]
   [test-project.middleware.authentication :as auth]
   [test-project.middleware.log-interceptor :refer [log-wrap]]
   [ring.util.http-response :refer :all]
   [clojure.java.io :as io]
   [clojure.spec.alpha :as s]
   [spec-tools.core :as st]))

(s/def :base/openid
  (st/spec
    {:spec string?
     :swagger/default "omoIR5aFIw6gL2YOnGjBmNk_9yrQ"
     :description "微信openid"}))

(s/def :base/appid
  (st/spec
    {:spec string?
     :swagger/default "wx069c4d7574501e36"
     :description "微信appid"}))

(s/def :base/authorization
  (st/spec
    {:spec string?
     :swagger/default
           "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyIjp7InVzZXJfaWQiOiIxIiwidXNlcm5hbWUiOiJhZG1pbiIsIm5pY2tuYW1lIjoi566h55CG5ZGYIn0sInR5cGUiOiJhY2Nlc3MtdG9rZW4iLCJpYXQiOjE2MTEzMTAxMTksImV4cCI6Mzc2MTEzMTAxMTl9.-fprFr9bCcgHRrv3pMmA8k_M8Ty7KnZIslHsS0un0KM"
     :description "token"}))

(s/def :base/page
  (st/spec
    {:spec            int?
     :description     "页码，从0开始"
     :swagger/default "0"
     :reason          "页码参数不能为空"}))

(s/def :base/size
  (st/spec
    {:spec            int?
     :description     "每页条数"
     :swagger/default "10"
     :reason          "条数参数不能为空"}))

(s/def :base/id
  (st/spec
   {:spec        any?
    :description "主键id"
    :reason      "主键id不能为空"}))

(s/def :base/code
  (st/spec
   {:spec int?
    :swagger/default 0
    :description "状态"}))

(s/def :base/msg
  (st/spec
   {:spec string?
    :swagger/default "操作成功"
    :description "消息"}))

(s/def :base/data
  (st/spec
   {:spec any?
    :description "返回数据"}))

(s/def :base/body (s/keys :req-un [:base/code :base/msg :base/data]))

(defn service-routes []
  [""
   {:coercion spec-coercion/coercion
    :muuntaja formats/instance
    :swagger {:id ::api}
    :middleware [ ;; query-params & form-params
                 parameters/parameters-middleware
                 ;; content-negotiation
                 muuntaja/format-negotiate-middleware
                 ;; encoding response body
                 muuntaja/format-response-middleware
                 ;; exception handling
                 exception/exception-middleware
                 ;; decoding request body
                 muuntaja/format-request-middleware
                 ;; coercing response bodys
                 coercion/coerce-response-middleware
                 ;; coercing request parameters
                 coercion/coerce-request-middleware
                 ;; multipart
                 multipart/multipart-middleware
                 ;; log
                 log-wrap]}])

(defn swagger-routes []
  [""
   {:no-doc true
    :swagger {:info {:title "my-api"
                     :description ""}}}
   ["/" {:get
         {:handler (constantly {:status 301 :headers {"Location" "/api-docs/index.html"}})}}]

   ["/swagger.json"
    {:get (swagger/create-swagger-handler)}]

   ["/api-docs/*"
    {:get (swagger-ui/create-swagger-ui-handler
            {:url "/swagger.json"
             :config {:validator-url nil}})}]])

(defn api-routes []
  ["/api"
   {:parameters {:header (s/keys :req-un [:base/openid :base/appid])}
    :middleware [auth/auth-openid-wrap]}])

(defn api-public-routes []
  ["/api/public"
   {:parameters {:header (s/keys :req-un [:base/appid])}
    :middleware [auth/auth-appid-wrap]}])

(defn api-callback-routes []
  ["/api/callback"])

(defn admin-routes []
  ["/admin"
   {:parameters {:header (s/keys :req-un [:base/authorization])}
    :middleware [auth/auth-token-wrap]}])

(defn admin-public-routes []
  ["/admin/public"
   ])
