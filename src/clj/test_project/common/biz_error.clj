(ns test-project.common.biz-error
  (:require
   [clojure.tools.logging :as log]))

(declare biz-error-map)

(defn- custom-throw
  ([code msg]
   (throw (ex-info "服务器错误"
                   {:type :biz/error
                    :code code
                    :msg msg
                    :data nil})))
  ([code msg data]
   (throw (ex-info "服务器错误"
                   {:type :biz/error
                    :code code
                    :msg msg
                    :data data}))))

(defn throw-error
  "抛出系统错误
  case1：通过code在常量里获取msg
  case2: code + data"
  ([error-code]
   (let [error-text (get biz-error-map error-code)]
     (log/error "发生系统错误."
                "\n错误码:" error-code
                "\n错误描述:" error-text)
     (custom-throw error-code error-text)))
  ([error-code obj]
   (let [error-text (get biz-error-map error-code)]
     (log/error "发生系统错误."
                "\n错误码:" error-code
                "\n错误描述:" error-text
                "\n错误数据:" obj)
     (custom-throw error-code error-text obj))))

(def biz-error-map
  {1000 "用户名或密码错误"
   1001 "无效的refresh token"
   })
