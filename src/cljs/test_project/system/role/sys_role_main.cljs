(ns test-project.system.role.sys-role-main
  (:require
   [re-frame.core :as rf]
   [test-project.system.role.sys-role-events :as event]
   [test-project.system.role.sys-role-views :as views]))

(defn sys-role-page []
  [:div
   [views/role-table @(rf/subscribe [:system-role/role-table])]
   [views/role-form @(rf/subscribe [:system-role/role-form])]
   [views/assign-menu-form @(rf/subscribe [:system-role/assign-form])]])
