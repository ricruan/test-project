(ns test-project.common.encrypt-test
  (:require [test-project.common.encrypt :as sut]
            [clojure.test :refer [deftest testing use-fixtures is]]))

(deftest encrypt-test
  (testing "match?"
    (is (sut/match? "admin.123" "bd92ff7cffc281d94e657b5dd707a7721e62124cd01537f0944a4e5329fcab57")))
  )
