package ui;

import chess.*;
import dataaccess.DataAccessDAO;
import dataaccess.DataAccessException;
import dataaccess.DatabaseDAO;
import dbobjects.GameRecord;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.BLACK_PAWN;

public class BoardDrawer {
    ChessBoard board;
    HashMap<ChessPiece.PieceType, String> whitePieces;
    HashMap<ChessPiece.PieceType, String> blackPieces;
    static final String PIECE_COLOR = SET_TEXT_COLOR_BLUE;
    static final String AXIS_COLOR = SET_TEXT_COLOR_WHITE;
    final String whiteUsername;
    final String blackUsername;

    public BoardDrawer(ChessBoard board, String whiteUsername, String blackUsername) {
        this.board = board;
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
        initializePiecesMaps();
    }


    public void drawChessBoard(ChessGame.TeamColor perspective, ChessGame.TeamColor activePlayer, ChessBoard board) {
        initializePiecesMaps();

        if(perspective == ChessGame.TeamColor.WHITE) {
            System.out.println(SET_TEXT_COLOR_WHITE + SET_BG_COLOR_BLACK+ EMPTY + EMPTY + "Black Player: " + blackUsername);
        } else {
            System.out.println(SET_TEXT_COLOR_WHITE + SET_BG_COLOR_BLACK+ EMPTY + EMPTY + "White Player: " + whiteUsername);
        }

        printBoard(perspective);
        printColumnLabels(perspective);

        if(perspective == ChessGame.TeamColor.BLACK) {
            System.out.println(SET_BG_COLOR_BLACK+ EMPTY + EMPTY + "Black Player: " + blackUsername);
        } else {
            System.out.println(SET_BG_COLOR_BLACK+ EMPTY + EMPTY + "White Player: " + whiteUsername);
        }
        printGameInfo(activePlayer);
    }

    private void printBoard(ChessGame.TeamColor perspective) {
        int start, end;
        int step;
        String squareColor;
        if(perspective == ChessGame.TeamColor.WHITE) {
            start = 1;
            end = 9;
            step = 1;
            squareColor = SET_BG_COLOR_BLACK;
        } else {
            start = 8;
            end = 0;
            step = -1;
            squareColor = SET_BG_COLOR_WHITE;
        }
        int r, c;
        for(r = start; r != end; r+=step) {
            System.out.print(SET_BG_COLOR_BLACK+ EMPTY + EMPTY);
            System.out.print(String.valueOf(SET_BG_COLOR_BLACK + AXIS_COLOR + String.valueOf( 9 - r) + " "));

            for(c = start; c != end; c+=step) {
                squareColor = (squareColor.equals(SET_BG_COLOR_WHITE)) ? SET_BG_COLOR_BLACK : SET_BG_COLOR_WHITE;
                printPiece(9 - r, c, squareColor);
            }
            squareColor = (squareColor.equals(SET_BG_COLOR_WHITE)) ? SET_BG_COLOR_BLACK : SET_BG_COLOR_WHITE;
            System.out.print(SET_BG_COLOR_BLACK + EMPTY + EMPTY);
            System.out.print("\n");
        }

    }

    public boolean showPieceMoves(int row, int col, ChessGame.TeamColor perspective) {
        ChessPosition pos = new ChessPosition(row, col);
        ChessPiece movingPiece = board.getPiece(pos);
        Collection<ChessMove> moves = movingPiece.pieceMoves(board, pos);
        boolean printedMovesFlag = false;

        HashSet<ChessPosition> possibleSquares = new HashSet<ChessPosition>();

        for(ChessMove move: moves) {
            possibleSquares.add(move.getEndPosition());
        }

        int start, end;
        int step;
        String squareColor = SET_BG_COLOR_WHITE;
        boolean isWhite;
        if(perspective == ChessGame.TeamColor.WHITE) {
            start = 1;
            end = 9;
            step = 1;
            isWhite = false;
        } else {
            start = 8;
            end = 0;
            step = -1;
            isWhite = true;
        }
        int r, c;
        ChessPosition currentPos;
        for(r = start; r != end; r+=step) {
            System.out.print(SET_BG_COLOR_BLACK+ EMPTY + EMPTY);
            System.out.print(String.valueOf(SET_BG_COLOR_BLACK + AXIS_COLOR + String.valueOf( 9 - r) + " "));

            for(c = start; c != end; c+=step) {
                currentPos = new ChessPosition(9 - r, c);
                if(possibleSquares.contains(currentPos)) {
                    squareColor = (isWhite) ? SET_BG_COLOR_DARK_GREEN: SET_BG_COLOR_GREEN;
                    printedMovesFlag = true;
                } else {
                    squareColor = (isWhite) ? SET_BG_COLOR_BLACK : SET_BG_COLOR_WHITE;
                }
                isWhite = !isWhite;
                printPiece(9 - r, c, squareColor);
            }
            isWhite = !isWhite;
            System.out.print(SET_BG_COLOR_BLACK + EMPTY + EMPTY);
            System.out.print("\n");
        }
        printColumnLabels(perspective);
        return printedMovesFlag;
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
    private void printColumnLabels(ChessGame.TeamColor perspective) {
        String row = " A " + " B " + " C " + " D " + " E " + " F " + " G " + " H ";
        String buffer = "  " + EMPTY + EMPTY;
        if(perspective == ChessGame.TeamColor.WHITE) {
            System.out.println(AXIS_COLOR + buffer + SET_BG_COLOR_BLACK + row);
            return;
        }
        System.out.println(AXIS_COLOR + buffer + SET_BG_COLOR_BLACK +  new StringBuilder(row).reverse().toString());
    }
    private void printGameInfo(ChessGame.TeamColor activePlayer) {
        System.out.println("----------------------");
        System.out.println("Active player: " + ((activePlayer) == ChessGame.TeamColor.WHITE ? "White": "Black"));
        System.out.println("----------------------");
    }
}