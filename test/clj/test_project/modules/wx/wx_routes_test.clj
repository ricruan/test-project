(ns test-project.modules.wx.wx-routes-test
  (:require [test-project.modules.wx.wx-routes :as sut]
            [clojure.test :refer [deftest testing use-fixtures is]]
            [muuntaja.core :as m]
            [test-project.handler :refer [app]]
            [test-project.utils :refer [add-app-header add-app-openid-error-header add-app-appid-error-header add-app-appid-header]]
            [ring.mock.request :refer [request query-string json-body]]))

(deftest wx-routes-test
  (testing "测试wx-app-openid"
    (let [response ((app) (add-app-header (request :get (str "/api/xw/list/app"))))
          result (m/decode-response-body response)]
      (is (= 200 (:status response)))
      (is (get-in result [:msg]))))

  (testing "测试wx-app-openid-error"
    (let [response ((app) (add-app-openid-error-header (request :get (str "/api/xw/list/app"))))
          result (m/decode-response-body response)]
      (is (get-in result [:msg]))))

  (testing "测试wx-app-appid"
    (let [response ((app) (add-app-appid-header (request :get (str "/api/public/xw/list/app"))))
          result (m/decode-response-body response)]
      (is (= 200 (:status response)))
      (is (get-in result [:msg]))))

  (testing "测试wx-app-openid-error"
    (let [response ((app) (add-app-appid-error-header (request :get (str "/api/public/xw/list/app"))))
          result (m/decode-response-body response)]
      (is (get-in result [:msg]))))
  )
