(ns test-project.modules.sys.sys-routes
  (:require
   [clojure.spec.alpha :as s]
   [spec-tools.core :as st]
   [spec-tools.data-spec :as ds]
   [test-project.common.result :refer [ok sorry]]
   [test-project.modules.sys.sys-user-service :as service]
   [test-project.modules.sys.sys-role-service :as role-service]
   [test-project.modules.sys.sys-specs :as sys-specs]
   [test-project.modules.sys.sys-menu-service :as menu-service]))

(s/def ::id
  (st/spec
    {:spec string?
     :description "系统用户ID"}))

(s/def ::username
  (st/spec
    {:spec string?
     :description "用户名"}))

(s/def ::nickname
  (st/spec
    {:spec string?
     :description "昵称"}))

(s/def ::password
  (st/spec
    {:spec string?
     :description "密码"}))

(s/def ::old_password
  (st/spec
    {:spec        string?
     :description "旧密码"}))

(def users-page-spec
  {:page :base/page
   :size :base/size
   (ds/opt :username) ::username
   (ds/opt :nickname) ::nickname})

(def user-save-spec
  {:username ::username
   :nickname ::nickname
   :password ::password})

(def user-update-spec
  {:id ::id
   :username ::username
   :nickname ::nickname})

(def change-user-password-spec
  {:id           ::id
   :old_password ::old_password
   :password     ::password})

(def change-password-spec
  {:id ::id
   :password ::password})

(defn sys-user-routes []
  ["/sys-user"
   {:swagger {:tags ["后台-系统用户管理"]}}

   ["/menus"
    {:get {:summary "当前用户菜单列表"
           :responses {200 {:body ::sys-specs/sys-user-menus-body}}
           :handler (fn [{user :current-user}]
                      (-> (service/get-current-user-menus user)
                          (ok)))}}]
   ["/page"
    {:get {:summary "系统用户分页列表"
           :parameters {:query users-page-spec}
           :responses {200 {:body ::sys-specs/sys-user-page-body}}
           :handler (fn [{{query :query} :parameters}]
                      (-> (service/sys-user-page query)
                          (ok)))}}]

   ["/save"
    {:post {:summary "保存系统用户"
            :parameters {:body user-save-spec}
            :responses {200 {:body :base/body}}
            :handler (fn [{{params :body} :parameters
                           user :current-user}]
                       (if (service/reg-username? params)
                         (sorry -1 "用户名已经存在")
                         (do
                           (service/save-sys-user params user)
                           (ok))))}}]

   ["/update"
    {:post {:summary "更新系统用户"
            :parameters {:body user-update-spec}
            :responses {200 {:body :base/body}}
            :handler (fn [{{params :body} :parameters
                           user :current-user}]
                       (if (service/reg-username? params)
                         (sorry -1 "用户名已经存在")
                         (do
                           (service/update-sys-user params user)
                           (ok))))}}]

   ["/current-user-pwd"
    {:post {:summary    "当前使用者修改密码(需验证旧密码)"
            :parameters {:body change-user-password-spec}
            :responses {200 {:body :base/body}}
            :handler    (fn [{{params :body} :parameters
                              user           :current-user}]
                          (ok (service/update-user-password params user)))}}]

   ["/change-pwd"
    {:post {:summary "修改密码"
            :parameters {:body change-password-spec}
            :responses {200 {:body :base/body}}
            :handler (fn [{{params :body} :parameters
                           user :current-user}]
                       (service/update-password params user)
                       (ok))}}]

   ["/remove/:id"
    {:delete {:summary "系统用户删除"
              :parameters {:path (s/keys :req-un [::id])}
              :responses {200 {:body :base/body}}
              :handler (fn [{{{:keys [id]} :path} :parameters
                             user :current-user}]
                         (service/remove-sys-user id user)
                         (ok))}}]])

(s/def ::role_id
  (st/spec
    {:spec string?
     :description "系统角色ID"}))

(def role-page-spec
  {:page :base/page
   :size :base/size
   (ds/opt :name) string?
   (ds/opt :code) string?})

(def role-save-spec
  {:name string?
   :code string?})

(def role-update-spec
  {:id string?
   :name string?
   :code string?})

(def role-assign-spec
  {:user_id string?
   :role_ids [string?]})

