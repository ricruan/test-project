(ns test-project.handler
  (:require
   [test-project.middleware :as middleware]
   [test-project.routes.base :as base]
   [reitit.swagger-ui :as swagger-ui]
   [reitit.ring :as ring]
   [ring.middleware.content-type :refer [wrap-content-type]]
   [ring.middleware.webjars :refer [wrap-webjars]]
   [test-project.env :refer [defaults]]
   [test-project.routes.demo :refer [demo-routes]]
   [test-project.modules.base.base-amdin-routes :refer [base-amdin-routes]]
   [test-project.modules.sys.sys-routes :refer [sys-user-routes sys-role-routes sys-menu-routes]]
   [test-project.modules.sys.auth-routes :refer [auth-routes]]
   [test-project.modules.sys.sys-dict-routes :refer [sys-dict-routes]]
   [test-project.modules.file.file-admin-routes :refer [file-admin-routes]]
   [test-project.modules.wx.wx-routes :refer [wx-routes]]
   [test-project.modules.log.log-amdin-routes :refer [log-admin-routes]]
   [mount.core :as mount]))

(mount/defstate init-app
                :start ((or (:init defaults) (fn [])))
                :stop  ((or (:stop defaults) (fn []))))

;;api接口
(defn api-routes []
  (conj (base/api-routes)
        (wx-routes)))

;;api-public接口
(defn api-public-routes []
  (conj (base/api-public-routes)
        (wx-routes)))

;; 回调接口，无权限控制
(defn api-callback-routes []
  (conj (base/api-callback-routes)
        (demo-routes)))

;;管理接口
(defn admin-routes []
  (conj (base/admin-routes)
        (base-amdin-routes)
        (sys-user-routes)
        (sys-role-routes)
        (sys-menu-routes)
        (sys-dict-routes)
        (file-admin-routes)
        (log-admin-routes)))

;;管理公共接口
(defn admin-pulic-routes []
  (conj (base/admin-public-routes)
        (auth-routes)))

(mount/defstate app-routes
                :start
                (ring/ring-handler
                  (ring/router
                    [(conj (base/service-routes)
                           (base/swagger-routes)
                           (api-public-routes)
                           (api-routes)
                           (api-callback-routes)
                           (admin-routes)
                           (admin-pulic-routes))])
                  (ring/routes
                    (ring/create-resource-handler {:path "/"})
                    (wrap-content-type (wrap-webjars (constantly nil)))
                    (ring/create-default-handler))))

(defn app []
  (middleware/wrap-base #'app-routes))

