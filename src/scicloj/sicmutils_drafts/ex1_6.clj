(ns scicloj.sicmutils-drafts.ex1-6
  (:require [notespace.api]))

(require '[sicmutils.env :as env]
         '[sicmutils.mechanics.lagrange :as lagrange])

["# Exercise 1.6: Minimizing action

 This Clojure namespaces adapts the code [Sam Ritchie's notes](https://github.com/sicmutils/sicm-exercises/blob/master/md/1_lagrangian_mechanics.md#sec-7) from Scheme to Clojure. It was written in session 3C of the Sicmutils study group."]

["The original code we are adapting here is imperative. It is supposed to affect a mutable visualization component during a sequence of steps. But here we will visualize it in a static way -- first collecting the data from all the steps, and then creating a visualization of all that data. We will use an atom as the mutable state where data are collected."]

["The adaptation of the computation itself is rather stateforward. We do not need to handle the window for plotting. Rather, we are writing to our memory of paths."]

(defn parametric-path-action*
  [paths-memory] ; an atom to collect paths
  (fn [Lagrangian t0 q0 offset0 t1 q1 offset1]
    (fn [intermediate-qs]
      ;; See the two new points?
      (let [intermediate-qs* (concat [(+ q0 offset0)]
                                     intermediate-qs
                                     [(+ q1 offset1)])
            path             (lagrange/make-path t0 q0 t1 q1 intermediate-qs*)]
        (swap! paths-memory conj path)
        (println ["collected" (count @paths-memory) "paths"])
        (lagrange/Lagrangian-action Lagrangian path t0 t1)))))

(defn find-path* [paths-memory]
  (fn [L t0 q0 offset0 t1 q1 offset1 n]
    (let [initial-qs    (env/linear-interpolants q0 q1 n)
          action (parametric-path-action* paths-memory)
          minimizing-qs (env/multidimensional-minimize
                         (action L t0 q0 offset0 t1 q1 offset1)
                         initial-qs)]
      (lagrange/make-path t0 q0 t1 q1 minimizing-qs))))

(defn one-six [offset0 offset1 n]
  (let [tmax 10
        *paths-memory (atom [])
        find (find-path* *paths-memory)
        L (lagrange/L-free-particle 3.0)
        path (find L
                   0. 1. offset0
                   tmax 0. offset1
                   n)]
    (lagrange/Lagrangian-action L path 0 tmax)
    @*paths-memory))

(defonce paths
  (one-six 1 1 3))

(count paths)

["Now, let us plot the data:"]

(defonce data-to-plot
  (->> paths
       (map-indexed (fn [i path]
                      (->> (range 0 10 0.1)
                           (map (fn [t]
                                  {:i i
                                   :t t
                                   :x (path t)})))))
       (apply concat)))

(require '[notespace.kinds :as kind]
         '[aerial.hanami.common :as hanami-common]
         '[aerial.hanami.templates :as hanami-templates])

^kind/vega
(hanami-common/xform
 hanami-templates/point-chart
 :DATA data-to-plot
 :X :t
 :Y :x
 :OPACITY {:condition {:test  "selected_i == datum['i']"
                       :value 1}
           :value     0}
 :SELECTION {:selected {:fields [:i]
                        :type   :single
                        :bind   {:i {:min   0
                                     :max   (count paths)
                                     :input :range
                                     :step  1}}}})

["Please try to play with the slider."]

^kind/hidden
(comment
  (require 'scicloj.sicmutils-drafts.utils)
  (println (scicloj.sicmutils-drafts.utils/ns->toc-link "Exercise 1.6")))
