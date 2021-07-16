(ns test-project.common.biz-error-test
  (:require [test-project.common.biz-error :as sut]
            [test-project.common.result :refer [ok]]
            [clojure.test :refer [deftest testing use-fixtures is]]))

(deftest biz-error-test
  (testing "抛出系统错误"
    (let [res (try
                (sut/throw-error 1000 "测试错误")
                (catch Exception e
                  (.data e)))]
      (is (get-in res [:value])))))
