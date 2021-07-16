(ns test-project.system.user.sys-user-sub
  (:require [re-frame.core :as rf]))

(rf/reg-sub
  :system-user/user-table
  (fn [data]
    (get-in data [:system-user :user-table])))

(rf/reg-sub
  :system-user/user-form
  (fn [data]
    (get-in data [:system-user :user-form])))

(rf/reg-sub
  :system-user/password-form
  (fn [data]
    (get-in data [:system-user :password-form])))
