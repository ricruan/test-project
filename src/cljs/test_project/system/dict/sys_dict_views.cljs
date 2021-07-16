(ns test-project.system.dict.sys-dict-views
  (:require
    ["antd" :as ant]
    ["moment" :as moment]
    [reagent.core :as r]
    [re-frame.core :as rf]
    [test-project.common.utils :as utils]
	  [test-project.common.utils-auth :refer [auth-wrapper]]
    [test-project.components.common-page :as page]))

(def FormItem (.-Item ant/Form))

(defn search-list [query]
  (rf/dispatch [:system-dict/fetch-list query]))

(defn search-details [query]
  (rf/dispatch [:system-dict/fetch-details query]))

(def list-columns [
                   {:title "字典名称" :dataIndex "name"}
                   {:title "编码" :dataIndex "code"}
                   {:title "分组索引" :dataIndex "group_code"}
                   {:title  "操作"
                    :align  "center"
                    :render (fn [record]
                              (r/as-element
                               [:div
                                [auth-wrapper
                                 "dict:list:edit"
                                 [:span
                                  [page/event-btn
                                   {:title "修改"
                                    :event [:system-dict/dict-list-modal {:record record :visible true :attribute "edit"}]}]
                                  [:> ant/Divider {:type "vertical"}]]]
                                [auth-wrapper
                                 "dict:list:delete"
                                 [page/remove-btn
                                  {:event [:system-user/list-remove (:id (js->clj record :keywordize-keys true))]}]]]))}])

(def details-columns [
                      {:title "字典名称" :dataIndex "name"}
                      {:title "编码" :dataIndex "code"}
                      {:title "上级节点" :dataIndex "parent_id"}
                      {:title "排序" :dataIndex "sort"}
                      {:title  "操作"
                       :align  "center"
                       :render (fn [record]
                                 (r/as-element
                                  [:div
                                   [auth-wrapper
                                    "dict:detail:edit"
                                    [:span
                                     [page/event-btn
                                      {:title "修改"
                                       :event [:system-dict/dict-details-modal {:record    record
                                                                                :visible   true
                                                                                :attribute "edit"
                                                                                :parent_id (:parent_id (js->clj record :keywordize-keys true))}]}]
                                     [:> ant/Divider {:type "vertical"}]]]

                                   [auth-wrapper
                                    "dict:detail:delete"
                                    [page/remove-btn
                                     {:event [:system-user/details-remove (:id (js->clj record :keywordize-keys true))]}]]]))}])

(defn list-search [value]
  [:div.table-search
   [:> ant/Input {:onChange #(reset! value (-> % .-target .-value))
                  :style     {:width         "15vw"
                              :margin-right  "2vw"
                              :margin-bottom "1vw"}}]
   [:> ant/Button {:type     "primary"
                   :htmlType "submit"
                   :onClick (fn []
                               (search-list {:page 0 :size 10 :name @value}))} "查询"]])

(defn details-search [value parent_id dict-details-title]
  [:div.table-search
   [:> ant/Input {:onChange #(reset! value (-> % .-target .-value))
                  :style     {:width         "15vw"
                              :margin-right  "2vw"
                              :margin-bottom "1vw"}}]
   [:> ant/Button {:type     "primary"
                   :htmlType "submit"
                   :disabled (if (nil? dict-details-title) true false)
                   :onClick (fn []
                               (search-details {:page 0 :size 10 :parent_id @parent_id :name @value}))} "查询"]])

(defn dict-table []
  (let [list-keyword (r/atom "")
        details-keyword (r/atom "")
        parent_id (r/atom nil)]
    (fn [{:keys [dict-list-table list-pagination dict-details-table dict-details-title details-pagination]}]
      [:div
       [page/main-page {:title "字典管理"}
        [:div
         [:> ant/Row {:style {:margin-bottom 20}}
          [:> ant/Col {:span 22}]]
         [:div {:style {:display         "flex"
                        :justify-content "space-between"}}
          [page/card-table {:title "字典列表"
                            :width "40vw"
                            :event [:system-dict/dict-list-modal {:visible true :attribute "add"}]
							:code "dict:list:add"}
           [list-search list-keyword]
           [page/pagination-table
            {:rowKey            "id"
             :columns           list-columns
             :showQuickJumper false
             :loading           (or (get-in dict-list-table [:loading]) false)
             :dataSource        (get-in dict-list-table [:data :list])
             :pagination        list-pagination
             :onRowClick      (fn [record]
                                  (reset! parent_id (:id (js->clj record :keywordize-keys true)))
                                  (rf/dispatch [:system-dict/fetch-details {:parent_id @parent_id}])
                                  (rf/dispatch [:system-dict/dict-details-title-save {:title (:name (js->clj record :keywordize-keys true))}]))
             :onSizeChange    (fn [current size] (search-list {:page (dec current) :size size :name @list-keyword}))
             :onChange         (fn [{:keys [current pageSize] :as list-pagination}] (search-list {:page (dec current) :size pageSize :name @list-keyword}))}]]
          [page/card-table {:title (str (if (nil? dict-details-title) "字典" dict-details-title) "详情")
                            :width "40vw"
                            :disabled (if (nil? dict-details-title) true false)
                            :event [:system-dict/dict-details-modal {:visible true :attribute "add" :parent_id @parent_id}]
							:code "dict:detail:add"}
           [details-search details-keyword parent_id dict-details-title]
           [page/pagination-table
            {:rowKey            "id"
             :columns           details-columns
             :showQuickJumper false
             :loading           (or (get-in dict-details-table [:loading]) false)
             :dataSource        (get-in dict-details-table [:data :list])
             :pagination        details-pagination
             :onSizeChange    (fn [current size] (search-details {:page (dec current) :size size :parent_id @parent_id :name @details-keyword}))
             :onChange         (fn [{:keys [current pageSize] :as details-pagination}] (search-details {:page (dec current) :size pageSize :parent_id @parent_id :name @details-keyword}))}]]]]]])))

