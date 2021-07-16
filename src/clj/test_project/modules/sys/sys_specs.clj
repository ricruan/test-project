(ns test-project.modules.sys.sys-specs
  (:require  [clojure.test :as t]
             [clojure.spec.alpha :as s]
             [spec-tools.core :as st]
             [spec-tools.data-spec :as dt]
             [test-project.modules.base.base-specs :as base-specs]))

(s/def ::parent_id
  (st/spec
   {:spec (s/nilable any?)
    :swagger/default "0"
    :description "父级id"}))

(s/def ::name
  (st/spec
   {:spec (s/nilable string?)
    :swagger/default "首页"
    :description "菜单名称"}))

(s/def ::code
  (st/spec
   {:spec (s/nilable string?)
    :swagger/default "home"
    :description "菜单编码"}))

(s/def ::path
  (st/spec
   {:spec (s/nilable string?)
    :swagger/default "/main"
    :description "路由地址"}))

(s/def ::icon
  (st/spec
   {:spec (s/nilable string?)
    :swagger/default "home"
    :description "图标"}))

(s/def ::sort
  (st/spec
   {:spec (s/nilable int?)
    :swagger/default 0
    :description "排序"}))

(s/def ::menu_type
  (st/spec
   {:spec int?
    :swagger/default 0
    :description "0:菜单;1:功能"}))

(s/def ::update_time
  (st/spec
   {:spec any?
    :swagger/default "2019-11-25"
    :description "更新时间"}))

(s/def ::update_timestamp
  (st/spec
   {:spec number?
    :swagger/default 1574611200
    :description "更新时间戳"}))

(s/def ::create_time
  (st/spec
   {:spec any?
    :swagger/default "2019-11-25"
    :description "创建时间"}))

(s/def ::create_timestamp
  (st/spec
   {:spec number?
    :swagger/default 1574611200
    :description "创建时间戳"}))

(s/def ::create_user_id
  (st/spec
   {:spec (s/nilable string?)
    :swagger/default "1"
    :description "创建用户"}))

(s/def ::update_user_id
  (st/spec
   {:spec (s/nilable string?)
    :swagger/default "1"
    :description "更新用户"}))

(s/def ::total
  (st/spec
   {:spec number?
    :swagger/default 1
    :description "总条数"}))

(s/def ::total-elements
  (st/spec
   {:spec number?
    :swagger/default 1
    :description "总条数"}))


(s/def ::password
  (st/spec
   {:spec string?
    :swagger/default "123456"
    :description "密码"}))

(s/def ::username
  (st/spec
   {:spec string?
    :swagger/default "admin"
    :description "用户名"}))

(s/def ::delete_flag
  (st/spec
   {:spec any?
    :swagger/default 0
    :description "0:未删除 1:删除"}))

(s/def ::deleted
  (st/spec
   {:spec int?
    :swagger/default 0
    :description "0:未删除 1:删除"}))

(s/def ::selected
  (st/spec
   {:spec boolean?
    :swagger/default true
    :description "选择"}))

(s/def ::value
  (st/spec
   {:spec (s/nilable string?)
    :swagger/default "9"
    :description "值"}))

(s/def ::label
  (st/spec
   {:spec (s/nilable string?)
    :swagger/default "用户管理"
    :description "label"}))

(s/def ::children
  (st/spec
   {:spec (s/nilable  any?)
    :swagger/default ""
    :description "children里面的参数和父类的相同"}))

(s/def ::type
  (st/spec
   {:spec (s/nilable  int?)
    :swagger/default 0
    :description  "类型：0：索引，1：字典值" }))

(s/def ::group_code
  (st/spec
   {:spec (s/nilable  string?)
    :swagger/default "测试分组"
    :description  "分组" }))

(s/def ::remark
  (st/spec
   {:spec (s/nilable  string?)
    :description  "备注" }))

;;user data
(s/def :sys-user-menus/data (s/coll-of (s/keys :req-un [:base/id ::parent_id ::name ::code ::path ::icon ::sort ::menu_type])))
(s/def :sys-user-page/list (s/coll-of (s/keys :req-un [:base/id ::update_time ::create_user_id ::password ::base-specs/nickname ::username
                                                       ::create_timestamp ::create_time ::delete_flag ::update_user_id ::update_timestamp])))
(s/def :sys-user-page/data (s/keys :req-un [:base/page :base/size ::total :sys-user-page/list]))

;;role data
(s/def :sys-role-page/list (s/coll-of (s/keys :req-un [:base/id ::name ::code ::delete_flag ::create_time])))
(s/def :sys-role-page/data (s/keys :req-un [:base/page :base/size ::total :sys-role-page/list]))
(s/def :sys-role-user-roles/data (s/coll-of (s/keys :req-un [:base/id ::name ::code ::selected])))

;;menu data
(s/def :sys-menu-list/list (s/coll-of (s/keys :req-un [::path ::children ::menu_type ::name ::value ::icon ::parent_id
                                                       ::label :base/id ::create_time ::delete_flag ::code ::sort])))
(s/def :sys-menu-list/data (s/keys :req-un [::total :sys-menu-list/list]))
(s/def :sys-menu-menu-cascader/children (s/coll-of (s/keys :req-un [::path ::children ::menu_type  ::value ::icon ::parent_id
                                                                    ::label ::create_time ::delete_flag ::code ::sort])))
(s/def :sys-menu-menu-cascader/data (s/coll-of (s/keys :req-un [:sys-menu-menu-cascader/children ::value ::label])))
(s/def :sys-menu-role-menus/data (s/coll-of (s/keys :req-un [::path ::selected ::menu_type ::name ::icon ::parent_id
                                                             :base/id ::create_time ::delete_flag ::code ::sort])))

;;dict data
(s/def :sys-dict-list/list (s/coll-of (s/keys :req-un [::update_time ::deleted ::name ::type ::parent_id
                                                       :base/id ::create_time ::code ::sort  ::group_code])))
(s/def :sys-dict-list/data (s/keys :req-un [::total-elements :base/page :base/size :sys-dict-list/list]))

;;user Response Body
(s/def ::sys-user-menus-body (s/keys :req-un [:base/code :base/msg :sys-user-menus/data]))
(s/def ::sys-user-page-body (s/keys :req-un [:base/code :base/msg :sys-user-page/data]))

;;role Response Body
(s/def ::sys-role-page-body (s/keys :req-un [:base/code :base/msg :sys-role-page/data]))
(s/def ::sys-role-user-roles-body (s/keys :req-un [:base/code :base/msg :sys-role-user-roles/data]))

;;menu Response Body
(s/def ::sys-menu-list-body (s/keys :req-un [:base/code :base/msg :sys-menu-list/data]))
(s/def ::sys-menu-menu-cascader-body (s/keys :req-un [:base/code :base/msg :sys-menu-menu-cascader/data]))
(s/def ::sys-menu-role-menus-body (s/keys :req-un [:base/code :base/msg :sys-menu-role-menus/data]))

;;dict Response Body
(s/def ::sys-dict-list-body (s/keys :req-un [:base/code :base/msg :sys-dict-list/data]))
