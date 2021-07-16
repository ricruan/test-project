(ns test-project.layout.layout-events
  (:require
   [kee-frame.core :as kf]
   [re-frame.core :as rf]
   [test-project.common.storage :as storage]
   [clojure.string :as string]
   [test-project.router :refer [routes]]
   [test-project.common.route-mapping :refer [get-routes-by-path]]
   [test-project.url :refer [sys-url]]))

(defn saveTokens [tokens]
  (let [tokenStr (first tokens) refreshStr (second tokens)]
    (let [token (second (string/split tokenStr "=")) refresh (second (string/split refreshStr "="))]
      (storage/set-token-storage {:access-token token :refresh-token refresh}))))

(kf/reg-controller
  :layout/breadcrumbs-controller
  {:params (fn [route] (identity route))
   :start (fn [_ route]
            (rf/dispatch [:set-breadcrumbs route]))})

(rf/reg-event-db
  :set-breadcrumbs
  (fn [db [_ {:keys [path-params]}]]
    (assoc-in
      db
      [:layout :breadcrumbs]
      (get-routes-by-path (:path path-params) @routes))))

(rf/reg-sub
  :layout/breadcrumbs
  (fn [data]
    (get-in data [:layout :breadcrumbs] [])))

(rf/reg-event-db
  :change-password
  (fn [db _]
    db))

;; 给其它系统嵌套使用
(kf/reg-controller
  ::from-other-site
  {:params (fn [route]
             (let [query (get route :query-string)]
               (when (re-find #"token=\w+" (or query ""))
                 (let [tokens (string/split query "&")]
                   (saveTokens tokens)
                   true))))
   :start [::hide-layout]})

(rf/reg-event-db
  ::hide-layout
  (fn [db]
    (assoc-in db [:layout :hide-layout] true)))

(rf/reg-sub
  :layout/hide-layout
  (fn [db]
    (get-in db [:layout :hide-layout] false)))

;;用户重置自己密码modal
(rf/reg-event-db
  :layout/reset-password
  (fn [db [_ {:keys [visible]}]]
    (assoc-in
      db
      [:layout :password-modal] {:visible visible})))

(rf/reg-sub
  :layout/reset-password-modal
  (fn [data]
    (get-in data [:layout :password-modal])))

;;用户重置本人登录密码接口请求并抹除现有token强制跳转登录页
(kf/reg-event-fx
  :layout/change-password
  (fn [{:keys [db]} [params]]
    {:dispatch [:request/post {:url            (:current-user-password sys-url)
                               :params         params
                               :callback-event ::change-password-success}]}))

(kf/reg-event-fx
  ::change-password-success
  (fn [{:keys [db]} res]
    (if (= (first res) "error")
      {:msg "修改失败，请检查旧密码是否输入正确"})
      {:db       (assoc-in db [:layout :password-modal :visible] false)
       :dispatch [:logout]
       :msg      "修改成功"}))

;;获取登录用户基础信息
(rf/reg-sub
  :layout/user-base-data
  (fn [data]
    (get-in data [:base :user])))
