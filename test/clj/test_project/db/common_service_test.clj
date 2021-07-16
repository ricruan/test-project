(ns test-project.db.common-service-test
  (:require [test-project.db.common-service :as sut]
            [clojure.test :refer [deftest testing use-fixtures is]]))


(deftest  common-servcie-test
  (testing "sql"
    (is (sut/insert-table! "t_sys_role" {:id 3 :name "测试2" :code "admin3" :delete_flag 0}))
    (is (sut/insert-or-update-table! "t_sys_role" :id {:id 3 :name "测试3" :code "admin3" :delete_flag 0}))
    (is (sut/batch-insert-or-update-table! "t_sys_role" [{:id 4 :name "测试3" :code "admin3" :delete_flag 0}]))
    (is (sut/batch-insert-or-update-table! "t_sys_role" :id  [{:id 4 :name "测试3" :code "admin3" :delete_flag 0}]))
    (is (sut/complete-id!  :id {:name "name"}))
    (is (sut/complete-id!  :id {:name "name" :id "1"}))
    (is  (sut/complete-then-batch-insert-or-update! {:table-name "t_sys_role" :id-key ":id" :entities {:name "测试3" :code "admin3" :delete_flag 0}}))
    (is (sut/complete-auto-id-then-batch-insert-or-update! {:table-name "t_sys_role" :id-key ":id" :entities  {:id 4 :name "测试3" :code "admin3" :delete_flag 0}}))
    (is (sut/complete-id-then-batch-insert-or-update! {:table-name "t_sys_role" :id-key ":id" :entities {:name "测试3" :code "admin3" :delete_flag 0}} ))
    ))
