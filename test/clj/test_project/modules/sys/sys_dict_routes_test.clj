(ns test-project.modules.sys.sys-dict-routes-test
  (:require [test-project.modules.sys.auth-routes :as sut]
            [test-project.handler :refer [app]]
            [mount.core :as mount]
            [muuntaja.core :as m]
            [user :refer [reset-db]]
            [test-project.utils :refer [add-header]]
            [ring.mock.request :refer [request query-string json-body]]
            [clojure.test :refer [deftest testing use-fixtures is]]))

(def dict-params {:id 2
                  :code "admin1"
                  :name "管理员1"
                  :group-code "管理"
                  :type 0
                  :sort 0
                  :parent-id 1})

(use-fixtures
  :once
  (fn [f]
    (mount/start
     #'test-project.config/env
     #'test-project.db.core/*db*
     #'test-project.handler/app-routes)
    (reset-db)
    (f)))


(deftest sys-dict-routes-test
  (testing "添加字典数据"
    (let [response ((app) (-> (request :post (str "/admin/sys/dict/list"))
                              (json-body {:code "admin"
                                          :name "管理员"
                                          :group-code "管理"
                                          :parent-id 1})
                              (add-header)))
          result (m/decode-response-body response) ]
      (is (= 200 (:status response)))
      (is (zero? (:code (m/decode-response-body response))))
      (is (get-in result [:msg]))))

  (testing "添加字典数据-parent-id等于0"
    (let [response ((app) (-> (request :post (str "/admin/sys/dict/list"))
                              (json-body {:code "admin"
                                          :name "管理员"
                                          :group-code "管理"
                                          :parent-id 0})
                              (add-header)))
          result (m/decode-response-body response)]
      (is (= 200 (:status response)))
      (is (zero? (:code (m/decode-response-body response))))
      (is (get-in result [:msg]))))

  (testing "修改字典数据"
    (let [response ((app) (-> (request :put (str "/admin/sys/dict/list"))
                              (json-body dict-params)
                              (add-header)))
          result (m/decode-response-body response)]
      (is (= 200 (:status response)))
      (is (zero? (:code (m/decode-response-body response))))
      (is (get-in result [:msg]))))

  (testing "修改字典数据-type等于1"
    (let [response ((app) (-> (request :put (str "/admin/sys/dict/list"))
                              (json-body (assoc dict-params :type 1))
                              (add-header)))
          result (m/decode-response-body response)]
      (is (= 200 (:status response)))
      (is (zero? (:code (m/decode-response-body response))))
      (is (get-in result [:msg]))))

  (testing "获取字典列表"
    (let [response ((app) (-> (request :get (str "/admin/sys/dict/list"))
                              (query-string {:page 0 :size 10})
                              (add-header)))
          result (m/decode-response-body response)]
      (is (= 200 (:status response)))
      (is (zero? (:code (m/decode-response-body response))))
      (is (get-in result [:msg]))))

  (testing "获取字典列表"
    (let [response ((app) (-> (request :get (str "/admin/sys/dict/list"))
                              (query-string {:page 0 :size 10 :parent-id 1})
                              (add-header)))
          result (m/decode-response-body response)]
      (is (= 200 (:status response)))
      (is (zero? (:code (m/decode-response-body response))))
      (is (get-in result [:msg]))))
  
  (testing "删除字典数据"
    (let [response ((app) (-> (request :delete (str "/admin/sys/dict/list"))
                              (query-string {:id 2})
                              (add-header)))
          result (m/decode-response-body response)]
      (is (= 200 (:status response)))
      (is (zero? (:code (m/decode-response-body response))))
      (is (get-in result [:msg]))))
  )
