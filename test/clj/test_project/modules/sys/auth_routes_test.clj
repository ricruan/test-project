(ns test-project.modules.sys.auth-routes-test
  (:require [test-project.modules.sys.auth-routes :as sut]
            [test-project.handler :refer [app]]
            [mount.core :as mount]
            [muuntaja.core :as m]
            [user :refer [reset-db]]
            [test-project.utils :refer [add-header]]
            [ring.mock.request :refer [request query-string json-body]]
            [clojure.test :refer [deftest testing use-fixtures is]]))

(def auth-spec
  {:username "admin"
   :password "admin.123"})

(def auth-refresh {:refresh-token "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyIjp7InVzZXItaWQiOiIxIiwidXNlcm5hbWUiOiJhZG1pbiIsIm5pY2tuYW1lIjoi566h55CG5ZGYIn0sInR5cGUiOiJyZWZyZXNoLXRva2VuIiwiZXhwIjoxNjEwODYyMDY1fQ.woILCEzfYyjiWjLimk-J45iBB0XdxjZ_lLAm-cpbyPc"})

(def auth-refresh-error {:refresh-token "1"})

(use-fixtures
  :once
  (fn [f]
    (mount/start
     #'test-project.config/env
     #'test-project.db.core/*db*
     #'test-project.handler/app-routes)
    (reset-db)
    (f)))

(deftest auth-routes-test
  (testing "测试登录获取token"
    (let [response ((app) (json-body  (request :post "/admin/public/auth/token") auth-spec))
          result (m/decode-response-body response)]
      (is (not (nil? (get-in result [:data :access-token]))))
      (is (get-in result [:msg]))))

  (testing "测试登录获取token-密码用户错误"
    (let [response ((app) (json-body  (request :post "/admin/public/auth/token") {:username "admin" :password "admin"}))
          result (m/decode-response-body response)]
      (is (nil? (get-in result [:data])))
      (is (get-in result [:msg]))))

  (testing "刷新token"
    (let [response ((app) (json-body  (request :post "/admin/public/auth/refresh-token") auth-refresh))
          result (m/decode-response-body response)]
      (is (get-in result [:data :access-token]))
      (is (get-in result [:msg]))))

  (testing "刷新token-无效token"
    (let [response ((app) (json-body  (request :post "/admin/public/auth/refresh-token") auth-refresh-error))
          result (m/decode-response-body response)]
      (is (nil? (get-in result [:data])))
      (is (get-in result [:msg]))))
  )