(defn sys-role-routes []
  ["/sys-role"
   {:swagger {:tags ["后台-系统角色管理"]}}

   ["/page"
    {:get {:summary "系统角色分页列表"
           :parameters {:query role-page-spec}
           :responses {200 {:body ::sys-specs/sys-role-page-body}}
           :handler (fn [{{query :query} :parameters}]
                      (-> (role-service/sys-role-page query)
                          (ok)))}}]

   ["/save"
    {:post {:summary "保存系统角色"
            :parameters {:body role-save-spec}
            :responses {200 {:body :base/body}}
            :handler (fn [{{params :body} :parameters}]
                       (-> (role-service/save-sys-role params)
                           (ok)))}}]

   ["/update"
    {:post {:summary "更新系统角色"
            :parameters {:body role-update-spec}
            :responses {200 {:body :base/body}}
            :handler (fn [{{params :body} :parameters}]
                       (-> (role-service/update-sys-role params)
                           (ok)))}}]

   ["/remove/:role_id"
    {:delete {:summary "系统角色删除"
              :parameters {:path (s/keys :req-un [::role_id])}
              :responses {200 {:body :base/body}}
              :handler (fn [{{{:keys [role_id]} :path} :parameters
                             user :current-user}]
                         (role-service/remove-sys-role role_id user)
                         (ok))}}]
   ["/user/roles/:id"
    {:get {:summary "系统用户角色"
           :parameters {:path (s/keys :req-un [::id])}
           :responses {200 {:body ::sys-specs/sys-role-user-roles-body}}
           :handler (fn [{{{:keys [id]} :path} :parameters}]
                      (-> (role-service/get-user-roles id)
                          (ok)))}}]
   ["/assign/role"
    {:post {:summary "分配用户角色"
            :parameters {:body role-assign-spec}
            :responses {200 {:body :base/body}}
            :handler (fn [{{{:keys [role_ids user_id]} :body} :parameters}]
                       (-> (role-service/assign-user-role user_id role_ids)
                           (ok)))}}]])



(s/def ::menu_id
  (st/spec
    {:spec string?
     :description "系统菜单ID"}))

(def menu-list-spec
  {(ds/opt :name) string?
   (ds/opt :code) string?})

(def menu-save-spec
  {:parent_id string?
   :name string?
   :code string?
   :path any?
   :icon any?
   :sort any?
   :menu_type int?})

(def menu-update-spec
  (merge menu-save-spec {:id string?}))

(def menu-assign-spec
  {:role_id string?
   :menu_ids [string?]})

(defn sys-menu-routes []
  ["/sys-menu"
   {:swagger {:tags ["后台-系统菜单管理"]}}

   ["/list"
    {:get {:summary "系统菜单分页列表"
           :parameters {:query menu-list-spec}
           :responses {200 {:body ::sys-specs/sys-menu-list-body}}
           :handler (fn [{{query :query} :parameters}]
                      (-> (menu-service/get-sys-menus query)
                          (ok)))}}]

   ["/save"
    {:post {:summary "保存系统菜单"
            :parameters {:body menu-save-spec}
            :responses {200 {:body :base/body}}
            :handler (fn [{{params :body} :parameters}]
                       (-> (menu-service/save-sys-menu params)
                           (ok)))}}]

   ["/update"
    {:post {:summary "更新系统菜单"
            :parameters {:body menu-update-spec}
            :responses {200 {:body :base/body}}
            :handler (fn [{{params :body} :parameters}]
                       (-> (menu-service/update-sys-menu params)
                           (ok)))}}]

   ["/remove/:menu_id"
    {:delete {:summary "系统菜单删除"
              :parameters {:path (s/keys :req-un [::menu_id])}
              :responses {200 {:body :base/body}}
              :handler (fn [{{{:keys [menu_id]} :path} :parameters}]
                         (menu-service/remove-sys-menu menu_id)
                         (ok))}}]

   ["/menu-cascader"
    {:get {:summary "获取系统菜单选择级联数据"
           :responses {200 {:body ::sys-specs/sys-menu-menu-cascader-body}}
           :handler (fn [{user :current-user}]
                      (-> (menu-service/get-sys-menus-cascader)
                          (ok)))}}]

   ["/role/menus/:role_id"
    {:get {:summary "角色所属菜单"
           :parameters {:path (s/keys :req-un [::role_id])}
           :responses {200 {:body ::sys-specs/sys-menu-role-menus-body}}
           :handler (fn [{{{:keys [role_id]} :path} :parameters}]
                      (-> (menu-service/get-role-menus role_id)
                          (ok)))}}]
   ["/assign/menu"
    {:post {:summary "角色分配菜单"
            :parameters {:body menu-assign-spec}
            :responses {200 {:body :base/body}}
            :handler (fn [{{{:keys [role_id menu_ids]} :body} :parameters}]
                       (-> (menu-service/assign-role-menu role_id menu_ids)
                           (ok)))}}]])
