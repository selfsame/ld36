(ns game.data
  (:use arcadia.linear))

(def GRID (atom {}))

(defonce SCORE (atom 0))

(defonce SELECTED (atom nil))

(def cardinal->euler
  {:n (v3 0 0 0)
   :e (v3 0 90 0)
   :s (v3 0 180 0)
   :w (v3 0 270 0)})

(def clockwise {:n :e :e :s :s :w :w :n})

(def tile-data {
  :core {
    :type :core
    :resource :core
    :limit 1}
  :$ {
    :type :$
    :symbol "$"
    :limit 1
    :no-rotate true
    :input {}}
  :splitter {
    :type :splitter
    :symbol "Y"
    :limit 20}})