(ns game.tiles
	(:use 
		arcadia.core
		arcadia.linear
		hard.core
    tween.core
    game.data
    pdfn.core
    clojure.pprint))


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
  (swap! SCORE inc)
  (timeline* 
    (tween {:material {:color (color 1 0 0)}} (first (children o)) 0.2)
    (tween {:material {:color (color 1 1 1)}} (first (children o)) 0.2)))