(ns fix
  (:use 
    tween.pool
    tween.core
    arcadia.core
    arcadia.linear
    hard.core)
  (:require [arcadia.repl :as repl]))



(defn start-repl [go]
  (repl/start-server 11211))

(defn update-repl [go]
  (repl/eval-queue))


(defn testy [o] 
  (clear-cloned!)
  (mapv clone! [:camera :sun :pellet])
  (timeline* :loop
    #(do (destroy (the stone)) false)
    (wait 1.0)
    #(do (clone! :stone) false)
    (tween {:local {:scale (v3 10)}} (the pellet) 0.6)
    (tween {:local {:scale (v3 1)}} (the pellet) 0.6)
    ))





'(prn (macroexpand '(tween {:local {:scale (v3 10)}} (the pellet) 0.1)))

'(testy nil)

'(hook+ (the fix) :start #'fix/testy)
'(hook+ (the fix) :start #'fix/start-repl)

