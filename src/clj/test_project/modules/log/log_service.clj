(ns test-project.modules.log.log-service
  (:require [test-project.modules.log.log-db :as db]
            [test-project.common.biz-error :refer [throw-error biz-error-map]]
            [clojure.data.json :as json]))

(defn query-log
  "日志查询"
  [params]
  (let [list (db/find-log-by-condition params )]
    {:page (:page params)
     :size (:size params)
     :totalData (:total (db/find-log-counts params))
     :list (map (fn [item]
            (assoc item :db_detail (json/read-str (:db_detail item ))
                        :detail_count (count (json/read-str (:db_detail item )))))
          list) }))



