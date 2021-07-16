(ns test-project.modules.sys.sys-role-service
  (:require
   [clojure.string :as string]
   [conman.core :as conman]
   [test-project.db.core :refer [*db*]]
   [test-project.common.utils :as utils]
   [test-project.modules.sys.sys-user-db :as db]))

(defn save-sys-role
  "保存系统角色"
  [params]
  (db/insert-sys-role!
    (merge
      params
      {:id (utils/snowflake-id)})))

(defn update-sys-role
  "更新系统角色"
  [{:keys [id] :as params}]
  (db/upate-sys-role! params))

(defn remove-sys-role
  "删除系统角色"
  [role_id user]
  (when-let [db-role (db/find-sys-role {:role_id role_id})]
    (db/remove-sys-role! {:role_id role_id})))

(defn sys-role-page
  "系统角色分页数据"
  [{:keys [page size] :as query}]
  {:page page
   :size size
   :list (db/find-sys-roles query)
   :total (:total (db/find-sys-role-total query))})


(defn assign-user-role
  "用户分配角色"
  [user_id role_ids]
  (conman/with-transaction [*db*]
    (db/remove-user-roles! {:user_id user_id})
    (when (seq role_ids)
      (db/insert-user-role!
       {:rows
        (map #(vector (utils/uuid) user_id %) role_ids)}))))

(defn get-user-roles
  "获取用户角色"
  [user_id]
  (let [all-roles (db/find-sys-roles)
        user-roles (db/find-user-roles {:user_id user_id})]
    (map
     #(select-keys % [:id :name :code :selected])
     (map
      (fn [role]
        (assoc role :selected (not (nil? (some #(= (:id role) (:id %))  user-roles)))))
      all-roles))))
