(ns test-project.layout.layout-main
  (:require
   [re-frame.core :as rf]
   [test-project.router :refer [routes]]
   [test-project.common.route-mapping :refer [main-switch-route]]
   [test-project.layout.side-menu :refer [create-side-menus-by-data]]
   [test-project.layout.layout-events :as layout-events]
   [test-project.layout.layout-views :refer [basic-layout]]))

(def breadcrumbs (rf/subscribe [:layout/breadcrumbs]))
(def menus (rf/subscribe [:base/current-menus]))


(defn layout-page []
  (fn []
    (let [side-menus (create-side-menus-by-data @menus)
          switch-route (main-switch-route @routes)]
      [:div
       [basic-layout side-menus @breadcrumbs switch-route]])))
