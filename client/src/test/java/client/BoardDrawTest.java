package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import org.junit.jupiter.api.*;

import java.util.HashMap;

import static ui.EscapeSequences.*;

public class BoardDrawTest {
    ChessBoard board;
    HashMap<ChessPiece.PieceType, String> whitePieces;
    HashMap<ChessPiece.PieceType, String> blackPieces;

    @Test
    public void testDrawChessBoardWhite() {
        drawChessBoardWhite("Tom", "Jenn");
        Assertions.assertTrue(true);
    }

    @Test
    public void testDrawChessBoardBlack() {
        drawChessBoardBlack("Tom", "Jenn");
        Assertions.assertTrue(true);
    }

    public void drawChessBoardBlack(String whiteUsername, String blackUsername) {
        initializePiecesMaps();
        board = new ChessBoard();
        board.resetBoard();
        ChessPiece currPiece;
        ChessPosition currPosition;
        String pieceChar;
        String squareColor = SET_BG_COLOR_BLACK;

        System.out.println(SET_BG_COLOR_BLACK+ EMPTY + EMPTY + whiteUsername);
        for(int r = 8; r >= 1; r--) {
            System.out.print(SET_BG_COLOR_BLACK+ EMPTY + EMPTY);
            for(int c = 8; c >= 1; c--) {
                currPosition = new ChessPosition(r, c);
                currPiece = board.getPiece(currPosition);

                squareColor = (squareColor.equals(SET_BG_COLOR_WHITE)) ? SET_BG_COLOR_BLACK : SET_BG_COLOR_WHITE;
                if(currPiece == null) {
                    System.out.print(squareColor + EMPTY);
                    continue;
                }

                if(currPiece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                    pieceChar = whitePieces.get(currPiece.getPieceType());
                } else {
                    pieceChar = blackPieces.get(currPiece.getPieceType());
                }
                System.out.print(squareColor + pieceChar);
            }
            squareColor = (squareColor.equals(SET_BG_COLOR_WHITE)) ? SET_BG_COLOR_BLACK : SET_BG_COLOR_WHITE;
            System.out.print(SET_BG_COLOR_BLACK+ EMPTY + EMPTY);
            System.out.print("\n");
        }
        System.out.println(SET_BG_COLOR_BLACK+ EMPTY + EMPTY + blackUsername);
    }

    public void drawChessBoardWhite(String whiteUsername, String blackUsername) {
        initializePiecesMaps();
        board = new ChessBoard();
        board.resetBoard();
        ChessPiece currPiece;
        ChessPosition currPosition;
        String pieceChar;
        String squareColor = SET_BG_COLOR_BLACK;
        if (whiteUsername == null) {
            whiteUsername = "[vacant]";
        }
        if (blackUsername == null) {
            blackUsername = "[vacant]";
        }
        System.out.println(SET_BG_COLOR_BLACK+ EMPTY + EMPTY + blackUsername);
        for(int r = 1; r <= 8; r++) {
            System.out.print(SET_BG_COLOR_BLACK+ EMPTY + EMPTY);
            for(int c = 1; c <= 8; c++) {
                currPosition = new ChessPosition(r, c);
                currPiece = board.getPiece(currPosition);

                squareColor = (squareColor.equals(SET_BG_COLOR_WHITE)) ? SET_BG_COLOR_BLACK : SET_BG_COLOR_WHITE;
                if(currPiece == null) {
                    System.out.print(squareColor + EMPTY);
                    continue;
                }

                if(currPiece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                    pieceChar = whitePieces.get(currPiece.getPieceType());
                } else {
                    pieceChar = blackPieces.get(currPiece.getPieceType());
                }
                System.out.print(squareColor + pieceChar);
            }
            squareColor = (squareColor.equals(SET_BG_COLOR_WHITE)) ? SET_BG_COLOR_BLACK : SET_BG_COLOR_WHITE;
            System.out.print(SET_BG_COLOR_BLACK+ EMPTY + EMPTY);
            System.out.print("\n");
        }
        System.out.println(SET_BG_COLOR_BLACK+ EMPTY + EMPTY + whiteUsername);
    }

    private void initializePiecesMaps() {
        whitePieces = new HashMap<ChessPiece.PieceType, String>();
        whitePieces.put(ChessPiece.PieceType.KING, WHITE_KING);
        whitePieces.put(ChessPiece.PieceType.QUEEN, WHITE_QUEEN);
        whitePieces.put(ChessPiece.PieceType.BISHOP, WHITE_BISHOP);
        whitePieces.put(ChessPiece.PieceType.KNIGHT, WHITE_KNIGHT);
        whitePieces.put(ChessPiece.PieceType.ROOK, WHITE_ROOK);
        whitePieces.put(ChessPiece.PieceType.PAWN, WHITE_PAWN);


        blackPieces = new HashMap<ChessPiece.PieceType, String>();
        blackPieces.put(ChessPiece.PieceType.QUEEN, BLACK_QUEEN);
        blackPieces.put(ChessPiece.PieceType.KING, BLACK_KING);
        blackPieces.put(ChessPiece.PieceType.BISHOP, BLACK_BISHOP);
        blackPieces.put(ChessPiece.PieceType.KNIGHT, BLACK_KNIGHT);
        blackPieces.put(ChessPiece.PieceType.ROOK, BLACK_ROOK);
        blackPieces.put(ChessPiece.PieceType.PAWN, BLACK_PAWN);

    }
}
