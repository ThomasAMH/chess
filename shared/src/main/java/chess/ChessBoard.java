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

    public enum MoveResult {
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
    public MoveResult isValidMove(ChessMove proposedMove) {
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
            return MoveResult.ILLEGAL;
        }

        // Due to the complexity of pawn moves, call the helper function
        if (movingPiece.getPieceType() == ChessPiece.PieceType.PAWN) {
            return checkPawnMoves(proposedMove);
        }

        ChessPiece pieceAtEndLocation = getPiece(proposedMove.getEndPosition());

        ChessGame.TeamColor movingPieceTeamColor = movingPiece.getTeamColor();

        if (pieceAtEndLocation == null) {
            return MoveResult.LEGAL;
        }
//        Debug line: System.out.println("Moving piece color: " + movingPieceTeamColor + ",
//        occupying piece color: " + pieceAtEndLocation.getTeamColor());
        if(pieceAtEndLocation.getTeamColor() == movingPieceTeamColor) {
            return MoveResult.ILLEGAL;
        } else {
            return MoveResult.CAPTURE;
        }
    }

    /**
     *
     * @param proposedMove: one of the pawn's possible moves
     * @return If the pawn's move is legal
     */
    private MoveResult checkPawnMoves(ChessMove proposedMove) {
//        Debug: System.out.println("check pawn moves " + proposedMove);
        ChessPiece movingPiece = getPiece(proposedMove.getStartPosition());
        ChessGame.TeamColor movingPieceTeamColor = movingPiece.getTeamColor();

        //Direction of travel multiplier helps determine the "forward" direction
        int dirOfTravelMult = 1;
        if (movingPieceTeamColor == ChessGame.TeamColor.BLACK) {
            dirOfTravelMult = -1;
        }
        int startRow, startColumn;
        startRow = proposedMove.getStartPosition().getRow();
        startColumn = proposedMove.getStartPosition().getColumn();

        int endRow, endColumn;
        endRow = proposedMove.getEndPosition().getRow();
        endColumn = proposedMove.getEndPosition().getColumn();

        ChessPosition pieceOneAheadPos = new ChessPosition(startRow + (1 * dirOfTravelMult), startColumn);
        ChessPiece pieceOneAheadPiece = getPiece(pieceOneAheadPos);

        //Initial moves

        if (Math.abs(startRow - endRow) == 2) {
            if((!(startRow == 2 && movingPieceTeamColor == ChessGame.TeamColor.WHITE) &&
               !(startRow == 7 && movingPieceTeamColor == ChessGame.TeamColor.BLACK))) {
                return MoveResult.ILLEGAL;
            }

            ChessPosition pieceTwoAheadPos = new ChessPosition(startRow + (2 * dirOfTravelMult), startColumn);
            ChessPiece pieceTwoAheadPiece = getPiece(pieceTwoAheadPos);

            if(pieceOneAheadPiece == null && pieceTwoAheadPiece == null) {
                return MoveResult.LEGAL;
            }
            return MoveResult.ILLEGAL;
        }

        //Captures
        ChessPiece pieceAtEndLocation = getPiece(proposedMove.getEndPosition());
        if (endColumn != startColumn) {
            if (pieceAtEndLocation == null) {
                return MoveResult.ILLEGAL;
            } else {
                if (movingPieceTeamColor == pieceAtEndLocation.getTeamColor()) {
                    return MoveResult.ILLEGAL;
                } else {
                    return MoveResult.CAPTURE;
                }
            }
        }

        //Forward moves
        if (pieceAtEndLocation == null) {
            return MoveResult.LEGAL;
        } else {
            return MoveResult.ILLEGAL;
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
