(ns scicloj.sicmutils-drafts.exercise-1p44
  (:require
   [sicmutils.env :as s :refer [+ - * sin cos square up]]))
(comment
  "Exercise 1.44
Simulating a double pendulum.")

(defn cartesian<-angles [l-1 l-2]
  "Returns a function for converting from a pair of angles to Cartesian coordinates.
"
  (fn [[t [angle-1 angle-2]]]
    (let [x-1 (* l-1 (sin angle-1))
          y-1 (- (* l-1 (cos angle-1)))
          x-2 (+ x-1 (* l-2 (sin (+ angle-1 angle-2))))
          y-2 (- y-1 (* l-2 (cos (+ angle-1 angle-2))))]
      (up x-1 y-1 x-2 y-2))))

(defn kinetic [m1 m2]
  (fn [[_ _ [xdot1 ydot1 xdot2 ydot2]]]
    (+ (* (/ 1 2) m1 (+ (square xdot1)
                        (square ydot1)))
       (* (/ 1 2) m2 (+ (square xdot2)
                        (square ydot2))))))
(defn potential [m1 m2 g]
  (fn [[_ [_ y1 _ y2]]]
    (+ (* m1 g y1)
       (* m2 g y2))))

(defn L-cartesian [m1 m2 g]
  (- (kinetic m1 m2) (potential m1 m2 g)))

(defn L-double-pendulum [m1 m2 l1 l2 g]
  "The Lagrangian of the full system in terms of angles.

NOTE: Calling a useful function 'F->C' with no context should be a crime!
"
  (s/compose (L-cartesian m1 m2 g)
             (s/F->C (cartesian<-angles l1 l2))))

;; ---------------------------------------------------
;; --- CONSTANTS ---
;; ---------------------------------------------------
;; PHYSICAL
(def m1 1.0)
(def m2 3.0)
(def l1 1.0)
;; (def l2 0.9)
(def l2 1.0)
(def g 9.8)
;; SIMULATION
(def step "in seconds." 0.03)
(def horizon "in seconds." 50)

(def initial-chaotic (up (/ Math/PI 2) Math/PI))
(def initial-regular (up (/ Math/PI 2) 0.0))

;; ---------------------------------------------------
(def state-derivative (s/compose s/Lagrangian->state-derivative L-double-pendulum))
(defn run [step horizon initial-coords]
  (let [collector (atom (transient []))
        initial-state (up 0.0
                          initial-coords
                          (up 0.0 0.0))]
    ((s/evolve state-derivative m1 m2 l1 l2 g)
     initial-state
     step
     horizon
     {:compile? true
      :epsilon 1.0e-13
      :observe (fn [t state]
                 (swap! collector conj! state))})
    (persistent! @collector)))

(defonce data-chaotic
  (run step horizon initial-chaotic))
(def data-chaotic (run step horizon initial-chaotic))

(defonce data-regular
  (run step horizon initial-regular))

(defn rectilinear<-state [state]
  ((cartesian<-angles l1 l2) state))
