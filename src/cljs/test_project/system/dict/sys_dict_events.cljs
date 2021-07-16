(ns test-project.system.dict.sys-dict-events
  (:require
    [kee-frame.core :as kf]
    [re-frame.core :as rf]
    [test-project.url :refer [sys-url]]))

(kf/reg-controller
  ::list-controller-clear
  {:params (fn [route]
             (when (-> route :path-params :path (= "/system/dict")) true))
   :start  [:system-dict/clear]})

(kf/reg-controller
  ::list-controller
  {:params (fn [route]
             (when (-> route :path-params :path (= "/system/dict")) true))
   :start  [:system-dict/fetch-list nil]})

;;获取列表数据
(kf/reg-event-fx
  :system-dict/fetch-list
  (fn [{:keys [db]} [query]]
    (let [params (if (nil? query) {:page 0 :size 10 :name ""} query)]
      {:db       (update-in db [:system-dict :dict-list-table]
                            #(assoc % :loading true))
       :dispatch [:request/get
                  {:url            (:dict-list sys-url)
                   :params         params
                   :callback-event :system-dict/fetch-list-success}]})))

(kf/reg-event-fx
  :system-dict/fetch-list-success
  (fn [{:keys [db]} [data]]
    {:db (update-in
           db
           [:system-dict :dict-list-table]
           #(assoc % :loading false
                     :data data
                     :pagination {:total    (:total-elements data)
                                  :pageSize (:size data)
                                  :current  (inc (:page data))}))}))

