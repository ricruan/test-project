
-- :name create-user! :! :n
-- :doc creates a new demo user record
INSERT INTO demo_user
(id, first_name, last_name, email, pass, remark)
VALUES (:id, :first-name, :last-name, :email, :pass, :remark)

-- :name update-user! :! :n
-- :doc updates an existing user record
UPDATE demo-user
SET first_name = :first-name, last_name = :last-name, email = :email
WHERE id = :id

-- :name get-user :? :1
-- :doc retrieves a user record given the id
SELECT * FROM demo_user
WHERE id = :id

-- :name delete-user! :! :n
-- :doc deletes a user record given the id
DELETE FROM demo_user
WHERE id = :id


-- :name search-user :? :*
-- :doc like模糊查找用户
SELECT * FROM `demo_user` u
WHERE 1=1
--~ (if (and (:name params) (not= (:name params) "")) (str "AND (u.`first_name`  LIKE '%" (:name params) "%' OR u.`last_name` LIKE  :last-name )") "AND 1=1")
--~ (if (and (:email params) (not= (:email params) "")) "AND u.`email` = :email" "AND 1=1")


-- :name search-users-page-in-email :? :*
-- :doc 分页查询
SELECT 
/*~ (if (:count params) */
  count(*) AS 'total-elements'
/*~*/
  *
/*~ ) ~*/
FROM `demo_user` u
WHERE 
--~ (if (and (:email params) (not= (:email params) "")) "u.`email` in (:v*:email)" "1=1")
ORDER BY u.`first_name`
--~ (if (:count params) ";" "LIMIT :page, :size ;")


-- :name batch-create-user! :! :n
-- :doc 批量插入
INSERT INTO `demo_user`
(`id`, `first_name`, `last_name`, `email`, `pass`, `admin`, `is_active`)
VALUES :t*:records

