(ns test-project.common.utils-auth
  (:require [re-frame.core :as rf]
            [kee-frame.core :as kf]
            [com.rpl.specter :as s]))

;;; 1.1.1 利用cofx将数据的处理过程剥离出来，使得reg-event-fx更加纯粹
(rf/reg-cofx
 ::auth-tree
 (fn [cofx _]
   (let [data @(rf/subscribe [:base/current-menus])]
     (assoc cofx :auth-tree (s/select [:data s/ALL :code #(not (or (= "" %) (= " " %)))]
                                      data)))))
;;; 接收消息来保存权限菜单数据到DB
(kf/reg-event-fx
 :auth/get-auth-tree
 [(rf/inject-cofx ::auth-tree)]
 (fn [{:keys [db auth-tree]} _]
   {:db (assoc-in db [:component-auth] auth-tree)}))

;;; 1.2 从DB订阅保存的菜单权限数据集
(rf/reg-sub
 :auth/auth-tree
 (fn [db]
   (get-in db [:component-auth] {})))

;;; 2 可以把数据处理和处理结果链式方式传递给UI，使得UI只获取需要的
(rf/reg-sub
 :auth/chain
 (fn [_]
   (rf/subscribe [:base/current-menus]))
 (fn [menus]
   (s/select [:data s/ALL :code #(not (or (= "" %) (= " " %)))]
             menus)))


(defn auth-wrapper
  "
  管理后台给控件增加权限管理
  符合当前用户权限的控件返回控件本身；否则返回nil
  判断条件是控件的属性中，定义的:auth-id，是否包含在服务器权限表里，该属性是个String
  参数：需要权限控制的控件
  或者将auth-id 作为第一个参数传入，第二个参数是控件
  "
  ([component]
   (let [id (get-in component [1 :auth-id])]
     (auth-wrapper id component)))

  ([auth-id component]
   (let [auth-tree @(rf/subscribe [:auth/chain])]
     (when (or (nil? (seq auth-id))
               (empty? auth-tree)
               (some #(= auth-id %) auth-tree))
       component))))

