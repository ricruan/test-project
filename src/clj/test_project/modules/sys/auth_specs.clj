(ns test-project.modules.sys.auth-specs
  (:require  [clojure.test :as t]
             [clojure.spec.alpha :as s]
             [spec-tools.core :as st]
             [spec-tools.data-spec :as dt]))

(s/def ::access-token
  (st/spec
   {:spec string?
    :swagger/default "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyIjp7InVzZXJfaWQiOiIxIiwidXNlcm5hbWUiOiJhZG1pbiIsIm5pY2tuYW1lIjoi566h55CG5ZGYIn0sInR5cGUiOiJhY2Nlc3MtdG9rZW4iLCJleHAiOjE2MTU5NjgyOTEsImlhdCI6MTYxNTk2NjQ5MX0.QnfNUQPwJQMNQaqBoOtnung3aSJX9fMeElt26ZH2l-s"
    :description "token"}))

(s/def ::refresh-token
  (st/spec
   {:spec string?
    :swagger/default "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyIjp7InVzZXJfaWQiOiIxIiwidXNlcm5hbWUiOiJhZG1pbiIsIm5pY2tuYW1lIjoi566h55CG5ZGYIn0sInR5cGUiOiJyZWZyZXNoLXRva2VuIiwiZXhwIjoxNjE2ODMwNDkxfQ.jXEGvHevXUbiTlreNRQuMTmTTwJeZnpWQMOyKFH1KoU"
    :description "刷新token"}))



(s/def :public-anth-token/data (s/keys :req-un [::access-token ::refresh-token]))


(s/def ::public-anth-token (s/keys :req-un [:base/code :base/msg :public-anth-token/data]))
