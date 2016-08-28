(ns game.tiles
	(:use 
		arcadia.core
		arcadia.linear
		hard.core
    tween.core
    game.data
    pdfn.core
    clojure.pprint))

(declare output)

(defpdfn clone-tile)
(pdfn clone-tile [x z m]
  (clone! :blank (v3 x 0 z)))
(pdfn clone-tile [x z ^:resource m]
  (if-let [o (clone! (:resource m) (v3 x 0 z))] o 
    (clone! :blank (v3 x 0 z))))
(pdfn clone-tile [x z ^:symbol m]
  (let [o (clone! :stone (v3 x 0 z))
        sym (child-named o "symbol")]
    (set! (.text (.* sym>TextMesh)) (:symbol m))
    o))

(defpdfn mouse-enter)
(pdfn mouse-enter [o os otype]
  (tween {:local {:scale (v3 1.3)}} o 0.2))
(pdfn mouse-enter [o os ^{nil 1} otype]
  (tween {:material {:color (color 0 1 1)}} (first (children o)) 0.1))

(defpdfn mouse-exit)
(pdfn mouse-exit [o os otype]
  (tween {:local {:scale (v3 1)}} o 0.2))
(pdfn mouse-exit [o os ^{nil 1} otype]
  (tween {:material {:color (color 1 1 1)}} (first (children o)) 0.1))

(defpdfn init)

(pdfn init [k m]
  (let [o (:obj m)] 
    (timeline* :loop
      (NOT #(state o :hover))
      #(do (set! (.text (.* (the debug)>Text))
        (with-out-str (pprint (state o)))) 
        false)
      (mouse-enter o m (:type m))
     #(state o :hover)
      (mouse-exit o m (:type m)))))



(defpdfn click)
(pdfn click [x z o os otype]
  (reset! SELECTED o))
(pdfn click [x z o os otype]
  {otype (is* :core)}
  
  (output o os otype)
  (timeline* 
    (tween {:material {:color (color 1 0 0)}} (first (children o)) 0.2)
    (tween {:material {:color (color 1 1 1)}} (first (children o)) 0.2)))


(defpdfn input)
(pdfn input [p ps o os otype])
(pdfn input [p ps o os otype]
  {otype (is* :$)}
  (timeline* 
    (tween {:material {:color (color 1 0.7 0)}} (first (children o)) 0.2)
    (tween {:material {:color (color 1 1 1)}} (first (children o)) 0.2))
  (destroy p)
  (swap! SCORE inc))
(pdfn input [p ps o os otype]
  {otype (is* :splitter)}
  (set-state! p :dir (:rotation os)))

(defn update-pellet [o]
  (timeline* :loop
    (tween {:local {:position (v3+ (>v3 o) (cardinal->pos (state o :dir)))}} o 0.3)
   #(let [pos (mapv int [(X o) (Z o)])]
      (if-let [tile (:obj (get @GRID pos))]
        (if (state tile :type)
            (input o (state o) tile (state tile) (state tile :type)))
        (destroy o))
      nil)))

(defn make-pellet [[x z] dir]
  (let [o (clone! :pellet (v3 x 1 z))]
    (timeline* (tween {:material {:color (color 1 0 0)}} o 0.1))
    (set-state! o :dir dir)
    (update-pellet o)))

(defpdfn output)
(pdfn output [o os otype])
(pdfn output [o os otype]
  {otype (is* :core)}
  (mapv (partial make-pellet (state o :tile)) [:n :e :s :w]))