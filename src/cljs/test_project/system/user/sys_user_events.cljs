(ns test-project.system.user.sys-user-events
  (:require
    [ajax.core :as http]
    [kee-frame.core :as kf]
    [re-frame.core :as rf]
    [test-project.url :refer [sys-url]]))

(def default-form
  {:visible false
   :data {}
   :loading false
   :submit-loading false})

(kf/reg-controller
  :system-user/user-list-controller
  {:params (fn [route]
             (when (-> route :path-params :path (= "/system/user")) true))
   :start [:system-user/fetch-list nil]})

(kf/reg-event-fx
  :system-user/fetch-list
  (fn [{:keys [db]} [query]]
    (let [db-query (get-in db [:system-user :user-table :query] {})
          params (if query (merge db-query query) {:page 0 :size 10})]
      {:db (update-in db [:system-user :user-table]
                      #(assoc % :loading true :query params))
       :dispatch [:request/get {:url (:user-list sys-url)
                                :params params
                                :callback-event :system-user/fetch-list-success}]})))

(kf/reg-event-fx
  :system-user/fetch-list-success
  (fn [{:keys [db]} [data]]
    {:db (update-in db [:system-user :user-table]
                    #(assoc
                       %
                       :loading false
                       :data (:list data)
                       :pagination {:total (:total data)
                                    :pageSize (:size data)
                                    :current (inc (:page data))}))}))

(kf/reg-event-fx
  :system-user/update
  (fn [{:keys [db]} [params]]
    (let [url (if (:id params) (:user-update sys-url) (:user-add sys-url))
          event (if (:id params) :system-user/update-success :system-user/add-success)]
      {:dispatch [:request/post {:url url :params params :callback-event event}]})))

(kf/reg-event-fx
  :system-user/add-success
  (fn [{:keys [db]} [data]]
    {:db (assoc-in db [:system-user :user-form] default-form)
     :dispatch [:system-user/fetch-list nil]
     :msg "添加成功"}))

(kf/reg-event-fx
  :system-user/update-success
  (fn [{:keys [db]} [data]]
    (let [query (get-in db [:system-user :user-table :query])]
      {:db (assoc-in db [:system-user :user-form] default-form)
       :dispatch [:system-user/fetch-list query]
       :msg "更新成功"})))

(rf/reg-event-db
  :system-user/user-form-close
  (fn [db _]
    (assoc-in db [:system-user :user-form] default-form)))

(rf/reg-event-db
  :system-user/user-form-open
  (fn [db [_ data]]
    (update-in db [:system-user :user-form] #(assoc % :visible true :data data))))

(rf/reg-event-db
  :system-user/password-form-open
  (fn [db [_ data]]
    (update-in db [:system-user :password-form] #(merge % {:visible true :data data}))))

(rf/reg-event-db
  :system-user/password-form-close
  (fn [db [_ data]]
    (assoc-in db [:system-user :password-form] default-form)))

(kf/reg-event-fx
  :system-user/change-password
  (fn [{:keys [db]} [params]]
    {:dispatch [:request/post {:url (:user-password sys-url)
                               :params params
                               :callback-event :system-user/change-password-success}]}))

(kf/reg-event-fx
  :system-user/change-password-success
  (fn [{:keys [db]} _]
    (let [query (get-in db [:system-user :user-table :query])]
      {:db (assoc-in db [:system-user :password-form] default-form)
       :dispatch [:system-user/fetch-list query]
       :msg "修改成功"})))

(kf/reg-event-fx
  :system-user/user-remove
  (fn [_ [id]]
    {:dispatch [:request/delete {:url ((:user-remove sys-url) id)
                                 :callback-event ::user-remove-success}]}))

(kf/reg-event-fx
  ::user-remove-success
  (fn [{:keys [db]} _]
    (let [query (get-in db [:system-user :user-table :query])]
      {:dispatch [:system-user/fetch-list query]
       :msg "删除成功"})))


(rf/reg-event-fx
  :system-user/assign-form-open
  (fn [{:keys [db]} [_ user-id]]
    {:dispatch [:request/get {:url ((:user-roles sys-url) user-id)
                              :params user-id
                              :callback-event :system-user/change-assign-data}]}))

(rf/reg-event-db
  :system-user/change-assign-data
  (fn [db [_ data user-id]]
    (let [options (map (fn [item] {:label (:name item) :value (:id item)}) data)
          values (map :id (filter #(true? (:selected %)) data))]
      (update-in
        db
        [:system-user :assign-form]
        #(assoc % :visible true :data {:options options :values values :user-id user-id})))))

(rf/reg-event-db
  :system-user/assign-form-close
  (fn [db _]
    (assoc-in db [:system-user :assign-form] {})))

(kf/reg-event-fx
  :system-user/assign-role
  (fn [{:keys [db]} [params]]
    {:dispatch [:request/post {:url (:role-assign sys-url)
                               :params params
                               :callback-event :system-user/assign-user-success}]}))

(kf/reg-event-fx
  :system-user/assign-user-success
  (fn [{:keys [db]} [data]]
    (let [query (get-in db [:system-user :user-table :query])]
      {:db (assoc-in db [:system-user :assign-form] {})
       :dispatch [:system-user/fetch-list query]
       :msg "分配成功"})))

(rf/reg-sub
  :system-user/assign-form
  (fn [data]
    (get-in data [:system-user :assign-form])))
