(ns test-project.common.storage)

(defn set-local-storage [key value]
  (.setItem js/localStorage key value))

(defn get-local-storage [key]
  (.getItem js/localStorage key))

(defn remove-local-storage [key]
  (.removeItem js/localStorage key))

(defn set-session-storage [key value]
  (.setItem js/sessionStorage key value))

(defn get-session-storage [key]
  (.getItem js/sessionStorage key))

(defn remove-session-storage [key]
  (.removeItem js/sessionStorage key))

(def token-key "current-token")

(defn set-token-storage [value]
  (set-local-storage token-key value))

(defn get-token-storage []
  (cljs.reader/read-string (get-local-storage token-key)))

(defn remove-token-storage []
  (remove-local-storage token-key))
