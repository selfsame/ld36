(ns fix
  (:use 
    arcadia.core
    arcadia.linear
    hard.core
    tween.core)
  (:require [arcadia.repl :as repl]))



(defn start-repl [go]
  (repl/start-server 11211))

(defn update-repl [go]
  (repl/eval-queue))


(defn testy [o] 
  (clear-cloned!)
  (mapv clone! [:camera :sun :pellet])
  (timeline* :loop
    #(do (clone! :stone) nil)
    (wait 1.0)
    #(do (destroy (the stone)) nil)
    (tween {:local {:scale (v3 10)}} (the pellet) 0.6)
    (tween {:local {:scale (v3 1)}} (the pellet) 0.6)))


'(prn (macroexpand '(tween {:local {:scale (v3 10)}} (the pellet) 0.1)))

'(testy nil)

'(hook+ (the fix) :start #'fix/testy)
'(hook+ (the fix) :start #'fix/start-repl)

