-- :name insert-sys-user! :! :n
-- :保存系统用户
insert into t_sys_user
(id, username, password, nickname, create_user_id, update_user_id, create_time, update_time, delete_flag)
values
(:id, :username, :password, :nickname, :create-user-id, :create-user-id, now(), now(), 0)

-- :name upate-sys-user! :! :n
-- :doc 更新系统用户
update t_sys_user set
username = :username,
nickname = :nickname,
update_user_id = :update-user-id,
update_time = now()
where id = :id

-- :name update-sys-user-password! :! :n
-- :doc 修改密码
update t_sys_user
set password = :password
where id = :id

-- :name remove-sys-user! :! :n
-- :doc 逻辑删除系统用户
update t_sys_user
set delete_flag = 1
where id = :id


-- :name find-sys-user :? :1
-- :doc 查询系统用户
select *, id as user_id from t_sys_user
where delete_flag = 0
--~(if (:id params) " and id = :id" " and 1=1")
--~(if (:not-id params) " and id != :not-id" " and 1=1")
--~(if (:password params) " and password = :password" " and 1=1")
--~(if (:username params) " and username = :username" " and 1=1")

-- :name find-sys-user-total :? :1
-- :doc
select count(*) as total
from t_sys_user
where delete_flag = 0
--~(if (not (empty? (:username params))) (str " and username like '%" (:username params) "%'") " and 1=1")
--~(if (not (empty? (:nickname params))) (str " and nickname like '%" (:nickname params) "%'") " and 1=1")

-- :name find-sys-users :? :*
-- :doc 查询系统用户列表
select *
,unix_timestamp(create_time) as create_timestamp
,unix_timestamp(update_time) as update_timestamp
from t_sys_user
where delete_flag = 0
--~(if (not (empty? (:username params))) (str " and username like '%" (:username params) "%'") " and 1=1")
--~(if (not (empty? (:nickname params))) (str " and nickname like '%" (:nickname params) "%'") " and 1=1")
order by create_time desc
--~(if (and (:page params) (:size params)) (str " LIMIT " (* (:page params) (:size params)) "," ":size") ";")



-- :name find-user-menus :? :*
-- :获取用户菜单
SELECT m.* from t_sys_user u, t_sys_user_role ur, t_sys_role r, t_sys_role_menu rm, t_sys_menu m
WHERE u.delete_flag = 0 and r.delete_flag = 0 AND m.delete_flag = 0 and
u.id = ur.user_id and ur.role_id = r.id and r.id = rm.role_id and rm.menu_id = m.id and u.id = :user_id GROUP BY m.id

--:name find-user-by-password :? :1
--:doc通过id和密码查询是否存在该用户
SELECT
	count(*) AS total
FROM
	t_sys_user
WHERE
	delete_flag = FALSE
	AND id = :id
	AND `password` = :password
