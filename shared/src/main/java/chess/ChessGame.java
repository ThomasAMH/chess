package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;


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

    private ChessPosition whiteKingPos;
    private ChessPosition blackKingPos;

    public ChessGame() {
        gameBoard = new ChessBoard();
        activePlayer = TeamColor.WHITE;
        gameState = GameState.NORMAL;
        whitePieces = new HashMap<ChessPosition, Collection<ChessMove>>();
        blackPieces = new HashMap<ChessPosition, Collection<ChessMove>>();
        initializePieceHashmaps();
        whiteKingPos = new ChessPosition(1, 5);
        blackKingPos = new ChessPosition(8, 5);
    }

    private void initializePieceHashmaps() {
        ChessPosition currPosition;
        ChessPiece currPiece;

        for(int i = 1; i <= 8; i++) {
            for(int j = 1; j <= 2; j++) {
                currPosition = new ChessPosition(j, i);
                currPiece = gameBoard.getPiece(currPosition);
                whitePieces.put(currPosition, currPiece.pieceMoves(gameBoard, currPosition));
            }
            for(int k = 7; k <= 8; k++) {
                currPosition = new ChessPosition(k, i);
                currPiece = gameBoard.getPiece(currPosition);
                blackPieces.put(currPosition, currPiece.pieceMoves(gameBoard, currPosition));
            }
        }
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
        if(pieceAtPos == null) return null;
        if(pieceAtPos.getTeamColor() == TeamColor.WHITE) return whitePieces.get(startPosition);
        if(pieceAtPos.getTeamColor() == TeamColor.BLACK) return blackPieces.get(startPosition);
        return null;
    }

    /**
     * Makes a move in a chess game and update the game state
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {

        if(!isMoveLegal(move)) throw new InvalidMoveException();
        updateBoard(move, gameBoard);
    }

    private boolean isMoveLegal(ChessMove proposedMove) {
        ChessPosition startingPosition = proposedMove.getStartPosition();
        ChessPiece movingPiece = gameBoard.getPiece(startingPosition);

        // Is the first safeguard needed?
        if(movingPiece == null) return false;
        else return movingPiece.getTeamColor() == activePlayer;
    }

    /**
     * Update the provided game board with the move, handling captures, promotes and castling
     *
     * @param proposedMove is the move to execute
     */
    private void updateBoard(ChessMove proposedMove, ChessBoard board) {
        //Update piece's position
        ChessPosition startingPosition = proposedMove.getStartPosition();
        ChessPosition endPosition = proposedMove.getEndPosition();
        ChessPiece movingPiece = board.getPiece(startingPosition);

        //Address promotions
        if(proposedMove.getPromotionPiece() != null) {
            movingPiece = new ChessPiece(activePlayer, proposedMove.getPromotionPiece());
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


    }

    /**
     *
     * @param moveList the list of moves to be updated based on current game state
     */
    private HashMap<ChessPosition, Collection<ChessMove>> updateMoveList(ChessBoard board, TeamColor teamToCheck) {
        HashMap<ChessPosition, Collection<ChessMove>> returnList = new HashMap<ChessPosition, Collection<ChessMove>>();
        ChessPiece currPiece;
        ArrayList<ChessMove> proposedMoves;
        ArrayList<ChessMove> legalMoves;

        for(ChessPosition piecePos: moveList.keySet()) {

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

        if(kingEndCol == 3) rStartCol = 1;
        else rStartCol = 8;
        if(rStartCol == 1) rEndCol = 4;
        else rEndCol = 6;

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
    // FIXME: I suspect this needs review
    public boolean isInCheck(TeamColor teamColor) {
        return teamColor == activePlayer && gameState == GameState.CHECK;
    }

    /**
     *
     * @param defendingTeam the team who you are check if they will be in check after the move
     * @param proposedMove the move that opponent to defendingTeam will make whose consequences will be evaluated
     * @return if the move results in a hypothetical game state where an opponent's piece can capture the king
     */
    private boolean isInCheck(TeamColor defendingTeam, ChessMove proposedMove) {
        HashMap<ChessPosition, Collection<ChessMove>> attackingTeamMoves;
        if(defendingTeam == TeamColor.WHITE) {
            attackingTeamMoves = new HashMap<ChessPosition, Collection<ChessMove>>(blackPieces);
        }
        else {
            attackingTeamMoves = new HashMap<ChessPosition, Collection<ChessMove>>(whitePieces);
        }


    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        gameBoard = board;
        activePlayer = TeamColor.WHITE;
        gameState = GameState.NORMAL;
        whitePieces = new HashMap<ChessPosition, ArrayList<ChessMove>>();
        blackPieces = new HashMap<ChessPosition, ArrayList<ChessMove>>();
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
}