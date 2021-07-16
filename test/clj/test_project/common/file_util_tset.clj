(ns test-project.common.file-util-tset
  (:require [test-project.common.file-util :as sut]
            [clojure.java.io :as io]
            [clojure.test :refer [deftest testing use-fixtures is]]))

(deftest file-util-test
  (testing "测试上传"
    (let  [re (sut/upload-file-local "1" {:size 22817, :filename "11.jpg", :content-type "image/jpeg", :tempfile (io/file (str (System/getProperty "user.dir") "\\test\\11.jpg"))})]
      (is re)))

  (testing "测试上传七牛云"
    (let  [re (sut/upload-files-qiniu "1" [{:size 22817, :filename "11.jpg", :content-type "image/jpeg", :tempfile (io/file (str (System/getProperty "user.dir") "\\test\\11.jpg"))}])]
      (is re)))

  (testing "测试上传七牛云-java"
    (let  [re (sut/upload-files-qiniu "1" [(io/file (str (System/getProperty "user.dir") "\\test\\11.jpg"))])]
      (is re)))
  )
