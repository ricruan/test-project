(ns test-project.system.role.sys-role-events
  (:require
   [kee-frame.core :as kf]
   [re-frame.core :as rf]
   [test-project.url :refer [sys-url]]
   [com.rpl.specter :as s]
   [test-project.common.utils :as u]))

(kf/reg-controller
  ::list-controller
  {:params (fn [route]
             (when (-> route :path-params :path (= "/system/role")) true))
   :start [:system-role/fetch-list nil]})

(kf/reg-event-fx
  :system-role/fetch-list
  (fn [{:keys [db]} [query]]
    (let [db-query (get-in db [:system-role :role-table :query] {})
          params (if query (merge db-query query) {:page 0 :size 10})]
      {:db (update-in db [:system-role :role-table]
                      #(assoc % :loading true :query params))
       :dispatch [:request/get {:url (:role-list sys-url)
                                :params params
                                :callback-event :system-role/fetch-list-success}]})))

(kf/reg-event-fx
  :system-role/fetch-list-success
  (fn [{:keys [db]} [data]]
    {:db (update-in db [:system-role :role-table]
                    #(assoc
                       %
                       :loading false
                       :data (:list data)
                       :pagination {:total (:total data)
                                    :pageSize (:size data)
                                    :current (inc (:page data))}))}))

(kf/reg-event-fx
  :system-role/update
  (fn [{:keys [db]} [params]]
    (let [url (if (:id params) (:role-update sys-url) (:role-add sys-url))
          event (if (:id params) :system-role/update-success :system-role/add-success)]
      {:dispatch [:request/post {:url url :params params :callback-event event}]})))

(kf/reg-event-fx
  :system-role/add-success
  (fn [{:keys [db]} [data]]
    {:db (assoc-in db [:system-role :role-form] {})
     :dispatch [:system-role/fetch-list nil]
     :msg "添加成功"}))

(kf/reg-event-fx
  :system-role/update-success
  (fn [{:keys [db]} [data]]
    (let [query (get-in db [:system-role :role-table :query])]
      {:db (assoc-in db [:system-role :role-form] {})
       :dispatch [:system-role/fetch-list query]
       :msg "修改成功"})))

(rf/reg-event-db
  :system-role/role-form-close
  (fn [db _]
    (assoc-in db [:system-role :role-form] {})))