;;字典列表model
(defn dict-list-modal [{:keys [visible data attribute]}]
  (utils/create-form
    (fn [props]
      (let [form (utils/get-form)
            set-fields-value (:setFieldsValue form)]
        [:> ant/Modal
         {:title    (if (= attribute "add") "添加字典列表modal" (str "编辑" (:name data) "modal"))
          :visible  visible
          :onCancel (fn []
                      (rf/dispatch [:system-dict/dict-list-modal {:visible false :attribute ""}]))
          :onOk     (fn []
                      ((:validateFieldsAndScroll form)
                       (fn [err values]
                         (when (not err)
                           (let [params (r/atom (js->clj values :keywordize-keys true))]
                             (if (= attribute "add")
                               ((rf/dispatch [:system-dict/list-add (merge {:parent_id 0} @params)])
                                (rf/dispatch [:system-dict/dict-list-modal {:visible false :attribute ""}]))
                               ((rf/dispatch [:system-dict/list-edit (merge {:id        (:id data)
                                                                             :parent_id 0
                                                                             :type      0
                                                                             :sort      0} @params)])
                                (rf/dispatch [:system-dict/dict-list-modal {:visible false :attribute ""}]))))))))}
         [:> ant/Form
          [:> FormItem {:label      "字典名称:"
                        :labelCol   {:span 4}
                        :wrapperCol {:span 18}}
           (utils/decorate-field
             form "name"
             {:initialValue (get-in data [:name])
              :rules        [{:required true :message "请输入正确的字典名称"}]}
             [:> ant/Input {:placeholder "请输入字典名称"}])]]
         [:> ant/Form
          [:> FormItem {:label      "代码:"
                        :labelCol   {:span 4}
                        :wrapperCol {:span 18}}
           (utils/decorate-field
             form "code"
             {:initialValue (get-in data [:code])
              :rules        [{:required true :message "请输入正确的代码"}]}
             [:> ant/Input {:placeholder "请输入代码"}])]]
         [:> ant/Form
          [:> FormItem {:label      "分组索引:"
                        :labelCol   {:span 4}
                        :wrapperCol {:span 18}}
           (utils/decorate-field
             form "group_code"
             {:initialValue (get-in data [:group_code])
              :rules        [{:required false :message "请输入正确的索引"}]}
             [:> ant/Input {:placeholder "请输入索引"}])]]]))))

;;字典详情model
(defn dict-details-modal [{:keys [data visible attribute parent_id]}]
  (utils/create-form
    (fn [props]
      (let [form (utils/get-form)
            set-fields-value (:setFieldsValue form)]
        [:> ant/Modal
         {:title    (if (= attribute "add") "添加字典详情modal" (str "编辑" (:name data) "modal"))
          :visible  visible
          :onCancel (fn []
                      (rf/dispatch [:system-dict/dict-details-modal {:visible false :attribute ""}]))
          :onOk     (fn []
                      ((:validateFieldsAndScroll form)
                       (fn [err values]
                         (when (not err)
                           (let [params (r/atom (js->clj values :keywordize-keys true))]
                             (swap! params assoc-in [:sort] (js/parseInt (get-in @params [:sort])))
                             (if (= attribute "add")
                               ((rf/dispatch [:system-dict/details-add (merge {:id         (:id data)
                                                                               :group_code ""
                                                                               :type       1} @params)])
                                (rf/dispatch [:system-dict/dict-details-modal {:visible false :attribute ""}]))
                               ((rf/dispatch [:system-dict/details-edit (merge {:id         (:id data)
                                                                                :group_code ""
                                                                                :type       1} @params)])
                                (rf/dispatch [:system-dict/dict-details-modal {:visible false :attribute ""}]))))))))}

         [:> ant/Form
          [:> FormItem {:label      "字典名称:"
                        :labelCol   {:span 4}
                        :wrapperCol {:span 18}}
           (utils/decorate-field
             form "name"
             {:initialValue (get-in data [:name])
              :rules        [{:required true :message "请输入正确的字典名称"}]}
             [:> ant/Input {:placeholder "请输入字典名称"}])]]
         [:> ant/Form
          [:> FormItem {:label      "代码:"
                        :labelCol   {:span 4}
                        :wrapperCol {:span 18}}
           (utils/decorate-field
             form "code"
             {:initialValue (get-in data [:code])
              :rules        [{:required true :message "请输入正确的代码"}]}
             [:> ant/Input {:placeholder "请输入代码"}])]]
         [:> ant/Form
          [:> FormItem {:label      "排序:"
                        :labelCol   {:span 4}
                        :wrapperCol {:span 18}}
           (utils/decorate-field
             form "sort"
             {:initialValue (get-in data [:sort])
              :rules        [{:required false :message "请输入正确的排序"}]}
             [:> ant/Input {:type        "number"
                            :placeholder "数值从小到大排序"
                            :disabled    (if (nil? data) true false)}])]]
         [:> ant/Form
          [:> FormItem {:label      "修改上级:"
                        :labelCol   {:span 4}
                        :wrapperCol {:span 18}}
           (utils/decorate-field
             form "parent_id"
             {:initialValue parent_id
              :rules        [{:required false :message "请输入正确的目录"}]}
             [:> ant/Input {:placeholder "请输入目录编号"
                            :disabled    (if (nil? data) true false)}])]]]))))
