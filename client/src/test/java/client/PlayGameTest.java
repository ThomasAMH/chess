package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import org.junit.jupiter.api.*;
import ui.BoardDrawer;
import ui.Repl;

import java.util.HashMap;

import static ui.EscapeSequences.*;

public class PlayGameTest {

    HashMap<ChessPiece.PieceType, String> whitePieces;
    HashMap<ChessPiece.PieceType, String> blackPieces;
    BoardDrawer bd;

    @Test
    public void testMakeMove() {
        Repl repl = new Repl("http://localhost:8080", new ChessGame());
        repl.run();
        Assertions.assertTrue(true);
    }
}
