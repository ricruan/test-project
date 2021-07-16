(ns test-project.modules.base.base-admin-routes-test
  (:require [test-project.modules.sys.auth-routes :as sut]
            [test-project.handler :refer [app]]
            [mount.core :as mount]
            [muuntaja.core :as m]
            [user :refer [reset-db]]
            [test-project.utils :refer [add-header add-user-header add-error-header]]
            [test-project.common.result :refer [ok sorry]]
            [ring.mock.request :refer [request query-string json-body]]
            [clojure.test :refer [deftest testing use-fixtures is]]))


(use-fixtures
  :once
  (fn [f]
    (mount/start
     #'test-project.config/env
     #'test-project.db.core/*db*
     #'test-project.handler/app-routes)
    (reset-db)
    (f)))

(deftest base-admin-routes-test
  (testing "获取系统当前登录用户基础数据"
    (let [response ((app) (add-header (request :get (str "/admin/base/user"))))
          result (m/decode-response-body response)]
      (is (= 200 (:status response)))
      (is (zero? (:code (m/decode-response-body response))))
      (is (get-in result [:msg]))))

  (testing "获取系统当前登录用户基础数据-用户不存在"
    (let [response ((app) (add-user-header (request :get (str "/admin/base/user"))))
          result (m/decode-response-body response)]
      (is (= 200 (:status response)))
      (is (zero? (:code (m/decode-response-body response))))
      (is (get-in result [:msg])))

    (testing "获取系统当前登录用户基础数据-错误token"
      (let [response ((app) (add-error-header (request :get (str "/admin/base/user"))))
            result (m/decode-response-body response)]
        (is (get-in result [:msg]))))))
