(ns test-project.common.msg-fx
  (:require
   ["antd" :as ant]
   [re-frame.fx :as fx]))

(fx/reg-fx
 :msg
 (fn [value]
   (case (:type value)
     "success" (.success ant/message (:content value))
     "warning" (.warning ant/message (:content value))
     "error" (.error ant/message (:content value))
     nil (.success ant/message value))))
