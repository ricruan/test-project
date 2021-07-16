(ns test-project.modules.wx.wx-routes
  (:require
   [clojure.spec.alpha :as s]
   [spec-tools.core :as st]
   [spec-tools.data-spec :as ds]
   [test-project.modules.app.app-db :as app-db]
   [test-project.modules.wx.wx-specs :as wx-specs]
   [test-project.common.result :refer [ok sorry]]))


(defn wx-routes []
  ["/xw"
   {:swagger {:tags ["后台-微信管理"]}}

   ["/list/app"
    {:get {:summary "获取app"
           :responses {200 {:body ::wx-specs/wx-app-list-body}}
           :handler (fn [_]
                      (-> (app-db/find-app)
                          (ok)))}}]])

