2024-01-13

## Careful To...
* Use naming conventions
* You have to justify why a more simple solution would not work better!

## Tasks
_And debug statements along the way via toString()_
1. In order for the tests to pass, you are required to override the equals() and hashCode() methods in your class implementations as necessary. This includes the...
   - [x] ChessPosition
   - [x] ChessPiece
   - [x] ChessMove
   - [x] Chess Board
2. Ensure that the pieces have the following properties set up (Used to calculate the pieces move correctly)
   - [x] color
   - [x] possible_moves
3. Create the isValidMove function, which Checks to see if a move goes off the board or on a friendly class
   - [x] function
4. Implement the piece possible piece movement functions
   - [x] function
5. Debug the chess moves!
   - [x] Debugged moves

! Consider implementing a Debug class

## Piece Movement Notes
* Every piece has a function that is called on a switch
* There are two types of moves:
  * **Iterator Moves** are moves that must check every square in a direction until an enemy or friendly piece is encountered
  * **Fixed Move** are moves that must only check a small quantity of fixed moved opportunities if they are possible or not
* Each piece has a linear (unless knight) range of what is possible:
  * Diagonal for bishops is every (+1, +1)...
  * Knights is a hardcoded number of possible positions to check if valid moves exist
* The movement function takes the type of piece, the alliance, then cycles through all the moves in the array
  * Any location occupied by an unfriendly piece in range is OK, but any beyond it in that direction are NOT
    * (iterate direction cutoff)
  * Any location occupied by a friendly piece in range is NOT OK
    * (iterate direction cutoff)
  * Iterator pieces include:
    * Rook, bishop, queen
  * Hardcode positions include
    * King, pawn, knight

### Moving a piece entails...
1. Selecting the piece
2. Passing the board and the piece to the helper class

## Outstanding Questions
1. Is this going to play well with future assignments? The server?
2. Does this follow best OOP practices?