(ns test-project.middleware.exception
  (:require [clojure.tools.logging :as log]
            [expound.alpha :as expound]
            [reitit.coercion :as coercion]
            [reitit.ring.middleware.exception :as exception]
            [test-project.common.result :refer [sorry]]
            [com.rpl.specter :as s]
            [ring.util.response :refer [bad-request]]))

;;抓取400的错误，并且返回problems这个属性的内容
(defn coercion-error-handler [status]
  (let [printer (expound/custom-printer {:theme :figwheel-theme, :print-specs? true})
        handler (exception/create-coercion-handler status)]
    (fn [exception request]
      (let [problems (-> exception ex-data :problems)
            res (:clojure.spec.alpha/problems
                 (s/transform [:clojure.spec.alpha/problems s/ALL]
                              #(select-keys % [:path :pred :val])
                              problems))]
        (printer problems)
        #_(handler exception request)
        (bad-request res)))))

(defn exception-handler [status error e request]
  {:status status
   :body {:code error
          :msg (.getMessage e)}})

(def exception-middleware
  (exception/create-exception-middleware
   (merge
    exception/default-handlers
    {;; log stack-traces for all exceptions
     :auth/unauthorized (partial exception-handler 401 "unauthorized")
     :biz/error (fn [exception request]
                  (let [data (ex-data exception)]
                    (sorry (:code data) (:msg data) (:data data))))
     ::exception/wrap (fn [handler e request]
                        (log/error e (.getMessage e))
                        (handler e request))}
    {:reitit.coercion/request-coercion (coercion-error-handler 400)})))
