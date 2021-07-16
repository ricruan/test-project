(ns test-project.system.menu.sys-menu-views
  (:require
   ["antd" :as ant]
   ["moment" :as moment]
   [reagent.core :as r]
   [re-frame.core :as rf]
   [test-project.common.utils :as utils]
   [test-project.common.utils-auth :refer [auth-wrapper]]
   [test-project.components.common-page :as page]))

(def FormItem (.-Item ant/Form))
(def ^:private SelectOption (.-Option ant/Select))
(defn- search [query]
  (rf/dispatch [:system-menu/fetch-list query]))

(def menu [{:key "菜单" :value 0}
           {:key "按钮" :value 1}] )


(def columns [
              {:title "菜单名" :dataIndex "name"}
              {:title "图标"
               :dataIndex "icon"
               :render #(r/as-element [:> ant/Icon {:type %}])}
              {:title "编码" :dataIndex "code"}
              {:title "路径" :dataIndex "path"}
              {:title "排序" :dataIndex "sort"}
              {:title "类型" :dataIndex "menu_type"
               :render (fn [text]
                         (if (= text 0) "菜单" "按钮"))}
              {:title "创建时间"
               :dataIndex "create_time"
               :render #(r/as-element [:span (.format (moment % ) "YYYY/MM/DD HH:mm:ss")])}
              {:title "操作"
               :align "center"
               :render #(let [data (js->clj %2 :keywordize-keys true)]
                          (r/as-element
                           [:div
                            [auth-wrapper
                             "menu:edit"
                             [:span
                              [page/event-btn
                               {:title "编辑"
                                :event [:system-menu/menu-form-open data]}]
                              [:> ant/Divider {:type "vertical"}]]]
                            [auth-wrapper
                             "menu:delete"
                             [page/remove-btn
                              {:event [:system-menu/remove (:id data)]}]]]))}])

(defn table-search []
  (utils/create-form
   (fn [props]
     (let [this (utils/get-form)]
       [:> ant/Form {:layout "inline"
                     :onSubmit (fn [e]
                                 (.preventDefault e)
                                 ((:validateFields this)
                                  (fn [_ values]
                                    (search (into {} (remove (fn [[k v]] (nil? v)) (js->clj values)))))))}
        [:> ant/Row {:gutter 8}
         [:> ant/Col {:md 4}
          [:> FormItem
           (utils/decorate-field
            this "name"
            [:> ant/Input {:placeholder "菜单名称"}])]]
         [:> ant/Col {:md 4}
          [:> FormItem
           (utils/decorate-field
            this "code"
            [:> ant/Input {:placeholder "菜单编码"}])]]
         [:> ant/Col {:md 4}
          [:span {:className "submit-buttons"}
           [:> ant/Button {:type "primary" :htmlType "submit"} "查询"]
           [:> ant/Button
            {:style {:margin-left 10}
             :onClick (fn []
                         ((:resetFields this))
                         (search nil))} "重置"]]]]]))))

(defn menu-table [{:keys [data loading]}]
  [:div
   [page/main-page {:title "系统菜单列表"
                    :event [:system-menu/menu-form-open nil]
                    :extra (r/as-element
                            [auth-wrapper
                             "menu:add"
                             [:> ant/Button
                              {:type     "primary"
                               :icon     "plus"
                               :on-click #(rf/dispatch [:system-menu/menu-form-open])
                               :style    {:float "right"}} "新建"]])}
    [:div
     [:> ant/Row {:style {:margin-bottom 20}}
      [:> ant/Col {:span 22}
       [table-search]]]
     [:> ant/Table
      {:rowKey "id"
       :pagination false
       :columns columns
       :loading (or loading false)
       :dataSource data}]]]])

(defn menu-form []
  (utils/create-form
   (fn [props]
     (let [this (utils/get-form)
           item-col {:labelCol {:span 5} :wrapperCol {:span 15}}
           {:keys [visible data submit-loading]} @(rf/subscribe [:system-menu/menu-form])]
       [:> ant/Modal {:title (if (:id data) "更新系统菜单" "添加系统菜单")
                      :style {:top 20}
                      :closable false
                      :visible visible
                      :confirmLoading (or submit-loading false)
                      :afterClose #((:resetFields this))
                      :onCancel (fn []
                                  (rf/dispatch [:system-menu/menu-form-close]))
                      :onOk (fn []
                              ((:validateFieldsAndScroll this)
                               (fn [err values]
                                 (when (not err)
                                   (let [params (js->clj values :keywordize-keys true)
                                         vec (merge params {:menu_type (js/Number (:menu_type  params))} )]
                                     (rf/dispatch [:system-menu/update vec]))))))}
        (utils/decorate-field this "id" {:initialValue (:id data)} [:span])
        [:> ant/Form
         [:> FormItem (merge {:label "名称"} item-col)
          (utils/decorate-field
           this "name"
           {:rules [{:required true
                     :message "请输入名称"}]
            :initialValue (:name data)}
           [:> ant/Input {:placeholder "请输入名称"}])]
         [:> FormItem (merge {:label "编码"} item-col)
          (utils/decorate-field
           this "code"
           {:rules [{:required true
                     :message "请输入编码"}]
            :initialValue (:code data)}
           [:> ant/Input {:placeholder "请输入编码"}])]
         [:> FormItem (merge {:label "父菜单"} item-col)
          (utils/decorate-field
           this "parent_id"
           {:rules [{:required true
                     :message "请选择父菜单"}]
            :initialValue (:parent-ids data)}
           [:> ant/Cascader
            {:options @(rf/subscribe [:system-menu/menu-select])
             :changeOnSelect true
             :placeholder "请选择父菜单"}])]
         [:> FormItem (merge {:label "路径"} item-col)
          (utils/decorate-field
           this "path"
           {:initialValue (:path data)}
           [:> ant/Input {:placeholder "请输入路径"}])]
         [:> FormItem (merge {:label "类型"} item-col)
          (utils/decorate-field
           this "menu_type"
           {:rules [{:required true
                     :message "请选择类型"}]
            :initialValue  (:menu_type data)}
           [:> ant/Select {:placeholder "请选择类型" :defaultValue (:menu_type data) }
            (doall
             (for [item menu]
               ^{:key (:value item)}
               [:> SelectOption {:value (:value item)} (:key item)]))])]
         [:> FormItem (merge {:label "图标"} item-col)
          (utils/decorate-field
           this "icon"
           {:initialValue (:icon data)}
           [:> ant/Input {:placeholder "请输入图标"}])]
         [:> FormItem (merge {:label "排序"} item-col)
          (utils/decorate-field
           this "sort"
           {:initialValue (:sort data)}
           [:> ant/Input {:placeholder "请输入排序"}])]]]))))
