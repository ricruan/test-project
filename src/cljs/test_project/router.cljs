(ns test-project.router
  (:require
    [test-project.index :refer [index-page]]
    [test-project.system.user.sys-user-main :refer [sys-user-page]]
    [test-project.system.role.sys-role-main :refer [sys-role-page]]
    [test-project.system.menu.sys-menu-main :refer [sys-menu-page]]
    [test-project.system.dict.sys-dict-main :refer [sys-dict-page]]))

(def routes
  (reagent.core/atom
    [["" {:name  :home
          :title "首页"
          :icon  "home"
          :page  index-page}]
     ["/index" {:name :index :title "首页管理" :icon "home"}]
     ["/system" {:name :system :title "系统管理" :icon "setting"}
      ["/user" {:name  :system-user
                :title "系统用户"
                :page  sys-user-page}]
      ["/role" {:name  :system-role
                :title "系统角色"
                :page  sys-role-page}]
      ["/menu" {:name  :system-menu
                :title "系统菜单"
                :page  sys-menu-page}]
      ["/dict" {:name  :system-dict
                :title "系统字典"
                :page  sys-dict-page}]]]))
