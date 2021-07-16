(ns test-project.modules.sys.auth-service
  (:require
   [clojure.tools.logging :as log]
   [test-project.common.token :as token]
   [test-project.common.encrypt :as encrypt]
   [test-project.modules.sys.sys-user-db :as db]
   [test-project.common.biz-error :refer [throw-error biz-error-map]]))

(defn get-token [{:keys [username password]}]
  (if-let [user (db/find-sys-user {:username username :password (encrypt/encode password)})]
    (token/get-token (select-keys user [:user_id :username :nickname]))
    (throw-error 1000)))

(defn refresh-token [refresh-token-str]
  (if (token/valid-refresh-token? refresh-token-str)
    (token/refresh-token refresh-token-str)
    (throw-error 1001)))
