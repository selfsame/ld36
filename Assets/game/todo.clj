(ns game.todo)

'[IDEAS]

"The something device"
'(an idle clicker game)

'(A grid of stone squares, in the center is a button.
  
  Clicking the button increments your points, eventually allowing
  you to buy new tiles to place.

  Tiles interact with each other, by staging them you 
  create a sort of brainfuck point making machine)

'[UI]
'((x) score)

'[game]
'((x) init the map with a single :core tile)
'((/) route tile interaction to game.tiles pdfn
  ((x) tile-init)
  ((/) tile-destroy
    (( ) replace with blank))
  ((x) tile-click, mouse-enter, mouse-exit))
'((x) rotate tiles)


'[simulation]
'(( ) "point" flow
  (( ) ????)
  (( ) declare interaction in tile-data))

'(( ) upgrades
  ((x) clicking selects a tile)
  (( ) delete a tile)
  (( ) clicking empty tile shows upgrade panel?))