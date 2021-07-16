(ns test-project.modules.base.base-specs
  (:require  [clojure.test :as t]
             [clojure.spec.alpha :as s]
             [spec-tools.core :as st]
             [spec-tools.data-spec :as dt]))



(s/def ::nickname
  (st/spec
   {:spec string?
    :swagger/default "管理员"
    :description "角色名称"}))


(s/def ::data (s/keys :req-un [:base/id ::nickname]))

(s/def ::base-user-body (s/keys :req-un [:base/code :base/msg ::data]))
