package main;
import editor.MapEditor;
import editor.VisualMapEditor;
import game.Game;
import game.GameUI;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import records.RecordManager;
import records.RecordManager.Record;
import save.SaveManager;

public class Main {
    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        GameUI ui = new GameUI();
        ui.printWelcomeMessage();
        
        mainMenu:
        while (true) {
            System.out.println("1: Играть");
            System.out.println("2: Редактор карт");
            System.out.println("3: Просмотр рекордов");
            System.out.println("0: Выход из игры");
            System.out.print("Выбор: ");
            String choice = sc.nextLine().trim();
            
            switch (choice) {
                case "0":
                    System.out.println("До свидания!");
                    return;
                
                case "1": // Играть
                    System.out.print("Введите ваше имя: ");
                    String playerName = sc.nextLine().trim();
                    
                    // Создаем директорию для сохранений игрока
                    File userSaveDir = new File("save", playerName);
                    if (!userSaveDir.exists()) userSaveDir.mkdirs();
                    
                    SaveManager.init(playerName);
                    RecordManager.init();
                    
                    // Меню игры для конкретного игрока
                    System.out.println("1: Новая игра");
                    System.out.println("2: Загрузить сохранение");
                    System.out.println("0: Вернуться в главное меню");
                    System.out.print("Выбор: ");
                    choice = sc.nextLine().trim();
                    
                    switch (choice) {
                        case "0":
                            continue mainMenu;
                            
                        case "1": // Новая игра
                            // Выбор карты из карт игрока
                            List<String> maps = MapEditor.listMapNames(playerName);
                            if (maps.isEmpty()) {
                                ui.printSaveError("У вас нет доступных карт. Создайте карту в редакторе.");
                                continue mainMenu;
                            }
                            System.out.println("Доступные карты:");
                            for (int i = 0; i < maps.size(); i++) {
                                System.out.printf("%d: %s%n", i + 1, maps.get(i));
                            }
                            System.out.print("Выбор карты: ");
                            int idx = Integer.parseInt(sc.nextLine()) - 1;
                            if (idx < 0 || idx >= maps.size()) {
                                ui.printSaveError("Неверный выбор карты.");
                                continue mainMenu;
                            }
                            String mapFile = new File(new File("maps", playerName), maps.get(idx)).getPath();
                            
                            Game game = new Game();
                            game.currentPlayer = playerName;
                            game.currentMapName = maps.get(idx);
                            game.start(mapFile);
                            break;
                            
                        case "2": // Загрузить сохранение
                            List<File> saves = SaveManager.listSaves();
                            if (saves.isEmpty()) {
                                ui.printSaveError("Нет доступных сохранений.");
                                continue mainMenu;
                            }
                            System.out.println("Доступные сохранения:");
                            for (int i = 0; i < saves.size(); i++) {
                                System.out.printf("%d: %s%n", i + 1, saves.get(i).getName());
                            }
                            System.out.print("Выбор сохранения: ");
                            idx = Integer.parseInt(sc.nextLine()) - 1;
                            if (idx < 0 || idx >= saves.size()) {
                                ui.printSaveError("Неверный выбор сохранения.");
                                continue mainMenu;
                            }
                            
                            // Загружаем сохранение
                            File selectedSave = saves.get(idx);
                            String saveContent = SaveManager.loadSave(selectedSave);
                            String[] lines = saveContent.split("\n");
                            
                            // Парсинг метаданных из первой строки
                            String[] metadata = lines[0].split(";");
                            String savedMap = "";
                            for (int i = 0; i < metadata.length; i++) {
                                if (metadata[i].equals("Map") && i + 1 < metadata.length) {
                                    savedMap = metadata[i + 1];
                                    break;
                                }
                            }
                            
                            // Проверка существования карты
                            File mapFileForSave = new File(new File("maps", playerName), savedMap);
                            if (!mapFileForSave.exists()) {
                                ui.printSaveError("Карта из сохранения не найдена: " + savedMap);
                                continue mainMenu;
                            }
                            
                            // Запуск игры с сохраненного состояния
                            Game savedGame = new Game();
                            savedGame.currentPlayer = playerName;
                            savedGame.currentMapName = savedMap;
                            savedGame.loadFromSave(saveContent, mapFileForSave.getPath());
                            break;
                            
                        default:
                            ui.printSaveError("Неверный выбор.");
                            continue mainMenu;
                    }
                    break;
                
                case "2": // Редактор карт
                    System.out.print("Введите ваше имя: ");
                    playerName = sc.nextLine().trim();
                    new VisualMapEditor(playerName).run();
                    break;
                
                case "3": // Просмотр рекордов
                    showRecordsMenu(sc);
                    break;
                    
                default:
                    System.out.println("Неверный выбор. Попробуйте снова.");
                    break;
            }
        }
    }
    
    // Отображает меню просмотра рекордов
    private static void showRecordsMenu(Scanner sc) throws IOException {
        while (true) {
            System.out.println("\n=== МЕНЮ РЕКОРДОВ ===");
            System.out.println("1: Просмотр общего рейтинга игроков");
            System.out.println("2: Просмотр личных рекордов");
            System.out.println("0: Вернуться в главное меню");
            System.out.print("Выбор: ");
            
            String choice = sc.nextLine().trim();
            
            switch (choice) {
                case "0":
                    return;
                    
                case "1": // Общий рейтинг
                    showGlobalLeaderboard();
                    break;
                    
                case "2": // Личные рекорды
                    System.out.print("Введите имя игрока: ");
                    String playerName = sc.nextLine().trim();
                    showPlayerRecords(playerName);
                    break;
                    
                default:
                    System.out.println("Неверный выбор. Попробуйте снова.");
                    break;
            }
        }
    }
    
    //Отображает глобальный рейтинг всех
    private static void showGlobalLeaderboard() throws IOException {
        List<Record> topRecords = RecordManager.getTopRecords(10);
        
        System.out.println("\n=== ТОП-10 ИГРОКОВ ===");
        System.out.printf( "МЕСТО", "ИГРОК", "КАРТА", "ОЧКИ");
        if (topRecords.isEmpty()) {
            System.out.println("Пока нет рекордов.");
        } else {
            for (int i = 0; i < topRecords.size(); i++) {
                Record r = topRecords.get(i);
                System.out.printf("",(i + 1), r.player, r.mapName, r.score);
            }
        }

        System.out.println("Нажмите Enter для продолжения...");
        new Scanner(System.in).nextLine();
    }
    
    //Отображает рекорды конкретного игрока
    private static void showPlayerRecords(String playerName) throws IOException {
        List<Record> records = RecordManager.loadPlayerRecords(playerName);
        
        System.out.println("\n=== РЕКОРДЫ ИГРОКА " + playerName.toUpperCase() + " ===");
        System.out.printf( "МЕСТО", "КАРТА", "ОЧКИ");
        
        if (records.isEmpty()) {
            System.out.println("У игрока пока нет рекордов.");
        } else {
            for (int i = 0; i < records.size(); i++) {
                Record r = records.get(i);
                System.out.printf("",i + 1, r.mapName, r.score);
            }
        }

        System.out.println("Нажмите Enter для продолжения...");
        new Scanner(System.in).nextLine();
    }
}
