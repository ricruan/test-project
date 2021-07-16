(ns test-project.common.response-utils
  (:require
    [ring.util.response :refer [response]]))

(defn- rtn-result
  ([]
   (rtn-result nil))
  ([data]
   (rtn-result 0 "操作成功" data))
  ([code message data]
   {:code code
    :msg message
    :data data}))

(defn success
  ([]
   (response (rtn-result)))
  ([data]
   (response (rtn-result data))))

(defn biz-error
  ([code msg]
   (response (rtn-result code msg nil))))