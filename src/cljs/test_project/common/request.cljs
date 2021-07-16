(ns test-project.common.request
  (:require
   ["antd" :as ant]
   [ajax.core :as http]
   [re-frame.core :as rf]
   [kee-frame.core :as kf]
   [test-project.url :refer [auth-url]]
   [test-project.common.storage :as storage]))

(defn- get-token []
  (let [token (storage/get-token-storage)]
    (if token (:access-token token) "")))

(defn- get-refresh-token []
  (let [token (storage/get-token-storage)]
    (if token (:refresh-token token) "")))

(defn- error-notification
  [msg desc]
  (.error ant/notification (clj->js {:message msg :description desc})))

(defn- http-error [status desc]
  (prn "请求错误" status " " desc)
  (case status
    500 (error-notification (str status ":系统错误,请稍后再试") desc)
    502 (error-notification (str status ":系统错误,请稍后再试") desc)
    400 (error-notification (str status ":参数错误,请检查填写的内容") desc)
    404 (error-notification (str status ":您访问的的地址不存在,请联系运营商") desc)
    :else (error-notification (str status ":系统错误,请联系运营商") desc))  )

(defn- biz-error [msg]
  (.error ant/message msg))

;;封装接口请求的map，无指定回调 non callback
(defn request-non-callback [coeffects {:keys [method url params timeout]}]
  (let [http-param {:method method
                    :uri url
                    :params (or params {})
                    :headers {:authorization (get-token)}
                    :timeout (or timeout 30000)
                    :format          (http/json-request-format)
                    :response-format (http/json-response-format {:keywords? true})}]
    (if (nil? coeffects)
      http-param
      (assoc-in http-param [:on-failure] [::error (:original-event coeffects) nil]))))


(kf/reg-chain
  ;;Json请求 1.请求带着Token 2.Token失效自动刷新
  :request
  (fn [coeffect [{:keys [method url params callback-event callback with-code? on-error]}]]
    (let [request-event (:original-event coeffect)]
      {:http-xhrio {:method method
                    :uri url
                    :params (or params {})
                    :headers {:authorization (get-token)}
                    :timeout 30000
                    :format          (http/json-request-format)
                    :response-format (http/json-response-format {:keywords? true})
                    :on-failure [::error request-event on-error]}}))
  (fn [_ [{:keys [params callback-event callback with-code?]} data]]
    (if with-code?
      (if callback
        (callback data params)
        {:dispatch [callback-event data params]})
      (if (zero? (:code data))
        (if callback
          (callback (:data data) params)
          {:dispatch [callback-event (:data data) params]})
        (biz-error (:msg data))))))

(kf/reg-chain
 ;;文件上传请求 1.请求带着Token 2.Token失效自动刷新
 :request-form
 (fn [coeffect [{:keys [url form-data callback with-code?]}]]
   (let [request-event (:original-event coeffect)]
     {:http-xhrio {:method :post
                   :uri url
                   :body form-data
                   :headers {:authorization (get-token)}
                   :timeout 30000
                   :response-format (http/json-response-format {:keywords? true})
                   :on-failure [::error request-event nil]}}))
 (fn [_ [{:keys [callback with-code?]} data]]
   (if with-code?
     (callback data)
     (if (zero? (:code data))
       (when callback (callback (:data data)))
       (biz-error (:msg data))))))

(kf/reg-event-fx
  :request/get
  (fn [_ [data]]
    {:dispatch [:request (assoc data :method :get)]}))

(kf/reg-event-fx
  :request/post
  (fn [_ [data]]
    {:dispatch [:request (assoc data :method :post)]}))

(kf/reg-event-fx
  :request/put
  (fn [_ [data]]
    {:dispatch [:request (assoc data :method :put)]}))

(kf/reg-event-fx
  :request/delete
  (fn [_ [data]]
    {:dispatch [:request (assoc data :method :delete)]}))

(kf/reg-chain
  ::refresh-token
  (fn [_ [request-event]]
    {:http-xhrio {:method :post
                  :uri (:refresh-token auth-url)
                  :params {:refresh-token (get-refresh-token)}
                  :headers {:authorization (get-token)}
                  :timeout 30000
                  :format          (http/json-request-format)
                  :response-format (http/json-response-format {:keywords? true})
                  :on-failure [::refresh-token-error]}})
  (fn [_ [request-event {:keys [code msg data]}]]
    (if (zero? code)
      (do
        (storage/set-token-storage (merge data {:pre_charged (:pre_charged (storage/get-token-storage))}))
        (when request-event
         (rf/dispatch request-event)))
      (do
        (prn "refresh token 无效，重新登录")
        (rf/dispatch [:core/nav :login])))))

(kf/reg-event-fx
  ::refresh-token-error
  (fn [_ [response]]
    (let [status (get response :status)]
      (http-error status (:refresh-token auth-url)))))

(kf/reg-event-fx
 ::error
 (fn [_ [event on-error response]]
   (let [status (get response :status)
         url (if event (get (second event) :url "") "")]
     (if (= status 401)
       (rf/dispatch [::refresh-token event])
       (if on-error
         (on-error response)
         (http-error status response))))))
