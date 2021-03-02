(ns scicloj.sicmutils-drafts.notation
  (:require [notespace.api :as notespace]
            [notespace-sicmutils.setup]
            [scicloj.sicmutils-drafts.utils]))

^kind/hidden
(comment
  ;; Here is how you can use Notespace in a minimal way to render this namespace. Please keep these calls inside a comment block.
  ;; Notespace does offer a mode dynamic experience, with some integration into popular Clojure editors' functions.
  ;; See the Notespace tutorial:
  ;; https://scicloj.github.io/notespace/doc/notespace/v3-experiment1-test/index.html
  ;; A screencast is coming soon.

  ;; Initialize notespace:
  (notespace/init)

  ;; Alternatively, Initialize notespace and open the browser view:
  (notespace/init-with-browser)

  ;; Evauating all notes in this namespace, and updating the browser view:
  (notespace/eval-this-notespace)

  ;; Notespace does have bugs.
  ;; If the browser seems out-of-sync, you can try to refresh it.
  ;; If the state still seems problematic, you can init again.

  ;; Rendering current browser view into a static html file (under the `docs` directory):
  (notespace/render-static-html)

  ;; Printing the appropriate link that should be added to the Table of Contents (`toc.md`):
  (println (scicloj.sicmutils-drafts.utils/ns->toc-link "notation")))


["# Appendix: Our Notation

At the moment, this page only contains the code of [SICM Chapter 9](https://tgvaughan.github.io/sicm/chapter009.html) (the notation appendix), converted to Clojure using Sicmutils."]

["## Setup"]

(sicmutils.env/bootstrap-repl!)

(require '[notespace.kinds :as kind])

["## Functions"]

(defn distance [x1 y1 x2 y2]
  (sqrt (+ (square (- x2 x1))
           (square (- y2 y1)))))

(def h (compose cube sin))

(h 2)

(* (cube 2) (sin 2))

["## Symbolic values"]

((compose cube sin) 'a)

((- (+ (square sin) (square cos)) 1) 'a)

(simplify ((- (+ (square sin) (square cos)) 1) 'a))

((literal-function 'f) 'x)

((compose (literal-function 'f) (literal-function 'g)) 'x)

(def g (literal-function 'g (-> (X Real Real) Real)))

(g 'x 'y)

["## Tuples"]


(def v (up 'v0 'v1 'v2))

(def p (down 'p_0 'p_1 'p_2))

(def s (up 't (up 'x 'y) (down 'p_x 'p_y)))

((component 0 1) (up (up 'a 'b) (up 'c 'd)))

(ref (up 'a 'b 'c) 1)

(ref (up (up 'a 'b) (up 'c 'd)) 0 1)

(* p v)

["## Derivatives"]

(def derivative-of-sine (D sin))

(derivative-of-sine 'x)

(((* 5 D) cos) 'x)

(((* (+ D I) (- D I)) (literal-function 'f)) 'x)

["## Derivatives of functions of multiple arguments"]

((D g) 'x 'y)

(defn h [s]
  (g (ref s 0) (ref s 1)))

(h (up 'x 'y))

((D g) 'x 'y)

((D h) (up 'x 'y))

(def H
  (literal-function 'H
                    (-> (UP Real (UP Real Real) (DOWN Real Real)) Real)))

(H s)

((D H) s)

["## Structured results"]

(defn helix [t]
  (up (cos t) (sin t) t))

(def helix (up cos sin identity))

((D helix) 't)

(defn g [x y]
  (up (square (+ x y)) (cube (- y x)) (exp (+ x y))))

((D g) 'x 'y)

