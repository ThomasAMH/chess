import chess.*;

import ui.Repl;

public class Main {
    public static void main(String[] args) {
        Repl repl = new Repl("http://localhost:8080");
        repl.run();
    }
}