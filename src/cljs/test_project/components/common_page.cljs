(ns test-project.components.common-page
  (:require
   ["antd" :as ant]
   [reagent.core :as r]
   [test-project.common.utils-auth :refer [auth-wrapper]]
   [re-frame.core :as rf]))

(defn main-page []
  (let [this (r/current-component)
        props (r/props this)]
    [:div
     [:> ant/PageHeader {:onBack (:on-back props)
                         :title  (:title props)
                         :extra  (:extra props)
                         :style  {:margin-bottom 1
                                  :font-size     "16px"
                                  :font-weight   "700"}}]
     (into [:> ant/Card {:bordered false}] (r/children this))]))

;;分页表格
(defn pagination-table [{:keys [pagination onChange onSizeChange paginationSize showQuickJumper] :as props}]
  [:> ant/Table
   (merge
     {:size "middle"
      :pagination
            (merge
              {:size            (if (nil? paginationSize) "default" paginationSize)
               :total           0
               :pageSize        0
               :current         0
               :showQuickJumper showQuickJumper
               :showSizeChanger true
               :showTotal       (fn [total]
                                  (r/as-element [:span
                                                 {:style {:margin-right 20}}
                                                 "共计"
                                                 [:span {:style {:font-size   16
                                                                 :font-weight "bold"}} total]
                                                 "条"]))}
              pagination)
      :onShowSizeChange
            (fn [current size]
              (when onSizeChange
                (onSizeChange current size)))
      :onChange
            (fn [pagination]
              (when onChange
                (onChange (js->clj pagination :keywordize-keys true))))}
     (dissoc props :pagination :onChange))])

;;图片预览
(defn image-preview []
  (def visible? (r/atom false))
  (def preview-url (r/atom ""))
  (fn []
    (let [props (r/props (r/current-component))]
      [:div
       [:> ant/Tooltip {:title "点击预览"}
        [:a {:on-click (fn []
                         (reset! visible? true)
                         (reset! preview-url (:src props)))}
         [:img props]]]
       [:> ant/Modal {:visible  @visible?
                      :style    {:top 20}
                      :footer   nil
                      :onCancel #(reset! visible? false)}
        [:img
         {:style {:width "100%"}
          :src   @preview-url}]]])))

(defn footer-toolbar []
  (let [this (r/current-component)]
    [:div
     [:link {:rel "stylesheet" :href "/css/footer_toolbar.css"}]
     [:div.toolbar
      (into [:div.right] (r/children this))]]))

(defn remove-btn
  "删除按钮"
  [{:keys [event] :as props}]
  [:> ant/Popconfirm
   (merge
     {:title      (str "确定删除？" (:warning props))
      :okText     "确定"
      :cancleText "取消"
      :onConfirm  (fn []
                    (when event
                      (rf/dispatch event)))}
     props)
   [:a "删除"]])

(defn event-btn
  [{:keys [title event]}]
  [:a {:on-click (fn [] (rf/dispatch event))}
   title])

(defn card-table [{:keys [event code] :as add-event}]
  (let [this (r/current-component)
        props (r/props this)]
    [:div.table-box {:style {:width (:width props)}}
     [:> ant/Card {:title (:title props)
                   :size  "small"
                   :style {:width         "100%"
                           :border-radius "8px"
                           :box-shadow    "0 2px 12px 0 rgba(0,0,0,.1)"}
                   :extra (r/as-element
                           [auth-wrapper
                            code
                            [:> ant/Button (merge
                                            {:type     "primary"
                                             :icon     "plus"
                                             :size     "small"
                                             :disabled (if (nil? (:disabled props)) false true)
                                             :onClick (fn []
                                                        (rf/dispatch event))}
                                            add-event) "新增"]])}
      (into [:div] (r/children this))]]))
