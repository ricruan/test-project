(ns test-project.modules.log.log-specs
  (:require  [clojure.test :as t]
             [clojure.spec.alpha :as s]
             [spec-tools.core :as st]
             [spec-tools.data-spec :as dt]
             [test-project.modules.base.base-specs :as base-specs]
             ))


(s/def ::db_log_id
  (st/spec
    {:spec (s/nilable any?)
     :swagger/default "0"
     :description "数据库备份日志ID"}))

(s/def ::db_server_url
  (st/spec
    {:spec (s/nilable string?)
     :swagger/default "192.168.1.1"
     :description "数据库服务地址"}))

(s/def ::backup_path
  (st/spec
    {:spec (s/nilable string?)
     :swagger/default "1234"
     :description "备份文件路径"}))

(s/def ::db_detail_name
  (st/spec
    {:spec (s/nilable string?)
     :swagger/default "sdc"
     :description "数据库详情名称"}))

(s/def ::db_detail_size
  (st/spec
    {:spec (s/nilable vector?)
     :swagger/default "sdc"
     :description "数据库详情大小"}))

(s/def ::start_time
  (st/spec
    {:spec (s/nilable string?)
     :swagger/default "2000-01-01 00:00:00"
     :description "开始时间"}))

(s/def ::end_time
  (st/spec
    {:spec (s/nilable string?)
     :swagger/default "2030-01-01 00:00:00"
     :description "结束时间"}))

(s/def ::create_time
  (st/spec
    {:spec (s/nilable string?)
     :swagger/default "2030-01-01 00:00:00"
     :description "创建时间"}))

(s/def ::page
  (st/spec
    {:spec int?
     :swagger/default 1
     :description "页码"}))

(s/def ::size
  (st/spec
    {:spec int?
     :swagger/default 10
     :description "页面最多数据数"}))

(s/def ::total_data
  (st/spec
    {:spec int?
     :swagger/default 0
     :description "查询结果总条数"}))




;;db_detail
(s/def ::log-detail (s/coll-of (s/keys :req-un [::db_detail_name ::db_detail_size] )))


(s/def ::log-list (s/keys :req-un [::db_log_id ::db_server_url ::backup_path ::log-detail ::start_time ::end_time ::create_time]))

;;log_request_body
(s/def ::log-query-spec (s/keys :opt-un [::start_time ::end_time ::db_server_url ::page ::size] ))

;;log_data
(s/def ::list (s/coll-of (s/keys :req-un [::db_log_id ::db_server_url ::backup_path ::log-detail ::start_time ::end_time ::create_time])))
(s/def ::data (s/keys :req-un [::total_data ::log-list ::page ::size]))

;;log Response Body
(s/def ::log-body (s/keys :req-un [:base/code :base/msg ::data]))









