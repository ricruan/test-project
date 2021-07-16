(ns test-project.modules.file.file-admin-routes
  (:require [test-project.db.core :refer [*db*]]
            [test-project.common.result :refer [ok sorry]]
            [test-project.config :refer [env]]
            [reitit.ring.middleware.multipart :as multipart]
            [test-project.modules.file.file-specs :as file-specs]
            [test-project.common.file-util :as file]
            [clojure.java.io :as io]
            [clojure.spec.alpha :as s]
            [spec-tools.core :as st]
            [clojure.data.json :as json]
            [clj.qiniu :as qiniu]
            [clojure.tools.logging :as log]
            [test-project.common.utils :as utils]))

(defn file-admin-routes []
  ["/file"
   {:swagger {:tags ["后台-文件接口"]}}

   ["/upload"
    {:post {:summary    "文件上传"
            :parameters {:multipart {:file        multipart/temp-file-part
                                     :type        (st/spec
                                                   {:spec        string?
                                                    :description "类型，用做在文件上传时增加路径的前缀"
                                                    :reason      "类型必填"})}}
            :responses {200 {:body ::file-specs/file-upload}}
            :handler    (fn [{{{:keys [type file]} :multipart} :parameters}]
                          (ok "上传成功"
                              {:file-url
                               (file/upload-file-qiniu type file)}))}}]])
