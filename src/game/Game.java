package game;

import buildings.*;
import castle.Castle;
import castle.NeutralCastle;
import heroes.Hero;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import map.GameMapUI;
import map.Gamemap;
import records.RecordManager;
import records.PlayerScore;
import save.SaveManager;

public class Game {
    public String currentPlayer;
    public String currentMapName;
    private int currentScore = 0;
    private Hero playerHero;
    private Castle playerCastle;
    private Hero enemyHero;
    private NeutralCastle neutralCastle;


    public void start(String mapPath) {
        GameUI ui = new GameUI();
        Gamemap map;
        try {
            map = new Gamemap(mapPath);
        } catch (IOException e) {
            ui.printSaveError("Не удалось загрузить карту: " + e.getMessage());
            return;
        }
        
        // Получаем имя карты из пути
        String[] pathParts = mapPath.split("[\\\\/]");
        currentMapName = pathParts[pathParts.length - 1].replace(".csv", "");
        
        map.placeGoldPiles(3);

        int cols = map.getCols();
        int mid = cols / 2;
        // Координаты замков
        int playerCastleX = 0;
        int playerCastleY = mid;
        int enemyCastleX = map.getRows() - 1;
        int enemyCastleY = mid;
        // Координаты нейтрального замка (расположим его в нейтральной зоне)
        int neutralCastleX = 5;
        int neutralCastleY = (mid + 2 < cols) ? mid + 2 : mid - 2;

        // Создаем героя игрока (начало в замке игрока)
        playerHero = new Hero("Папаня", playerCastleX, playerCastleY, map);
        playerHero.addUnit("Костян");
        playerHero.addUnit("Гуль");
        playerHero.addGold(1000);

        // Создаем замок игрока
        playerCastle = new Castle(playerCastleX, playerCastleY);

        // Создаем врага (начало в замке врага, с иконкой 'E')
        enemyHero = new Hero("Враг", enemyCastleX, enemyCastleY, map);
        enemyHero.addUnit("Арбалетчик");
        enemyHero.addUnit("Мечник");
        enemyHero.addUnit("Мечник");
        enemyHero.addUnit("Мечник");
        enemyHero.addUnit("Мечник");
        enemyHero.setIcon('E');

        // Создаем нейтральный замок и размещаем его на карте
        neutralCastle = new NeutralCastle(neutralCastleX, neutralCastleY);
        map.placeNeutralCastle(neutralCastleX, neutralCastleY);

        // Для отображения карты используем GameMapUI
        GameMapUI mapUI = new GameMapUI();

        Scanner scanner = new Scanner(System.in);

        // Основной цикл раундов
        while (true) {
            ui.printNewRound();

            // Если героя нет, проверяем возможность найма
            if (playerHero == null) {
                ui.printNoHero();
                if (playerCastle.hasBuilding("Таверна")) {
                    ui.printNewHeroPrompt();
                    String choice = scanner.nextLine().trim().toLowerCase();
                    if (choice.equals("m")) {
                        playerHero = new Hero("Папаня", playerCastle.getX(), playerCastle.getY(), map);
                        ui.printHeroHired(playerHero.getName());
                    } else {
                        ui.printHeroNotHired();
                    }
                } else {
                    ui.printNoTavernNoHero();
                }
            }
            if (playerHero != null) {
                playerHero.resetMovementPoints();
            }
            enemyHero.resetMovementPoints();

            // Ход игрока если герой существует
            if (playerHero != null) {
                while (playerHero.getMovementPoints() > 0) {
                    mapUI.printMap(map);
                    // Вывод информации о герое
                    System.out.println(playerHero);
                    ui.printCastleBuildingsHeader();
                    playerCastle.showBuildings();
                    ui.printCommandOptions();

                    String input = scanner.nextLine().trim().toLowerCase();
                    if (input.equals("q")) {
                        ui.printExitGame();
                        // Сохраняем рекорд игрока перед выходом
                        savePlayerRecord();
                        // Автосохранение
                        try {
                            SaveManager.autoSave(serializeState(playerHero, playerCastle, enemyHero, neutralCastle));
                        } catch (Exception ex) {
                            ui.printAutoSaveError(ex.getMessage());
                        }
                        System.exit(0);
                    }
                    if (input.equals("~")) {
                        try {
                            SaveManager.manualSave(serializeState(playerHero, playerCastle, enemyHero, neutralCastle));
                            ui.printSaveSuccess();
                        } catch (Exception ex) {
                            ui.printSaveError(ex.getMessage());
                        }
                        continue;
                    }
                    if (input.equals("e")) {
                        ui.printEndTurn();
                        break;
                    }
                    if (input.equals("b")) {
                        // Разрешаем работу меню замка, если игрок находится в своем замке
                        // или в захваченном нейтральном замке
                        if ((playerHero.getX() == playerCastle.getX() && playerHero.getY() == playerCastle.getY())
                                || (neutralCastle.isPlayerCastle() && playerHero.getX() == neutralCastle.getX()
                                && playerHero.getY() == neutralCastle.getY())) {
                            Castle currentCastle;
                            if (playerHero.getX() == playerCastle.getX() && playerHero.getY() == playerCastle.getY()) {
                                currentCastle = playerCastle;
                            } else {
                                currentCastle = neutralCastle;
                            }
                            processCastleMenu(playerHero, currentCastle, scanner, ui);
                            if (currentCastle.hasBuilding("Конюшня")) {
                                playerHero.setMoveRange(2);
                                ui.printStableActive();
                            }
                        } else {
                            ui.printNotInCastle();
                        }
                        continue;
                    }
                    if (input.equals("h")) {
                        // Разрешаем меню найма, если игрок находится в своем замке или в захваченном нейтральном замке
                        if ((playerHero.getX() == playerCastle.getX() && playerHero.getY() == playerCastle.getY())
                                || (neutralCastle.isPlayerCastle() && playerHero.getX() == neutralCastle.getX()
                                && playerHero.getY() == neutralCastle.getY())) {
                            Castle currentCastle = (playerHero.getX() == playerCastle.getX()
                                    && playerHero.getY() == playerCastle.getY()) ? playerCastle : neutralCastle;
                            processHireMenu(playerHero, currentCastle, scanner, ui);
                        } else {
                            ui.printHireNotInCastle();
                        }
                        continue;
                    }
                    if (input.equals("m")) {
                        ui.printCannotHireHeroAlive();
                        continue;
                    }
                    // Обработка направления
                    int[] dir = parseDirection(input);
                    if (dir == null) {
                        ui.printInvalidDirection();
                        continue;
                    }
                    int tentativeX = playerHero.getX() + dir[0] * playerHero.getMoveRange();
                    int tentativeY = playerHero.getY() + dir[1] * playerHero.getMoveRange();
                    if (tentativeX < 0 || tentativeX >= map.getRows() ||
                            tentativeY < 0 || tentativeY >= map.getCols()) {
                        ui.printOutOfBoundsMove();
                        continue;
                    }
                    // Если целевая клетка содержит врага, запускаем бой
                    if (map.getCell(tentativeX, tentativeY) == 'E') {
                        ui.printSteppingOnEnemy();
                        playerHero = simulateBattle(playerHero, enemyHero, playerCastle, map, scanner, ui);
                        if (playerHero == null) break;
                        continue;
                    }
                    // Если целевая клетка – нейтральный замок, инициируем осаду
                    if (tentativeX == neutralCastle.getX() && tentativeY == neutralCastle.getY()) {
                        if (!neutralCastle.isPlayerCastle()) {
                            ui.printNeutralCastleReached();
                            // Запускаем цикл осады: игрок выбирает атаку ('a') или отмену ('e')
                            while (!neutralCastle.isCaptured()) {
                                ui.printSiegeOptions();
                                String siegeChoice = scanner.nextLine().trim().toLowerCase();
                                if (siegeChoice.equals("a")) {
                                    neutralCastle.siegeTurn();
                                    if (neutralCastle.isCaptured()) {
                                        neutralCastle.capture(playerHero, map);
                                        ui.printNeutralCastleCaptured();
                                        // Записываем захват замка для системы очков
                                        playerHero.recordCastleCaptured();
                                        // Автосохранение
                                        try {
                                            SaveManager.autoSave(serializeState(playerHero, playerCastle, enemyHero, neutralCastle));
                                        } catch (Exception ex) {
                                            ui.printAutoSaveError(ex.getMessage());
                                        }
                                        break;
                                    }
                                } else if (siegeChoice.equals("e")) {
                                    ui.printSiegeEnded();
                                    break;
                                } else {
                                    ui.printInvalidSiegeCommand();
                                }
                            }
                            // После осады переходим к следующему действию
                            continue;
                        }
                        // Если замок уже захвачен, продолжаем обычное перемещение
                    }
                    // Пытаемся переместить героя
                    if (!playerHero.move(dir[0], dir[1])) {
                        ui.printMoveNotPossible();
                    } else {
                        if (playerHero.getX() == enemyCastleX && playerHero.getY() == enemyCastleY) {
                            ui.printVictoryMessage();
                            try {
                                // Вычисляем итоговые очки
                                int finalScore = playerHero.getPlayerScore().calculateTotalScore();
                                // Выводим статистику
                                System.out.println("\n=== ИТОГОВАЯ СТАТИСТИКА ===");
                                System.out.println(playerHero.getPlayerScore().getScoreDetails());
                                // Обновляем рекорд
                                RecordManager.update(currentPlayer, currentMapName, finalScore);
                                ui.printVictoryAndRecordSaved();
                            } catch (Exception ex) {
                                ui.printSaveError("Ошибка обновления рекордов: " + ex.getMessage());
                            }
                            System.exit(0);
                        }
                    }
                    // Проверяем столкновение героев
                    if (playerHero.getX() == enemyHero.getX() &&
                            playerHero.getY() == enemyHero.getY()) {
                        ui.printHeroesCollision(playerHero.getX(), playerHero.getY());
                        playerHero = simulateBattle(playerHero, enemyHero, playerCastle, map, scanner, ui);
                        if (playerHero == null) break;
                    }
                    // Автосохранение
                    try {
                        SaveManager.autoSave(serializeState(playerHero, playerCastle, enemyHero, neutralCastle));
                    } catch (Exception ex) {
                        ui.printAutoSaveError(ex.getMessage());
                    }
                }
            }

            // Ход противника
            ui.printEnemyTurn();
            while (enemyHero.getMovementPoints() > 0) {
                int targetX, targetY;
                if (playerHero != null) {
                    targetX = playerHero.getX();
                    targetY = playerHero.getY();
                } else {
                    targetX = playerCastle.getX();
                    targetY = playerCastle.getY();
                }
                int enemyDx = Integer.compare(targetX, enemyHero.getX());
                int enemyDy = Integer.compare(targetY, enemyHero.getY());
                int tentativeEx = enemyHero.getX() + enemyDx * enemyHero.getMoveRange();
                int tentativeEy = enemyHero.getY() + enemyDy * enemyHero.getMoveRange();
                if (tentativeEx < 0 || tentativeEx >= map.getRows() ||
                        tentativeEy < 0 || tentativeEy >= map.getCols()) {
                    break;
                }
                if (playerHero != null && map.getCell(tentativeEx, tentativeEy) == 'H') {
                    ui.printEnemySteppingOnPlayer();
                    playerHero = simulateBattle(playerHero, enemyHero, playerCastle, map, scanner, ui);
                    if (playerHero == null) break;
                    break;
                }
                if (!enemyHero.move(enemyDx, enemyDy)) {
                    break;
                }
                if (playerHero != null &&
                        playerHero.getX() == enemyHero.getX() &&
                        playerHero.getY() == enemyHero.getY()) {
                    ui.printHeroesCollision(playerHero.getX(), playerHero.getY());
                    playerHero = simulateBattle(playerHero, enemyHero, playerCastle, map, scanner, ui);
                    if (playerHero == null) break;
                }
                if (enemyHero.getX() == playerCastle.getX() &&
                        enemyHero.getY() == playerCastle.getY()) {
                    ui.printDefeatMessage();
                    System.exit(0);
                }
            }
            ui.printEnemyTurnEnd();
            // Автосохранение
            try {
                SaveManager.autoSave(serializeState(playerHero, playerCastle, enemyHero, neutralCastle));
            } catch (Exception ex) {
                ui.printAutoSaveError(ex.getMessage());
            }
        }
    }

