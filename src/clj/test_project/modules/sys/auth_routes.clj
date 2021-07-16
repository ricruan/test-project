(ns test-project.modules.sys.auth-routes
  (:require
    [spec-tools.core :as st]
    [reitit.ring.middleware.exception :as exception]
    [test-project.common.result :refer [ok sorry]]
    [test-project.modules.sys.auth-specs :as auth-specs]
    [test-project.modules.sys.auth-service :as auth]))

(def token-spec
  {:username (st/spec
               {:spec string?
                :swagger/default "admin"
                :description "用户名"})
   :password (st/spec
               {:spec string?
                :swagger/default "admin.123"
                :description "密码"})})

(def refresh-token-spec
  {:refresh-token (st/spec
                    {:spec string?})})

(defn auth-routes []
  ["/auth"
   {:swagger {:tags ["后台-系统授权"]}}

   ["/token"
    {:post {:summary "获取token"
            :parameters {:body token-spec}
            :responses {200 {:body ::auth-specs/public-anth-token}}
            :handler (fn [{{body :body} :parameters}]
                       (-> (auth/get-token body)
                           (ok)))}}]

   ["/refresh-token"
    {:post {:summary "刷新token"
            :parameters {:body refresh-token-spec}
            :responses {200 {:body ::auth-specs/public-anth-token}}
            :handler (fn [{{body :body} :parameters}]
                       (-> (auth/refresh-token (:refresh-token body))
                           (ok)))}}]])

(and not-empty [] )