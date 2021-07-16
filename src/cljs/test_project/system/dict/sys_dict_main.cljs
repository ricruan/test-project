(ns test-project.system.dict.sys-dict-main
  (:require
    [re-frame.core :as rf]
    [test-project.system.dict.sys-dict-events :as events]
    [test-project.system.dict.sys-dict-sub :as sub]
    [test-project.system.dict.sys-dict-views :as views]))

(defn sys-dict-page []
  [:div
   [views/dict-table @(rf/subscribe [:system-dict/table-list])]
   [views/dict-list-modal @(rf/subscribe [:system-dict/list-modal])]
   [views/dict-details-modal @(rf/subscribe [:system-dict/details-modal])]])
