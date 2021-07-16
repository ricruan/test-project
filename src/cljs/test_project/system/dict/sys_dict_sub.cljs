(ns test-project.system.dict.sys-dict-sub
  (:require [re-frame.core :as rf]))

(rf/reg-sub
  :system-dict/table-list
  (fn [data]
    {:dict-list-table    (get-in data [:system-dict :dict-list-table])
     :list-pagination    (get-in data [:system-dict :dict-list-table :pagination])
     :dict-details-table (get-in data [:system-dict :dict-details-table])
     :dict-details-title (get-in data [:system-dict :dict-details-title])
     :details-pagination (get-in data [:system-dict :dict-details-table :pagination])}))

(rf/reg-sub
  :system-dict/list-modal
  (fn [data]
    (get-in data [:system-dict :dict-list-table :dict-modal])))

(rf/reg-sub
  :system-dict/details-modal
  (fn [data]
    (get-in data [:system-dict :dict-details-table :dict-modal])))
