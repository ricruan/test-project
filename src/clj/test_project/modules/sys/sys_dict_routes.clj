(ns test-project.modules.sys.sys-dict-routes
  (:require
    [test-project.db.core :refer [*db*]]
    [test-project.common.result :refer [ok sorry]]
    [test-project.db.db-sys-dict :as db]
    [test-project.common.utils :as utils]
    [clojure.data.json :as j]
    [clojure.spec.alpha :as s]
    [spec-tools.core :as st]
    [clojure.tools.logging :as log]
    [java-time :as time]
    [com.rpl.specter :as sp :refer [select transform ALL]]
    [test-project.modules.sys.sys-specs :as sys-specs]
    [clojure.string :as string]
    [schema.core :as sc]))

(s/def ::name string?)
(s/def ::parent_id int?)
(sc/defschema
  DictItem
  {:code       string?
   :name       string?
   :group_code (s/nilable string?)
   :parent_id  int?})
(sc/defschema
  UpdateDictItem
  {:id         int?
   :code       string?
   :name       string?
   :group_code (s/nilable string?)
   :type       int?
   :sort       int?
   :parent_id  int?})

(s/def ::get-list-params (s/keys :req-un [:base/page :base/size]
                                 :opt-un [::name ::parent_id]))

(defn sys-dict-routes []
  ["/sys"
   {:swagger {:tags ["后台-字典数据管理"]}}
   ["/dict/list"
    {:get    {:summary    "获取字典列表"
              :parameters {:query ::get-list-params}
              :responses {200 {:body ::sys-specs/sys-dict-list-body}}
              :handler    (fn [{{{:keys [page size name parent_id]} :query} :parameters}]
                            (if (nil? parent_id)
                              (ok {:total-elements
                                   (->> (db/get-dict-list {:name  name
                                                           :count true})
                                        (map :total-elements)
                                        (first))
                                   :page page
                                   :size size
                                   :list (db/get-dict-list {:page (* page size)
                                                            :size size
                                                            :name name})})
                              (ok {:total-elements
                                   (->> (db/get-dict-details {:parent_id parent_id
                                                              :name      name
                                                              :count     true})
                                        (map :total-elements)
                                        (first))
                                   :page page
                                   :size size
                                   :parent_id parent_id
                                   :list (db/get-dict-details {:page      (* page size)
                                                               :size      size
                                                               :parent_id parent_id
                                                               :name      name})})))}
     :delete {:summary    "删除字典数据"
              :parameters {:query {:id int?}}
              :responses {200 {:body :base/body}}
              :handler    (fn [{{{:keys [id]} :query} :parameters}]
                            (ok "删除成功" (db/delete-dict-list! {:id id})))}
     :post   {:summary    "添加字典数据(列表parent-id为0，详情为父级id)"
              :parameters {:body DictItem}
              :responses {200 {:body :base/body}}
              :handler    (fn [{{:keys [body]} :parameters}]
                            (if (= (:parent_id body) 0)
                              (ok (db/post-dict-list! body))
                              (ok (db/post-dict-details! (dissoc body :group_code)))))}
     :put    {:summary    "修改字典数据(type为0修改父节点，type为1修改字节点)"
              :parameters {:body UpdateDictItem}
              :responses {200 {:body :base/body}}
              :handler    (fn [{{:keys [body]} :parameters}]
                            (if (= (:type body) 0)
                              (ok (db/update-dict-list! (dissoc body :type :sort :parent_id)))
                              (ok (db/update-dict-details! (dissoc body :type)))))}}]])
