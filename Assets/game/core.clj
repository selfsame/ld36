(ns game.core
	(:use 
    tween.pool
    tween.core
		arcadia.core
		arcadia.linear
		hard.core
    hard.input
    
    game.data)
  (:require 
    game.tiles
    [arcadia.repl :as repl]))

(declare make-level tile-mouse-enter tile-mouse-exit tile-click click)

(defn start-repl [go]
  (repl/start-server 11211))

(defn update-repl [go]
  (repl/eval-queue))

'(hook+ (the make-level) :start #'game.core/start-repl)
'(hook+ (the make-level) :update #'game.core/update-repl)


(defn format-number [n]
  (let [left-pad (apply str (take (- 3 (mod (count (str n)) 3)) (repeat " ")))] 
    (apply str (interpose "," (map (partial apply str) (partition 3 (str left-pad n)))))))

(defn setup-tile [world [[x z] m]]
  (let [o (game.tiles/clone-tile x z m)]
    (state! o (conj {:rotation :n} m))
    (set-state! o :tile [x z])
    (swap! GRID assoc-in [[x z] :obj] o)
    (parent! o world)
    (hook+ o :on-mouse-enter #'game.core/tile-mouse-enter)
    (hook+ o :on-mouse-exit #'game.core/tile-mouse-exit)
    (hook+ o :on-mouse-down #'game.core/tile-click)
    (game.tiles/init [x z] (get @GRID [x z]))
    o))

(defn destroy-tile [o]
  (if-let [tile (state o :tile)]
    (swap! GRID update-in [tile] dissoc :obj))
  (destroy o)
  (reset! SELECTED nil))

(defn replace-tile [o k]
  (let [d (get tile-data k {})] 
    (destroy-tile o)
    (reset! SELECTED (setup-tile (the world) [(state o :tile) d]))))

(defn rotate-tile [o] 
  (when-let [s (state o)]
    (when-not (:no-rotate s)
      (let [dir (:rotation s)
            newdir (clockwise dir)]
        (set-state! o :rotation newdir)
      (timeline*
        (tween 
          {:local {:euler (cardinal->euler newdir)}}
          o 0.2 :pow3)) ))))

(defn tile-menu-str []
  (str "rotate [space]\ndelete [delete]\n\n"
    (apply str (interpose "\n" 
    (map 
      #(str (:type %) " [" (:key %)"]")
      (filter :key (vals tile-data)))))))

(defn update-game [_]
  (if (key-down? "escape")
    (make-level nil)
    (let [score (the score)]
      (set! (.text (.* (the score)>Text)) (format-number @SCORE))
      (set! (.text (.* (the debug)>Text)) (tile-menu-str))
      (if-let [o @SELECTED]
        (do 
          (position! (the cursor) (>v3 o))
          (cond
            (key-down? "delete")
            (replace-tile o {})
            (key-down? "space")
            (rotate-tile o))
          (dorun 
            (map 
             #(if (key-down? (:key %)) 
                  (replace-tile o (:type %))) 
              (filter :key (vals tile-data)))))
        (position! (the cursor) (v3 0 -1000 0))))))


(defn tile-click [o]
  (let [os (state o)
        [x z] (:tile os)]
    (game.tiles/click x z o os (:type os))))

(defn tile-mouse-enter [o]
  (set-state! o :hover true))

(defn tile-mouse-exit [o]
  (set-state! o :hover false))

(defn make-level [_]
  (reset! SCORE 0)
  (reset! SELECTED nil)
  (reset! GRID 
    (into {} 
      (for [x (range 11)
            z (range 11)]
        {(mapv - [x z][5 5]) {}})))
  (swap! GRID assoc-in [[0 0]] (:core tile-data))
  (clear-cloned!)
  (mapv clone! [:EventSystem :Canvas :camera :sun :backdrop :cursor])
  (let [world (clone! :empty)]
    (set! (.name world) "world")
    (dorun
      (map (partial setup-tile world) @GRID))
    (hook+ world :update #'game.core/update-game)))


(defn ns-wrap [s]
  (let [rs  (read-string s)
        nsn (last (take 2 rs))]
 (str 
  "(do (if-not (find-ns '"  nsn
   ") (try (require '" nsn
    " :reload) (catch Exception e (ns " nsn
     " )))) (in-ns '"  nsn
      ") "  s ")")))

(defn lisp [_]
  (try 
    (eval (read-string (ns-wrap (slurp "./tween/core.clj"))))
    (eval (read-string (ns-wrap (slurp "./game/data.clj"))))
    (eval (read-string (ns-wrap (slurp "./game/tiles.clj"))))
    (eval (read-string (ns-wrap (slurp "./game/core.clj"))))
    (make-level nil)
    (catch Exception e
      (mapv clone! [:intro :sun :camera]))))

'(hook+ (the make-level) :start #'game.core/lisp)
'(hook+ (the make-level) :start #'game.core/start-repl)
'(hook+ (the make-level) :update #'game.core/update-repl)

'(lisp nil)

(defn key-lisp [_] (if (key-down? "r") 
  (do 
    (.Enqueue arcadia.repl/work-queue 
          ["(game.core/lisp nil)" nil nil]))))

'(hook+ (the make-level) :update #'game.core/key-lisp)