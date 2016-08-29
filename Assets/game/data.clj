(ns game.data
  (:use arcadia.linear))

(defonce GRID (atom {}))

(defonce SCORE (atom 0))

(defonce SELECTED (atom nil))

(def cardinal->euler
  {:n (v3 0 0 0)
   :e (v3 0 90 0)
   :s (v3 0 180 0)
   :w (v3 0 270 0)})

(def cardinal->pos
  {:n (v3  0  0 -1)
   :e (v3 -1  0  0)
   :s (v3  0  0  1)
   :w (v3  1  0  0)})

(def clockwise {:n :e :e :s :s :w :w :n})

(def cardinal->distance {:n 0 :e 1 :s 2 :w 3})

(defn clockwise-n [d n] (reduce (fn [c _] (clockwise c)) d (range n)))

(defn cardinal->input [m]
  (let [inputs (:input m)
        dist (cardinal->distance (:rotation m))]
    (mapv #(clockwise-n % dist) inputs)))

(defn opposite? [a b]
  (= 2 (int (Mathf/Abs (apply - (mapv cardinal->distance [a b]))))))

(def tile-data {
  :core {
    :type :core
    :resource :core
    :clickable true}
  :finger {
    :type :finger
    :symbol "U"
    :key "u"}
  :$ {
    :type :$
    :symbol "$"
    :no-rotate true
    :input {}
    :key "m"}
  :arrow {
    :type :arrow
    :symbol "V"
    :key "v"}
  :splitter {
    :type :splitter
    :symbol "T"
    :rotation :n
    :input #{:w :e :s}
    :key "t"}
  :gate {
    :type :gate
    :symbol "&"
    :on true
    :key "p"}

  :drum1 {
    :type :drum1
    :audio true
    :key "1"
    :symbol "1"
    :clip "tight-hit-23"}
  :drum2 {
    :type :drum2
    :audio true
    :key "2"
    :symbol "2"
    :clip "tight-hit-08"}
  :drum3 {
    :type :drum3
    :audio true
    :key "3"
    :symbol "3"
    :clip "tight-hit-22"}

  :drum4 {
    :type :drum4
    :audio true
    :key "4"
    :symbol "4"
    :clip "arabicperc3-1"}

      :drum5 {
    :type :drum5
    :audio true
    :key "5"
    :symbol "5"
    :clip "arabicperc3-10"}

      :drum6 {
    :type :drum6
    :audio true
    :key "6"
    :symbol "6"
    :clip "arabicperc3-11"}

      :drum7 {
    :type :drum7
    :audio true
    :key "7"
    :symbol "7"
    :clip "arabicperc3-12"}

      :drum8 {
    :type :drum8
    :audio true
    :key "8"
    :symbol "8"
    :clip "arabicperc3-13"}

      :drum9 {
    :type :drum9
    :audio true
    :key "9"
    :symbol "9"
    :clip "arabicperc3-14"}

      :drum0 {
    :type :drum0
    :audio true
    :key "0"
    :symbol "0"
    :clip "arabicperc3-18"}
  }
  )

'(cardinal->input {:rotation :n :input #{:w :e :s}})