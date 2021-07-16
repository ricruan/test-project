-- :name find-log-by-condition :? :*
-- :doc 条件查询
select *
from `db-log`
where 1=1
--~(when (seq (:start_time params)) (str " and start_time >=  '" (:start_time params) "'"))
--~(when (seq (:end_time params)) (str " and end_time <=  '" (:end_time params) "'"))
--~(when (seq (:db_server_url params)) (str " and db_server_url like '%" (:db_server_url params) "%'"))
--~(if (and (:page params) (:size params)) (str " LIMIT " (* (:page params) (:size params)) "," ":size") ";")

-- :name find-log-counts :? :1
-- :doc 查询记录
select count(1) as total
from `db-log`
where 1=1
--~(when (seq (:start_time params)) (str " and start_time >= '" (:start_time params) "'"))
--~(when (seq (:end_time params)) (str " and end_time <= '" (:end_time params) "'"))
--~(when (seq (:db_server_url params)) (str " and db_server_url like '%" (:db_server_url params) "%'"))