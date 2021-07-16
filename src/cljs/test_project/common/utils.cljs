(ns test-project.common.utils
  (:refer-clojure :exclude [list])
  (:require [goog.object :refer [getValueByKeys]]
            [clojure.string :as s]
            [clojure.set :refer [rename-keys]]
            [clojure.walk :as w]
            [reagent.core :as r]
            ["antd" :as ant]))


(defn kebab-case->camel-case
  "kebab case to camel case, 例如: on-click to onClick"
  [input]
  (let [words (s/split input #"-")
        capitalize (->> (rest words)
                        (map #(apply str (s/upper-case (first %)) (rest %))))]
    (apply str (first words) capitalize)))


(defn map-keys->camel-case
  [data & {:keys [html-props]}]
  (let [convert-to-camel (fn [[key value]]
                           [(kebab-case->camel-case (name key)) value])]
    (w/postwalk (fn [x]
                  (if (map? x)
                    (let [new-map (if html-props
                                    (rename-keys x {:class :className :for :htmlFor})
                                    x)]
                      (into {} (map convert-to-camel new-map)))
                    x))
                data)))

(defn create-form
  "对应于 Form.create() 最主要的参数是form, from 可以是任意的hiccup, 其余的参数:

   * :options - map 对应 Form.create() 的 options. 参照:
                https://ant.design/components/form/#Form.create(options)
   * :props - props, 需要是js类型"
  [form & {:keys [options props] :or {options {} props {}}}]
  (r/create-element
   (((getValueByKeys ant "Form" "create")
     (clj->js (map-keys->camel-case options)))
    (r/reactify-component form))
   (clj->js props)))

(defn get-form
  "返回又From.create创建的 `form` 这个函数只能在form内部调用, 因为使用了reaget/current-component."
  []
  (-> (r/current-component)
      (r/props)
      (js->clj :keywordize-keys true)
      (:form)))

(defn decorate-field
  "修饰一个field, 对应于getFieldDecorator() 函数
   参数:

   * form -  `form` 对象, 获得自 `(get-form)`
   * id - field id, 支持嵌套
   * options - 用于验证field
   * field - 一般就是一个input

   参照文档:
   https://ant.design/components/form/#getFieldDecorator(id,-options)-parameters"
  ([form id field] (decorate-field form id {} field))
  ([form id options field]
   (let [field-decorator (:getFieldDecorator form)
         params (clj->js (map-keys->camel-case options))]
     ((field-decorator id params) (r/as-element field)))))

(defn set-fields-value
  "对应 setFieldsValue()函数"
  [form data]
  ((:setFieldsValue form) (clj->js data)))

;;此处的变量需要修改的请在项目跟目录的shadow-cljs.edn中修改
(goog-define print-log? false)
;;; 重新定义log方法，增加环境开关
(defn log
  [log & more]
  (when (boolean print-log?) (js/console.log log more)))

(defn info
  [log & more]
  (when (boolean print-log?) (js/console.info log more)))

(defn error
  [log & more]
  (js/console.error log more))

(defn warn
  [log & more]
  (js/console.warn log more))

(defn debug
  [log & more]
  (when (boolean print-log?)
    (js/console.debug log more)))

#_(defn tform []
    (fn [props]
      (let [the-form (utils/get-form)
            {:keys [getFieldDecorator
                    getFieldsError
                    getFieldError
                    isFieldTouched]} the-form
            usernameError (and (isFieldTouched "username")
                               (getFieldError "username"))]

        [:> ant/Form {:layout "inline"}
         [:> FormItem {:label "输入搜索"
                       :validateStatus (if usernameError "error" "success")
                       :help usernameError}
          (utils/decorate-field
           the-form "username" {:rules [{:required true}]}
           [:> ant/Input])]])))

(defn- recur-group-tree
  [group row level {:keys [value label] :as r}]
  (mapv (fn [e]
          (-> e
              (conj {:value (value e) :label (label e)}) ;; 使用 antd table 默认行为完成tree功能显示
              (conj {:children (if-let [c (get group (value e))]
                                 (if (= 1 level) c (recur-group-tree group c (dec level) r))
                                 nil)})))
        row))

(defn trans-muilti-tree
  "
  接受数据返回多级树结构
  参数level 表示返回几层树形结果
  参数required 定制分层依据
  {:group 上下级关系依据的数据库字段
   :root  根节点判断值
   :value 列表页面行实际值
   :label 列表页面行显示值}
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
    (if (and (> level 1) (keyword? (:group required)) (vector? (:list data)))
      (when-let [group (group-by (:group required) (:list data))]
        (-> data
            (merge {:total (count (get group (:root required)))})
            (assoc :list (recur-group-tree group (get group (:root required)) (dec level) required))))
      data)))
