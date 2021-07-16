(ns test-project.common.aes-utils
  (:import (java.security SecureRandom Key)
           (javax.crypto KeyGenerator Cipher)
           (javax.crypto.spec IvParameterSpec)
           (java.security.spec AlgorithmParameterSpec)
           (javax.crypto.spec SecretKeySpec))
  #_(import '[java.security SecureRandom Key]
            '[javax.crypto KeyGenerator Cipher]
            '[javax.crypto.spec IvParameterSpec]
            '[java.security.spec AlgorithmParameterSpec]
            '[javax.crypto.spec SecretKeySpec]))

(defn ^bytes rand-bytes [n]
  (let [b (byte-array n)]
    (.. (SecureRandom.) (nextBytes b))
    b))

(defn ^Key aes-genkey [^long bits]
  {:pre[(#{128 192 256} bits)]}
  (let [kg (KeyGenerator/getInstance "AES")]
    (.init kg bits)
    (.generateKey kg)))

(def ^:dynamic *aes-mode* "AES/CBC/PKCS5Padding")

(defn- ^bytes aes-*code [^long mode ^bytes bytes ^Key key ^AlgorithmParameterSpec spec]
  (let [c (Cipher/getInstance *aes-mode*)]
    (if spec
      (.init c mode key spec)
      (.init c mode key))
    (.doFinal c bytes)))

(defn ^bytes aes-encode [^bytes bytes ^Key key & [^AlgorithmParameterSpec spec]]
  (aes-*code Cipher/ENCRYPT_MODE bytes key spec))

(defn ^bytes aes-decode [^bytes bytes ^Key key & [^AlgorithmParameterSpec spec]]
  (aes-*code Cipher/DECRYPT_MODE bytes key spec))

(defn ^String aes-decode-string [^bytes encrypted-data ^bytes session-key & [^bytes iv]]
  (String. (aes-*code Cipher/DECRYPT_MODE encrypted-data (SecretKeySpec. session-key "AES") (IvParameterSpec. iv)) "UTF-8"))
