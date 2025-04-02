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
        bd = new BoardDrawer(new ChessBoard());
        bd.drawChessBoardWhite("Tom", "Jenn", "White");
        Assertions.assertTrue(true);
    }

    @Test
    public void testDrawChessBoardBlack() {
        bd = new BoardDrawer(new ChessBoard());
        bd.drawChessBoardBlack("Tom", "Jenn", "White");
        Assertions.assertTrue(true);
    }

}