    // Сохраняет рекорд игрока в файл
    private void savePlayerRecord() {
        if (currentPlayer != null && currentMapName != null && playerHero != null) {
            try {
                // Вычисляем итоговые очки на основе действий игрока
                int finalScore = playerHero.getPlayerScore().calculateTotalScore();
                
                // Выводим итоговую статистику
                System.out.println("\n=== ИТОГОВАЯ СТАТИСТИКА ===");
                System.out.println(playerHero.getPlayerScore().getScoreDetails());
                
                // Обновляем рекорд в файле
                RecordManager.update(currentPlayer, currentMapName, finalScore);
                System.out.println("Рекорд сохранен для игрока " + currentPlayer + 
                                " на карте " + currentMapName + ": " + finalScore + " очков");
            } catch (Exception e) {
                System.err.println("Не удалось сохранить рекорд: " + e.getMessage());
            }
        }
    }

    // Метод для загрузки игры из сохранения
    public void loadFromSave(String saveContent, String mapPath) {
        GameUI ui = new GameUI();
        
        try {
            // Загружаем карту из файла
            Gamemap map = new Gamemap(mapPath);
            map.placeGoldPiles(3); // Размещаем золото на карте
            
            // Парсинг данных из сохранения
            String[] lines = saveContent.split("\n");
            
            // Установка начальных значений
            int cols = map.getCols();
            int mid = cols / 2;
            
            // Координаты замков по умолчанию
            int playerCastleX = 0;
            int playerCastleY = mid;
            int enemyCastleX = map.getRows() - 1;
            int enemyCastleY = mid;
            
            // Координаты нейтрального замка по умолчанию
            int neutralCastleX = 5;
            int neutralCastleY = (mid + 2 < cols) ? mid + 2 : mid - 2;
            
            // Парсим метаданные (первая строка)
            String[] metadata = lines[0].split(";");
            for (int i = 0; i < metadata.length; i += 2) {
                if (i + 1 < metadata.length) {
                    if (metadata[i].equals("Score")) {
                        try {
                            currentScore = Integer.parseInt(metadata[i + 1]);
                        } catch (NumberFormatException e) {
                            currentScore = 0;
                        }
                    }
                }
            }
            
            // Создаем базовые объекты
            playerHero = null; // будем создавать из данных сохранения
            playerCastle = new Castle(playerCastleX, playerCastleY);
            
            // Данные врага
            int enemyX = enemyCastleX;
            int enemyY = enemyCastleY;
            int enemyGold = 0;
            String enemyName = "Враг";
            List<String> enemyUnits = new ArrayList<>();
            
            // Данные нейтрального замка
            boolean neutralCaptured = false;
            
            // Обходим остальные строки сохранения
            for (int i = 1; i < lines.length; i++) {
                // Данные для героя
                if (lines[i].startsWith("Hero;")) {
                    String[] heroData = lines[i].split(";");
                    if (heroData.length >= 5) {
                        String heroName = heroData[1];
                        int heroX = Integer.parseInt(heroData[2]);
                        int heroY = Integer.parseInt(heroData[3]);
                        int heroGold = Integer.parseInt(heroData[4]);
                        
                        // Создаем героя с сохраненными координатами
                        playerHero = new Hero(heroName, heroX, heroY, map);
                        playerHero.setGold(heroGold);
                        
                        // Если есть еще данные (опыт, уровень) - обрабатываем
                        if (heroData.length >= 7) {
                            int level = Integer.parseInt(heroData[5]);
                            int exp = Integer.parseInt(heroData[6]);
                            playerHero.setLevel(level);
                            playerHero.setExperience(exp);
                        }
                    }
                } 
                // Юниты героя
                else if (lines[i].startsWith("Units;")) {
                    String[] unitData = lines[i].split(";");
                    // Очищаем существующие юниты и добавляем из сохранения
                    if (playerHero != null) {
                        playerHero.clearUnits();
                        for (int j = 1; j < unitData.length; j++) {
                            if (!unitData[j].isEmpty()) {
                                playerHero.addUnit(unitData[j]);
                            }
                        }
                    }
                } 
                // Данные замка игрока
                else if (lines[i].startsWith("PlayerCastle;")) {
                    String[] castleData = lines[i].split(";");
                    if (castleData.length >= 3) {
                        playerCastleX = Integer.parseInt(castleData[1]);
                        playerCastleY = Integer.parseInt(castleData[2]);
                        playerCastle = new Castle(playerCastleX, playerCastleY);
                    }
                } 
                // Постройки в замке
                else if (lines[i].startsWith("Buildings;")) {
                    String[] buildingData = lines[i].split(";");
                    for (int j = 1; j < buildingData.length; j++) {
                        if (!buildingData[j].isEmpty()) {
                            playerCastle.addBuilding(buildingData[j]);
                        }
                    }
                }
                // Данные вражеского героя
                else if (lines[i].startsWith("Enemy;")) {
                    String[] enemyData = lines[i].split(";");
                    if (enemyData.length >= 5) {
                        enemyName = enemyData[1];
                        enemyX = Integer.parseInt(enemyData[2]);
                        enemyY = Integer.parseInt(enemyData[3]);
                        enemyGold = Integer.parseInt(enemyData[4]);
                    }
                }
                // Юниты врага
                else if (lines[i].startsWith("EnemyUnits;")) {
                    String[] unitData = lines[i].split(";");
                    for (int j = 1; j < unitData.length; j++) {
                        if (!unitData[j].isEmpty()) {
                            enemyUnits.add(unitData[j]);
                        }
                    }
                }
                // Данные нейтрального замка
                else if (lines[i].startsWith("NeutralCastle;")) {
                    String[] ncData = lines[i].split(";");
                    if (ncData.length >= 4) {
                        neutralCastleX = Integer.parseInt(ncData[1]);
                        neutralCastleY = Integer.parseInt(ncData[2]);
                        neutralCaptured = Boolean.parseBoolean(ncData[3]);
                    }
                }
            }
            
            // Если героя не нашли в сохранении, создаем дефолтного
            if (playerHero == null) {
                playerHero = new Hero("Папаня", playerCastleX, playerCastleY, map);
                playerHero.addUnit("Костян");
                playerHero.addUnit("Гуль");
                playerHero.addGold(1000);
            }
            
            // Создаем вражеского героя
            Hero enemyHero = new Hero(enemyName, enemyX, enemyY, map);
            enemyHero.setGold(enemyGold);
            enemyHero.setIcon('E');
            
            // Если вражеских юнитов нет в сохранении, добавляем стандартные
            if (enemyUnits.isEmpty()) {
                enemyHero.addUnit("Арбалетчик");
                enemyHero.addUnit("Мечник");
                enemyHero.addUnit("Мечник");
            } else {
                for (String unit : enemyUnits) {
                    enemyHero.addUnit(unit);
                }
            }
            
            // Создаем нейтральный замок
            NeutralCastle neutralCastle = new NeutralCastle(neutralCastleX, neutralCastleY);
            if (neutralCaptured) {
                neutralCastle.capture(playerHero, map);
            } else {
                map.placeNeutralCastle(neutralCastleX, neutralCastleY);
            }
            
            // Начинаем игру с восстановленным состоянием
            GameMapUI mapUI = new GameMapUI();
            Scanner scanner = new Scanner(System.in);
            
            // Основной цикл раундов
            while (true) {
                ui.printNewRound();
                
                // Сброс очков движения в начале раунда
                if (playerHero != null) {
                    playerHero.resetMovementPoints();
                }
                enemyHero.resetMovementPoints();
                
                // Ход игрока (если герой существует)
                if (playerHero != null) {
                    while (playerHero.getMovementPoints() > 0) {
                        mapUI.printMap(map);
                        // Вывод информации о герое
                        System.out.println(playerHero);
                        ui.printCastleBuildingsHeader();
                        playerCastle.showBuildings();
                        ui.printCommandOptions();
                        
                        String input = scanner.nextLine().trim().toLowerCase();
                        if (input.equals("q")) {
                            ui.printExitGame();
                            // Автосохранение
                            try {
                                SaveManager.autoSave(serializeState(playerHero, playerCastle, enemyHero, neutralCastle));
                            } catch (Exception ex) {
                                ui.printAutoSaveError(ex.getMessage());
                            }
                            System.exit(0);
                        }
                        if (input.equals("~")) {
                            try {
                                SaveManager.manualSave(serializeState(playerHero, playerCastle, enemyHero, neutralCastle));
                                ui.printSaveSuccess();
                            } catch (Exception ex) {
                                ui.printSaveError(ex.getMessage());
                            }
                            continue;
                        }
                        if (input.equals("e")) {
                            ui.printEndTurn();
                            break;
                        }
                        if (input.equals("b")) {
                            // Разрешаем работу меню замка, если игрок находится в своем замке
                            // или в захваченном нейтральном замке
                            if ((playerHero.getX() == playerCastle.getX() && playerHero.getY() == playerCastle.getY())
                                    || (neutralCastle.isPlayerCastle() && playerHero.getX() == neutralCastle.getX()
                                    && playerHero.getY() == neutralCastle.getY())) {
                                Castle currentCastle;
                                if (playerHero.getX() == playerCastle.getX() && playerHero.getY() == playerCastle.getY()) {
                                    currentCastle = playerCastle;
                                } else {
                                    currentCastle = neutralCastle;
                                }
                                processCastleMenu(playerHero, currentCastle, scanner, ui);
                                if (currentCastle.hasBuilding("Конюшня")) {
                                    playerHero.setMoveRange(2);
                                    ui.printStableActive();
                                }
                            } else {
                                ui.printNotInCastle();
                            }
                            continue;
                        }
                        if (input.equals("h")) {
                            // Разрешаем меню найма, если игрок находится в своем замке или в захваченном нейтральном замке
                            if ((playerHero.getX() == playerCastle.getX() && playerHero.getY() == playerCastle.getY())
                                    || (neutralCastle.isPlayerCastle() && playerHero.getX() == neutralCastle.getX()
                                    && playerHero.getY() == neutralCastle.getY())) {
                                Castle currentCastle = (playerHero.getX() == playerCastle.getX()
                                        && playerHero.getY() == playerCastle.getY()) ? playerCastle : neutralCastle;
                                processHireMenu(playerHero, currentCastle, scanner, ui);
                            } else {
                                ui.printHireNotInCastle();
                            }
                            continue;
                        }
                        if (input.equals("m")) {
                            ui.printCannotHireHeroAlive();
                            continue;
                        }
                        // Обработка направления (поддержка одиночных и комбинированных символов)
                        int[] dir = parseDirection(input);
                        if (dir == null) {
                            ui.printInvalidDirection();
                            continue;
                        }
                        int tentativeX = playerHero.getX() + dir[0] * playerHero.getMoveRange();
                        int tentativeY = playerHero.getY() + dir[1] * playerHero.getMoveRange();
                        if (tentativeX < 0 || tentativeX >= map.getRows() ||
                                tentativeY < 0 || tentativeY >= map.getCols()) {
                            ui.printOutOfBoundsMove();
                            continue;
                        }
                        // Если целевая клетка содержит врага, запускаем бой
                        if (map.getCell(tentativeX, tentativeY) == 'E') {
                            ui.printSteppingOnEnemy();
                            playerHero = simulateBattle(playerHero, enemyHero, playerCastle, map, scanner, ui);
                            if (playerHero == null) break;
                            continue;
                        }
                        
                        // Обработка остального хода (нейтральный замок, перемещение и т.д.)
                        // Если целевая клетка – нейтральный замок, инициируем осаду
                        if (tentativeX == neutralCastle.getX() && tentativeY == neutralCastle.getY()) {
                            if (!neutralCastle.isPlayerCastle()) {
                                ui.printNeutralCastleReached();
                                // Запускаем цикл осады: игрок выбирает атаку ('a') или отмену ('e')
                                while (!neutralCastle.isCaptured()) {
                                    ui.printSiegeOptions();
                                    String siegeChoice = scanner.nextLine().trim().toLowerCase();
                                    if (siegeChoice.equals("a")) {
                                        neutralCastle.siegeTurn();
                                        if (neutralCastle.isCaptured()) {
                                            neutralCastle.capture(playerHero, map);
                                            ui.printNeutralCastleCaptured();
                                            // Записываем захват замка для системы очков
                                            playerHero.recordCastleCaptured();
                                            // Автосохранение
                                            try {
                                                SaveManager.autoSave(serializeState(playerHero, playerCastle, enemyHero, neutralCastle));
                                            } catch (Exception ex) {
                                                ui.printAutoSaveError(ex.getMessage());
                                            }
                                            break;
                                        }
                                    } else if (siegeChoice.equals("e")) {
                                        ui.printSiegeEnded();
                                        break;
                                    } else {
                                        ui.printInvalidSiegeCommand();
                                    }
                                }
                                // После осады переходим к следующему действию
                                continue;
                            }
                            // Если замок уже захвачен, продолжаем обычное перемещение
                        }
                        
                        // Пытаемся переместить героя
                        if (!playerHero.move(dir[0], dir[1])) {
                            ui.printMoveNotPossible();
                        } else {
                            if (playerHero.getX() == enemyCastleX && playerHero.getY() == enemyCastleY) {
                                ui.printVictoryMessage();
                                try {
                                    // Вычисляем итоговые очки
                                    int finalScore = playerHero.getPlayerScore().calculateTotalScore();
                                    // Выводим статистику
                                    System.out.println("\n=== ИТОГОВАЯ СТАТИСТИКА ===");
                                    System.out.println(playerHero.getPlayerScore().getScoreDetails());
                                    // Обновляем рекорд
                                    RecordManager.update(currentPlayer, currentMapName, finalScore);
                                    ui.printVictoryAndRecordSaved();
                                } catch (Exception ex) {
                                    ui.printSaveError("Ошибка обновления рекордов: " + ex.getMessage());
                                }
                                System.exit(0);
                            }
                        }
                        
                        // Проверяем столкновение героев
                        if (playerHero.getX() == enemyHero.getX() &&
                                playerHero.getY() == enemyHero.getY()) {
                            ui.printHeroesCollision(playerHero.getX(), playerHero.getY());
                            playerHero = simulateBattle(playerHero, enemyHero, playerCastle, map, scanner, ui);
                            if (playerHero == null) break;
                        }
                    }
                }
                
                // Ход противника
                ui.printEnemyTurn();
                while (enemyHero.getMovementPoints() > 0) {
                    int targetX, targetY;
                    if (playerHero != null) {
                        targetX = playerHero.getX();
                        targetY = playerHero.getY();
                    } else {
                        targetX = playerCastle.getX();
                        targetY = playerCastle.getY();
                    }
                    int enemyDx = Integer.compare(targetX, enemyHero.getX());
                    int enemyDy = Integer.compare(targetY, enemyHero.getY());
                    int tentativeEx = enemyHero.getX() + enemyDx * enemyHero.getMoveRange();
                    int tentativeEy = enemyHero.getY() + enemyDy * enemyHero.getMoveRange();
                    if (tentativeEx < 0 || tentativeEx >= map.getRows() ||
                            tentativeEy < 0 || tentativeEy >= map.getCols()) {
                        break;
                    }
                    if (playerHero != null && map.getCell(tentativeEx, tentativeEy) == 'H') {
                        ui.printEnemySteppingOnPlayer();
                        playerHero = simulateBattle(playerHero, enemyHero, playerCastle, map, scanner, ui);
                        if (playerHero == null) break;
                        break;
                    }
                    if (!enemyHero.move(enemyDx, enemyDy)) {
                        break;
                    }
                    if (playerHero != null &&
                            playerHero.getX() == enemyHero.getX() &&
                            playerHero.getY() == enemyHero.getY()) {
                        ui.printHeroesCollision(playerHero.getX(), playerHero.getY());
                        playerHero = simulateBattle(playerHero, enemyHero, playerCastle, map, scanner, ui);
                        if (playerHero == null) break;
                    }
                    if (enemyHero.getX() == playerCastle.getX() &&
                            enemyHero.getY() == playerCastle.getY()) {
                        ui.printDefeatMessage();
                        System.exit(0);
                    }
                }
                
                ui.printEnemyTurnEnd();
                
                // Автосохранение после раунда
                try {
                    SaveManager.autoSave(serializeState(playerHero, playerCastle, enemyHero, neutralCastle));
                } catch (Exception ex) {
                    ui.printAutoSaveError(ex.getMessage());
                }
            }
            
        } catch (IOException e) {
            ui.printSaveError("Ошибка при загрузке сохранения: " + e.getMessage());
        }
    }

