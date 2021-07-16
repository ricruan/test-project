(ns test-project.common.utils-format)

(defn- recur-group-tree
  [group row level {:keys [value label] :as r}]
  (mapv (fn [e]
          (-> e
              (merge {:value (value e) :label (label e)}) ;; 使用 antd table 默认行为完成tree功能显示
              (merge {:children (if-let [c (get group (value e))]
                                  (if (= 1 level) c (recur-group-tree group c (dec level) r))
                                  nil)})))
        row))

(defn trans-muilti-tree
  "
  接受数据库SQL查询封装结果数据返回多级树结构
  参数level 表示返回几层树形结果
  参数required 定制分层依据
  {:group 上下级关系依据的数据库字段
   :root  根节点判断值
   :value 列表页面行实际值
   :lable 列表页面行显示值}
  接受的数据结构：
  {:total
   :list [{}...]
  ...}
  返回结果结构：
  {:total 根节点个数
   :list [{:value :label ... :children [{}...]}...]
  ...}
  "
  [data level required]
  (when (and (int? level) (map? data) (some? (seq data)))
    (if (and (> level 1) (keyword? (:group required)))
      (when-let [group (group-by (:group required) (:list data))]
        (-> data
            (merge {:total (count (get group (:root required)))})
            (assoc :list (recur-group-tree group (get group (:root required)) (dec level) required))))
      data)))
