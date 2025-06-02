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
            case '@': return "⭐";
            case 'C': return "🏛️";
            default: return String.valueOf(c);
        }
    }

    public void printMap(Gamemap map) {
        int rows = map.getRows();
        int cols = map.getCols();
        System.out.println();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                char cell = map.getCell(i, j);
                System.out.print(symbolToEmoji(cell) + " ");
            }
            System.out.print("\n");
        }
    }
    
    // Метод для отображения произвольной карты (с курсором)
    public void printCustomMap(char[][] mapData) {
        if (mapData == null || mapData.length == 0) {
            return;
        }
        
        int rows = mapData.length;
        int cols = mapData[0].length;
        System.out.println();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                char cell = mapData[i][j];
                System.out.print(symbolToEmoji(cell) + " ");
            }
            System.out.print("\n");
        }
    }
}
