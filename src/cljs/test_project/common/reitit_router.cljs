(ns test-project.common.reitit-router
  (:require
   [re-frame.core :as rf]
   [kee-frame.core :as kf]
   [clojure.string :as str]
   [kee-frame.api :as api]
   [reitit.core :as reitit]
   [test-project.common.storage :as storage]
   [test-project.router :as router]
   [test-project.common.utils :refer [log]]))

(kf/reg-event-fx
  ::nav
  (fn [_ [route-name x]]
    (if (nil? x)
      {:navigate-to [route-name]}
      {:navigate-to [route-name x]})))

(defn match-data [routes route hash?]
  (let [[_ path-params] route]
    (str (when hash? "/#") (:path (apply reitit/match-by-name routes route))
         (when-some [q (:query-string path-params)] (str "?" q))
         (when-some [h (:hash path-params)] (str "#" h)))))

(defn match-url [routes url]
  (let [[path+query fragment] (-> url (str/replace #"^/#" "") (str/split #"#" 2))
        [path query] (str/split path+query #"\?" 2)]
    (some-> (reitit/match-by-path routes path)
            (assoc :query-string query :hash fragment))))

(defn url-not-found [routes data]
  (throw (ex-info "Could not find url for the provided data"
                  {:routes routes
                   :data   data})))

(defn route-match-not-found [routes url]
  (log "No match for URL in routes" {:url url :routes routes})
  (reitit/Match. "" {:name :not-found} nil nil ""))


(defrecord ReititRouter [routes hash?]
  api/Router
  (data->url [_ data]
    (or (match-data (reitit/router routes) data hash?)
        (url-not-found (reitit/router routes) data)))
  (url->data [data url]
    (let [match (match-url (reitit/router routes) url)]
      (if match
        (case (get-in match [:data :name])
          :redirect (if (nil? (storage/get-token-storage))
                      (rf/dispatch [:core/nav :login])
                      (rf/dispatch [:core/nav :main {:path ""}]))
          :main (let [path (get-in match [:path-params :path])]
                  (if (match-url (reitit/router @router/routes) path)
                    (identity match)
                    (route-match-not-found routes url)))
          (identity match))
        (route-match-not-found routes url)))))
