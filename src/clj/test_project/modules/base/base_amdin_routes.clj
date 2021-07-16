(ns test-project.modules.base.base-amdin-routes
  (:require [test-project.common.result :refer [ok sorry]]
            [test-project.modules.base.base-db :as base-db]
            [spec-tools.data-spec :as ds]
            [test-project.modules.base.base-specs :as base-specs]
            [reitit.ring.middleware.exception :as exception]))


(defn base-amdin-routes []
  ["/base"
   {:swagger {:tags ["后台-基础数据接口"]}}
   ["/user"
    {:get {:summary "获取系统当前登录用户基础数据"
           :responses {200 {:body ::base-specs/base-user-body}}
           :handler (fn [{{:keys [user_id]} :current-user}]
                      (let [user (base-db/find-user-base-data {:id   user_id})]
                        (if (nil? user)
                          (sorry -1 "该用户不存在")
                          (ok user))))}}]])



