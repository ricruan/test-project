(ns test-project.login.login-events
  (:require
    [kee-frame.core :as kf]
    [re-frame.core :as rf]
    [test-project.url :refer [auth-url base-url]]
    [test-project.common.storage :as storage]))

(kf/reg-controller
  :login-interceptor
  {:params (fn [route]
             (when (nil? (storage/get-token-storage))
               route))
   :start  (fn [] [:core/nav :login])})

(kf/reg-event-fx
  :login
  (fn [{:keys [db]} [params]]
    {:db       (update-in db [:login] #(assoc % :loading true
                                         :username (:username params)
                                         :password (:password params)))
     :dispatch [:request/post {:url            (:token auth-url)
                               :params         params
                               :with-code?     true
                               :callback-event :login/handler}]}))

(kf/reg-chain
  :login/handler
  (fn [{:keys [db]} [{:keys [code msg data]}]]
    (when (zero? code)
      (storage/set-token-storage data)
      (rf/dispatch [:core/nav :main {:path ""}])
      (rf/dispatch [:login/fetch-user-base-data]))
    {:db (update db :login #(assoc % :result false
                                   :loading false
                                   :result-code code))}))

;;获取使用者基础数据
(kf/reg-event-fx
  :login/fetch-user-base-data
  (fn [_ _]
    {:dispatch [:request/get
                {:url            (:base-user base-url)
                 :callback-event ::fetch-user-base-data-success}]}))

(kf/reg-event-fx
  ::fetch-user-base-data-success
  (fn [{:keys [db]} [data]]
    {:db (update-in
           db
           [:base]
           #(assoc % :user data))}))

(kf/reg-event-fx
  :logout
  (fn [{:keys [db]}]
    (storage/remove-token-storage)
    {:db       db
     :dispatch [:core/nav :login]}))

(rf/reg-sub
  :login/result
  (fn [db]
    (get-in db [:login :result])))

(rf/reg-sub
  :login/loading
  (fn [db]
    (get-in db [:login :loading] false)))

(kf/reg-controller
  ::base-interceptor
  {:params (fn [route]
             (when (-> route :path (not= "/login"))
               true))
   :start (fn []
            (rf/dispatch [:base/fetch-user-menus]))})

(kf/reg-controller
 ::login-controller
 {:params (fn [route] (when (-> route :path (= "/login")) true))
  :start (fn []
           (rf/dispatch [::clear-user-menu]))})

(kf/reg-event-db
 ::clear-user-menu
 (fn [db]
   (assoc-in db [:base :menu] nil)))

(kf/reg-event-fx
  :base/fetch-user-menus
  (fn [{:keys [db]} _]
    (when-not (get-in db [:base :menu :data])
      {:db       (assoc-in db [:base :menu :loading] true)
       :dispatch [:request/get {:url            (:current-menus base-url)
                                :callback-event :base/fetch-user-menus-success}]})))
(kf/reg-event-fx
 :base/fetch-user-menus-success
 (fn [{:keys [db]} [data]]
   {:db (update-in db [:base :menu] #(assoc % :loading false :data data))
    :dispatch [:auth/get-auth-tree]}))

(rf/reg-sub
  :base/current-menus
  (fn [db]
    (get-in db [:base :menu])))
