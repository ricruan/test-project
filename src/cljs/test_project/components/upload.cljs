(ns test-project.components.upload
  (:require
   ["antd" :as ant]
   [reagent.core :as r]
   [test-project.config :refer [domain]]))

(def token "eyJhbGciOiJIUzI1NiJ9.eyJleHAiOjE1OTg1NzczODksInR5cGUiOiJhY2Nlc3MtdG9rZW4iLCJ1c2VyIjp7InVzZXJfbmFtZSI6ImFkbWluIiwiY29tcGFueV9pZCI6IjEiLCJhcHBfaWQiOiIxIn0sImlhdCI6MTU2NzA0MTM4OX0.IdQMZiRkcDnmo82bUv754zV3bpthNSL_DIzu438GmmE")

(defn file-upload [prefix]
  (def preview-visible? (r/atom false))
  (def preview-image (r/atom ""))
  (fn []
    (let [{:keys [multi? files data on-change on-remove]} (r/props (r/current-component))]
      [:div
       [:> ant/Upload
        {:multiple multi?
         :action (str domain "/admin/file/upload")
         :headers {:authorization token}
         :listType "picture-card"
         :name "file"
         :data data
         :fileList files
         :onPreview (fn [file]
                      (reset! preview-visible? true)
                      (reset! preview-image (get (js->clj file :keywordize-keys true) :url "")))
         :onChange #(on-change (js->clj % :keywordize-keys true))}
        (when (or multi? (zero? (count files)))
          [:div
           [:> ant/Icon {:type "plus"}]
           [:div {:style {:margin-top 8 :color "#666"}} "上传"]])]
       [:> ant/Modal {:visible @preview-visible?
                      :style {:top 20}
                      :footer nil
                      :onCancel (fn []
                                  (reset! preview-visible? false)
                                  (reset! preview-image ""))}
        [:img {:style {:width "100%"}
               :src @preview-image}]]])))
