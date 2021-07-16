(ns test-project.system.user.sys-user-views
  (:require
    ["antd" :as ant]
    ["moment" :as moment]
    [reagent.core :as r]
    [re-frame.core :as rf]
    [test-project.common.utils :as utils]
	[test-project.common.utils-auth :refer [auth-wrapper]]
    [test-project.components.common-page :as page]))

(def FormItem (.-Item ant/Form))
(def SelectOption (.-Option ant/Select))
(def Password (.-Password ant/Input))
(def CheckboxGroup (.-Group ant/Checkbox))

(defn- search [query]
  (rf/dispatch [:system-user/fetch-list query]))

(def columns [
              {:title "用户名" :dataIndex "username"}
              {:title "昵称" :dataIndex "nickname"}
              {:title "创建时间"
               :dataIndex "create_timestamp"
               :render #(r/as-element [:span (.format (.unix moment %) "YYYY/MM/DD HH:mm:ss")])}
              {:title "操作"
               :align "center"
               :render #(let [data (js->clj %2 :keywordize-keys true)]
                          (r/as-element
                           [:div
                            [auth-wrapper
                             "user:edit"
                             [:span
                              [page/event-btn
                               {:title "编辑"
                                :event [:system-user/user-form-open data]}]
                              [:> ant/Divider {:type "vertical"}]]]
                            [auth-wrapper
                             "user:delete"
                             [:span
                              [page/remove-btn
                               {:event [:system-user/user-remove (:id data)]}]
                              [:> ant/Divider {:type "vertical"}]]]
                            [auth-wrapper
                             "user:role"
                             [:span
                              [page/event-btn
                               {:title "分配角色"
                                :event [:system-user/assign-form-open (:id data)]}]
                              [:> ant/Divider {:type "vertical"}]]]
                            [auth-wrapper
                             "user:edit:password"
                             [page/event-btn
                              {:title "修改密码"
                               :event [:system-user/password-form-open data]}]]]))}])


(defn user-table-search []
  (utils/create-form
    (fn [props]
      (let [this (utils/get-form)]
        [:> ant/Form {:layout "inline"
                      :onSubmit (fn [e]
                                  (.preventDefault e)
                                  ((:validateFields this)
                                   (fn [_ values]
                                     (search (into {:page 0 :size 10} (remove (fn [[k v]] (nil? v)) (js->clj values)))))))}
         [:> ant/Row {:gutter 8}
          [:> ant/Col {:md 4}
           [:> FormItem
            (utils/decorate-field
              this "username"
              [:> ant/Input {:placeholder "用户名"}])]]
          [:> ant/Col {:md 4}
           [:> FormItem
            (utils/decorate-field
              this "nickname"
              [:> ant/Input {:placeholder "昵称"}])]]
          [:> ant/Col {:md 4}
           [:span {:className "submit-buttons"}
            [:> ant/Button {:type "primary" :htmlType "submit"} "查询"]
            [:> ant/Button
             {:style {:margin-left 10}
              :onClick (fn []
                          ((:resetFields this))
                          (search nil))} "重置"]]]]]))))

(defn user-table [{:keys [data loading pagination]}]
  [:div
   [page/main-page
    {:title "系统用户列表"
     :event [:system-user/user-form-open]
     :extra (r/as-element
             [auth-wrapper
              "user:add"
              [:> ant/Button
               {:type     "primary"
                :icon     "plus"
                :on-click #(rf/dispatch [:system-user/user-form-open])
                :style    {:float "right"}} "新建"]])}
    [:div
     [:> ant/Row {:style {:margin-bottom 20}}
      [:> ant/Col {:span 22}
       [user-table-search]]]
     [page/pagination-table
      {:rowKey "id"
       :columns columns
       :loading (or loading false)
       :dataSource data
       :pagination pagination
       :onSizeChange (fn [current size] (search {:page (dec current) :size size}))
       :onChange (fn [{:keys [current pageSize] :as pagination}] (search {:page (dec current) :size pageSize}))}]]]])


