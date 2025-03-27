package game;

import heroes.Hero;
import castle.Castle;
import castle.NeutralCastle;
import map.Gamemap;
import map.GameMapUI;
import buildings.Tavern;
import buildings.SentryPost;
import buildings.ArcherTower;
import buildings.Stable;
import buildings.WeaponBuilding;
import buildings.Arena;
import buildings.Cathedral;

import java.util.Scanner;

public class Game {

    public void start() {
        // Используем GameUI для всех сообщений пользователю
        GameUI ui = new GameUI();
        ui.printWelcomeMessage();

        // Создаем карту 10x10 и размещаем золото
        Gamemap map = new Gamemap(10, 10);
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
        Hero playerHero = new Hero("Папаня", playerCastleX, playerCastleY, map);
        playerHero.addUnit("Костян");
        playerHero.addUnit("Гуль");
        playerHero.addGold(1000);

        // Создаем замок игрока
        Castle playerCastle = new Castle(playerCastleX, playerCastleY);

        // Создаем врага (начало в замке врага, с иконкой 'E')
        Hero enemyHero = new Hero("Враг", enemyCastleX, enemyCastleY, map);
        enemyHero.addUnit("Арбалетчик");
        enemyHero.addUnit("Мечник");
        enemyHero.addUnit("Мечник");
        enemyHero.addUnit("Мечник");
        enemyHero.addUnit("Мечник");
        enemyHero.setIcon('E');

        // Создаем нейтральный замок и размещаем его на карте
        NeutralCastle neutralCastle = new NeutralCastle(neutralCastleX, neutralCastleY);
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
                        System.exit(0);
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
                    // Если целевая клетка – нейтральный замок, инициируем осаду (рейд)
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
        }
    }

    // Симуляция сражения между героем игрока и врагом
    private Hero simulateBattle(Hero player, Hero enemy, Castle playerCastle, Gamemap map,
                                Scanner scanner, GameUI ui) {
        ui.printBattleStarted();
        if (player != null) {
            ui.printPlayerPosition(player.getX(), player.getY());
        } else {
            ui.printNoHeroTargetCastle();
        }
        ui.printEnemyPosition(enemy.getX(), enemy.getY());
        boolean outcome = battle.Battle.startBattle(player, enemy);
        if (outcome) {
            ui.printBattleVictory();
            System.exit(0);
            return player; // недостижимый код
        } else {
            ui.printHeroDiedInBattle();
            // Исправление: удаляем иконку погибшего героя с карты
            if (player != null) {
                map.removeEntityAt(player.getX(), player.getY());
            }
            if (playerCastle.hasBuilding("Таверна")) {
                ui.printTavernOfferNewHero();
                ui.printEnterMForNewHero();
                String choice = scanner.nextLine().trim().toLowerCase();
                if (choice.equals("m")) {
                    Hero newHero = new Hero("Папаня", playerCastle.getX(), playerCastle.getY(), map);
                    newHero.addGold(player != null ? player.getGold() : 0);
                    ui.printHeroHired(newHero.getName());
                    return newHero;
                } else {
                    ui.printHeroNotHired();
                    return null;
                }
            } else {
                ui.printNoTavernHeroDied();
                return null;
            }
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
