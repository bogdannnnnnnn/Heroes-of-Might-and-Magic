package map;

public class GameMapUI {
    private String symbolToEmoji(char c) {
        switch(c) {
            case '&': return "🟩";
            case '?': return "🟥";
            case '.': return "⬜";
            case '+': return "🛣️";
            case 'И': return "🏰";
            case 'К': return "🏰";
            case 'N': return "🏯";
            case '#': return "🧱";
            case '$': return "💰";
            case 'H': return "🧙";
            case 'E': return "👹";
            default: return String.valueOf(c);
        }
    }

    public void printMap(Gamemap map) {
        int rows = map.getRows();
        int cols = map.getCols();
        System.out.print("    ");
        for (int j = 0; j < cols; j++) {
            System.out.print(j + " ");
        }
        System.out.println();
        System.out.print("   +");
        for (int j = 0; j < cols; j++) {
            System.out.print("--");
        }
        System.out.println("+");
        for (int i = 0; i < rows; i++) {
            System.out.printf("%2d |", i);
            for (int j = 0; j < cols; j++) {
                char cell = map.getCell(i, j);
                System.out.print(symbolToEmoji(cell) + " ");
            }
            System.out.println("|");
        }
        System.out.print("   +");
        for (int j = 0; j < cols; j++) {
            System.out.print("--");
        }
        System.out.println("+");
    }
}
