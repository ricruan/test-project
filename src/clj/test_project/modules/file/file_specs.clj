(ns test-project.modules.file.file-specs
  (:require  [clojure.test :as t]
             [clojure.spec.alpha :as s]
             [spec-tools.core :as st]
             [spec-tools.data-spec :as dt]))

(s/def ::file-url
  (st/spec
   {:spec string?
    :swagger/default "https://hcapplet.3vyd.com/files/1/20210317154149/11.jpg"
    :description "文件地址"}))



(s/def ::data (s/keys :req-un [::file-url]))

(s/def ::file-upload (s/keys :req-un [:base/code :base/msg ::data]))

