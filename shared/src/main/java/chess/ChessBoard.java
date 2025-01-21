package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private ChessPiece[][] board = new ChessPiece[8][8];
    public ChessBoard() {
        
    }

    public enum moveResult {
        CAPTURE,
        LEGAL,
        ILLEGAL,
        PROMOTE
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow() - 1][position.getColumn() - 1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Partial check if move is legal or not
     * Assumes that the caller will not propose illegal piece moves (ex. knight forward 3)
     * @param proposedMove is the move in question.
     * @return Returns true if the proposed space is not occupied by a friendly piece, and is on the board
     */
    public moveResult isValidMove(ChessMove proposedMove) {
        int startRow, startColumn;
        startRow = proposedMove.getStartPosition().getRow();
        startColumn = proposedMove.getStartPosition().getColumn();

        ChessPiece movingPiece = getPiece(proposedMove.getStartPosition());

        if (movingPiece == null) {
            throw new RuntimeException("No piece at " + startRow + "," + startColumn);
        }


        int endRow, endColumn;
        endRow = proposedMove.getEndPosition().getRow();
        endColumn = proposedMove.getEndPosition().getColumn();

        if (endRow < 1 || endRow > 8 || endColumn < 1 || endColumn > 8) {
            return moveResult.ILLEGAL;
        }

        ChessPiece pieceAtEndLocation = getPiece(proposedMove.getEndPosition());

        ChessGame.TeamColor movingPieceTeamColor = movingPiece.getTeamColor();

        if (pieceAtEndLocation == null) {
            return moveResult.LEGAL;
        }
//        Debug line: System.out.println("Moving piece color: " + movingPieceTeamColor + ", occupying piece color: " + pieceAtEndLocation.getTeamColor());
        if(pieceAtEndLocation.getTeamColor() == movingPieceTeamColor) {
            return moveResult.ILLEGAL;
        } else {
            return moveResult.CAPTURE;
        }
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }
}
