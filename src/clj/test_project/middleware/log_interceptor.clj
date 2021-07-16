(ns test-project.middleware.log-interceptor
  (:require
   [clojure.tools.logging :as log]
   [test-project.config :refer [env]]))

(defn log-wrap [handler]
  (fn [request]
    (if-not (:dev env)
      (let [request-id (java.util.UUID/randomUUID)]
        (log/info (str "\n================================ REQUEST START ================================"
                       "\n request-id:" request-id
                       "\n request-uri: " (:uri request)
                       "\n request-method: " (:request-method request)
                       "\n request-query: " (:query (:parameters request))
                       "\n request-body: " (:body (:parameters request))))
        (let [res (handler request)]
          (log/info (str "response: " (:body res)
                         "\n request-id:" request-id))
          (log/info (str "\n================================ response END ================================"))
          res))
      (handler request))))
