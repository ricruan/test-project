(ns test-project.common.route-mapping
  (:require
   [clojure.string :refer [split replace]]
   [reitit.core :as r]
   [kee-frame.core :as kf]))

(defn- key-map-args [routes]
  (flatten
   (map
    (fn [item] [(:name item) (:page item)])
    (filter map? (rest (tree-seq vector? identity routes))))))

(defn- routes->args [routes]
  (let [map-args (key-map-args routes)]
    (conj
     (concat map-args [nil [:div "404"]])
     (fn [route] (get-in route [:data :name])))))

(defn auto-mapping-route [routes]
  (let [args (routes->args routes)]
    [apply kf/switch-route args]))

(defn- get-paths [path]
  (map #(str "/" %) (split path #"/")))

(defn- get-sub-path [parent-path path]
  (str "/"
       (when (and parent-path path)
         (-> path
             (replace parent-path "")
             (split #"/")
             (rest)
             (first)))))

(defn recursion-path-routes [parent-path path routes path-routes]
  (let [sub-path (get-sub-path parent-path path)]
    (if-let [[rpath data & children] (some (fn [route]
                                             (when (= sub-path (first route)) route ) ) routes)]
      (do
        (recursion-path-routes
         (str parent-path sub-path)
         path
         children
         (into path-routes [{:path rpath :title (:title data)}])))
      (identity path-routes))))

(defn get-routes-by-path [path routes]
  (recursion-path-routes "" (if (nil? path) "" path) routes []))

(defn- children-routes [route]
  (let [[_ {:keys [page]} & children] route]
    (map (fn [[_ cdata]] [(:name cdata) (if children (page (vec children)) page)])
         (r/routes (r/router route)))))

#_(defn switch-route-args [routes]
    (let [roots (filter (fn [[_ _ & children]] (nil? children)) routes)
          parents (filter (fn [[_ _ & children]] (not (nil? children))) routes)]
      (concat
       [(fn [route] (get-in route [:data :name]))]
       (vec (flatten (map (fn [[path data]] [(:name data) (:page data)]) roots)))
       (flatten (map children-routes parents))
       [nil [:div "404"]])))

#_(defn root-switch-route
    "自动生成 kee-frame/switch-route"
    [routes]
    [apply kf/switch-route (switch-route-args routes)])

(defn main-switch-route-args [routes]
  (concat
   [(fn [route]
      (get-in route [:path-params :path]))]
   (flatten (map (fn [[path cdata]] [path (:page cdata)])
                 (r/routes (r/router routes))))
   [nil [:div "404"]]))

(defn main-switch-route
  [routes]
  [apply kf/switch-route (main-switch-route-args routes)])
