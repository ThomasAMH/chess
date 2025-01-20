package chess;

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

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow()][position.getColumn()] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow()][position.getColumn()];
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
     * @param proposedPiece is the piece attempting to move. Only team color is examined.
     * @return Returns true if the proposed space is not occupied by a friendly piece, and is on the board
     */
    public boolean isValidMove(ChessMove proposedMove) {
        int startRow, startColumn;
        startRow = proposedMove.getStartPosition().getRow();
        startColumn = proposedMove.getStartPosition().getColumn();

        if (board[startRow][startColumn] == null) {
            throw new RuntimeException("No piece at specified position");
        }
        ChessPiece movingPiece = board[startRow][startColumn];

        int endRow, endColumn;
        endRow = proposedMove.getEndPosition().getRow();
        endColumn = proposedMove.getEndPosition().getColumn();

        if (endRow < 0 || endRow >= 8 || endColumn < 0 || endColumn >= 8) {
            return false;
        }

        ChessPiece pieceAtEndLocation = board[endRow][endColumn];

        ChessGame.TeamColor movingPieceTeamColor = movingPiece.getTeamColor();

        if (pieceAtEndLocation == null) {
            return true;
        }
        return pieceAtEndLocation.getTeamColor() != movingPieceTeamColor;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
