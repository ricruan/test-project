--:name find-user-base-data :? :1
--:doc获取系统当前使用者基础数据
SELECT
    id,
	nickname
FROM
	t_sys_user
WHERE
	id = :id
	AND delete_flag = FALSE
