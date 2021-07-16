(ns test-project.url
  (:require
    [test-project.config :refer [domain]]))

;;授权相关url
(def auth-url
  {:token         (str domain "/admin/public/auth/token")
   :refresh-token (str domain "/admin/public/auth/refresh-token")})

;;基础数据url
(def base-url
  {:file-upload   (str domain "/admin/file/upload")
   :base-user     (str domain "/admin/base/user")
   :current-menus (str domain "/admin/sys-user/menus")})

;;系统管理url
(def sys-url
  {:user-list             (str domain "/admin/sys-user/page")
   :user-add              (str domain "/admin/sys-user/save")
   :user-update           (str domain "/admin/sys-user/update")
   :user-remove           #(str domain "/admin/sys-user/remove/" %)
   :user-password         (str domain "/admin/sys-user/change-pwd")
   :current-user-password (str domain "/admin/sys-user/current-user-pwd")
   :user-roles            #(str domain "/admin/sys-role/user/roles/" %)
   :role-list             (str domain "/admin/sys-role/page")
   :all-roles             (str domain "/admin/sys-role/all")
   :role-add              (str domain "/admin/sys-role/save")
   :role-update           (str domain "/admin/sys-role/update")
   :role-remove           #(str domain "/admin/sys-role/remove/" %)
   :role-assign           (str domain "/admin/sys-role/assign/role")
   :role-menus            #(str domain "/admin/sys-menu/role/menus/" %)
   :role-menus-select   (str domain "/admin/sys-menu/menu-cascader")
   :menu-list             (str domain "/admin/sys-menu/list")
   :menu-add              (str domain "/admin/sys-menu/save")
   :menu-update           (str domain "/admin/sys-menu/update")
   :menu-remove           #(str domain "/admin/sys-menu/remove/" %)
   :menu-assign           (str domain "/admin/sys-menu/assign/menu")
   :dict-list             (str domain "/admin/sys/dict/list")
   :dict-add              (str domain "/admin/sys/dict/list")
   :dict-remove           #(str domain "/admin/sys/dict/list?id=" %)})
