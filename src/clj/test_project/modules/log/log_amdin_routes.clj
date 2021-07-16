(ns test-project.modules.log.log-amdin-routes
  (:require
    [test-project.modules.log.log-specs :as log-specs]
    [spec-tools.data-spec :as ds]
    [test-project.modules.log.log-service :as log-service]
    [test-project.common.response-utils :refer [success]]))

(defn log-admin-routes []
  ["/my-logs"
   {:swagger {:tags ["后台-日志管理接口"]}}
   ["/logs"
    {:get {:summary   "分页查询日志信息"
           :parameters {:query  ::log-specs/log-query-spec}
           :handler   (fn [{{:keys [query]} :parameters}]
                   (success (log-service/query-log query)))}}]])

