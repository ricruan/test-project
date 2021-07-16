(ns test-project.common.utils-test
  (:require [test-project.common.utils :as sut]
            [clj-time.core :as tt]
            [clojure.test :refer [deftest testing use-fixtures is]])
  (:import [java.time  LocalDateTime]))


(deftest utils-test
  (testing "测试uuid"
    (is (sut/generate-db-id)))

  (testing "测试时间戳格式"
    (is (System/currentTimeMillis))
    (is (sut/format-date 1610434143236))
    (is (sut/format-time 1610434143236))
    (is (sut/format-date-time 1610434143236)))

  (testing "获取时间戳"
    (is (sut/get-timestamp (LocalDateTime/now)))
    )
  
  (testing "测试取map->name"
    (is (sut/trans-keyname-cols {:a "1" :b "2"})))

  (testing "测试类型转换"
    (is (sut/parse-int "123"))
    (is (sut/parse-double "1.1"))
    (is (sut/parse-float "1.1")))

  (testing "测试加密"
    (is (sut/get-str-md5 "123"))
    (is (sut/encode-base64 "123"))
    (is (sut/encode-base64-string "123")))

  (testing "测试解密"
    (is (sut/decode-base64 "MTIz"))
    (is (sut/decode-base64-byte "MTIz")))

  (testing "测试返回结果data进行处理"
    (is (sut/group-data-by-keys  [{:name "name" :id 1 :hh "hh"}] [:name]))
    (is (sut/group-data-by-keys  [{:name "name" :id 1 :hh "hh"}] [:name] :a (fn [v e] (+ v (:id e))) 0))
    (is (sut/group-data-by-keys  [{:name "name" :id 1 :hh "hh"}] [:name] :a (fn [v e] (+ v (:id e))) 0 :b (fn [v e] (+ v (:id e))) 0))
    (is (sut/list-from-group-by {:y 1 :a [{:b 2} {:b 3}]}))
    )
  )