;;添加字典列表数据
(kf/reg-event-fx
  :system-dict/list-add
  (fn [{:keys [db]} [query]]
    {:db       (update-in db [:system-dict :dict-list-table]
                          #(assoc % :loading true))
     :dispatch [:request/post
                {:url            (:dict-add sys-url)
                 :params         query
                 :callback-event :system-dict/list-add-success}]}))

(kf/reg-event-fx
  :system-dict/list-add-success
  (fn [{:keys [db]} _]
    (let [query {:page 0
                 :size 10
                 :name ""}]
      {:dispatch [:system-dict/fetch-list query]
       :msg "操作成功"})))

;;编辑字典列表数据
(kf/reg-event-fx
  :system-dict/list-edit
  (fn [{:keys [db]} [query]]
    {:db       (update-in db [:system-dict :dict-list-table]
                          #(assoc % :loading true))
     :dispatch [:request/put
                {:url            (:dict-add sys-url)
                 :params         query
                 :callback-event :system-dict/list-edit-success}]}))

(kf/reg-event-fx
  :system-dict/list-edit-success
  (fn [{:keys [db]} _]
    (let [query {:page (dec (get-in db [:system-dict :dict-list-table :pagination :current]))
                 :size (get-in db [:system-dict :dict-list-table :pagination :pageSize])
                 :name (if (nil? (get-in db [:system-dict :dict-list-table :pagination :name])) "" (get-in db [:system-dict :dict-list-table :pagination :name]))}]
      {:dispatch [:system-dict/fetch-list query]
       :msg      "修改成功"})))

;;删除字典列表数据
(kf/reg-event-fx
  :system-user/list-remove
  (fn [_ [id]]
    {:dispatch [:request/delete {:url            ((:dict-remove sys-url) id)
                                 :callback-event :system-user/list-remove-success}]}))

(kf/reg-event-fx
  :system-user/list-remove-success
  (fn [{:keys [db]} _]
    (let [query {:page (dec (get-in db [:system-dict :dict-list-table :pagination :current]))
                 :size (get-in db [:system-dict :dict-list-table :pagination :pageSize])
                 :name ""}
          parent_id {:parent_id (get-in db [:system-dict :dict-details-table :data :parent_id])}]
      (rf/dispatch [:system-dict/fetch-list query])
      (rf/dispatch [:system-dict/fetch-details parent_id])
      {:db (update-in db [:system-dict]
                      #(assoc % :dict-details-title nil))
       :msg "删除成功"})))

;;获取字典详情数据
(kf/reg-event-fx
  :system-dict/fetch-details
  (fn [{:keys [db]} [query]]
    (let [params (if (nil? (:name query))
                   {:page      0
                    :size      10
                    :name      ""
                    :parent_id (:parent_id query)}
                   query)]
      {:db       (update-in db [:system-dict :dict-details-table]
                            #(assoc % :loading true))
       :dispatch [:request/get
                  {:url            (:dict-list sys-url)
                   :params         params
                   :callback-event :system-dict/fetch-details-success}]})))

(kf/reg-event-fx
  :system-dict/fetch-details-success
  (fn [{:keys [db]} [data]]
    {:db (update-in
           db
           [:system-dict :dict-details-table]
           #(assoc % :loading false
                     :data data
                     :pagination {:total     (:total-elements data)
                                  :pageSize  (:size data)
                                  :current   (inc (:page data))
                                  :parent_id (:parent_id data)}))}))

;;添加字典列表数据
(kf/reg-event-fx
  :system-dict/details-add
  (fn [{:keys [db]} [query]]
    {:db       (update-in db [:system-dict :dict-details-table]
                          #(assoc % :loading true))
     :dispatch [:request/post
                {:url            (:dict-add sys-url)
                 :params         query
                 :callback-event :system-dict/details-add-success}]}))

(kf/reg-event-fx
  :system-dict/details-add-success
  (fn [{:keys [db]} _]
    (let [query {:page      0
                 :size      10
                 :parent_id (get-in db [:system-dict :dict-details-table :pagination :parent_id])
                 :name      ""}]
      {:dispatch [:system-dict/fetch-details query]
       :msg      "操作成功"})))

;;编辑字典列表数据
(kf/reg-event-fx
  :system-dict/details-edit
  (fn [{:keys [db]} [query]]
    {:db       (update-in db [:system-dict :dict-details-table]
                          #(assoc % :loading true))
     :dispatch [:request/put
                {:url            (:dict-add sys-url)
                 :params         query
                 :callback-event :system-dict/details-edit-success}]}))

(kf/reg-event-fx
  :system-dict/details-edit-success
  (fn [{:keys [db]} _]
    (let [query {:page      (dec (get-in db [:system-dict :dict-details-table :pagination :current]))
                 :size      (get-in db [:system-dict :dict-details-table :pagination :pageSize])
                 :parent_id (get-in db [:system-dict :dict-details-table :pagination :parent_id])
                 :name      (if (nil? (get-in db [:system-dict :dict-details-table :pagination :name])) "" (get-in db [:system-dict :dict-details-table :pagination :name]))}]
      {:dispatch [:system-dict/fetch-details query]
       :msg      "修改成功"})))

;;删除字典详情数据
(kf/reg-event-fx
  :system-user/details-remove
  (fn [_ [id]]
    {:dispatch [:request/delete {:url            ((:dict-remove sys-url) id)
                                 :callback-event :system-user/remove-success}]}))

(kf/reg-event-fx
  :system-user/remove-success
  (fn [{:keys [db]} _]
    (let [query {:page      (dec (get-in db [:system-dict :dict-details-table :pagination :current]))
                 :size      (get-in db [:system-dict :dict-details-table :pagination :pageSize])
                 :parent_id (get-in db [:system-dict :dict-details-table :pagination :parent_id])
                 :name      ""}]
      {:dispatch [:system-dict/fetch-details query]
       :msg      "删除成功"})))


;;字典列表modal
(rf/reg-event-db
  :system-dict/dict-list-modal
  (fn [db [_ {:keys [record visible attribute]}]]
    (assoc-in
      db
      [:system-dict :dict-list-table :dict-modal] {:data      (js->clj record :keywordize-keys true)
                                                   :attribute attribute
                                                   :visible   visible})))

;;字典详情列表modal
(rf/reg-event-db
  :system-dict/dict-details-modal
  (fn [db [_ {:keys [record visible attribute parent_id]}]]
    (assoc-in
      db
      [:system-dict :dict-details-table :dict-modal] {:data      (js->clj record :keywordize-keys true)
                                                      :attribute attribute
                                                      :visible   visible
                                                      :parent_id parent_id})))

;;选中列表时将标题写入db
(rf/reg-event-db
  :system-dict/dict-details-title-save
  (fn [db [_ {:keys [title]}]]
    (assoc-in
      db
      [:system-dict :dict-details-title] title)))


;;进入页面时清空db所有数据
(kf/reg-event-fx
  :system-dict/clear
  (fn [{:keys [db]} [data]]
    {:db (update-in
           db
           [:system-dict] {})}))