(defn user-form [{:keys [visible data loading submit-loading]}]
  (utils/create-form
    (fn [props]
      (let [this (utils/get-form)
            item-col {:labelCol {:span 5} :wrapperCol {:span 15}}]
        [:> ant/Modal {:title (if (:id data) "更新系统用户" "添加系统用户")
                       :closable false
                       :visible visible
                       :confirmLoading (or submit-loading false)
                       :onCancel (fn [] (rf/dispatch [:system-user/user-form-close]))
                       :onOk (fn []
                               ((:validateFieldsAndScroll this)
                                (fn [err values]
                                  (when (not err)
                                    (let [params (js->clj values :keywordize-keys true)]
                                      (rf/dispatch [:system-user/update params]))))))}
         (utils/decorate-field this "id" {:initialValue (:id data)} [:span])
         [:> ant/Form
          [:> FormItem (merge {:label "用户名"} item-col)
           (utils/decorate-field
             this "username"
             {:rules [{:required true
                       :message "请输入用户名"}]
              :initialValue (:username data)}
             [:> ant/Input {:placeholder "请输入用户名"}])]
          [:> FormItem (merge {:label "昵称"} item-col)
           (utils/decorate-field
             this "nickname"
             {:rules [{:required true
                       :message "请输入昵称"}]
              :initialValue (:nickname data)}
             [:> ant/Input {:placeholder "请输入昵称"}])]
          (when (nil? (:id data))
            [:> FormItem (merge {:label "密码"} item-col)
             (utils/decorate-field
               this "password"
               {:rules [{:required true
                         :message "请输入密码"}
						{:pattern "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z\\W]{6,}$"
                         :message "密码至少6位 数字+字母组合"}]
                :initialValue (:password data)}
               [:> Password {:placeholder "请输入密码"}])])]]))))

(defn password-form [{:keys [visible data submit-loading]}]
  (utils/create-form
    (fn [props]
      (let [this (utils/get-form)
            item-col {:labelCol {:span 5} :wrapperCol {:span 15}}]
        [:> ant/Modal {:title          "修改密码"
                       :closable       false
                       :visible        visible
                       :confirmLoading (or submit-loading false)
                       :onCancel       (fn [] (rf/dispatch [:system-user/password-form-close]))
                       :onOk           (fn []
                                         ((:validateFieldsAndScroll this)
                                          (fn [err values]
                                            (when (not err)
                                              (let [params (js->clj values :keywordize-keys true)]
                                                (rf/dispatch [:system-user/change-password params]))))))}
         [:> ant/Form
          (utils/decorate-field this "id" {:initialValue (:id data)} [:span])
          [:> FormItem (merge {:label "密码"} item-col)
           (utils/decorate-field
             this "password"
             {:rules [{:required true
                       :message  "请输入密码"}
					  {:pattern "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z\\W]{6,}$"
                       :message "密码至少6位 数字+字母组合"}]}
             [:> Password {:placeholder "请输入密码"}])]
          [:> FormItem (merge {:label "校验密码"} item-col)
           (utils/decorate-field
             this "confirm-password"
             {:rules [{:required true
                       :message  " "}
                      {:validator (fn [rule, value, callback]
                                    (if (= value ((:getFieldValue this) "password"))
                                      (callback)
                                      (callback "两次输入的密码不一致")))}]}
             [:> Password {:placeholder "请再次输入密码"}])]]]))))

(defn assign-role-form
  "分配角色"
  [{:keys [visible submit-loading data]}]
  (utils/create-form
    (fn [props]
      (let [this (utils/get-form)]
        [:> ant/Modal {:title "分配角色"
                       :closable false
                       :visible visible
                       :confirmLoading (or submit-loading false)
                       :onCancel (fn [] (rf/dispatch [:system-user/assign-form-close]))
                       :onOk (fn []
                               ((:validateFieldsAndScroll this)
                                (fn [err values]
                                  (when (not err)
                                    (let [params (js->clj values :keywordize-keys true)]
                                      (rf/dispatch [:system-user/assign-role params]))))))}
         [:> ant/Form
          (utils/decorate-field this "user_id" {:initialValue (:user-id data)} [:span])
          [:> FormItem
           (utils/decorate-field
             this "role_ids"
             {:initialValue (or (:values data) [])}
             [:> CheckboxGroup
              {:options (or (:options data) [])}])]]]))))
