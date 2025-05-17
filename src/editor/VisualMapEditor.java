package editor;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import map.GameMapUI;
import map.Gamemap;

public class VisualMapEditor {
    private String playerName;
    private File playerMapsDir;
    private Gamemap currentMap;
    private int cursorX = 1;
    private int cursorY = 1;
    private GameMapUI mapUI;
    
    // Символы для редактирования карты
    private static final char[] TERRAIN_TYPES = {'.', '&', '?', '+', '#', 'N'};
    private static final String[] TERRAIN_NAMES = {
        "Обычная местность", "Дружественная зона", "Вражеская зона", 
        "Дорога", "Препятствие", "Нейтральный замок"
    };

    public VisualMapEditor(String playerName) {
        this.playerName = playerName;
        this.playerMapsDir = MapEditor.getPlayerMapsDir(playerName);
        this.mapUI = new GameMapUI();
    }

    public void run() {
        Scanner sc = new Scanner(System.in);
        
        while (true) {
            System.out.println("\n=== Редактор карт ===");
            System.out.println("1. Создать новую карту");
            System.out.println("2. Редактировать существующую карту");
            System.out.println("3. Удалить карту");
            System.out.println("0. Выход в главное меню");
            System.out.print("Выбор: ");
            
            String choice = sc.nextLine().trim();
            try {
                switch (choice) {
                    case "1": createNewMap(sc); break;
                    case "2": editExistingMap(sc); break;
                    case "3": deleteMap(sc); break;
                    case "0": return;
                    default: System.out.println("Неверный выбор. Попробуйте ещё раз.");
                }
            } catch (IOException e) {
                System.err.println("Ошибка ввода/вывода: " + e.getMessage());
            }
        }
    }

    private void createNewMap(Scanner sc) throws IOException {
        System.out.print("Введите ширину карты (рекомендуется от 10 до 20): ");
        int width = Integer.parseInt(sc.nextLine());
        System.out.print("Введите высоту карты (рекомендуется от 10 до 20): ");
        int height = Integer.parseInt(sc.nextLine());
        
        // Создаем карту с базовыми настройками
        currentMap = new Gamemap(height, width);
        
        // Переходим к редактированию
        editMapContent(sc);
    }

    private void editExistingMap(Scanner sc) throws IOException {
        java.util.List<String> maps = MapEditor.listMapNames(playerName);
        
        if (maps.isEmpty()) {
            System.out.println("У вас нет доступных карт. Сначала создайте карту.");
            return;
        }
        
        System.out.println("Доступные карты:");
        for (int i = 0; i < maps.size(); i++) {
            System.out.printf("%d: %s%n", i + 1, maps.get(i));
        }
        
        System.out.print("Выберите карту для редактирования: ");
        int idx = Integer.parseInt(sc.nextLine()) - 1;
        
        if (idx < 0 || idx >= maps.size()) {
            System.out.println("Неверный выбор карты.");
            return;
        }
        
        // Загружаем выбранную карту
        String mapFile = new File(playerMapsDir, maps.get(idx)).getPath();
        currentMap = new Gamemap(mapFile);
        
        // Переходим к редактированию
        editMapContent(sc);
    }
    
