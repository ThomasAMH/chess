package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private ChessGame.TeamColor teamColor;
    private PieceType pieceType;
    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

     /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return pieceType;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove>possibleMoves =  new ArrayList<ChessMove>();

        switch(pieceType) {
            case PieceType.KING:
                pieceMoves
                break;
            case PieceType.QUEEN:
                break;
            case PieceType.BISHOP:
                break;
            case PieceType.KNIGHT:
                break;
            case PieceType.ROOK:
                break;
            case PieceType.PAWN:
                break;
            case default:
                break;
        }

    }
    private Collection<ChessMove> getChessMoves(PieceType pieceType) {
        ArrayList<chess.ChessMove> possibleMoves =  new ArrayList<chess.ChessMove>();
        String[] kingMoves = {"0,1","0,-1","1,0","-1,0","1,1","-1,-1","-1,1","1,-1"};
        String[] pawnMoves = {"0,1"};

        return possibleMoves;
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
