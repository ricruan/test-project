(ns test-project.layout.layout-views
  (:require
    [re-frame.core :as rf]
    [reagent.core :as r]
    ["antd" :as ant]
    ["antd/es/locale/zh_CN" :default zhCN]
    [test-project.layout.global-footer :refer [footer]]
    [test-project.components.hc-img :refer [size-img]]
    [test-project.common.storage :as storage]
    [test-project.common.utils :as utils]))

(def SubMenu (.-SubMenu ant/Menu))
(def MenuItem (.-Item ant/Menu))
(def Header (.-Header ant/Layout))
(def Sider (.-Sider ant/Layout))
(def Content (.-Content ant/Layout))
(def Footer (.-Footer ant/Layout))
(def BreadcrumbItem (.-Item ant/Breadcrumb))
(def FormItem (.-Item ant/Form))
(def Password (.-Password ant/Input))

(def collapsed (r/atom false))

(defn bread-crumbs [routes]
  [:> ant/Breadcrumb
   [:> BreadcrumbItem "首页"]
   (when routes
     (for [{:keys [title]} routes]
       ^{:key title} [:> BreadcrumbItem title]))])

(defn dropdown-menu []
  [:> ant/Menu {:className "menu"
                :onClick   (fn [menu]
                             (let [key (:key (js->clj menu :keywordize-keys true))]
                               (case key
                                 "change-password" (rf/dispatch [:layout/reset-password {:visible true}])
                                 "logout" (rf/dispatch [:logout]))))}
   [:> MenuItem {:key "change-password"}
    [:> ant/Icon {:type "lock"}]
    [:span "修改密码"]]
   [:> MenuItem {:key "logout"}
    [:> ant/Icon {:type "logout"}]
    [:span "退出"]]])

(defn header-dropdown []
  (let [user (rf/subscribe [:layout/user-base-data])]
    [:div {:className "right"}
     [:> ant/Dropdown {:overlay (r/as-element [dropdown-menu])}
      [:span {:className "action account"}
       [:> ant/Avatar {:className "avatar" :size "small" :icon "user"}]
       [:span (get-in @user [:nickname])]]]]))

(defn- password-form []
  (let [visible (rf/subscribe [:layout/reset-password-modal])
        user (rf/subscribe [:layout/user-base-data])]
    (utils/create-form
      (fn [props]
        (let [form (utils/get-form)]
          [:> ant/Modal
           {:title    "重置密码"
            :visible  (get-in @visible [:visible])
            :onCancel (fn []
                        (rf/dispatch [:layout/reset-password {:visible false}]))
            :onOk     (fn []
                        ((:validateFieldsAndScroll form)
                         (fn [err values]
                           (when (not err)
                             (let [params (js->clj values :keywordize-keys true)]
                               (rf/dispatch [:layout/change-password params]))))))}
           [:> ant/Form
            (utils/decorate-field form "id" {:initialValue (get-in @user [:id])} [:span])
            [:> FormItem {:label      "旧密码:"
                          :labelCol   {:span 4}
                          :wrapperCol {:span 18}}
             (utils/decorate-field
               form "old_password"
               {:rules [{:required true
                         :message  "请输入旧密码"}]}
               [:> Password {:placeholder "请输入旧密码"}])]
            [:> FormItem {:label      "新密码:"
                          :labelCol   {:span 4}
                          :wrapperCol {:span 18}}
             (utils/decorate-field
               form "password"
               {:rules [{:required true
                         :message  "请输入密码"}
						{:pattern "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z\\W]{6,}$"
                         :message "密码至少6位 数字+字母组合"}]}
               [:> Password {:placeholder "请输入新密码"}])]
            [:> FormItem {:label      "校验密码:"
                          :labelCol   {:span 4}
                          :wrapperCol {:span 18}}
             (utils/decorate-field
               form "confirm-password"
               {:rules [{:required true
                         :message  " "}
                        {:validator (fn [rule, value, callback]
                                      (if (= value ((:getFieldValue form) "password"))
                                        (callback)
                                        (callback "两次输入的密码不一致")))}]}
               [:> Password {:placeholder "请再次输入密码"}])]]])))))

(defn basic-layout [side-menus breadcrumbs switch-route]
  [:> ant/ConfigProvider {:locale zhCN}
   [:div
    [:link {:rel "stylesheet" :href "/css/layout.css"}]
    [:> ant/Layout {:style {:height "100vh"}}
     [:> Sider {:style       {:z-index 10}
                :trigger     nil
                :collapsible false
                :collapsed   @collapsed}
      [:div {:className "logo"}
       [size-img {:src    "/img/img_index_icon.png"
                  :style  {:width        "2.0833vw"
                           :height       "2.0833vw"
                           :margin-right "0.6944vw"}
                  :width  50
                  :height 30}]
       "管理后台模板"]
      [side-menus]]
     [:> ant/Layout
      [:> Header {:style {:background "#fff" :padding 0}}
       [:> ant/Icon {:className "trigger"
                     :type      (if-not @collapsed "menu-unfold" "menu-fold")
                     :onClick   (fn [] (swap! collapsed not))}]
       [header-dropdown]]
      [:> ant/PageHeader {:style {:margin "1px 0" :padding "0 24px 8px 24px"}}
       [bread-crumbs breadcrumbs]]
      [:> Content {:style {:margin    "10px 10px"
                           :minHeight "auto"}}
       [password-form]
       [:div switch-route]]
      [:> Footer
       [footer "红创科技"]]]]]])
