package ui;

import chess.ChessGame;
import websocket.NotificationHandler;
import websocket.messages.ServerMessage;

import javax.management.Notification;
import java.util.Objects;
import java.util.Scanner;

import static java.awt.Color.*;
import static ui.EscapeSequences.*;

public class Repl implements NotificationHandler {
    private final Client client;

    public Repl(String serverUrl) {
        client = new Client(serverUrl, this);
    }

    public void run() {
        System.out.println(SET_TEXT_COLOR_BLUE + "\u2659 Java Chess \u265f");
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();
            try {
                result = client.eval(line);
                System.out.print(result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    public static void printPrompt() {
        System.out.print(SET_TEXT_COLOR_GREEN + RESET_TEXT_ITALIC + "\n>>> ");
    }

    public static void printNotification(String text) {
        System.out.print(SET_TEXT_ITALIC + SET_TEXT_COLOR_MAGENTA + text);
    }

    @Override
    public void notify(ServerMessage notification) {
        if (Objects.requireNonNull(notification.serverMessageType) == ServerMessage.ServerMessageType.LOAD_GAME) {
            client.redrawGameScreen();
        } else {
            printNotification(notification.message);
        }
        printPrompt();
    }
}
