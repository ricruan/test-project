(ns test-project.common.utils-format-test
  (:require [test-project.common.utils :as sut]
            [test-project.common.utils-format :as sut-f]
            [clojure.test :refer [deftest testing use-fixtures is]]))


(deftest utils-format-test
  (testing "测试封装tree"
    (is (sut-f/trans-muilti-tree {:total 0
                                  :list [{:name "name" :id 0 :parent-id 1}]}
                                 3
                                 {}))))
