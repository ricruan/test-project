(ns test-project.db.common-service
  (:require [test-project.db.common-db :as common-db]
            [test-project.common.utils :refer [snowflake-id trans-keyname-cols]]))

(defn insert-table! [table-name m]
  (let [cols #(mapv name (keys %))]
    (common-db/insert-into-table! {:table-name table-name
                                   :cols (cols m)
                                   :info (vals m)})))

(defn insert-or-update-table! [table-name id-key m]
  (let [cols #(mapv name (keys %))]
    (common-db/insert-or-update-table-info! {:table-name table-name
                                             :cols (cols m)
                                             :info (vals m)
                                             :updates (dissoc m id-key)})))

(defn batch-insert-or-update-table!
  ([table-name entity-coll]
   (when (seq entity-coll)
     (common-db/batch-insert-or-update! {:table-name table-name
                                         :cols (-> entity-coll first trans-keyname-cols)
                                         :records (map vals entity-coll)})))
  ([table-name id-key entity-coll]
   (when (seq entity-coll)
     (common-db/batch-insert-or-update! {:table-name table-name
                                         :cols (-> entity-coll first trans-keyname-cols)
                                         :records (map vals entity-coll)
                                         :updates (-> entity-coll first (dissoc id-key))}))))

(defn complete-id! [id-key entity]
  (if (get entity id-key)
    entity
    (assoc entity id-key (snowflake-id))))

(defn complete-then-batch-insert-or-update! [{:keys [entities id-key table-name]}]
  (let [complete-entities (->> entities
                               (map #(complete-id! id-key %)))]
    (batch-insert-or-update-table! table-name id-key complete-entities)))

(defn complete-auto-id-then-batch-insert-or-update!
  "如果数据库的id是一个自增字段，则无需给uuid."
  [{:keys [entities id-key table-name]}]
  (batch-insert-or-update-table! table-name id-key entities))

(defn complete-id-then-batch-insert-or-update!
  "如果数据库无company-id,则无需给company-id"
  [{:keys [entities id-key table-name]}]
  (let [complete-entities (->> entities
                               (map #(complete-id! id-key %)))]
    (batch-insert-or-update-table! table-name id-key complete-entities)))
