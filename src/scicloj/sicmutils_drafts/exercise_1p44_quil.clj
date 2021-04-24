(ns scicloj.sicmutils-drafts.exercise-1p44-quil
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [gil.core :as gil]
            [sicmutils.env :as s :refer [up]]
            [scicloj.sicmutils-drafts.exercise-1p44 :as exercise]))

(def simulation  exercise/data-regular)

(def origin [250 250])

(defn positions [state]
  (into [] (map #(into [] %) (partition 2 (exercise/rectilinear<-state state)))))

(positions (nth simulation 10))

(defn setup []
  (q/frame-rate 30)
  (q/color-mode :rgb)

  {:index 0})

(defn draw-state [state]
  (let [poses (positions (nth simulation (:index state)))
        scale-factor 100
        position1 (into [] (s/+ origin (s/* scale-factor (apply up (poses 0)))))
        position2 (into []  (s/+ origin (s/* scale-factor (apply up (poses 1)))))]

    (q/background  215 227 244)
    (q/stroke 102 102 102)
    (q/line origin position1)
    (q/line position1 position2)

    (q/ellipse (position1 0) (position1 1) 50 50)
    (q/ellipse (position2 0) (position2 1) 50 50)

  ;; (gil/save-animation "ex_1-6.gif" 10000 0)
    ))

(defn update-state [state]
  (let [index-new (inc (:index state))]
    (if (< index-new (count simulation))
      {:index index-new}
      {:index (dec (count simulation))})))

(q/defsketch exercise_1p44
  :title "sicm exercise 1.6"
  :size [500 500]
  :setup setup
  :update update-state
  :draw draw-state
  :features [:keep-on-top]
  :middleware [m/fun-mode])
