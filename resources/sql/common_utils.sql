-- :name find-table-info :? :1
-- :doc 查询详情
SELECT * FROM :i:table-name
WHERE :i:id-name = :id AND delete_flag = '0'


-- :name find-table-infos-by-field :? :*
-- :doc 查询详情根据字段值
SELECT * FROM :i:table-name
WHERE :i:field-name = :value AND delete_flag = '0'
--~(when (and (:field2-name params) (:value2 params)) "AND :i:field2-name = :value2")
--~(when (and (:page params) (:size params)) (str " LIMIT " (* (:page params) (:size params)) "," ":size"))

-- :name find-table-infos-by-field-even-deleted :? :*
-- :doc 查询详情根据字段值
SELECT * FROM :i:table-name
WHERE :i:field-name = :value
--~(when (and (:field2-name params) (:value2 params)) "AND :i:field2-name = :value2")

-- :name find-table-infos :? :*
-- :doc 查询列表, 查询指定字段，多个条件
/* :require [clojure.string :as string]
            [hugsql.parameters :refer [identifier-param-quote]] */
SELECT
/*~ (if (seq (:cols params)) */
:i*:cols
/*~*/
*
/*~ ) ~*/
FROM :i:table-name
WHERE
/*~
(string/join " AND "
  (for [[field _] (:conds params)]
    (str (identifier-param-quote (name field) options)
      " = :v:conds." (name field))))
~*/
--~(when (and (:page params) (:size params)) (str " LIMIT " (* (:page params) (:size params)) "," ":size"))


-- :name count-table-infos :? :1
-- :doc 查询列表, 查询指定字段，多个条件
/* :require [clojure.string :as string]
            [hugsql.parameters :refer [identifier-param-quote]] */
SELECT
count(1) as total
FROM :i:table-name
WHERE
/*~
(string/join " AND "
  (for [[field _] (:conds params)]
    (str (identifier-param-quote (name field) options)
      " = :v:conds." (name field))))
~*/


-- :name find-table-infos-complex :? :*
-- :doc 查询列表, 查询指定字段，多个条件
/* :require [clojure.string :as string]
            [hugsql.parameters :refer [identifier-param-quote]] */
SELECT
/*~ (if (seq (:cols params)) */
:i*:cols
/*~*/
*
/*~ ) ~*/
FROM :i:table-name
WHERE
/*~
(string/join " AND "
  (for [[field v] (:conds params)]
    (str (identifier-param-quote (name field) options)
      " " (or (:symbol v) "=") " "
      (if (contains? #{"in" "IN"} (:symbol v))
        (str "(:v*:conds." (name field) ".v)")
        (str ":v:conds." (name field) ".v")))))
~*/
--~(when (and (:page params) (:size params)) (str " LIMIT " (* (:page params) (:size params)) "," ":size"))

-- :name insert-into-table! :! :1
-- :doc 新增
INSERT INTO :i:table-name
(:i*:cols)
VALUES
(:v*:info)

-- :name batch-insert-into-table! :! :n
-- :doc 新增
INSERT INTO :i:table-name
(:i*:cols)
VALUES
:t*:records

-- :name update-table-info! :! :1
-- :doc 修改
/* :require [clojure.string :as string]
            [hugsql.parameters :refer [identifier-param-quote]] */
UPDATE :i:table-name
SET
/*~
(string/join ","
  (for [[field _] (:updates params)]
    (str (identifier-param-quote (name field) options)
      " = :v:updates." (name field))))
~*/
WHERE :i:id-name = :id

-- :name insert-or-update-table-info! :! :1
-- :doc 新增或修改
/* :require [clojure.string :as string]
            [hugsql.parameters :refer [identifier-param-quote]] */
INSERT INTO :i:table-name
(:i*:cols)
VALUES
(:v*:info)
ON DUPLICATE KEY
UPDATE
/*~
(string/join ","
  (for [[field _] (:updates params)]
    (str (identifier-param-quote (name field) options)
      " = :v:updates." (name field))))
~*/

-- :name batch-insert-or-update! :! :n
-- :doc 新增或修改
/* :require [clojure.string :as string]
            [hugsql.parameters :refer [identifier-param-quote]] */
INSERT INTO :i:table-name
(:i*:cols)
VALUES
:t*:records
ON DUPLICATE KEY
UPDATE
/*~
(string/join ","
  (for [field (:cols params)]
    (str (identifier-param-quote field options)
      " = VALUES(" field ")")))
~*/

-- :name delete-table-info! :! :1
-- :doc 删除信息
DELETE FROM :i:table-name
WHERE :i:id-name = :id

-- :name batch-delete-table-infos! :! :n
-- :doc 批量删除信息
DELETE FROM :i:table-name
WHERE :i:id-name in :v*:ids

-- :name logic-delete-table-info! :! :1
-- :doc 逻辑删除信息
UPDATE :i:table-name
SET delete_flag = '1'
WHERE :i:id-name = :id

-- :name batch-logic-delete! :! :n
-- :doc 批量删除
UPDATE :i:table-name
SET delete_flag = '1'
WHERE :i:id-name in (:v*:ids)

-- :name delete-all! :! :n
-- :doc 删除该表所有数据
DELETE FROM :i:table-name
