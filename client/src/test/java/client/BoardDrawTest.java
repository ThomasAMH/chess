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
    ChessBoard board;
    HashMap<ChessPiece.PieceType, String> whitePieces;
    HashMap<ChessPiece.PieceType, String> blackPieces;
    BoardDrawer bd;

    @Test
    public void testDrawChessBoardWhite() {
        bd = new BoardDrawer();
        bd.drawChessBoardWhite("Tom", "Jenn");
        Assertions.assertTrue(true);
    }

    @Test
    public void testDrawChessBoardBlack() {
        bd = new BoardDrawer();
        bd.drawChessBoardBlack("Tom", "Jenn");
        Assertions.assertTrue(true);
    }

}
