(ns images.exploration
  (:require [me.raynes.fs :as fs]
            [clojure.reflect :as reflect]
            [clojure.pprint :as pp]
            [tech.v3.datatype :as dtype]
            [tech.v3.datatype.functional :as fun]
            [tech.v3.tensor :as tensor]
            [gorilla-notes.core :as gn]
            [images.vis :as vis]
            [clojure.java.io :as io]
            [tech.v3.libs.buffered-image :as bufimg])
  (:import java.io.File
           io.jhdf.HdfFile
           io.jhdf.api.Dataset
           io.jhdf.api.Attribute))

(comment
  (vis/start!))

(set! *warn-on-reflection* true) ; part of Clojure core

(def dataset-names
  {:atom "atom image"
   :dark "dark image"
   :probe "probe image"})

(defn read-file [path]
  (let [file ^File (io/file path)
        hdf-file ^HdfFile (HdfFile. file)]
    (->> dataset-names
         (map (fn [[k dataset-path]]
                (let [dataset ^Dataset (.getDatasetByPath hdf-file dataset-path)]
                  [k (-> dataset
                         (.getData)
                         tensor/->tensor)])))
         (into {}))))


(defn tensor->img [t]
  (let [shape   (dtype/shape t)
        new-img (bufimg/new-image (shape 0)
                                  (shape 1)
                                  :ushort-gray)]
    (-> t
        (tensor/reshape [(shape 0)
                         (shape 1)
                         1])
        (dtype/copy! (tensor/ensure-tensor new-img)))
    new-img))


(def tensors0
  (read-file "data/absorption_0.hdf"))

(def tensors0-as-floats
  (->> tensors0
       (map (fn [[k t]]
              [k (tensor/->tensor t :datatype :float32)]))
       (into {})))


(defn max-and-min [tensor]
  (let [f (flatten tensor)]
    {:max (apply max f)
     :min (apply min f)}))


(let [{:keys [probe dark atom]} tensors0-as-floats]
  (->> (fun/- atom
             dark)
       max-and-min))

(let [{:keys [probe dark atom]} tensors0-as-floats]
  (->> (fun/- atom
              dark)
       (fun/max 1)))

(let [{:keys [probe dark atom]} tensors0-as-floats]
  (-> (fun// (fun/max (fun/- probe
                             dark)
                      1)
             (fun/max (fun/- atom
                             dark)
                      1000))
      (fun/log)
      (fun/max 0)
      (fun/* 50000)
      tensor->img
      (vis/show-image! "test")))
