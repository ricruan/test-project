(ns test-project.modules.wx.wx-specs
  (:require
   [clojure.spec.alpha :as s]
   [spec-tools.core :as st]
   [spec-tools.data-spec :as ds]
   [test-project.modules.sys.sys-specs :as sys-specs]))

(s/def ::ai_app_secret
  (st/spec
   {:spce (s/nilable string?)
    :description "智量secret"}))

(s/def ::wx_cert_pwd
  (st/spec
   {:spce (s/nilable string?)
    :description "小程序支付证书密码"}))

(s/def ::wx_app_id
  (st/spec
   {:spce (s/nilable string?)
    :description "小程序id"}))

(s/def ::wx_api_secret
  (st/spec
   {:spce (s/nilable string?)
    :description "小程序支付API密钥"}))

(s/def ::qiniu_bucket
  (st/spec
   {:spce (s/nilable string?)
    :description "七牛BUCKET"}))

(s/def ::wx_certificate_path
  (st/spec
   {:spce (s/nilable string?)
    :description "微信证书存放路径"}))

(s/def ::wx_mch_id
  (st/spec
   {:spce (s/nilable string?)
    :description "小程序商户id"}))

(s/def ::app_id
  (st/spec
   {:spce (s/nilable string?)
    :description "主键"}))

(s/def ::qiniu_ak
  (st/spec
   {:spce (s/nilable string?)
    :description "七牛accessToken"}))

(s/def ::wx_app_secret
  (st/spec
   {:spce (s/nilable string?)
    :description "小程序密钥"}))

(s/def ::qiniu_base_url
  (st/spec
   {:spce (s/nilable string?)
    :description "七牛基础url地址"}))

(s/def ::wx_cert_path
  (st/spec
   {:spce (s/nilable string?)
    :description "小程序支付证书路径"}))

(s/def ::wx_notify_url
  (st/spec
   {:spce (s/nilable string?)
    :description "微信支付回调url"}))

(s/def ::qiniu_sk
  (st/spec
   {:spce (s/nilable string?)
    :description "七牛secretToken"}))

(s/def ::ai_app_id
  (st/spec
   {:spce (s/nilable string?)
    :description "智量id"}))

(s/def ::wx_pay_body
  (st/spec
   {:spce (s/nilable string?)
    :description "微信支付描述"}))

(s/def ::app_name
  (st/spec
   {:spce (s/nilable string?)
    :description "app名称"}))

(s/def ::data (s/keys :req-un [::sys-specs/update_time ::ai_app_secret ::wx_cert_pwd ::wx_app_id ::sys-specs/create_user_id
                               ::sys-specs/remark ::wx_api_secret ::qiniu_bucket ::wx_certificate_path ::wx_mch_id ::app_id
                               ::qiniu_ak ::wx_app_secret ::qiniu_base_url ::wx_cert_path ::sys-specs/create_time
                               ::wx_notify_url ::sys-specs/delete_flag ::sys-specs/update_user_id ::qiniu_sk ::ai_app_id
                               ::wx_pay_body ::app_name]))

(s/def ::wx-app-list-body (s/keys :req-un [:base/code :base/msg ::data]))