    // Обновленный метод сериализации состояния для сохранения
    private String serializeState(Hero playerHero, Castle playerCastle, Hero enemyHero, NeutralCastle neutralCastle) {
        StringBuilder sb = new StringBuilder();
        
        // Первая строка: метаданные
        sb.append("Player;").append(currentPlayer)
          .append(";Map;").append(currentMapName)
          .append(";Score;").append(playerHero != null ? playerHero.getPlayerScore().calculateTotalScore() : 0)
          .append("\n");
        
        // Сохраняем полную информацию о герое
        if (playerHero != null) {
            sb.append("Hero;")
              .append(playerHero.getName()).append(";")
              .append(playerHero.getX()).append(";")
              .append(playerHero.getY()).append(";")
              .append(playerHero.getGold()).append(";")
              .append(playerHero.getLevel()).append(";")
              .append(playerHero.getExperience()).append("\n");
            
            // Сохраняем юниты игрока
            sb.append("Units;");
            for (String unit : playerHero.getUnits()) {
                sb.append(unit).append(";");
            }
            sb.append("\n");
        }
        
        // Сохраняем информацию о замке игрока
        sb.append("PlayerCastle;")
          .append(playerCastle.getX()).append(";")
          .append(playerCastle.getY()).append("\n");
          
        // Сохраняем постройки в замке
        sb.append("Buildings;");
        for (String building : playerCastle.getBuildingsNames()) {
            sb.append(building).append(";");
        }
        sb.append("\n");
        
        // Сохраняем данные врага
        if (enemyHero != null) {
            sb.append("Enemy;")
              .append(enemyHero.getName()).append(";")
              .append(enemyHero.getX()).append(";")
              .append(enemyHero.getY()).append(";")
              .append(enemyHero.getGold()).append("\n");
              
            // Сохраняем юниты врага
            sb.append("EnemyUnits;");
            for (String unit : enemyHero.getUnits()) {
                sb.append(unit).append(";");
            }
            sb.append("\n");
        }
        
        // Сохраняем данные нейтрального замка
        if (neutralCastle != null) {
            sb.append("NeutralCastle;")
              .append(neutralCastle.getX()).append(";")
              .append(neutralCastle.getY()).append(";")
              .append(neutralCastle.isPlayerCastle()).append("\n");
        }
        
        return sb.toString();
    }

