(ns test-project.common.result-test
  (:require [test-project.common.result :as sut]
            [clojure.test :refer [deftest testing use-fixtures is]]))



(deftest result-test
  (testing "操作失败测试"
    (is (sut/sorry 0)))

  (testing "unauthorized-401"
    (is (sut/unauthorized "token错误")))

  )
