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
  ((x) tile-destroy
    ((x) replace with blank))
  ((x) tile-click, mouse-enter, mouse-exit)
  ((/) input output))
'((x) rotate tiles)


'[simulation]
'((x) "point" flow)

'([upgrades]
  ((x) clicking selects a tile
    ((x) menu - list tile keys)
    (( ) generate key-commands from tile data))
  ((x) delete a tile)
  (( ) placing a tile costs points
    (( ) distance from center tile is a cost multiplier)))

'[tiles]
'((x) :finger     - clicks something in a direction
  ((x) kill input))
'((x) :arrow      - redirects input
  ((x) don't allow returned input (ping pong loop)))
'((x) :splitter   - splits input into two outputs
  ((x) use T shape, only accept inputs on bottom and split to other two
    ((/) make this a generic fn using data)))
'((x) :logic-gate - "&" has on off state toggled from bottom. Off state blocks pellets)
'(( ) :delay      - inputs are held for a time)


'BUGS
'((x) pellet colliders interfere with tile clicking)
'(( ) score string has leading "," for mod 3 numbers)

'POLISH
'(( ) pprint table for tile key command menu)
