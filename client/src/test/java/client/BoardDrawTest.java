package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import org.junit.jupiter.api.*;
import ui.BoardDrawer;

import java.util.HashMap;

import static ui.EscapeSequences.*;

public class BoardDrawTest {

    HashMap<ChessPiece.PieceType, String> whitePieces;
    HashMap<ChessPiece.PieceType, String> blackPieces;
    BoardDrawer bd;

    @Test
    public void testDrawChessBoardWhite() {
        bd = new BoardDrawer(new ChessBoard());
        bd.drawChessBoard("Tom", "Jenn", ChessGame.TeamColor.WHITE, ChessGame.TeamColor.WHITE);
        Assertions.assertTrue(true);
    }

    @Test
    public void testDrawChessBoardBlack() {
        bd = new BoardDrawer(new ChessBoard());
        bd.drawChessBoard("Tom", "Jenn", ChessGame.TeamColor.BLACK, ChessGame.TeamColor.BLACK);
        Assertions.assertTrue(true);
    }

    @Test
    public void testMoveOptions() {
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        bd = new BoardDrawer(board);
        boolean b = bd.showPieceMoves(2, 2, ChessGame.TeamColor.BLACK);
        Assertions.assertTrue(b);

    }

}