(rf/reg-event-db
  :system-role/role-form-open
  (fn [db [_ data]]
    (update-in db [:system-role :role-form] #(assoc % :visible true :data data))))

(kf/reg-event-fx
  :system-role/remove
  (fn [_ [id]]
    {:dispatch [:request/delete {:url ((:role-remove sys-url) id)
                                 :callback-event :system-role/remove-success}]}))

(kf/reg-event-fx
  :system-role/remove-success
  (fn [{:keys [db]} _]
    (let [query (get-in db [:system-role :role-table :query])]
      {:dispatch [:system-role/fetch-list query]
       :msg "删除成功"})))

(rf/reg-event-fx
  :system-role/assign-form-open
  (fn [{:keys [db]} [_ role]]
    {:db (update-in
           db
           [:system-role :assign-form]
           #(assoc % :visible true :loading true :role (select-keys role [:id :name])))
     :dispatch [:request/get {:url ((:role-menus sys-url) (:id role))
                              :callback-event :system-role/change-assign-data}]}))

(defn- find-root-menus
  "查找父菜单"
  [menus]
  (sort-by :sort (filter #(= (:parent_id %) "0") menus)))

(defn- find-children-menus
  "查找子菜单"
  [menus]
  (sort-by :sort (filter #(not= (:parent_id %) "0") menus)))

(defn- find-parent-children
  "获取子菜单"
  [parent menus]
  (sort-by :sort (filter #(= (:parent_id %) (:id parent)) menus)))

(defn- find-selected-menus
  "查询选中的菜单"
  [menus]
  (filter #(true? (:selected %)) menus))

(defn- find-no-children
  "获取没有子菜单的"
  [menus]
  (filter #(nil? (seq (find-parent-children % menus))) menus))

(defn- to-tree-data
  "转为树结构"
  [menus]
  (->> (u/trans-muilti-tree {:total 0
                             :list menus}
                            3
                            {:group :parent_id
                             :root "0"
                             :value :id
                             :label :name})
       :list
       (#(sort-by :sort %))))

(defn- trans-select-menus-to-comp-need
  "
  将菜单数据的选中状态转成AntD Tree控件需要的
  即将子节点未选中时的父节点选中状态改为false
  适应多级树形结构
  "
  [menus]
  (let [simple-menus (mapv #(select-keys % [:id :parent_id :selected]) menus)
        menus-map-by-id (group-by :id simple-menus)
        child-not-select-menus (s/select [s/MAP-VALS s/ALL #(= false (:selected %)) :parent_id]
                                         (group-by :parent_id simple-menus))]
    (->> (reduce (fn [ret id]
                   (if (= "0" (or id "0"))
                     ret
                     (recur (assoc-in ret [id 0 :selected] false)
                            (get-in ret [id 0 :parent_id]))))
                 menus-map-by-id
                 child-not-select-menus)
         (s/select [s/MAP-VALS s/ALL]))))

(defn- find-selected
  "
  获取选中项
  "
  [menus]
  (s/select [s/ALL #(:selected %) :id] (trans-select-menus-to-comp-need menus)))

(rf/reg-event-db
 :system-role/change-assign-data
 (fn [db [_ menus]]
   (let [select-menus (find-selected menus)
         expand-menus (s/select [s/MAP-VALS s/ALL #(:selected %) :id]
                                (group-by :parent_id menus))
         tree (to-tree-data menus)
         check-all? (= (count (find-selected-menus menus)) (count menus))]
     (update-in
      db
      [:system-role :assign-form]
      #(assoc
        %
        :loading false
        :check-all? check-all?
        :menus menus
        :tree tree
        :checked-keys select-menus
        :expand-keys expand-menus)))))

(rf/reg-event-db
  :system-role/on-expand
  (fn [db [_ keys]]
    (assoc-in db [:system-role :assign-form :expand-keys] keys)))

(rf/reg-event-db
  :system-role/on-checked
  (fn [db [_  keys]]
    (let [menus (get-in db [:system-role :assign-form :menus])]
      (update-in
        db
        [:system-role :assign-form]
        #(assoc % :checked-keys keys :check-all? (= (count keys) (count menus)))))))

(rf/reg-event-db
 :system-role/on-checked-all
 (fn [db [_ checked]]
   (let [keys (if checked
                {:checked (map :id (get-in db [:system-role :assign-form :menus]))}
                [])]
     (update-in
      db
      [:system-role :assign-form]
      #(assoc % :checked-keys keys :check-all? checked)))))


(defn find-all-keys
  [menus keys]
  (let [parent-ids (map
                    :parent_id
                    (filter
                     #(some (fn [k] (= (:id %) k)) keys)
                     (filter #(not= (:parent_id %) "0") menus)))]
    (into [] (set (into keys parent-ids)))))

(rf/reg-event-fx
 :system-role/assign-menus
 (fn [{:keys [db]} _]
   (let [form (get-in db [:system-role :assign-form])
         role_id (get-in form [:role :id])
         menu_ids (find-all-keys (:menus form) (:checked (:checked-keys form)))]
     {:dispatch [:request/post
                 {:url (:menu-assign sys-url)
                  :params {:role_id role_id :menu_ids menu_ids}
                  :callback-event :system-role/assign-menus-success}]})))

(kf/reg-event-fx
  :system-role/assign-menus-success
  (fn [{:keys [db]} [data]]
    (let [query (get-in db [:system-role :role-table :query])]
      {:db (assoc-in db [:system-role :assign-form] {})
       :dispatch [:system-role/fetch-list query]
       :msg "分配成功"})))

(rf/reg-event-db
  :system-role/assign-form-close
  (fn [db _]
    (assoc-in db [:system-role :assign-form] {})))

(rf/reg-sub
  :system-role/role-table
  (fn [data]
    (get-in data [:system-role :role-table])))

(rf/reg-sub
  :system-role/role-form
  (fn [data]
    (get-in data [:system-role :role-form] {})))

(rf/reg-sub
  :system-role/assign-form
  (fn [data]
    (get-in data [:system-role :assign-form])))
