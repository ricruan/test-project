(ns test-project.modules.sys.sys-menu-service
  (:require
   [clojure.string :as string]
   [conman.core :as conman]
   [test-project.db.core :refer [*db*]]
   [test-project.common.utils :as utils]
   [test-project.modules.sys.sys-user-db :as db]
   [com.rpl.specter :as s]
   [test-project.common.utils-format :as uf]))


(defn save-sys-menu
  "保存系统菜单"
  [params]
  (db/insert-sys-menu!
   (assoc params :id (utils/snowflake-id))))

(defn update-sys-menu
  "更新系统菜单"
  [{:keys [id] :as params}]
  (db/upate-sys-menu! params))

(defn get-sys-menus-cascader
  "
  获取系统菜单选择级联数据
  返回二级菜单树形结构
  符合antd cascader显示需求
  "
  []
  (->> (db/find-sys-menus)
       (s/transform [s/ALL :create_time] #(utils/get-timestamp %))
       (s/setval [s/ALL s/MAP-KEYS #(= :name %)] :label)
       (s/setval [s/ALL s/MAP-KEYS #(= :id %)] :value)
       (#(uf/trans-muilti-tree {:total 0
                                :list %}
                               2
                               {:group :parent_id
                                :root "0"
                                :value :value
                                :label :label}))
       (#(s/setval [s/ALL :children] (:list %) [{:label "根菜单" :value "0" :children nil}]))))

(defn remove-sys-menu
  "删除系统菜单"
  [menu_id]
  (db/remove-sys-menu! {:menu_id menu_id}))

(defn get-sys-menus
  "所有系统菜单"
  [query]
  (->> (db/find-sys-menus query)
       (s/transform [s/ALL :create_time] #(utils/get-timestamp %))
       (#(uf/trans-muilti-tree {:total 0
                                :list %}
                               3
                               {:group :parent_id
                                :root "0"
                                :value :id
                                :label :name} ))))

(defn assign-role-menu
  "角色分配菜单"
  [role_id menu_ids]
  (conman/with-transaction [*db*]
    (db/remove-role-menus! {:role_id role_id})
    (when (seq menu_ids)
      (db/insert-role-menus!
       {:rows
        (map #(vector (utils/uuid) role_id %) menu_ids)}))))

(defn get-role-menus
  "获取角色菜单"
  [role_id]
  (let [role-menus (db/find-role-menus {:role_id role_id})]
    (->> role-menus
         (s/transform [s/ALL :selected] #(if (nil? (seq %)) false true)))))
