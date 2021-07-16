(ns test-project.components.hc-img
  (:require
   [goog.string :refer [format]]))

(defn- get-url [src width height]
  (if (and width height)
    (format "%s?imageView2/2/w/%d/h/%d" src width height)
    (if width
      (format "%s?imageView2/2/w/%d" src width)
      (if height
        (format "%s?imageView2/2/h/%d" src height)
        src))))


(defn size-img
  "获取一个指定尺寸大小的图片(如果指定了宽度或高度；否则返回原图)
  限定缩略图的宽最多为<Width>，高最多为<Height>，进行等比缩放，不裁剪。
  如果只指定 w 参数则表示限定宽（高自适应），只指定 h 参数则表示限定高（宽自适应）。
  https://developer.qiniu.com/dora/api/1279/basic-processing-images-imageview2"
  [{:keys [className style src width height]}]
  [:img {:className className
         :style style
         :src (get-url src width height)}])
