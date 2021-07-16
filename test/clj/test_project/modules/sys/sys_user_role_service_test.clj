(ns test-project.modules.sys.sys-user-role-service-test
  (:require [test-project.modules.sys.auth-routes :as sut]
            [test-project.handler :refer [app]]
            [mount.core :as mount]
            [muuntaja.core :as m]
            [user :refer [reset-db]]
            [test-project.utils :refer [add-header]]
            [ring.mock.request :refer [request query-string json-body]]
            [clojure.test :refer [deftest testing use-fixtures is]]))

(def role-params {:page 0 :size 10 :username "管理员"})

(use-fixtures
  :once
  (fn [f]
    (mount/start
     #'test-project.config/env
     #'test-project.db.core/*db*
     #'test-project.handler/app-routes)
    (reset-db)
    (f)))


(deftest sys-user-role-service-test
  (testing "系统角色分页列表"
    (let [response ((app) (-> (request :get (str "/admin/sys-role/page"))
                              (query-string role-params)
                              (add-header)))
          result (m/decode-response-body response)]
      (is (= 200 (:status response)))
      (is (zero? (:code result)))
      (is (get-in result [:msg]))))

  (testing "保存系统角色"
    (let [response ((app) (-> (request :post (str "/admin/sys-role/save"))
                              (json-body {:name "测试" :code "test"})
                              (add-header)))
          result (m/decode-response-body response)]
      (is (= 200 (:status response)))
      (is (zero? (:code result)))
      (is (get-in result [:msg]))))

  (testing "保存系统角色-不传参数"
    (let [response ((app) (add-header (request :post (str "/admin/sys-role/save"))))
          result (m/decode-response-body response)]
      (is (= 400 (:status response)))))

  (testing "更新系统角色"
    (let [response ((app) (-> (request :post (str "/admin/sys-role/update"))
                              (json-body {:id "2" :name "测试" :code "test"})
                              (add-header)))
          result (m/decode-response-body response)]
      (is (= 200 (:status response)))
      (is (zero? (:code result)))
      (is (= 1 (:data result)))
      (is (get-in result [:msg]))))

  (testing "更新系统角色-不传参数"
    (let [response ((app) (add-header (request :post (str "/admin/sys-role/update"))))
          result (m/decode-response-body response)]
      (is (= 400 (:status response)))))

  (testing "系统用户角色"
    (let [response ((app) (add-header (request :get (str "/admin/sys-role/user/roles/2"))))
          result (m/decode-response-body response)]
      (is (= 200 (:status response)))
      (is (zero? (:code result)))
      (is (get-in result [:msg]))))

  (testing "分配用户角色"
    (let [response ((app) (-> (request :post (str "/admin/sys-role/assign/role"))
                              (json-body {:user-id "2" :role-ids ["2"]})
                              (add-header)))
          result (m/decode-response-body response)]
      (is (= 200 (:status response)))
      (is (zero? (:code result)))
      (is (get-in result [:msg]))))

  (testing "分配用户角色-不传参数"
    (let [response ((app) (add-header (request :post (str "/admin/sys-role/assign/role"))))
          result (m/decode-response-body response)]
      (is (= 400 (:status response)))))
	  
	  
  (testing "系统角色删除"
    (let [response ((app) (add-header (request :delete (str "/admin/sys-role/remove/2"))))
          result (m/decode-response-body response)]
      (is (= 200 (:status response)))
      (is (zero? (:code result)))
      (is (get-in result [:msg]))))

  )
