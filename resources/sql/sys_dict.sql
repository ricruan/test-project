-- :name get-dict-list :? :*
-- :doc 查询字典列表
SELECT
--~ (if (:count params) "count(*) AS 'total-elements'" "*")
FROM t_sys_dict
WHERE type = 0 AND deleted=0
--~ (if (:name params) (str "AND name like '%" (:name params) "%'") "AND 1=1")
--~ (if (:count params) ";" "LIMIT :page, :size ")

-- :name post-dict-list!  :! :n
-- :doc 添加字典列表
INSERT INTO t_sys_dict (`code`, `name`, parent_id, type, group_code
	, sort, deleted, create_time)
VALUES (:code, :name, 0, 0, :group_code
	, 0, 0, NOW());

-- :name delete-dict-list!  :! :n
-- :doc 删除字典列表
UPDATE t_sys_dict
SET deleted = 1, update_time = NOW()
WHERE id = :id or parent_id=:id;

-- :name update-dict-list!  :! :n
-- :doc 修改字典列表
UPDATE t_sys_dict
SET `name` = :name, `code` = :code, group_code = :group_code, update_time = NOW()
WHERE id = :id;

-- :name get-dict-details :? :*
-- :doc 查询查询字典详情
SELECT
--~ (if (:count params) "count(*) AS 'total-elements'" "*")
FROM t_sys_dict
WHERE type = 1 AND parent_id=:parent_id AND deleted=0
--~ (if (:name params) (str "AND name like '%" (:name params) "%'") "AND 1=1")
ORDER BY sort
--~ (if (:count params) ";" "LIMIT :page, :size ")

-- :name post-dict-details!  :! :n
-- :doc 添加字典详情数据
INSERT INTO t_sys_dict (`code`, `name`, parent_id, type, sort
	, deleted, create_time)
SELECT :code, :name, :parent_id, 1
	, IFNULL( MAX(sort) + 1,1), 0
	, NOW()
FROM t_sys_dict
WHERE parent_id = :parent_id;

-- :name update-dict-details!  :! :n
-- :doc 修改字典详情列表
UPDATE t_sys_dict
SET `name` = :name, `code` = :code, parent_id=:parent_id, sort=:sort, update_time = NOW()
WHERE id = :id;
