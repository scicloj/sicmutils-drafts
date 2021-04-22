(ns scicloj.sicmutils-drafts.ex1-6-quil
  (:require [quil.core :as q]
            [gil.core :as gil]
            [sicmutils.env :as env]
            [sicmutils.mechanics.lagrange :as lagrange]))

;; Exercise 1.6 visualized with quil. Adapted from ex1_6.clj

(def paths-memory (atom []))
(def paths-counter (atom 0))


(defn setup []
  ; Set frame rate to 30 frames per second.
  (q/frame-rate 30)
  ; Set color mode to HSB (HSV) instead of default RGB.
  (q/color-mode :hsb))

(defn draw []
  (q/stroke 255 0 0)
  (q/background 240)
  
  (q/translate 0 (/ (q/height) 2))
  (q/scale 5)
  (doseq [[x y]  @paths-memory]
    (q/point (* 10 x)  (* 10 y)))
  (gil/save-animation "ex_1-6.gif" 10000 0))

(defn parametric-path-action*
  [] 
  (fn [Lagrangian t0 q0 offset0 t1 q1 offset1]
    (fn [intermediate-qs]
      ;; See the two new points?
      (let [intermediate-qs* (concat [(+ q0 offset0)]
                                     intermediate-qs
                                     [(+ q1 offset1)])
            path             (lagrange/make-path t0 q0 t1 q1 intermediate-qs*)]
        (println "plotting iteration " (swap! paths-counter inc))
        (reset! paths-memory
                (mapv (fn [t] [t (path t)]) (range t0 t1 (/ (- t1 t0) 100))))
      
        (lagrange/Lagrangian-action Lagrangian path t0 t1)))))

(defn find-path* []
  (fn [L t0 q0 offset0 t1 q1 offset1 n]
    (let [initial-qs    (env/linear-interpolants q0 q1 n)
          action (parametric-path-action*)
          minimizing-qs (env/multidimensional-minimize
                         (action L t0 q0 offset0 t1 q1 offset1)
                         initial-qs)]
      (lagrange/make-path t0 q0 t1 q1 minimizing-qs))))

(defn one-six [offset0 offset1 n]
  (let [tmax 10
        find (find-path*)
        L (lagrange/L-free-particle 3.0)
        path (find L
                   0. 1. offset0
                   tmax 0. offset1
                   n)]
    (lagrange/Lagrangian-action L path 0 tmax)))

(comment
  
(q/defsketch ex1_6
  :title "sicm exercise 1.6"
  :size [500 500]
  ; setup function called only once, during sketch initialization.
  :setup setup
  :draw draw
  :features [:keep-on-top])

  (one-six 1 1 3)
  
  "")
