(ns test-project.core
  (:require
   ["antd" :as ant]
   [reitit.core :as reitit]
   [kee-frame.core :as kf]
   [re-frame.core :as rf]
   [kee-frame.api :as api]
   [test-project.common.request :as request]
   [test-project.common.reitit-router :refer [ReititRouter]]
   [test-project.login.login-views :refer [login-page]]
   [test-project.layout.layout-main :refer [layout-page]]
   [test-project.common.msg-fx]))

(kf/reg-event-fx
  :core/message-error
  (fn [{:keys [db]} [msg]]
    {:error "请求失败,请稍后再试"}))

(kf/reg-event-fx
  :core/message-success
  (fn [{:keys [db]} [msg]]
    {:msg "操作成功"}))

(rf/reg-event-fx
  :core/nav
  (fn [_ [_ route-name x]]
    (if (nil? x)
      {:navigate-to [route-name]}
      {:navigate-to [route-name x]})))

(defn loading-page []
  [:div {:style {:text-align "center" :margin-top 100}}
   [:> ant/Spin {:size "large"}]])

(defn root-component []
  [:div
   [kf/switch-route (fn [route] (get-in route [:data :name]))
    :login [login-page]
    :main [layout-page]
    nil [loading-page]]])

(def routes
  [["/" :redirect]
   ["/login" :login]
   ["/main*path" :main]])

;; Initialize app
(defn ^:dev/after-load mount-components
  ([] (mount-components true))
  ([debug?]
   (rf/clear-subscription-cache!)
   (kf/start! {:debug?         (boolean debug?)
               :router         (ReititRouter. routes true)
               :hash-routing?  true
               :initial-db     nil
               :root-component [root-component]})))

(defn init! [debug?]
  #_(ajax/load-interceptors!)
  (mount-components debug?))
