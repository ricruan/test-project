(ns test-project.common.utils
  (:require [clj-time.format :as f]
            [clojure.string :as s])
  (:import (com.twitter.snowflake.sequence SnowFlakeGenerator)
           (java.util.concurrent TimeUnit)))

(def year-month-day
  "yyyy-MM-dd")

(def year-month-day-hour-minute-second
  "yyyy-MM-dd HH:mm:ss")

(def year-month-day-hour-minute-second-no-line
  "yyyyMMddHHmmss")

(defn generate-db-id []
  (s/replace (str (java.util.UUID/randomUUID)) "-" ""))
  
 ;; 生成指定长度随机字符串
(defn rand-str [n]
  (s/join (repeatedly n #(rand-nth "abcdefghijklmnopqrstuvwxyz0123456789"))))

;; 生成指定长度随机数字字符串
(defn rand-num [n]
  (s/join (repeatedly n #(rand-nth "0123456789"))))

(defn format-date [timestamp]
  (-> year-month-day
      (java.text.SimpleDateFormat.)
      (.format timestamp)))

(defn format-time [timestamp]
  (-> year-month-day-hour-minute-second
      (java.text.SimpleDateFormat.)
      (.format timestamp)))

(defn format-date-time [timestamp]
  (-> year-month-day-hour-minute-second-no-line
      (java.text.SimpleDateFormat.)
      (.format timestamp)))

(defn uuid []
  (s/replace (str (java.util.UUID/randomUUID)) "-" ""))

(def snowFlakeGenerator
  (new SnowFlakeGenerator (TimeUnit/MILLISECONDS) 41 10 12 1514736000000 20))

(defn snowflake-id 
  "生成数据库ID，数字格式，并按照从小到大顺序生成,
   (snow flake id 时间戳从2018-1-1开始)"
  [] (str (.nextId snowFlakeGenerator)))

(defn trans-keyname-cols [m]
  (->>
   m
   (keys)
   (map #(name %))))

(defn parse-int
  "string 转 int"
  [s]
  (Integer/parseInt (re-find #"\A-?\d+" s)))

(defn parse-double
  "string 转 doule"
  [s]
  (Double/parseDouble s))

(defn parse-float
  "string 转 float"
  [s]
  (Float/parseFloat s))

;;md5加密
(defn get-str-md5 [s]
  (s/join
   (map (partial format "%02x")
        (.digest (doto (java.security.MessageDigest/getInstance "MD5")
                   .reset
                   (.update (.getBytes s)))))))

(defn encode-base64 [to-encode]
  (.encode (java.util.Base64/getEncoder) (.getBytes to-encode)))

(defn encode-base64-string [to-encode]
  (.encodeToString (java.util.Base64/getEncoder) (.getBytes to-encode)))

;; base64-decode
(defn decode-base64 [to-decode]
  (String. (.decode (java.util.Base64/getDecoder) to-decode)))

(defn decode-base64-byte [to-decode]
  (.decode (java.util.Base64/getDecoder) to-decode))


(defn group-data-by-keys
  "对一组数据库返回结果{data}进行处理, 使用{group-keys}中的key进行group-by:
  (sut/group-data-by-keys test-dict
                                  [:group-code1
                                   :group-code2]
                                  )
  可以带多组额外的集合函数，多组[key reducing-function init-value]的格式：
  (sut/group-data-by-keys test-dict
                                  [:group-code]

                                  :a
                                  (fn [v e] (+ v (:id e)))
                                  0

                                  :b
                                  (fn [v e] (+ v (:id e)))
                                  0
                                  )"
  ([data group-keys]
   (->> data
        (group-by (fn [m] (select-keys m group-keys)))

        (reduce-kv (fn [m k v]
                     (assoc m k {:list
                                 (mapv
                                  (fn [e]
                                    (apply dissoc e group-keys))
                                  v)}))
                   {})
        (mapv (fn [e] (apply merge e)))))

  ([data group-keys key r-func val & krvs]
   (->> data
        (group-by (fn [m] (select-keys m group-keys)))

        (reduce-kv (fn [m k v]
                     (assoc m
                            k (if (empty? krvs)
                                {:list
                                 (mapv
                                  (fn [e]
                                    (apply dissoc e group-keys))
                                  v)
                                 key (reduce r-func val v)}

                                (apply assoc {:list
                                              (mapv
                                               (fn [e]
                                                 (apply dissoc e group-keys))
                                               v)
                                              key (reduce r-func val v)}


                                       (let [krvs-seq (partition 3 krvs)]
                                         (mapcat (fn [krv]
                                                   (let [[key r-func val] krv]
                                                     [key (reduce r-func val v)]))
                                                 krvs-seq))))))

                   {})
        (map (fn [e] (apply merge e))))))

(defn list-from-group-by
  "去除分组, 作用和group-data-by-key 相反:
  一层嵌套: (list-from-group-by {:y 1 :a [{:b 2} {:b 3}]} )))  ->  [{:b 2, :y 1} {:b 3, :y 1}]


  双层嵌套:  (mapcat sut/list-from-group-by
                  (sut/list-from-group-by {:y 1 :a [{:b [{:x 99}]} {:b [{:x 77}]}]} ))

           ->

           [{:x 99, :y 1} {:x 77, :y 1}]
  "
  [m]
  (reduce-kv (fn [r k v]
               (if (vector? v)
                 (->> (apply conj r v)
                      (map #(merge % (dissoc m k))))

                 r))

             []
             m))


;; 获取时间戳，单位秒
(defn get-timestamp [localdate]
  (when localdate
    (.toEpochSecond
     localdate
     (java.time.ZoneOffset/of "+8"))))