    // Симуляция сражения между героем игрока и врагом
    private Hero simulateBattle(Hero player, Hero enemy, Castle playerCastle, Gamemap map,
                                Scanner scanner, GameUI ui) {
        ui.printBattleStarted();
        
        // Сохраняем позицию врага для обновления при победе
        int enemyX = enemy.getX();
        int enemyY = enemy.getY();
        
        // Запускаем бой между героями
        boolean outcome = battle.Battle.startBattle(player, enemy);
        if (outcome) {
            ui.printBattleVictory();
            
            // Если враг находился в точке нейтрального замка, и замок еще не захвачен,
            // отмечаем захват замка игроком в статистике
            if (enemyX == neutralCastle.getX() && enemyY == neutralCastle.getY() && !neutralCastle.isPlayerCastle()) {
                player.recordCastleCaptured();
            }
            
            return player;
        } else {
            ui.printHeroDiedInBattle();
            
            // При поражении сохраняем текущий рекорд
            savePlayerRecord();
            
            return null;
        }
    }

    // Метод для парсинга направления (поддержка одиночных и комбинированных символов)
    private int[] parseDirection(String input) {
        int dx = 0, dy = 0;
        for (char c : input.toCharArray()) {
            switch (c) {
                case 'w': dx -= 1; break;
                case 's': dx += 1; break;
                case 'a': dy -= 1; break;
                case 'd': dy += 1; break;
                default: return null;
            }
        }
        return (dx == 0 && dy == 0) ? null : new int[]{dx, dy};
    }

