(ns test-project.modules.sys.sys-user-service
  (:require
   [clojure.string :as string]
   [test-project.common.utils :as utils]
   [test-project.common.encrypt :as encrypt]
   [test-project.modules.sys.sys-user-db :as db]))

;;用户名是否已经注册
(defn reg-username? [{:keys [username id]}]
  (not (nil? (db/find-sys-user {:not-id id :username username}))))

;;保存系统用户
(defn save-sys-user [params user]
  (db/insert-sys-user!
   (merge
    params
    {:id (utils/snowflake-id)
     :create-user-id (:user_id user)
     :password (encrypt/encode (:password params))})))

;;更新系统用户
(defn update-sys-user [{:keys [id] :as params} user]
  (when-let [db-user (db/find-sys-user {:id id})]
    (db/upate-sys-user!
     (merge
      params
      {:update-user-id (:user_id user)}))))

;;修改密码
(defn update-password [{:keys [id password] :as params} user]
  (when-let [db-user (db/find-sys-user {:id id})]
    (db/update-sys-user-password! {:id id
                                   :password (encrypt/encode password)})))

;;当前使用者修改密码
(defn update-user-password [{:keys [id old_password password] :as params} user]
  (when-let [db-user (db/find-user-by-password {:id       id
                                                :password (encrypt/encode old_password)})]
    (if (= (:total db-user) 1)
      (update-password params user)
      "error")))

;;删除系统用户
(defn remove-sys-user [id user]
  (when-let [db-user (db/find-sys-user {:id id})]
    (db/remove-sys-user! {:id id})))

;;系统用户分页列表
(defn sys-user-page [{:keys [page size] :as query}]
  {:page page
   :size size
   :list (db/find-sys-users query)
   :total (:total (db/find-sys-user-total query))})

(defn get-current-user-menus
  "获取当前用户菜单"
  [user]
  (let [menu-list (db/find-user-menus (select-keys user [:user_id]))]
    (map #(select-keys % [:id :parent_id :name :code :path :icon :sort :menu_type])  menu-list)))
