(ns test-project.common.file-util
  (:require
   [test-project.config :refer [env]]
   [clojure.java.io :as io]
   [clj.qiniu :as qiniu]
   [clojure.tools.logging :as log]))

(defn- format-date-time [timestamp]
  (-> "yyyyMMddHHmmss"
      (java.text.SimpleDateFormat.)
      (.format timestamp)))

(defn upload-file-local
  "上传文件到本地指定目录
   env里指定file-path这个配置，可以是绝对路径，也可以是相对路径，有读写权限就可以
  此方法可以保留文件扩展名"
  [type file]
  (let [filename (:filename file)
        file-path (str (:file-path env) type
                       "/" (format-date-time (java.util.Date.))
                       "/" filename)
        fileType (subs filename (clojure.string/last-index-of filename ".") (count filename))]
    (io/make-parents file-path)
    (with-open [writer (io/output-stream file-path)]
      (io/copy (:tempfile file) writer))
    file-path))

;;上传到七牛配置
(defn set-qiniu-config []
  (qiniu/set-config! :access-key (str (get-in env [:qiniu :access-key]))
                     :secret-key (str (get-in env [:qiniu :secret-key]))))

;;配置也可以从环境变量获取
(defn qiniu-config []
  {:bucket (str (get-in env [:qiniu :bucket]))
   :domain (str (get-in env [:qiniu :domain]))
   :prefix (str (get-in env [:qiniu :prefix]))})

(defn qiniu-upload-path
  "构造上传地址"
  [prefix filename]
  (set-qiniu-config)
  (str (:prefix (qiniu-config))
       prefix "/"
       (format-date-time (java.util.Date.))
       "/"
       filename))

;;七牛云上传，返回上传后地址
(defn upload-file-qiniu
  "实现七牛云public的上传"
  [prefix file]
  (let [filename (if (instance? java.io.File file)
                   (.getName file)
                   (:filename file))
        real-file (if (instance? java.io.File file)
                    file
                    (:tempfile file))
        key (qiniu-upload-path prefix filename)
        bucket (:bucket (qiniu-config) :bucket)
        res (qiniu/upload-bucket bucket key real-file)]
    (log/info "文件【 " filename " 】上传七牛云结果：" res)
    (if-not (= 200 (:status res))
      (throw (Exception. " 附件上传失败 ")))
    (str (:domain (qiniu-config)) key)))

;; 七牛云文件批量上传，返回上传后的地址数组
(defn upload-files-qiniu [prefix files]
  (log/info "批量上传文件开始，文件数量为【" (count files) "】")
  (mapv #(upload-file-qiniu prefix %) files))
