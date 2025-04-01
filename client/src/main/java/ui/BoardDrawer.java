package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.HashMap;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.BLACK_PAWN;

public class BoardDrawer {
    ChessBoard board;
    HashMap<ChessPiece.PieceType, String> whitePieces;
    HashMap<ChessPiece.PieceType, String> blackPieces;
    static final String PIECE_COLOR = SET_TEXT_COLOR_BLUE;
    static final String AXIS_COLOR = SET_TEXT_COLOR_WHITE;

    public void drawGenericBoardWhite() {
        drawChessBoardWhite("Generic Board", "Generic Board");
    }

    public void drawGenericBoardBlack() {
        drawChessBoardBlack("Generic Board", "Generic Board");
    }

    public void drawChessBoardBlack(String whiteUsername, String blackUsername) {
        initializePiecesMaps();
        board = new ChessBoard();
        board.resetBoard();
        String squareColor = SET_BG_COLOR_WHITE;

        System.out.println(SET_BG_COLOR_BLACK+ EMPTY + EMPTY + whiteUsername);
        for(int r = 8; r >= 1; r--) {
            System.out.print(SET_BG_COLOR_BLACK+ EMPTY + EMPTY);
            System.out.print(String.valueOf(SET_BG_COLOR_BLACK + AXIS_COLOR + String.valueOf(9 - r) + " "));
            for(int c = 8; c >= 1; c--) {
                squareColor = (squareColor.equals(SET_BG_COLOR_WHITE)) ? SET_BG_COLOR_BLACK : SET_BG_COLOR_WHITE;
                printPiece(r, c, squareColor);
            }
            squareColor = (squareColor.equals(SET_BG_COLOR_WHITE)) ? SET_BG_COLOR_BLACK : SET_BG_COLOR_WHITE;
            System.out.print(SET_BG_COLOR_BLACK+ EMPTY + EMPTY);
            System.out.print("\n");
        }
        printColumnLabels("black");
        System.out.println(SET_BG_COLOR_BLACK+ EMPTY + EMPTY + blackUsername);
    }

    public void drawChessBoardWhite(String whiteUsername, String blackUsername) {
        initializePiecesMaps();
        board = new ChessBoard();
        board.resetBoard();

        String squareColor = SET_BG_COLOR_WHITE;
        if (whiteUsername == null) {
            whiteUsername = "[vacant]";
        }
        if (blackUsername == null) {
            blackUsername = "[vacant]";
        }
        System.out.println(SET_BG_COLOR_BLACK+ EMPTY + EMPTY + blackUsername);
        for(int r = 1; r <= 8; r++) {
            System.out.print(SET_BG_COLOR_BLACK+ EMPTY + EMPTY);
            System.out.print(String.valueOf(SET_BG_COLOR_BLACK + AXIS_COLOR + String.valueOf( 9 - r) + " "));

            for(int c = 1; c <= 8; c++) {
                squareColor = (squareColor.equals(SET_BG_COLOR_WHITE)) ? SET_BG_COLOR_BLACK : SET_BG_COLOR_WHITE;
                printPiece(r, c, squareColor);
            }
            squareColor = (squareColor.equals(SET_BG_COLOR_WHITE)) ? SET_BG_COLOR_BLACK : SET_BG_COLOR_WHITE;
            System.out.print(SET_BG_COLOR_BLACK + EMPTY + EMPTY);
            System.out.print("\n");
        }
        printColumnLabels("white");
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

    private void printPiece(int r, int c, String squareColor) {
        ChessPiece currPiece;
        ChessPosition currPosition;
        String pieceChar;
        currPosition = new ChessPosition(r, c);
        currPiece = board.getPiece(currPosition);

        squareColor = (squareColor.equals(SET_BG_COLOR_WHITE)) ? SET_BG_COLOR_BLACK : SET_BG_COLOR_WHITE;
        if(currPiece == null) {
            System.out.print(squareColor + EMPTY);
            return;
        }
        if(currPiece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            pieceChar = whitePieces.get(currPiece.getPieceType());
        } else {
            pieceChar = blackPieces.get(currPiece.getPieceType());
        }
        System.out.print(PIECE_COLOR + squareColor + pieceChar);
    }
    private void printColumnLabels(String perspectiveColor) {
        String row = " A " + " B " + " C " + " D " + " E " + " F " + " G " + " H ";
        String buffer = "  " + EMPTY + EMPTY;
        if(perspectiveColor.equals("white")) {
            System.out.println(AXIS_COLOR + buffer + SET_BG_COLOR_BLACK + row);
            return;
        }
        System.out.println(AXIS_COLOR + buffer + SET_BG_COLOR_BLACK +  new StringBuilder(row).reverse().toString());
    }
}