(ns test-project.common.encrypt
  (:require
    [buddy.core.hash :as hs]
    [buddy.core.codecs :as codecs]))

(defn encode [plain-text]
  (codecs/bytes->hex (hs/sha256 plain-text)))

(defn match? [plain-text cipher-text]
  (= (encode plain-text) cipher-text))
