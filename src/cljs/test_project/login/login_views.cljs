(ns test-project.login.login-views
  (:require
   ["antd" :as ant]
   [reagent.core :as r]
   [re-frame.core :as rf]
   [test-project.login.login-events :as events]
   [test-project.common.utils :as utils]
   [test-project.layout.global-footer :refer [footer]]))

(def Password (.-Password ant/Input))
(def FormItem (.-Item ant/Form))

(def login-result (rf/subscribe [:login/result]))
(def login-loading (rf/subscribe [:login/loading]))

(defn login-page []
  [:div
   [:link {:rel "stylesheet" :href "/css/login.css"}]
   [:div {:className "container"}
    [:div {:className "top"}
     [:div {:className "header"}
      [:a {:href "/main"}
       [:span {:className "title"} "模板管理后台"]]]
     [:div {:className "desc"} "欢迎使用"]]
    [:div {:className "main"}
     (utils/create-form
       (fn [{:keys [form]}]
         (let [this (utils/get-form)
               item-col {:labelCol {:span 5} :wrapperCol {:span 15}}]
           [:> ant/Form {:onSubmit (fn [e]
                                      (.preventDefault e)
                                      (.validateFields
                                        form
                                        (fn [err values]
                                          (when (not err)
                                            (rf/dispatch [:login (js->clj values :keywordize-keys true)])))))}
            (when (= false @login-result )
              [:> ant/Alert {:style {:marginBottom 10}
                             :message "用户名或密码错误"
                             :type "error"
                             :showIcon true}])
            [:> FormItem
             (utils/decorate-field
               this "username"
               {:rules [{:required true
                         :message "请输入用户名"}]}
               [:> ant/Input {:placeholder "用户名"
                              :size "large"
                              :allowClear true
                              :prefix (r/as-element
                                        [:> ant/Icon {:type "user"
                                                      :className "prefix-icon"}])}])]
            [:> FormItem
             (utils/decorate-field
               this "password"
               {:rules [{:required true
                         :message "请输入密码"}]}
               [:> Password {:type "password"
                              :placeholder "密码"
                              :size "large"
                              :allowClear true
                              :prefix (r/as-element
                                        [:> ant/Icon {:type "lock"
                                                      :className "prefix-icon"}])}])]

            [:> FormItem {:className "additional"}
             [:> ant/Button {:className "submit"
                             :size "large"
                             :type "primary"
                             :htmlType "submit"
                             :loading @login-loading} "登录"]]])))]
    [:div {:className "footer"}
     [footer "红创科技"]]]])