    // Меню замка (строительство, найм юнитов)
    private void processCastleMenu(Hero hero, Castle castle, Scanner scanner, GameUI ui) {
        while (true) {
            ui.printCastleMenuOptions(hero, castle);
            String choice = scanner.nextLine();
            if (choice.equals("0")) {
                break;
            }
            if (choice.equals("1")) {
                processBuildingMenu(hero, castle, scanner, ui);
            } else if (choice.equals("2")) {
                castle.showBuildings();
            } else {
                ui.printInvalidSelection();
            }
        }
    }

    // Меню строительства в замке
    private void processBuildingMenu(Hero hero, Castle castle, Scanner scanner, GameUI ui) {
        ui.printBuildingMenu(hero);
        String choice = scanner.nextLine();
        if (choice.equals("0")) {
            return;
        }
        int cost = 0;
        buildings.Building building = null;
        switch (choice) {
            case "1": building = new Tavern(); break;
            case "2": building = new SentryPost(); break;
            case "3": building = new ArcherTower(); break;
            case "4": building = new Stable(); break;
            case "5": building = new WeaponBuilding(); break;
            case "6": building = new Arena(); break;
            case "7": building = new Cathedral(); break;
            default:
                ui.printInvalidSelection();
                return;
        }
        cost = building.getCost();
        if (hero.getGold() < cost) {
            ui.printNotEnoughGoldBuild(building.getName());
        } else {
            hero.addGold(-cost);
            castle.addBuilding(building);
        }
    }

