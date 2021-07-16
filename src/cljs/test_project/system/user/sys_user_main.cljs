(ns test-project.system.user.sys-user-main
  (:require
   [re-frame.core :as rf]
   [test-project.system.user.sys-user-sub :as sub]
   [test-project.system.user.sys-user-events :as events]
   [test-project.system.user.sys-user-views :as views]))

(defn sys-user-page []
  [:div
   [views/user-table @(rf/subscribe [:system-user/user-table])]
   [views/user-form @(rf/subscribe [:system-user/user-form])]
   [views/password-form @(rf/subscribe [:system-user/password-form])]
   [views/assign-role-form @(rf/subscribe [:system-user/assign-form])]])
