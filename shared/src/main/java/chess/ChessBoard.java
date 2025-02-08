package chess;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard implements Iterable<ChessPosition>{
    private ChessPiece[][] board;
    public ChessBoard() {
        board = new ChessPiece[8][8];
    }
    public ChessBoard(ChessPiece[][] someBoard) {
        board = new ChessPiece[8][8];
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                board[i][j] = someBoard[i][j];
            }
        }
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
     * Remove the piece at the specified position.
     * @param position assumes a piece is at this position
     */
    public void removePiece(ChessPosition position) {
        board[position.getRow() - 1][position.getColumn() - 1] = null;
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
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = null;
            }
        }
        board[0][0] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        board[0][7] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        board[0][1] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        board[0][6] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        board[0][2] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        board[0][5] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        board[0][3] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
        board[0][4] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);

        board[7][0] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        board[7][7] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        board[7][1] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        board[7][6] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        board[7][2] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        board[7][5] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        board[7][3] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
        board[7][4] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);

        for (int i = 0; i < 8; i++) {
            board[1][i] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            board[6][i] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        }

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
                    if (isPawnMovePromotion(proposedMove)) {
                        return MoveResult.PROMOTE;
                    }
                    return MoveResult.CAPTURE;
                }
            }
        }

        //Forward moves
        if (pieceAtEndLocation == null) {
            if (isPawnMovePromotion(proposedMove)) {
                return MoveResult.PROMOTE;
            }
            return MoveResult.LEGAL;
        } else {
            return MoveResult.ILLEGAL;
        }

    }

    private boolean isPawnMovePromotion(ChessMove proposedMove) {
        ChessPiece movingPiece = getPiece(proposedMove.getStartPosition());
        ChessGame.TeamColor movingPieceTeamColor = movingPiece.getTeamColor();
        int endRow = proposedMove.getEndPosition().getRow();

        return (movingPieceTeamColor == ChessGame.TeamColor.WHITE && endRow == 8) ||
                (movingPieceTeamColor == ChessGame.TeamColor.BLACK && endRow == 1);
    }

    public ChessPiece[][] getBoard() {
        return board;
    }

    public ChessPosition getKingPos(ChessGame.TeamColor teamColor) {
        ChessPiece currPiece;
        ChessPosition currPos;
        for(int i = 1; i < 9; i++) {
            for(int j = 1; j < 9; j++) {
                currPos = new ChessPosition(i,j);
                currPiece = getPiece(currPos);
                if(currPiece == null) continue;
                if(currPiece.getTeamColor() == teamColor && currPiece.getPieceType() == ChessPiece.PieceType.KING) {
                    return currPos;
                }
            }
        }
        return null;
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

    @Override
    public Iterator<ChessPosition> iterator() {
        return new ChessBoardIterator();
    }

    private class ChessBoardIterator implements Iterator<ChessPosition> {
        private int row = 1, column = 1;
        ChessPosition currPos;
        ChessPiece pieceAtCurrPos;
        @Override
        public boolean hasNext() {
            while(row <= 8) {
                while(column <= 8) {
                    currPos = new ChessPosition(row, column);
                    pieceAtCurrPos = getPiece(currPos);
                    if(pieceAtCurrPos != null) {
                        return true;
                    }
                    column++;
                }
                column = 1;
                row++;
            }
            return false;
        }

        @Override
        public ChessPosition next() {
            if(!hasNext()) {
                throw new NoSuchElementException();
            }
            column++;
            return currPos;
        }
    }

}
