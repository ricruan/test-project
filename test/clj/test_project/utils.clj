(ns test-project.utils
  (:require  [clojure.test :as t]
             [ring.mock.request :refer [header]]))


(defn add-header
  "接口测试公共header"
  [request]
  (-> request
      (header "accept" "application/json")
      (header "authorization" "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyIjp7InVzZXJfaWQiOiIxIiwidXNlcm5hbWUiOiJhZG1pbiIsIm5pY2tuYW1lIjoi566h55CG5ZGYIn0sInR5cGUiOiJhY2Nlc3MtdG9rZW4iLCJpYXQiOjE2MTEzMTAxMTksImV4cCI6Mzc2MTEzMTAxMTl9.-fprFr9bCcgHRrv3pMmA8k_M8Ty7KnZIslHsS0un0KM")))

(defn add-user-header
  "接口测试公共header-错误usre-id"
  [request]
  (-> request
      (header "accept" "application/json")
      (header "authorization" "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyIjp7InVzZXJfaWQiOiIxMTExIiwidXNlcm5hbWUiOiJhZG1pbiIsIm5pY2tuYW1lIjoi566h55CG5ZGYIn0sInR5cGUiOiJhY2Nlc3MtdG9rZW4iLCJpYXQiOjE2MTE3MTE2MTEsImV4cCI6Mzc2MTE3MTE2MTF9.ZZPIF2LK4AfHQdNWiX9nTiD0cQjMqPyaWpp4To-CP0c")))

(defn add-error-header
  "接口测试公共header-错误的toekn"
  [request]
  (-> request
      (header "accept" "application/json")
      (header "authorization" "eyJhbGciOiJIUzI1NiJ9.e1yJ1c2VyIjp7InVzZXJfaWQiOiIxIiwidXNlcm5hbWUiOiJhZG1pbiIsIm5pY2tuYW1lIjoi566h55CG5ZGYIn0sInR5cGUiOiJhY2Nlc3MtdG9rZW4iLCJpYXQiOjE2MTEzMTAxMTksImV4cCI6Mzc2MTEzMTAxMTl9.-fprFr9bCcgHRrv3pMmA8k_M8Ty7KnZIslHsS0un0KM")))

(defn add-app-header
  "接口测试app-header"
  [request]
  (-> request
      (header "accept" "application/json")
      (header "openid" "omoIR5aFIw6gL2YOnGjBmNk_9yrQ")
      (header "appid" "wx069c4d7574501e36")))

(defn add-app-openid-error-header
  "接口测试app-openid-error-header"
  [request]
  (-> request
      (header "accept" "application/json")
      (header "openid" "omoIR5aFIw6gL2YOnGjBmNk_9yrQ1")
      (header "appid" "wx069c4d7574501e361")))

(defn add-app-appid-header
  "接口测试app-apppid-header"
  [request]
  (-> request
      (header "accept" "application/json")
      (header "appid" "wx069c4d7574501e36")))


(defn add-app-appid-error-header
  "接口测试app-appid-error-header"
  [request]
  (-> request
      (header "accept" "application/json")
      (header "appid" "wx069c4d7574501e361")))
