(ns game.tiles
	(:use 
    tween.pool
    tween.core
		arcadia.core
		arcadia.linear
		hard.core
    hard.sound

    game.data
    pdfn.core
    ;clojure.pprint
    ))

(declare output make-pellet)

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








(defpdfn click)
(pdfn click [x z o os otype]
  (reset! SELECTED o))
(pdfn click [x z o os otype]
  {otype (is* :core)}  
  (output o os otype)
  (timeline* 
    (tween {:material {:color (color 1 0 0)}} (first (children o)) 0.2)
    (tween {:material {:color (color 1 1 1)}} (first (children o)) 0.2)))



(defpdfn setup)
(pdfn setup [o os otype])
(pdfn setup [o os otype]
  {otype (is* :finger)}
  (timeline* :loop 
    (wait 0.8)
    (tween {:material {:color (color 1 0 0)}} (first (children o)) 0.2)
   #(let [os (state o)
          dir (:rotation os)
          [x z] (:tile os)
          target-pos (cardinal->pos dir)
          target-tile [(int (+ x (X target-pos))) (int (+ z (Z target-pos)))]
          target-o (:obj (get @GRID target-tile))] 
      (when (:clickable (state target-o)) 
        (click 0 0 target-o (state target-o) (state target-o :type)) nil))
   (tween {:material {:color (color 1 1 1)}} (first (children o)) 0.2)))

(defpdfn init)
(pdfn init [k m]
  (let [o (:obj m)] 
    (timeline* :loop
      (NOT #(state o :hover))
      #(do (set! (.text (.* (the debug)>Text))
        (with-out-str (prn (state o)))) 
        false)
      (mouse-enter o m (:type m))
     #(state o :hover)
      (mouse-exit o m (:type m)))
    (setup o (state o) (state o :type))))


(defpdfn input)
(pdfn input [p ps o os otype])

(pdfn input [p ps o os otype]
  {otype (is* :finger)}
  (destroy p))

(pdfn input [p ps o os otype]
  {otype (is* :$)}
  (timeline* 
    (tween {:material {:color (color 1 0.7 0)}} (first (children o)) 0.2)
    (tween {:material {:color (color 1 1 1)}} (first (children o)) 0.2))
  (destroy p)
  (swap! SCORE inc))

(pdfn input [p ps o os otype]
  {otype (is* :arrow)}
  (let [pdir (:dir ps)]
  (if (opposite? pdir (:rotation os))
    (destroy p)
    (set-state! p :dir (:rotation os)))))

(pdfn input [p ps o os otype]
  {otype (is* :splitter)}
  (when (opposite? (:dir ps) (:rotation os)) 
    (let [pos (:tile os)
        inputs (set (cardinal->input os))]
    (when-let [in (inputs (:dir ps))]
      (let [out (disj inputs in)]
        (mapv #(make-pellet pos %) out)))
    (destroy p))))

(pdfn input [p ps o os otype]
  {otype (is* :gate)}
  (let [] 
    (if (opposite? (:dir ps) (:rotation os))
      (do (update-state! o :on not)
          (if (:on os)
            (timeline* (tween {:material {:color (color 1 1 1)}} (first (children o)) 0.2))
            (timeline* (tween {:material {:color (color 0 0 1)}} (first (children o)) 0.2)))
          (destroy p))
      (if-not (state o :on)
          (destroy p)))))

(pdfn input [p ps o ^:audio os otype]
  {}
  (play-clip! (:clip os) {:volume 0.5})
  (timeline*
    (tween {:material {:color (color 0 0 0)}} (first (children o)) 0.1)
    (tween {:material {:color (color 1 1 1)}} (first (children o)) 0.1)))


(defn update-pellet [o]
  (timeline* :loop
    (tween {:local {:position (v3+ (>v3 o) (cardinal->pos (state o :dir)))}} o 0.2 :pow3)
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