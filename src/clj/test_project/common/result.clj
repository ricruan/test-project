(ns test-project.common.result
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

(defn ok
  "返回一个api期待的map
  结构形如：{:status 200
           :body {:code 1
                  :message '操作成功'
                  :data map or vector}}"
  ([]
   (response (rtn-result)))

  ([data]
   (response (rtn-result data)))

  ([message data]
   (response (rtn-result 0 message data))))

;;sorry msg 约定
(def sorry-msg "操作失败")

(defn sorry
  "返回一个catch的map
  结构形如：{:status 400
           :body {:code 10020  ;业务代码
                  :message '操作失败'}}"
  ([code]
   (response (rtn-result code sorry-msg nil)))

  ([code message]
   (response (rtn-result code message nil)))
  
 ([code message data]
   (response (rtn-result code message data))) )

(defn unauthorized
  ([message]
   {:status 401
    :body (sorry "unauthorized" message)}))
