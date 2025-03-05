package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Objects;


/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard gameBoard;
    private TeamColor activePlayer;
    private HashMap<ChessPosition, Collection<ChessMove>> whitePieces;
    private HashMap<ChessPosition, Collection<ChessMove>> blackPieces;
    private GameState gameState;

    public ChessGame() {
        gameBoard = new ChessBoard();
        gameBoard.resetBoard();
        activePlayer = TeamColor.WHITE;
        gameState = GameState.NORMAL;
        initializePieceHashmaps();
    }

    private void initializePieceHashmaps() {
        ChessPosition currPosition;
        ChessPiece currPiece;
        TeamColor currTeamColor;
        whitePieces = new HashMap<ChessPosition, Collection<ChessMove>>();
        blackPieces = new HashMap<ChessPosition, Collection<ChessMove>>();

        whitePieces = updateMoveList(gameBoard, TeamColor.WHITE, true);
        blackPieces = updateMoveList(gameBoard, TeamColor.BLACK, true);
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return activePlayer;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        activePlayer = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    public enum GameState {
        NORMAL,
        CHECK,
        CHECKMATE,
        STALEMATE
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece pieceAtPos = gameBoard.getPiece(startPosition);
        if(pieceAtPos == null) {return null;}
        if(pieceAtPos.getTeamColor() == TeamColor.WHITE) { return whitePieces.get(startPosition);}
        if(pieceAtPos.getTeamColor() == TeamColor.BLACK) {return blackPieces.get(startPosition);}
        return null;
    }

    /**
     * Makes a move in a chess game and update the game state
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {

        if(!isMoveLegal(move)) {
            throw new InvalidMoveException();
        }
        updateBoard(move, gameBoard);
        whitePieces = updateMoveList(gameBoard, TeamColor.WHITE, true);
        blackPieces = updateMoveList(gameBoard, TeamColor.BLACK, true);

        //Update game state: is the next player in check?
        TeamColor nextPlayer;
        if(activePlayer == TeamColor.WHITE) {nextPlayer = TeamColor.BLACK;}
        else {nextPlayer = TeamColor.WHITE;}

        if(isInCheck(nextPlayer)) {gameState = GameState.CHECK;};
        if(isInCheckmate(nextPlayer)) {gameState = GameState.CHECKMATE;}
        if(isInStalemate(nextPlayer)) {gameState = GameState.STALEMATE;}

        if(activePlayer == TeamColor.BLACK) {activePlayer = TeamColor.WHITE;}
        else {activePlayer = TeamColor.BLACK;}
    }

    /**
     *
     * @param proposedMove: the user-provided move to be evaluated;
     *                    evaluates legality of moves prior to execution on active game state
     *
     * @return boolean value if the move is for the active player AND on a legit piece AND a move from the move list
     * Assumes that only legit moves will be presented to player for selection from movelist
     */
    private boolean isMoveLegal(ChessMove proposedMove) {
        ChessPosition startingPosition = proposedMove.getStartPosition();
        ChessPiece movingPiece = gameBoard.getPiece(startingPosition);

        if(movingPiece == null) {return false;}
        if(movingPiece.getTeamColor() != activePlayer) {return false;}

        //Check if move is legal / offered to player
        HashMap<ChessPosition, Collection<ChessMove>> teamMoveset;
        if(activePlayer == TeamColor.WHITE) {teamMoveset = whitePieces;}
        else {teamMoveset = blackPieces;}

        if(!teamMoveset.containsKey(startingPosition)) {return false;}
        return teamMoveset.get(startingPosition).contains(proposedMove);
    }

    /**
     * Update the provided game board with the move, handling captures, promotes and castling
     *
     * @param proposedMove is the move to execute
     */
    private ChessBoard updateBoard(ChessMove proposedMove, ChessBoard board) {
        //Update piece's position
        ChessPosition startingPosition = proposedMove.getStartPosition();
        ChessPosition endPosition = proposedMove.getEndPosition();
        ChessPiece movingPiece = board.getPiece(startingPosition);

        //Address promotions
        if(proposedMove.getPromotionPiece() != null) {
            movingPiece = new ChessPiece(movingPiece.getTeamColor(), proposedMove.getPromotionPiece());
        }

        board.addPiece(endPosition, movingPiece);

        //Remove old piece
        board.removePiece(startingPosition);

        //Move Rook, if castle
        if((movingPiece.getPieceType() == ChessPiece.PieceType.KING) &&
                (Math.abs(endPosition.getColumn()) - startingPosition.getColumn()) > 1) {
            ChessMove rookMove = getRookCastleMove(proposedMove);
            updateBoard(rookMove, board);
        }
        return board;
    }


    /**
     *
     * @param board: The board, real or hypothetical, to generate a move list from
     * @param teamColor: The team whose moves will be evaluated
     * @param checkForCheck: If true, do NOT add a move to the list that will result in a check for the player.
     *                     False is used when checking if a king capture is possible (in which case it doesn't matter
     *                     if the king-capturing team is in check or not as a result of the move, as it's game over)
     * @return the map of possible moves,
     */
    private HashMap<ChessPosition, Collection<ChessMove>> updateMoveList(ChessBoard board, TeamColor teamColor, boolean checkForCheck) {
        HashMap<ChessPosition, Collection<ChessMove>> returnList = new HashMap<ChessPosition, Collection<ChessMove>>();
        ChessPiece currPiece;
        Collection<ChessMove> proposedMoves;
        ArrayList<ChessMove> legalMoves;

        for(ChessPosition boardSquare: board) {
            currPiece = board.getPiece(boardSquare);
            if(currPiece.getTeamColor() != teamColor) {continue;}
            proposedMoves = currPiece.pieceMoves(board, boardSquare);
            legalMoves = new ArrayList<ChessMove>();

            for(ChessMove proposedMove: proposedMoves) {
                // Note: the proposedMoves will NOT contain moves that move any piece but this one,
                // And will not contain illegal moves
                // The only check needed is if the move will result in check or not
                if(checkForCheck) {
                    if(!isInCheck(proposedMove)) {
                        legalMoves.add(proposedMove);
                    }
                } else {
                    legalMoves.add(proposedMove);
                }
            }
            //Do tests require an empty array of no moves are possible?
            returnList.put(boardSquare, legalMoves);
//            if(!legalMoves.isEmpty()) returnList.put(boardSquare, legalMoves);
        }
        return returnList;
    }
    private ChessMove getRookCastleMove(ChessMove proposedMove) {
        int kingStartRow, kingEndCol;
        kingStartRow = proposedMove.getStartPosition().getRow();
        kingEndCol = proposedMove.getEndPosition().getColumn();

        int rStartCol, rStartRow, rEndCol, rEndRow;
        ChessPosition rookStartPos, rookEndPos;

        rStartRow = kingStartRow;
        rEndRow = rStartRow;

        if(kingEndCol == 3){rStartCol = 1;}
        else {rStartCol = 8;}
        if(rStartCol == 1) {rEndCol = 4;}
        else {rEndCol = 6;}

        rookStartPos = new ChessPosition(rStartRow, rStartCol);
        rookEndPos = new ChessPosition(rEndRow, rEndCol);
        return new ChessMove(rookStartPos, rookEndPos, null);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        HashMap<ChessPosition, Collection<ChessMove>> attackingTeamMoves;
        if(teamColor == TeamColor.WHITE) {
            attackingTeamMoves = blackPieces;
        }
        else {
            attackingTeamMoves = whitePieces;
        }

        return isKingInDanger(teamColor, attackingTeamMoves, gameBoard);
    }

    /**
     *
     * @param proposedMove the move in question
     * @return return true if the move puts the mover's team in check, false if not
     */
    private boolean isInCheck(ChessMove proposedMove) {
        ChessBoard hypotheticalBoard = updateBoard(proposedMove, new ChessBoard(gameBoard.getBoard()));
        HashMap<ChessPosition, Collection<ChessMove>> attackingTeamMoves;
        TeamColor attackingTeamColor;
        TeamColor defendingTeam = hypotheticalBoard.getPiece(proposedMove.getEndPosition()).getTeamColor();
        if(defendingTeam == TeamColor.WHITE) {
            attackingTeamColor = TeamColor.BLACK;
        }
        else {
            attackingTeamColor = TeamColor.WHITE;
        }
        // Does not matter if they put their own king in danger, so checking "check" flag is not needed.
        attackingTeamMoves = updateMoveList(hypotheticalBoard, attackingTeamColor, false);
        return isKingInDanger(defendingTeam, attackingTeamMoves, hypotheticalBoard);
    }

    private boolean isKingInDanger(TeamColor defendingTeamColor, HashMap<ChessPosition,
            Collection<ChessMove>> attackingTeamMoves, ChessBoard gameBoard) {
        ChessPosition defendingKingPos = gameBoard.getKingPos(defendingTeamColor);
        ChessPosition potentialEnemyPos;
        for(ChessPosition chessPos: attackingTeamMoves.keySet()) {
            for(ChessMove enemyMove: attackingTeamMoves.get(chessPos)) {
                potentialEnemyPos = enemyMove.getEndPosition();
                if(potentialEnemyPos.equals(defendingKingPos)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if(!isInCheck(teamColor)) {return false;}
        return areMovesAvailable(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if(isInCheck(teamColor)) {return false;}
        return areMovesAvailable(teamColor);
    }

    private boolean areMovesAvailable(TeamColor teamColor) {
        HashMap<ChessPosition, Collection<ChessMove>> moveSet;
        if(teamColor == TeamColor.WHITE) {moveSet = whitePieces;}
        else {moveSet = blackPieces;}

        for(ChessPosition piecePos: moveSet.keySet()) {
            if(!moveSet.get(piecePos).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.gameBoard = board;
        activePlayer = TeamColor.WHITE;
        gameState = GameState.NORMAL;
        initializePieceHashmaps();
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return gameBoard;
    }

    @Override
    public boolean equals(Object o) {
        if(o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(gameBoard, chessGame.gameBoard) &&
                activePlayer == chessGame.activePlayer && Objects.equals(whitePieces, chessGame.whitePieces) &&
                Objects.equals(blackPieces, chessGame.blackPieces) && gameState == chessGame.gameState;
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameBoard, activePlayer, whitePieces, blackPieces, gameState);
    }
}