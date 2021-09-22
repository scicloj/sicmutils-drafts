(ns images.vis
  (:require [gorilla-notes.core :as gn]
            [tech.v3.resource :as resource]
            [tech.v3.libs.buffered-image :as bufimg]
            [clojure.java.io :as io]))

(defn show-hiccup! [hiccup]
  (gn/assoc-note! 0 hiccup))

(defn image->hiccup [image-buffer title]
  (let [filename (str "tmp-" (rand-int 999999) ".png")
        path     (str "resources/public/images/" filename)
        hiccup   [:img
                  {:src   (str "images/" filename)
                   :width 400}]]
    (resource/track hiccup
                    {:track-type :gc
                     :dispose-fn #(.delete ^java.io.File
                                           (io/file path))})
    (bufimg/save! image-buffer path)
    [:div
     [:p [:big title]]
     hiccup]))

(defn show-image! [image-buffer title]
  (show-hiccup! (image->hiccup image-buffer title)))

(defn images->hiccup [& image-buffers]
  (->> image-buffers
       (map #(image->hiccup % ""))
       (into [:div])))

(defn show-images! [& image-buffers]
  (->> image-buffers
       (apply images->hiccup)
       (show-hiccup!)))

(defn start! []
  (gn/start-server!) 
  (gn/browse-http-url)
  (gn/reset-notes!))



