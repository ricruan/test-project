(ns test-project.middleware.authentication
  (:require
   [test-project.common.token :as token]
   [test-project.modules.app.app-db :as app-db]
   [test-project.modules.user.user-db :as user-db]))

(defn auth-token-wrap [handler]
  (fn [request]
    (let [token-str (get-in request [:headers "authorization"])]
      (if (token/valid-access-token? token-str)
        (let [user (token/get-user token-str)]
          (handler (assoc request :current-user user)))
        {:status 401
         :body {:code "unauthorized"
                :message "token错误"}}))))

(defn- get-current-user
  [openid appid]
  (user-db/find-user-by-openid-appid {:openid openid :appid appid}))

(defn- get-current-app
  [appid]
  (when-let [app (app-db/find-app {:wx-app-id appid})]
    (select-keys app [:app-id :wx-app-id :wx-app-secret :app-name :company-id :ai-app-id :ai-app-secret :wx-mch-id :wx-pay-body :wx-api-secret :wx-notify-url])))

(defn auth-openid-wrap [handler]
  (fn [request]
    (let [openid (get-in request [:headers "openid"])
          appid (get-in request [:headers "appid"])
          user (get-current-user openid appid)
          app (get-current-app appid)]
      (if user
        (handler (assoc request :current-user user :current-app app))
        (throw (ex-info (format "微信openid【%s】或微信appid【%s】错误" openid appid) {:type :auth/unauthorized}))
        #_{:status "401"
         :body {:code "unauthorized"
                :message (format "微信openid【%s】或微信appid【%s】错误" openid appid)}}))))

(defn auth-appid-wrap [handler]
  (fn [request]
    (let [appid (get-in request [:headers "appid"])
          app (get-current-app appid)]
      (if app
        (handler (assoc request :current-app app))
        (throw (ex-info (format "微信appid【%s】错误" appid) {:type :auth/unauthorized}))
        #_{:status "401"
         :body {:code "unauthorized"
                :message (format "微信appid【%s】错误" appid)}}))))
