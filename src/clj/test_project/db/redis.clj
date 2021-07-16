(ns test-project.db.redis
  (:require 
   [taoensso.carmine :as car :refer (wcar)]
   [test-project.config :refer [env]]
   [mount.core :refer [defstate]]))

(defstate server1-conn
          :start
          {:pool {}
           :spec {:host (get-in env [:redis :redis-host])
                  :port (get-in env [:redis :redis-port])
                  :password (get-in env [:redis :redis-password])
                  :timeout-ms (get-in env [:redis :redis-timeout-ms])
                  :db (get-in env [:redis :redis-db])}})

(defmacro wcar* [& body] `(car/wcar server1-conn ~@body))


;; api地址：https://ptaoussanis.github.io/carmine/taoensso.carmine.html

;; 保存数据到redis
;; 如果指定seconds，则表示seconds后会自动销毁
;; 如果不指定seconds，则表示永久保存
(defn set-value
  "保存数据到redis"
  ([key value]
   (wcar* (car/set key value)))
  ([key value seconds]
   (wcar* (car/setex key seconds value))))

;; 从redis中获取数据
(defn get-value
  "从redis中获取数据"
  [key]
  (wcar* (car/get key)))

;; 判断key值在redis中是否存在
;; 存在返回1，不存在返回0
(defn exists
  "判断key值是否存在"
  [key]
  (wcar* (car/exists key)))

;; 设置key过期时间，单位为秒
(defn expire
  "设置key过期时间，单位为秒"
  [key seconds]
  (wcar* (car/expire key seconds)))

;; 设置key值在哪个时间点过期
(defn expireat
  "设置key在哪个时间点过期"
  [key timestamp]
  (wcar* (car/expireat key timestamp)))

;; 删除key
;; 如果key存在并成功删除返回1
;; 其他返回0
(defn del
  "删除key"
  [key]
  (wcar* (car/del key)))

;; 自增，只针对整数起作用
;; 如果指定increment,则每次都按increment自增
;; 未指定，则每次加1
(defn incr
  "自增"
  ([key]
   (wcar* (car/incr key)))
  ([key increment]
   (wcar* (car/incrby key increment))))

;; 自减，只针对整数起作用
;; 如果指定decrement，则每次都按decrement自减
;; 未指定，则每次减1
(defn decr
  "自减"
  ([key]
   (wcar* (car/decr key)))
  ([key decrement]
   (wcar* (car/decrby key decrement))))
