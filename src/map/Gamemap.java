package map;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Gamemap {
    private char[][] map;
    private char[][] originalMap;
    private char[][] buildingLayer;
    private static final int OBSTACLE_COUNT = 5;
    private String creatorName;
    private int communityCenterFunds;

    // Существующий конструктор
    public Gamemap(int rows, int cols) {
        map = new char[rows][cols];
        originalMap = new char[rows][cols];
        buildingLayer = new char[rows][cols];
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                buildingLayer[i][j] = ' ';
        initializeMap();
        placeObstacles();
        communityCenterFunds = 0;
    }

    // Новый конструктор: загрузка карты из CSV-файла
    public Gamemap(String filePath) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        }
        if (lines.isEmpty()) throw new IOException("Пустой файл карты: " + filePath);
        
        // Читаем метаданные (первая строка)
        String[] metadata = lines.get(0).split(";");
        int cols = Integer.parseInt(metadata[0]);
        int rows = Integer.parseInt(metadata[1]);
        creatorName = metadata.length > 2 ? metadata[2] : "Unknown";
        communityCenterFunds = metadata.length > 3 ? Integer.parseInt(metadata[3]) : 0;
        
        map = new char[rows][cols];
        originalMap = new char[rows][cols];
        buildingLayer = new char[rows][cols];
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                buildingLayer[i][j] = ' ';
                
        for (int y = 0; y < rows; y++) {
            String[] tokens = lines.get(y + 1).split(";");
            for (int x = 0; x < cols; x++) {
                map[y][x] = tokens[x].charAt(0);
                originalMap[y][x] = map[y][x];
            }
        }
    }

    // Метод для сохранения текущей карты в CSV
    public void saveToFile(String filePath) throws IOException {
        int rows = map.length;
        int cols = map[0].length;
        try (PrintWriter pw = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8))) {
            // Сохраняем метаданные
            pw.println(cols + ";" + rows + ";" + creatorName + ";" + communityCenterFunds);
            
            // Сохраняем карту
            for (int y = 0; y < rows; y++) {
                for (int x = 0; x < cols; x++) {
                    pw.print(map[y][x]);
                    if (x < cols - 1) pw.print(";");
                }
                pw.println();
            }
        }
    }

    private void initializeMap() {
        int rows = map.length;
        int cols = map[0].length;
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++) {
                char terrain;
                if (i < rows / 3) terrain = '&';
                else if (i >= 2 * rows / 3) terrain = '?';
                else terrain = '.';
                map[i][j] = terrain;
                originalMap[i][j] = terrain;
            }
        int roadWidth = (cols >= 6) ? 2 : 1;
        int mid = cols / 2;
        int startRoad = mid - roadWidth / 2;
        for (int i = 0; i < rows; i++)
            for (int j = startRoad; j < startRoad + roadWidth; j++)
                map[i][j] = originalMap[i][j] = '+';
        map[0][mid] = originalMap[0][mid] = 'И';
        map[rows - 1][mid] = originalMap[rows - 1][mid] = 'К';
    }

    private void placeObstacles() {
        Random rand = new Random();
        int rows = map.length;
        int cols = map[0].length;
        int placed = 0;
        while (placed < OBSTACLE_COUNT) {
            int x = rand.nextInt(rows);
            int y = rand.nextInt(cols);
            if (originalMap[x][y] == '.' && map[x][y] == '.') {
                map[x][y] = originalMap[x][y] = '#';
                placed++;
            }
        }
    }
    public void addObstacle(int x, int y) {
        if (originalMap[x][y] == '.' && map[x][y] == '.') {
            map[x][y] = '#';
            originalMap[x][y] = '#';

        }

    }


    public void placeGoldPiles(int count) {
        Random rand = new Random();
        int rows = map.length;
        int cols = map[0].length;
        int placed = 0;
        while (placed < count) {
            int x = rand.nextInt(rows);
            int y = rand.nextInt(cols);
            if (originalMap[x][y] == '.' && map[x][y] == '.') {
                map[x][y] = '$';
                placed++;
            }
        }
    }

    public boolean placeNeutralCastle(int x, int y) {
        if (x < 0 || x >= map.length || y < 0 || y >= map[0].length) {
            return false;
        }
        map[x][y] = 'N';
        originalMap[x][y] = 'N';
        return true;
    }

    public boolean placeBuilding(int x, int y, char buildingIcon) {
        int rows = map.length;
        int cols = map[0].length;
        if (x < 0 || x >= rows || y < 0 || y >= cols) return false;
        char cell = map[x][y];
        if ((cell == '.' || cell == '&' || cell == '?') && buildingLayer[x][y] == ' ') {
            buildingLayer[x][y] = buildingIcon;
            map[x][y] = buildingIcon;
            return true;
        }
        return false;
    }

    public char getCell(int x, int y) {
        return map[x][y];
    }

    public void removeGoldAt(int x, int y) {
        if (buildingLayer[x][y] != ' ') {
            map[x][y] = buildingLayer[x][y];
        } else {
            map[x][y] = originalMap[x][y];
        }
    }

    public boolean canMoveToForHero(int x, int y, char heroIcon) {
        int rows = map.length;
        int cols = map[0].length;
        if (x < 0 || x >= rows || y < 0 || y >= cols) return false;
        char cell = map[x][y];
        return cell != '#';
    }

    public int getMovementCost(int x, int y, char moverIcon) {
        char cell = getCell(x, y);
        switch (cell) {
            case '+': return 2;
            case 'И':
            case 'К': return 1;
            case '&':
                if (moverIcon == 'H') return 2;
                else if (moverIcon == 'E') return 4;
                else return 3;
            case '?':
                if (moverIcon == 'H') return 4;
                else if (moverIcon == 'E') return 2;
                else return 3;
            case '.': return 3;
            case '$': return 3;
            case 'N': return 3;
            case 'C': return 3;
            default: return Integer.MAX_VALUE;
        }
    }

    public int getMovementCost(int x, int y) {
        return getMovementCost(x, y, 'H');
    }

    public void updatePosition(int oldX, int oldY, int newX, int newY, char entityChar) {
        if (buildingLayer[oldX][oldY] != ' ') {
            map[oldX][oldY] = buildingLayer[oldX][oldY];
        } else {
            map[oldX][oldY] = originalMap[oldX][oldY];
        }
        map[newX][newY] = entityChar;
    }

    public int getRows() {
        return map.length;
    }

    public int getCols() {
        return map[0].length;
    }

    public void setCell(int x, int y, char cellType) {
        if (x >= 0 && x < map.length && y >= 0 && y < map[0].length) {
            map[x][y] = cellType;
            originalMap[x][y] = cellType;
        }
    }

    public void removeEntityAt(int x, int y) {
        if (buildingLayer[x][y] != ' ') {
            map[x][y] = buildingLayer[x][y];
        } else {
            map[x][y] = originalMap[x][y];
        }
    }

    public void setCreatorName(String name) {
        this.creatorName = name;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public int getCommunityCenterFunds() {
        return communityCenterFunds;
    }

    public void addToCommunityCenter(int amount) {
        if (amount > 0) {
            communityCenterFunds += amount;
        }
    }

    public boolean withdrawFromCommunityCenter(int amount, String playerName) {
        if (amount > 0 && playerName.equals(creatorName) && amount <= communityCenterFunds) {
            communityCenterFunds -= amount;
            return true;
        }
        return false;
    }

    public boolean hasCommunityCenter() {
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] == 'C') {
                    return true;
                }
            }
        }
        return false;
    }
}
