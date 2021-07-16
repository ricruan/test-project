(ns test-project.layout.global-footer
  [:require ["antd" :as ant]])

(defn footer [copyright]
  [:div {:style {:padding "0 16px" :margin "30px 0 24px 0" :text-align "center"}}
   [:div {:style {:font-size 14}}
    [:span "Copyright"]
    [:> ant/Icon {:type "copyright" :style {:margin "0 5px"}}]
    [:span copyright]]])
