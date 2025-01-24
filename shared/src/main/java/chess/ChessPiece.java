package chess;

import java.util.*;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final ChessGame.TeamColor teamColor;
    private final PieceType pieceType;
    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        teamColor = pieceColor;
        pieceType = type;
    }

    private final String[] kingMoves = {"0,1","0,-1","1,0","-1,0","1,1","-1,-1","-1,1","1,-1"};
    private final String[] pawnMoves = {"1,0", "1,1","1,-1", "2,0"};
    private final String[] knightMoves = {"2,1","-2,1","2,-1","-2,-1","1,2","1,-2","-1,2","-1,-2"};
    private final String[] bishopMoves = {"1,1","-1,-1","1,-1","-1,1"};
    private final String[] rookMoves = {"1,0","-1,0","0,-1","0,1"};
    private final String[] queenMoves = {"1,1","-1,-1","1,-1","-1,1","1,0","-1,0","0,-1","0,1"};


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
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition startPosition) {

        return switch (pieceType) {
            case PieceType.KING -> getLegalFixedMoves(board, startPosition, kingMoves);
            case PieceType.KNIGHT -> getLegalFixedMoves(board, startPosition, knightMoves);
            case PieceType.PAWN -> getLegalFixedMoves(board, startPosition, pawnMoves);
            case PieceType.ROOK -> getLegalVariableMoves(board, startPosition, rookMoves);
            case PieceType.BISHOP -> getLegalVariableMoves(board, startPosition, bishopMoves);
            case PieceType.QUEEN -> getLegalVariableMoves(board, startPosition, queenMoves);
        };
    }
    private Collection<ChessMove> getLegalFixedMoves(ChessBoard board,
                                                     ChessPosition startPosition, String[] moveDeltas) {
        Collection<ChessMove> legalMoves = new ArrayList<ChessMove>();
        int xStart, yStart;
        xStart = startPosition.getRow();
        yStart = startPosition.getColumn();

        int xDelta, yDelta;
        String[] deltas;

        for (String move : moveDeltas) {
            deltas = move.split(",");
            xDelta = Integer.parseInt(deltas[0]);
            yDelta = Integer.parseInt(deltas[1]);
            if (pieceType == PieceType.PAWN && teamColor == ChessGame.TeamColor.BLACK) {
                xDelta *= -1;
                yDelta *= -1;
            }

            ChessPosition initialPosition = new ChessPosition(xStart , yStart);
            ChessPosition proposedPosition = new ChessPosition(xStart + xDelta, yStart + yDelta);
            ChessMove proposedMove = new ChessMove(initialPosition, proposedPosition, null);

            ChessBoard.MoveResult moveState = board.isValidMove(proposedMove);
            if (moveState == ChessBoard.MoveResult.CAPTURE || moveState == ChessBoard.MoveResult.LEGAL) {
                legalMoves.add(proposedMove);
            } else if (moveState == ChessBoard.MoveResult.PROMOTE) {
                ChessMove bishopMove = new ChessMove(initialPosition, proposedPosition, PieceType.BISHOP);
                legalMoves.add(bishopMove);
                ChessMove queenMove = new ChessMove(initialPosition, proposedPosition, PieceType.QUEEN);
                legalMoves.add(queenMove);
                ChessMove rookMove = new ChessMove(initialPosition, proposedPosition, PieceType.ROOK);
                legalMoves.add(rookMove);
                ChessMove knightMove = new ChessMove(initialPosition, proposedPosition, PieceType.KNIGHT);
                legalMoves.add(knightMove);
            }

        }
        return legalMoves;
    }

    private Collection<ChessMove> getLegalVariableMoves(ChessBoard board,
                                                        ChessPosition startPosition, String[] moveDeltas) {
        Collection<ChessMove> legalMoves = new ArrayList<ChessMove>();
        int xStart, yStart;
        xStart = startPosition.getRow();
        yStart = startPosition.getColumn();

        int xDelta, yDelta;
        int xDeltaInit, yDeltaInit;
        String[] deltas;

        boolean validMoveFlag;

        for (String move: moveDeltas) {
            // Debug: System.out.println("Now examining delta: " + move);
            ChessPosition initialPosition = new ChessPosition(xStart, yStart);

            deltas = move.split(",");

            xDeltaInit = Integer.parseInt(deltas[0]);
            xDelta = xDeltaInit;

            yDeltaInit = Integer.parseInt(deltas[1]);
            yDelta = yDeltaInit;

            validMoveFlag = true;

            while(validMoveFlag) {
                ChessPosition proposedPosition = new ChessPosition(xStart + xDelta, yStart + yDelta);
                ChessMove proposedMove = new ChessMove(initialPosition, proposedPosition, null);
                ChessBoard.MoveResult moveState = board.isValidMove(proposedMove);
                if (moveState == ChessBoard.MoveResult.LEGAL) {
                    // Debug: System.out.println("Move " + proposedMove.toString());
                    legalMoves.add(proposedMove);
                    xDelta = xDelta + xDeltaInit;
                    yDelta = yDelta + yDeltaInit;
                } else if (moveState == ChessBoard.MoveResult.CAPTURE) {
                    // Debug: System.out.println("Move " + proposedMove.toString());
                    legalMoves.add(proposedMove);
                    xDelta = xDelta + xDeltaInit;
                    yDelta = yDelta + yDeltaInit;
                    validMoveFlag = false;
                } else if (moveState == ChessBoard.MoveResult.ILLEGAL) {
                    validMoveFlag = false;
                }
            }
        }
        // Debug: System.out.println("Legal moves are:");
//        for (ChessMove move: legalMoves) {
//            System.out.println("Move " + move.toString());
//        }
        return legalMoves;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return teamColor == that.teamColor && pieceType == that.pieceType &&
                Objects.deepEquals(kingMoves, that.kingMoves) &&
                Objects.deepEquals(pawnMoves, that.pawnMoves) &&
                Objects.deepEquals(knightMoves, that.knightMoves) &&
                Objects.deepEquals(bishopMoves, that.bishopMoves) &&
                Objects.deepEquals(rookMoves, that.rookMoves) &&
                Objects.deepEquals(queenMoves, that.queenMoves);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamColor, pieceType,
                Arrays.hashCode(kingMoves),
                Arrays.hashCode(pawnMoves),
                Arrays.hashCode(knightMoves),
                Arrays.hashCode(bishopMoves),
                Arrays.hashCode(rookMoves),
                Arrays.hashCode(queenMoves));
    }
}