    // Меню найма юнитов в замке
    private void processHireMenu(Hero hero, Castle castle, Scanner scanner, GameUI ui) {
        ui.printHireMenu(hero, castle);
        String choice = scanner.nextLine();
        if (choice.equals("0")) {
            return;
        }
        int cost = 0;
        String unitType = "";
        switch (choice) {
            case "1":
                if (!castle.hasBuilding("Сторожевой пост")) {
                    ui.printHireUnavailable("Копейщик", "Сторожевой пост");
                    return;
                }
                unitType = "копейщик"; cost = 30; break;
            case "2":
                if (!castle.hasBuilding("Башня арбалетчиков")) {
                    ui.printHireUnavailable("Арбалетчик", "Башня арбалетчиков");
                    return;
                }
                unitType = "арбалетчик"; cost = 40; break;
            case "3":
                if (!castle.hasBuilding("Оружейная")) {
                    ui.printHireUnavailable("мечник", "Оружейная");
                    return;
                }
                unitType = "мечник"; cost = 50; break;
            case "4":
                if (!castle.hasBuilding("Арена")) {
                    ui.printHireUnavailable("кавалерист", "Арена");
                    return;
                }
                unitType = "кавалерист"; cost = 60; break;
            case "5":
                if (!castle.hasBuilding("Собор")) {
                    ui.printHireUnavailable("паладин", "Собор");
                    return;
                }
                unitType = "паладин"; cost = 70; break;
            default:
                ui.printInvalidSelection();
                return;
        }
        if (hero.getGold() < cost) {
            ui.printNotEnoughGoldHire(unitType);
        } else {
            hero.addGold(-cost);
            hero.addUnit(unitType);
            ui.printUnitHired(unitType, hero.getGold());
        }
    }


}
