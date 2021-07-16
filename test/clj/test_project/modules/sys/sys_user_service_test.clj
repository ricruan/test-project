(ns test-project.modules.sys.sys-user-service-test
  (:require [test-project.modules.sys.auth-routes :as sut]
            [test-project.handler :refer [app]]
            [mount.core :as mount]
            [muuntaja.core :as m]
            [user :refer [reset-db]]
            [test-project.utils :refer [add-header]]
            [ring.mock.request :refer [request query-string json-body]]
            [clojure.test :refer [deftest testing use-fixtures is]]))


(def paging-params {:page 0 :size 10 :username "admin"})
(def save-params {:username "admin1" :password "admin1" :nickname "管理员"})
(def save-params-have {:username "admin" :password "admin1" :nickname "管理员"})
(def update-params {:username "admin" :id "1" :nickname "管理员"})
(def update-params-have {:username "admin" :id "11" :nickname "管理员1"})
(def current-user-pwd-params {:id "2" :old-password "admin.123" :password "admin"})


(use-fixtures
  :once
  (fn [f]
    (mount/start
     #'test-project.config/env
     #'test-project.db.core/*db*
     #'test-project.handler/app-routes)
    (reset-db)
    (f)))



(deftest sys-user-service-test
  (testing "获取当前用户菜单列表"
    (let [response ((app) (add-header (request :get (str "/admin/sys-user/menus"))))
          result (m/decode-response-body response)]
      (is (= 200 (:status response)))
      (is (zero? (:code result)))
      (is (get-in result [:msg]))))

  (testing "系统用户分页列表"
    (let [response ((app) (-> (request :get (str "/admin/sys-user/page"))
                              (query-string paging-params)
                              (add-header)))
          result (m/decode-response-body response)]
      (is (= 200 (:status response)))
      (is (zero? (:code result)))
      (is (get-in result [:msg]))))

  (testing "保存系统用户"
    (let [response ((app) (-> (request :post (str "/admin/sys-user/save"))
                              (json-body save-params)
                              (add-header)))
          result (m/decode-response-body response)]
      (is (= 200 (:status response)))
      (is (zero? (:code result)))
      (is (get-in result [:msg]))))

  (testing "保存系统用户-用户存在"
    (let [response ((app) (-> (request :post (str "/admin/sys-user/save"))
                              (json-body save-params-have)
                              (add-header)))
          result (m/decode-response-body response)]
      (is (= 200 (:status response)))
      (is (= -1 (:code result)))
      (is (get-in result [:msg]))))

  (testing "更新系统用户"
    (let [response ((app) (-> (request :post (str "/admin/sys-user/update"))
                              (json-body update-params)
                              (add-header)))
          result (m/decode-response-body response)]
      (is (= 200 (:status response)))
      (is (zero? (:code result)))
      (is (get-in result [:msg]))))

  (testing "更新系统用户-用户名存在"
    (let [response ((app) (-> (request :post (str "/admin/sys-user/update"))
                              (json-body update-params-have)
                              (add-header)))
          result (m/decode-response-body response)]
      (is (= 200 (:status response)))
      (is (= -1 (:code result)))
      (is (get-in result [:msg]))))

  (testing "当前使用者修改密码(需验证旧密码)"
    (let [response ((app) (-> (request :post (str "/admin/sys-user/current-user-pwd"))
                              (json-body current-user-pwd-params)
                              (add-header)))
          result (m/decode-response-body response)]
      (is (= 200 (:status response)))
      (is (= 1 (get-in result [:data])))
      (is (get-in result [:msg]))))

  (testing "当前使用者修改密码(需验证旧密码)-错误"
    (let [response ((app) (-> (request :post (str "/admin/sys-user/current-user-pwd"))
                              (json-body current-user-pwd-params)
                              (add-header)))
          result (m/decode-response-body response)]
      (is (= 200 (:status response)))
      (is (= "error" (get-in result [:data])))
      (is (get-in result [:msg]))))

  (testing "修改密码"
    (let [response ((app) (-> (request :post (str "/admin/sys-user/change-pwd"))
                              (json-body {:id "2" :password "admin.123"})
                              (add-header)))
          result (m/decode-response-body response)]
      (is (= 200 (:status response)))
      (is (zero? (:code result)))
      (is (get-in result [:msg]))))

  (testing "系统用户删除"
    (let [response ((app) (add-header (request :delete (str "/admin/sys-user/remove/2"))))
          result (m/decode-response-body response)]
      (is (= 200 (:status response)))
      (is (zero? (:code result)))
      (is (get-in result [:msg]))))
  )
