(ns test-project.components.hc-upload
  (:require
    ["antd" :as ant]
    ["uuid" :as uid]
    ["rc-upload" :as rcupload]
    [reagent.core :as r]
    [re-frame.core :as rf]
    [test-project.common.storage :as storage]
    [test-project.url :refer [base-url]]))


(defn- file->base64 [file callback]
  (let [reader (new js/FileReader)]
    (.addEventListener reader "load" (fn [] (callback (.-result reader))))
    (.readAsDataURL reader file)))

(defn- text-upload-btn []
  [:> ant/Button {:icon "upload"} "上传"])

(defn- image [{:keys [url thumbnail]}]
  [:img {:src       (if url url thumbnail)
         :className "ant-upload-list-item-image"
         :style {:width "100%"}}])

(defn- card-upload-btn []
  [:div {:className "ant-upload ant-upload-select ant-upload-select-picture-card"}
   [:span {:className "ant-upload" :role "button"}
    [:div
     [:> ant/Icon {:type "plus"}]
     [:div {:className "ant-upload-text"} "上传"]]]])

(defn- card-error [file {:keys [on-preview on-remove]}]
  [:div {:className "ant-upload-list-item ant-upload-list-item-error"}
   [:div {:className "ant-upload-list-item-info error-item-info"
          :style     {:position "relative"}}
    [:div {:className "error-item-title"} (or (:error file) "上传错误")]
    [:span
     [image file]]]
   [:span {:className "ant-upload-list-item-actions"}
    [:a {:on-click #(on-preview file)}
     [:> ant/Icon {:type "eye-o"}]]
    [:a {:on-click #(on-remove file)}
     [:> ant/Icon {:type "delete"}]]]])

(defn- card-done [{:keys [index first? last?]} file {:keys [on-preview on-remove on-sort]}]
  [:div {:className "ant-upload-list-item ant-upload-list-item-done"}
   [:div {:className "ant-upload-list-item-info"}
    [:span
     [image file]]]
   [:span {:className "ant-upload-list-item-actions"}
    [:div {:style {:margin-bottom 3}}
     [:a {:on-click #(on-preview file)}
      [:> ant/Icon {:type "eye-o"}]]
     [:a {:on-click #(on-remove file)}
      [:> ant/Icon {:type "delete"}]]]
    (when on-sort
      [:div
       (when (not first?)
         [:a {:on-click #(on-sort -1 index file)}
          [:> ant/Icon {:type "left-circle"}]])
       (when (not last?)
         [:a {:on-click #(on-sort 1 index file)}
          [:> ant/Icon {:type "right-circle"}]])])]])

(defn- card-uploading [{:keys [percent]}]
  [:div {:className "ant-upload-list-item ant-upload-list-item-uploading"}
   [:div {:className "ant-upload-list-item-info"}
    [:span
     [:div {:className "ant-upload-list-item-uploading-text"} "文件上传中"]]]
   [:> ant/Icon {:type "close"}]
   [:> ant/Progress {:percent     percent
                     :size        "small"
                     :status      "active"
                     :showInfo    false
                     :strokeWidth 2}]])

(defn- card-file-item [index file fns]
  [:div {:key (:uid file)}
   (case (:status file)
     "uploading" [card-uploading file]
     "done" [card-done index file fns]
     "error" [card-error file fns]
     nil)])

(def upload-btn
  "上传按钮"
  {:card [#'card-upload-btn]
   :text [#'text-upload-btn]})

(defn- file-object->file-map [{:keys [file status percent response thumbnail error]}]
  {:uid         (.-uid file)
   :name        (.-name file)
   :status      status
   :size        (.-size file)
   :type        (.-type file)
   :percent     (or percent 0)
   :response    response
   :error       error
   :thumbnail   thumbnail
   :origin-file file})

(defn- update-files [file files]
  (if (seq files)
    (if (some #(= (:uid file) (:uid %)) files)
      (mapv (fn [item] (if (= (:uid item) (:uid file)) file item)) files)
      (conj files file))
    (vector file)))

(defn- remove-files [file files]
  (vec (remove #(= (:uid file) (:uid %)) files)))

(defn- sort-files [direct index files]
  (let [file (get files index)
        idx (+ index direct)
        ex-file (get files idx)]
    (vec
      (map-indexed (fn [i item]
                     (cond
                       (= i index) ex-file
                       (= i idx) file
                       :else item)) files))))

;;图片预览
(defn- image-preview [{:keys [visible? image on-cancel]}]
  [:> ant/Modal {:visible  visible?
                 :style    {:top 20}
                 :footer   nil
                 :onCancel #(on-cancel)}
   [:img {:style {:width "100%"} :src image}]])

(defn- byte-exp [n]
  (reduce * (repeat n 1024)))

(defn- file-size-text
  [size]
  (cond
    (< size (byte-exp 1)) (str size "B")
    (and (>= size (byte-exp 1)) (< size (byte-exp 2))) (str (int (/ size (byte-exp 1))) "KB")
    (and (>= size (byte-exp 2)) (< size (byte-exp 3))) (str (int (/ size (byte-exp 2))) "MB")
    (and (>= size (byte-exp 3)) (< size (byte-exp 4))) (str (int (/ size (byte-exp 3))) "GB")))

(defn- value->files [value]
  (if (string? value)
    (if (clojure.string/blank? value) [] [{:uid -1 :status "done" :url value}])
    (mapv
      (fn [v]
        (if (string? v)
          {:uid (.v1 uid) :status "done" :url v}
          {:uid (.v1 uid) :status "done" :url (:url v) :name (:name v)}))
      value)))

(defn default-resolve-url
  "默认获取图片上传url"
  [response]
  (:file-url (js->clj
               (:data (js->clj response :keywordize-keys true))
               :keywordize-keys true)))

(defn- files->value [files]
  (->> files
       (filter #(= "done" (:status %)))
       (mapv (fn [file]
               {:url  (or (:url file) (default-resolve-url (:response file)))
                :name (:name file)}))))
(defn upload
  "文件上传组件"
  [{:keys [data init-value type multiple? sort? max-size on-done tip] :as props}]
  (let [files (r/atom (value->files init-value))
        preview-visible? (r/atom false)
        preview-image (r/atom "")]
    (r/create-class
      {:component-will-receive-props (fn [this new-args]
                                       (let [old-value (:init-value (r/props this))
                                             new-value (:init-value (second new-args))]
                                         (when-not (= old-value new-value)
                                           (reset! files (value->files new-value)))))
       :render                       (fn [this]
                                       (let [show-type (or type :text)
                                             over-size? (fn [file]
                                                          (> (.-size file) (or max-size 0)))
                                             change-file (fn [file]
                                                           (let [new-files (update-files (file-object->file-map file) (if multiple? @files []))]
                                                             (reset! files new-files)
                                                             new-files))
                                             success-file (fn [response file]
                                                            (file->base64
                                                              file
                                                              #(let [new-files (change-file {:file file :status "done" :thumbnail % :response response})]
                                                                 (when on-done (on-done
                                                                                 (let [values (files->value new-files)]
                                                                                   (if multiple? values (first values))))))))
                                             error-file (fn [file error]
                                                          (file->base64
                                                            file
                                                            #(change-file {:file file :status "error" :thumbnail % :error error})))
                                             fns (merge
                                                   {:on-preview (fn [{:keys [url thumbnail]}]
                                                                  (reset! preview-visible? true)
                                                                  (reset! preview-image (if url url thumbnail)))
                                                    :on-remove  (fn [file]
                                                                  (let [new-files (remove-files file @files)]
                                                                    (reset! files new-files)
                                                                    (when on-done (on-done (files->value new-files)))))}
                                                   (if (and multiple? sort?)
                                                     {:on-sort (fn [direct index file]
                                                                 (let [new-files (sort-files direct index @files)]
                                                                   (reset! files new-files)
                                                                   (when on-done (on-done (files->value new-files)))))}
                                                     {}))]
                                         [:div
                                          [:link {:rel "stylesheet" :href "/css/upload.css"}]
                                          [:div {:className "ant-upload-list ant-upload-list-picture-card"}
                                           (doall
                                             (map-indexed (fn [idx item]
                                                            (let [index {:index idx :first? (zero? idx) :last? (= (inc idx) (count @files))}]
                                                              (case show-type
                                                                :card (card-file-item index item fns)
                                                                :nil))) @files))]
                                          [:> rcupload
                                           (merge
                                             props
                                             {:action       (:file-upload base-url)
                                              :data         (or data {})
                                              :component    "div"
                                              :headers      {:authorization (:access-token (storage/get-token-storage))}
                                              :style        (clj->js {:outline "none"})
                                              :multiple     multiple?
                                              :beforeUpload (fn [file]
                                                              (if (over-size? file)
                                                                (do
                                                                  (error-file file (str "大小超过" (file-size-text max-size)))
                                                                  (identity false))
                                                                (identity true)))
                                              :onStart      #(change-file {:file % :status "uploading"})
                                              :onProgress   #(change-file {:percent (.-percent %1) :file %2 :status "uploading"})
                                              :onSuccess    #(success-file %1 %2)
                                              :onError      (fn [err response file]
                                                              (let [code (:code (js->clj response :keywordize-keys true))]
                                                                (if (= code "unauthorized")
                                                                  (let [form-data (let [f-d (js/FormData.)]
                                                                                    (.append f-d (or (:name props) "file") file)
                                                                                    (doseq [k (keys (or data {}))
                                                                                            v (vals (or data {}))]
                                                                                      (.append f-d (name k) v))
                                                                                    f-d)]
                                                                    (rf/dispatch [:request-form {:url       (:file-upload base-url)
                                                                                                 :form-data form-data
                                                                                                 :callback  (fn [data]
                                                                                                              (if (= (:code data) 1)
                                                                                                                (success-file data file)
                                                                                                                (error-file file "上传错误")))}]))
                                                                  (error-file file "上传错误"))))})
                                           [:div
                                            (get upload-btn show-type)
                                            [:div {:style {:clear "both" :color "#faad14" :font-size 12}}
                                             tip]]
                                           ]
                                          [image-preview {:visible?  @preview-visible?
                                                          :image     @preview-image
                                                          :on-cancel (fn []
                                                                       (reset! preview-visible? false)
                                                                       (reset! preview-image ""))}]]))})))

(defn image-upload
  "图片上传组件"
  [props]
  [upload
   (assoc
     (or props {})
     ;;展示形式
     :type :card
     ;;默认最大3M
     :max-size (or (:max-size props) (reduce * [3 1024 1024]))
     ;;默认所有图片类型
     :accept (or (:accept props) "image/*"))])
