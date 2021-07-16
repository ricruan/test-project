-- :name insert-sys-role! :! :n
-- :保存系统用户
insert into t_sys_role
(id, name, code, create_time, delete_flag)
values
(:id, :name, :code, now(), 0)

-- :name upate-sys-role! :! :n
-- :doc 更新系统角色
update t_sys_role set
name = :name,
code = :code
where id = :id

-- :name find-sys-role-total :? :1
-- :doc
select count(*) as total
from t_sys_role
where delete_flag = 0
--~(if (not (empty? (:name params))) (str " and name like '%" (:name params) "%'") " and 1=1")
--~(if (not (empty? (:code params))) (str " and code like '%" (:code params) "%'") " and 1=1")

-- :name find-sys-roles :? :*
-- :doc 查询系统角色列表
select *
from t_sys_role
where delete_flag = 0
--~(if (not (empty? (:name params))) (str " and name like '%" (:name params) "%'") " and 1=1")
--~(if (not (empty? (:code params))) (str " and code like '%" (:code params) "%'") " and 1=1")
order by create_time desc
--~(if (and (:page params) (:size params)) (str " LIMIT " (* (:page params) (:size params)) "," ":size") ";")

-- :name find-sys-role :? :1
-- :doc 获取系统角色
select * from t_sys_role where delete_flag = 0 and id = :role_id


-- :name remove-sys-role! :! :n
-- :doc 逻辑删除系统角色
update t_sys_role
set delete_flag = 1
where id = :role_id

-- :name insert-user-role! :! :n
-- :doc 用户分配角色
insert into t_sys_user_role(id, user_id, role_id)
values :tuple*:rows

-- :name remove-user-roles! :! :n
-- :doc
delete from t_sys_user_role where user_id = :user_id

-- :name find-user-roles :? :*
-- :doc 查询用户角色
SELECT r.* from t_sys_user u, t_sys_role r, t_sys_user_role ur
where ur.role_id = r.id and u.id = ur.user_id and u.id= :user_id