    private void editMapContent(Scanner sc) throws IOException {
        cursorX = 1; // Начальная позиция курсора
        cursorY = 1;
        char currentTerrain = '.'; // Тип местности по умолчанию
        
        while (true) {
            // Очищаем консоль для лучшего отображения
            clearConsole();
            
            // Отображаем карту с курсором
            displayMapWithCursor();
            
            // Инструкции
            System.out.println("\nРедактирование карты:");
            System.out.println("Управление: w,a,s,d - перемещение курсора, q - выход");
            System.out.println("t - выбрать тип местности, space - установить выбранный тип");
            System.out.println("1 - установить замок игрока, 2 - установить замок врага");
            System.out.println("n - установить нейтральный замок, c - очистить клетку");
            System.out.println("Текущий тип местности: " + getCurrentTerrainDescription(currentTerrain));
            System.out.println("save - сохранить карту");
            
            String cmd = sc.nextLine().trim().toLowerCase();
            
            if (cmd.equals("q")) {
                System.out.print("Сохранить изменения перед выходом? (да/нет): ");
                if (sc.nextLine().trim().toLowerCase().equals("да")) {
                    saveMap(sc);
                }
                return;
            } else if (cmd.equals("save")) {
                saveMap(sc);
                continue;
            }
            
            // Обработка команд редактирования
            switch (cmd) {
                case "w": moveCursor(-1, 0); break;  // Вверх
                case "s": moveCursor(1, 0); break;   // Вниз
                case "a": moveCursor(0, -1); break;  // Влево
                case "d": moveCursor(0, 1); break;   // Вправо
                
                case "t": // Меню выбора типа местности
                    System.out.println("Выберите тип местности:");
                    for (int i = 0; i < TERRAIN_TYPES.length; i++) {
                        System.out.printf("%d: %s (%c)%n", i + 1, TERRAIN_NAMES[i], TERRAIN_TYPES[i]);
                    }
                    int terrainChoice = Integer.parseInt(sc.nextLine()) - 1;
                    if (terrainChoice >= 0 && terrainChoice < TERRAIN_TYPES.length) {
                        currentTerrain = TERRAIN_TYPES[terrainChoice];
                    }
                    break;
                
                case " ": // Установка выбранного типа местности
                    setTerrain(currentTerrain);
                    break;
                    
                case "1": // Установка замка игрока
                    setTerrain('И');
                    break;
                    
                case "2": // Установка замка врага
                    setTerrain('К');
                    break;
                    
                case "n": // Установка нейтрального замка
                    setTerrain('N');
                    break;
                    
                case "c": // Очистка клетки
                    setTerrain('.');
                    break;
            }
        }
    }

    private void saveMap(Scanner sc) throws IOException {
        System.out.print("Введите имя файла для сохранения (без расширения): ");
        String filename = sc.nextLine().trim() + ".csv";
        
        File mapFile = new File(playerMapsDir, filename);
        currentMap.saveToFile(mapFile.getPath());
        
        System.out.println("Карта успешно сохранена: " + mapFile.getPath());
    }

    private void deleteMap(Scanner sc) {
        java.util.List<String> maps = MapEditor.listMapNames(playerName);
        
        if (maps.isEmpty()) {
            System.out.println("У вас нет доступных карт для удаления.");
            return;
        }
        
        System.out.println("Доступные карты:");
        for (int i = 0; i < maps.size(); i++) {
            System.out.printf("%d: %s%n", i + 1, maps.get(i));
        }
        
        System.out.print("Выберите карту для удаления: ");
        int idx = Integer.parseInt(sc.nextLine()) - 1;
        
        if (idx < 0 || idx >= maps.size()) {
            System.out.println("Неверный выбор карты.");
            return;
        }
        
        File mapToDelete = new File(playerMapsDir, maps.get(idx));
        if (mapToDelete.delete()) {
            System.out.println("Карта успешно удалена.");
        } else {
            System.out.println("Не удалось удалить карту.");
        }
    }

    private void displayMapWithCursor() {
        // Получаем копию карты для отображения с курсором
        char[][] mapData = getMapWithCursor();
        mapUI.printCustomMap(mapData);
    }

    private char[][] getMapWithCursor() {
        int rows = currentMap.getRows();
        int cols = currentMap.getCols();
        char[][] mapWithCursor = new char[rows][cols];
        
        // Копируем данные карты
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                mapWithCursor[i][j] = currentMap.getCell(i, j);
            }
        }
        
        // Устанавливаем курсор
        // Используем символ '@' для курсора (можно заменить на другой символ)
        mapWithCursor[cursorX][cursorY] = '@';
        
        return mapWithCursor;
    }

    private void moveCursor(int dx, int dy) {
        int newX = cursorX + dx;
        int newY = cursorY + dy;
        
        // Проверяем, чтобы курсор не вышел за границы карты
        if (newX >= 0 && newX < currentMap.getRows() && 
            newY >= 0 && newY < currentMap.getCols()) {
            cursorX = newX;
            cursorY = newY;
        }
    }

    private void setTerrain(char terrainType) {
        // Обновляем клетку карты
        if (currentMap != null) {
            currentMap.setCell(cursorX, cursorY, terrainType);
        }
    }

    private String getCurrentTerrainDescription(char terrain) {
        for (int i = 0; i < TERRAIN_TYPES.length; i++) {
            if (TERRAIN_TYPES[i] == terrain) {
                return TERRAIN_NAMES[i] + " (" + terrain + ")";
            }
        }
        return "Неизвестный тип (" + terrain + ")";
    }

    private void clearConsole() {
        // В консолях Java очистка может быть проблематична
        // Используем этот простой метод для виртуальной "очистки"
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }
} 