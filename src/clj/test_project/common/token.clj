(ns test-project.common.token
  (:require [buddy.sign.jwt :as jwt]
            [buddy.core.hash :as hash]
            [clj-time.core :refer [plus now minutes hours]]))

(def access-token-name "access-token")
(def refresh-token-name "refresh-token")

(def secret (hash/sha256 "mysecret"))

(defn- jwt-token [claims]
  (jwt/sign claims secret))

(defn- get-claims
  [token]
  (jwt/unsign token secret))

(defn- token-type? [token type]
  (let [claims (get-claims token)]
    (= (:type claims) type)))

(defn- token-valid? [token]
  (try
    (let [claims (get-claims token)]
      (not (nil? claims)))
    (catch Exception e
      (identity false))))

(defn get-user
  "获取用户"
  [token]
  (:user (get-claims token)))

(defn get-access-token
  "获取access token"
  ([user]
   (get-access-token user 30))
  ([user exp-minutes]
   (jwt-token {:user user
               :type access-token-name
               :exp  (plus (now) (minutes exp-minutes))
               :iat  (now)})))

(defn valid-access-token?
  "是否是有效的access token"
  [access-token]
  (and (token-valid? access-token)
       (token-type? access-token access-token-name)))

(defn get-refresh-token
  "获取refresh token"
  ([user]
   (get-refresh-token user (* 10 24)))
  ([user exp-hours]
   (jwt-token {:user user
               :type refresh-token-name
               :exp  (plus (now) (hours exp-hours))})))

(defn valid-refresh-token?
  "是否是有效的refresh token"
  [refresh-token]
  (and (token-valid? refresh-token)
       (token-type? refresh-token refresh-token-name)))

(defn get-token
  "获取access-token 和 refresh-token"
  [user]
  {:access-token  (get-access-token user)
   :refresh-token (get-refresh-token user)})

(defn refresh-token
  "刷新token"
  [refresh-token]
  (let [user (get-user refresh-token)]
    (get-token user)))
