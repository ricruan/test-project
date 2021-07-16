(ns test-project.common.enums)

;;;;;;;;;;;;;;;;;;;;;;;;;;
;; 枚举值工具类         ;;
;; 枚举结构:            ;;
;; (def suit-type-enums ;;
;;   {:单品 "0"         ;;
;;    :套装 "1"})       ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn get-value-by-key
  "获取枚举值"
  [enums k]
  (-> enums k))

(defn get-key-by-value
  "获取枚举key"
  [enums value]
  (some
   (fn [[k v]]
     (when (= (get-value-by-key enums k) value) k))
   enums))

(defn get-name-by-value
  "获取枚举名称"
  [enums value]
  (name (get-key-by-value enums value)))

(defn get-items
  "获取枚举项目"
  [enums]
  (map
   (fn [[k v]]
     {:name (name k)
      :value v})
   enums))
