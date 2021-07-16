(ns test-project.modules.sys.sys-user-menu-service-test
  (:require [test-project.modules.sys.auth-routes :as sut]
            [test-project.handler :refer [app]]
            [mount.core :as mount]
            [muuntaja.core :as m]
            [user :refer [reset-db]]
            [test-project.utils :refer [add-header]]
            [ring.mock.request :refer [request query-string json-body]]
            [clojure.test :refer [deftest testing use-fixtures is]]))



(def save-params {:parent-id "1"
                  :name "测试"
                  :code "string"
                  :path "/main"
                  :icon "i"
                  :sort "1"
                  :menu_type 0})


(use-fixtures
  :once
  (fn [f]
    (mount/start
     #'test-project.config/env
     #'test-project.db.core/*db*
     #'test-project.handler/app-routes)
    (reset-db)
    (f)))

(deftest sys-user-menu-service-test
  (testing "系统菜单分页列表"
    (let [response ((app) (-> (request :get (str "/admin/sys-menu/list"))
                              (query-string {:name "首页"})
                              (add-header)))
          result (m/decode-response-body response)]
      (is (= 200 (:status response)))
      (is (zero? (:code result)))
      (is (get-in result [:msg]))))

  (testing "保存系统菜单"
    (let [response ((app) (-> (request :post (str "/admin/sys-menu/save"))
                              (json-body save-params)
                              (add-header)))
          result (m/decode-response-body response)]
      (is (= 200 (:status response)))
      (is (zero? (:code result)))
      (is (get-in result [:msg]))))

  (testing "保存系统菜单-参数不传"
    (let [response ((app) (add-header (request :post (str "/admin/sys-menu/save"))))
          result (m/decode-response-body response)]
      (is (= 400 (:status response)))))

  (testing "更新系统菜单"
    (let [response ((app) (-> (request :post (str "/admin/sys-menu/update"))
                              (json-body (assoc save-params :id "2"))
                              (add-header)))
          result (m/decode-response-body response)]
      (is (= 200 (:status response)))
      (is (zero? (:code result)))
      (is (get-in result [:msg]))))

  (testing "更新系统菜单-参数不传"
    (let [response ((app) (add-header (request :post (str "/admin/sys-menu/update"))))
          result (m/decode-response-body response)]
      (is (= 400 (:status response)))))

  (testing "获取系统菜单选择级联数据"
    (let [response ((app) (add-header (request :get (str "/admin/sys-menu/menu-cascader"))))
          result (m/decode-response-body response)]
      (is (= 200 (:status response)))
      (is (zero? (:code result)))
      (is (get-in result [:msg]))))

  (testing "角色所属菜单"
    (let [response ((app) (add-header (request :get (str "/admin/sys-menu/role/menus/2"))))
          result (m/decode-response-body response)]
      (is (= 200 (:status response)))
      (is (zero? (:code result)))
      (is (get-in result [:msg]))))

  (testing "角色分配菜单"
    (let [response ((app) (-> (request :post (str "/admin/sys-menu/assign/menu"))
                              (json-body {:role-id "2" :menu-ids ["2"]})
                              (add-header)))
          result (m/decode-response-body response)]
      (is (= 200 (:status response)))
      (is (zero? (:code result)))
      (is (get-in result [:msg]))))

  (testing "角色分配菜单-不传参数"
    (let [response ((app) (add-header (request :post (str "/admin/sys-menu/assign/menu"))))
          result (m/decode-response-body response)]
      (is (= 400 (:status response)))))
	  
	  
  (testing "系统菜单删除"
    (let [response ((app) (add-header (request :delete (str "/admin/sys-menu/remove/2"))))
          result (m/decode-response-body response)]
      (is (= 200 (:status response)))
      (is (zero? (:code result)))
      (is (get-in result [:msg]))))
  )
