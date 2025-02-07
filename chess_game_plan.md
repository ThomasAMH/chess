# Chess game logic and organization
## Managing Game State
### Properties
☑ activeTurn - TeamColor
* Initiated to white, switches after each call to move (use a TeamColor for this)

☑ game stateFlag - Struct
* A struct with CHECKMATE, STALEMATE, CHECK, NORMAL to indicate if the game should continue.
* Updated after each move

☑ whitePieces / blackPieces - Map
* A map using the position as the key (whitepieces) and an array of ChessMoves as the value
* Created at initialization and updated after each move

☑ gameBoard - ChessBoard
* Set at initialization or by the setter

### Functions
1. Initialization
   1. ☑ Create an empty game board
   2. ☑ Initialize the board property

2. validMoves(somePosition)
   1. ☑ Return the piece's move set

3. isMoveLegal
   1. ☑ Check to see if a given move will result in check for the mover with a call to isInCheck(). If so, illegal.
   2. ☑ Check to see if move is not on player's turn. If so, illegal. (??)
   3. ☑ If no piece at location, null

4. makeMove
   1. ☑ Check if proposed move is in whitePieces for piece @ start position AND check turn
      * ☑ If not, throw InvalidMoveException
   2. Move piece on board (updateChessBoard) and in whitePieces (updateMoveList)
   3. Evaluate if move resulted in stalemate, checkmate or check for new player by updating game state property
   4. Update move lists for updated game state with call to updateMoveList
      * Somebody clever could probably assess the possibility of only recalculating effected moves to reduce computing time...
   5. Switch game turn, if game mode is OK

5. updateMoveList - _Called when game state changes_
   1. For all pieces in the whitePieces and blackPieces, call their possible move function
   2. Run all moves through isMoveLegal
   3. Update a data structure (hashmap) of all possible legal in the current game state
   4. Append any special moves at the end based on unit type (En Passant & Castling)

6. updateChessBoard(board, move)
   1. ☑ Move the piece on the board provided
      1. ☑ Remove old piece, if capture
      2. ☑ Add new piece, if promote

7. updateSpecialMoveFlags - _Called when a move happens_
    #### Castle
   1. wLRook, wRRook, wKing and black counterparts are checked. If moved, set flag to false.
   2. Ensure squares are vacant
   3. Call isMoveLegal to check King Checks

    #### En Passant
   1. Check if the move is a double pawn move...
   2. If so, check all friendly pawn positions

8. isInCheck(TeamColor, default constructor with move = null)
   1. Evaluate all unfriendly piece's moves, and if any can kill you, return true.
   2. If there is a move provided, make a local copy of the board after that move is provided
      * Then check all possible moves to see if any can capture the king
   3. If no move is provided, evaluate all opposing piece moves and return true if any can capture the king

9. isInCheckmate(TeamColor)
   1. Define checkMateFlag = True
   2. Iterate through all possible friendly moves and call the isInCheck on the game state, and if any can return false, flip the flag and return
      * Otherwise, return true

10. isInStalemate(TeamColor)
    1. Define isStalemateFlag = False
    2. Try to flip the flag by checking every next player's move against the isInCheck function

11. setBoard
    1. Accept a board, iterate through 

12. initializePieces(board)
    1. Given a board, clear out the black/white pieces map and correct with new pieces 