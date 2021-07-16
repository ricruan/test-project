(ns test-project.layout.side-menu
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [kee-frame.core :as kf]
            ["antd" :refer [Menu Icon Spin]]))

(def SubMenu (.-SubMenu Menu))
(def MenuItem (.-Item Menu))

#_(defn- router->menu [route]
    (fn []
      (when route
        (let [[path data & children] route]
          (if (zero? (count children))
            [:> MenuItem {:key (:name data) :className "ant-menu-item"} (:title data)]
            [:> SubMenu {:key (:name data) :title (:title data)}
             (map #(vector (router->menu %)) children)])))))

(def open-keys (r/atom []))
(def selected-keys (r/atom []))


(defn create-side-menus [routes]
  (fn []
    [:> Menu {:theme "dark"
              :mode "inline"
              :openKeys @open-keys
              :selected-keys @selected-keys
              :onOpenChange (fn [openKeys]
                              (reset! open-keys (js->clj openKeys)))
              :onClick (fn [item]
                         (reset! selected-keys (:key (js->clj item :keywordize-keys true)) ))}

     (for [route routes
           :let [[path {:keys [name title icon show]} & children] route]
           :when (not= false show)]
       (if (zero? (count children))
         ^{:key name} [:> MenuItem {:key name}
                       [:a {:href (str "/main" path)}
                        [:> Icon {:type icon}]
                        [:span title]]]
         ^{:key name} [:> SubMenu {:key name
                                   :title (r/as-element [:span
                                                         [:> Icon {:type icon}]
                                                         [:span title]])}
                       (for [child children
                             :let [[cpath cdata] child]
                             :when (not= false (:show cdata))]
                         ^{:key (:name cdata)} [:> MenuItem {:key (:name cdata)}
                                                [:a {:href (str "/main" path cpath)} (:title cdata)]])]))]))


(defn create-side-menus-by-data [{:keys [loading data]}]
  (fn []
    [:div
     [:> Spin {:spinning loading}]
     [:> Menu {:theme "dark"
               :mode "inline"
               :openKeys @open-keys
               :selected-keys @selected-keys
               :onOpenChange #(reset! open-keys (js->clj %))
               :onClick #(reset! selected-keys (vector (:key (js->clj % :keywordize-keys true))) )}
      (for [route (sort-by :sort (filter #(= (:parent_id %) "0") data))
            :let [{:keys [id name path icon]} route
                  children (sort-by :sort (filter #(= (:parent_id %) (:id route)) data))]]
        (if (seq children)
          ^{:key id} [:> SubMenu {:key id
                                  :title (r/as-element [:span
                                                        [:> Icon {:type icon}]
                                                        [:span name]])}
                      (for [child children]
                        ^{:key (:id child)} [:> MenuItem {:key (:id child)}
                                             [:a {:href (:path child)} (:name child)]])]
          ^{:key id} [:> MenuItem {:key id}
                      [:a {:href path }
                       [:> Icon {:type icon}]
                       [:span name]]]))]]))
