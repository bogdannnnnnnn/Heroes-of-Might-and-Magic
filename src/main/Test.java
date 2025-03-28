package main;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

// Импорт тестируемых классов (предполагается, что они доступны в classpath)
import battle.Battle;
import heroes.Hero;
import map.Gamemap;
import castle.Castle;
import castle.NeutralCastle;
import buildings.Tavern;

/**
 * Тестовый класс для покрытия функционала игры.
 */
public class Test {

    private Gamemap map;

    @BeforeEach
    public void setup() {
        // Для большинства тестов создаём небольшую карту 5x5
        map = new Gamemap(5, 5);
    }

    // Тест 1. Завершение игры: победа игрока (если у врага нет юнитов).
    @org.junit.jupiter.api.Test
    public void testPlayerVictoryInBattle() {
        Hero player = new Hero("Player", 0, 0, map);
        player.addUnit("мечник"); // хотя бы один юнит
        Hero enemy = new Hero("Enemy", 1, 1, map);
        // У врага не добавляем юниты, значит его армия пуста
        boolean result = Battle.startBattle(player, enemy);
        assertTrue(result, "Если у врага нет юнитов, то игрок должен победить.");
    }

    // Тест 2. Завершение игры: победа бота (если у игрока нет юнитов).
    @org.junit.jupiter.api.Test
    public void testBotVictoryInBattle() {
        Hero player = new Hero("Player", 0, 0, map);
        // У игрока нет юнитов
        Hero enemy = new Hero("Enemy", 1, 1, map);
        enemy.addUnit("мечник");
        boolean result = Battle.startBattle(player, enemy);
        assertFalse(result, "Если у игрока нет юнитов, бот должен победить.");
    }

    // Тест 3. Логика перемещения: корректное списание очков хода.
    @org.junit.jupiter.api.Test
    public void testHeroMovementDeductsPoints() {
        Hero hero = new Hero("Player", 2, 2, map);
        int initialMP = hero.getMovementPoints();
        int dx = 0, dy = 1; // перемещение вправо
        int expectedCost = map.getMovementCost(2, 3, hero.getIcon());
        boolean moved = hero.move(dx, dy);
        assertTrue(moved, "Перемещение должно быть возможным.");
        assertEquals(initialMP - expectedCost, hero.getMovementPoints(), "Очки хода должны уменьшиться на стоимость перемещения.");
    }

    // Тест 4. Логика перемещения: невозможность выйти за пределы карты.
    @org.junit.jupiter.api.Test
    public void testHeroCannotMoveOutOfBounds() {
        // Помещаем героя в левый верхний угол
        Hero hero = new Hero("Player", 0, 0, map);
        boolean moved = hero.move(-1, 0); // попытка переместиться за пределы карты
        assertFalse(moved, "Перемещение за пределы карты должно быть запрещено.");
        assertEquals(0, hero.getX());
        assertEquals(0, hero.getY());
    }

    // Тест 5. Логика перемещения: невозможность перемещения на клетку с препятствием.
    // Создаём подкласс Gamemap, переопределяющий getCell для выбранной координаты.
    static class TestGamemap extends Gamemap {
        public TestGamemap(int rows, int cols) {
            super(rows, cols);
        }
        @Override
        public char getCell(int x, int y) {
            // Для клетки (2,3) возвращаем символ препятствия
            if(x == 2 && y == 3) return '#';
            return super.getCell(x, y);
        }
    }

    @org.junit.jupiter.api.Test
    public void testHeroCannotMoveToObstacle() {
        Gamemap testMap = new TestGamemap(5, 5);
        Hero hero = new Hero("Player", 2, 2, testMap);
        // Попытка переместиться вправо (на (2,3), где препятствие)
        boolean moved = hero.move(0, 1);
        assertFalse(moved, "Перемещение на клетку с препятствием должно быть запрещено.");
        assertEquals(2, hero.getX());
        assertEquals(2, hero.getY());
    }

    // Тест 6. Корректность расчёта силы юнитов.
    @org.junit.jupiter.api.Test
    public void testGetUnitPower() {
        assertEquals(10, Battle.getUnitPower("копейщик"), "Сила копейщика должна быть 10.");
        assertEquals(15, Battle.getUnitPower("арбалетчик"), "Сила арбалетчика должна быть 15.");
        assertEquals(20, Battle.getUnitPower("мечник"), "Сила мечника должна быть 20.");
        assertEquals(25, Battle.getUnitPower("кавалерист"), "Сила кавалериста должна быть 25.");
        assertEquals(30, Battle.getUnitPower("паладин"), "Сила паладина должна быть 30.");
        // Проверка значения по умолчанию
        assertEquals(10, Battle.getUnitPower("неизвестный"), "Для неизвестного юнита должна возвращаться базовая сила 10.");
    }

    // Тест 7. Симуляция боя: проверка, что в процессе сражения выводится сообщение об удалении юнита.
    @org.junit.jupiter.api.Test
    public void testBattleSimulatesUnitRemoval() {
        Hero player = new Hero("Player", 0, 0, map);
        player.addUnit("мечник");
        Hero enemy = new Hero("Enemy", 1, 1, map);
        enemy.addUnit("арбалетчик");
        // Перехватываем вывод в консоль
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));
        Battle.startBattle(player, enemy);
        System.setOut(originalOut);
        String output = outContent.toString();
        boolean messageFound = output.contains("Игрок теряет") || output.contains("Враг теряет");
        assertTrue(messageFound, "Должно выводиться сообщение о потере юнита в ходе боя.");
    }

    // Тест 8. Логика героя: начисление опыта и повышение уровня.
    @org.junit.jupiter.api.Test
    public void testHeroGainsExperienceAndLevelsUp() {
        Hero hero = new Hero("Player", 2, 2, map);
        int initialLevel = hero.getLevel();
        // Для повышения уровня требуется level*100 опыта – добавляем необходимое количество опыта
        hero.gainExperience(initialLevel * 100);
        assertEquals(initialLevel + 1, hero.getLevel(), "Герой должен повысить уровень после набора необходимого опыта.");
    }

    // Тест 9. Логика героя: корректное изменение золота.
    @org.junit.jupiter.api.Test
    public void testHeroGoldManagement() {
        Hero hero = new Hero("Player", 2, 2, map);
        int initialGold = hero.getGold();
        hero.addGold(200);
        assertEquals(initialGold + 200, hero.getGold(), "При добавлении золота значение должно увеличиться.");
        hero.addGold(-100);
        assertEquals(initialGold + 100, hero.getGold(), "При вычитании золота значение должно уменьшиться.");
    }

    // Тест 10. Логика героя: принудительное перемещение (forceMoveTo).
    @org.junit.jupiter.api.Test
    public void testHeroForceMoveTo() {
        Hero hero = new Hero("Player", 2, 2, map);
        hero.forceMoveTo(4, 4);
        assertEquals(4, hero.getX(), "Принудительное перемещение должно обновлять координату X.");
        assertEquals(4, hero.getY(), "Принудительное перемещение должно обновлять координату Y.");
    }

    // Тест 11. Работа зданий: добавление здания в замок и проверка наличия.
    @org.junit.jupiter.api.Test
    public void testCastleBuildingAddition() {
        Castle castle = new Castle(0, 0);
        castle.addBuilding("Таверна");
        assertTrue(castle.hasBuilding("Таверна"), "Замок должен содержать здание «Таверна» после добавления.");
    }

    // Тест 12. Работа здания Tavern: применение эффекта (вывод сообщения).
    @org.junit.jupiter.api.Test
    public void testTavernApplyEffect() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));
        Tavern tavern = new Tavern();
        tavern.applyEffect();
        System.setOut(originalOut);
        String output = outContent.toString();
        assertTrue(output.contains("Таверна построена"), "При постройке таверны должно выводиться соответствующее сообщение.");
    }

    // Тест 13. Работа здания Tavern: проверка иконки.
    @org.junit.jupiter.api.Test
    public void testTavernIcon() {
        Tavern tavern = new Tavern();
        assertEquals('T', tavern.getIcon(), "Иконка таверны должна быть 'T'.");
    }

    // Тест 14. Работа зданий: проверка отображения списка построек замка.
    @org.junit.jupiter.api.Test
    public void testCastleShowBuildings() {
        Castle castle = new Castle(0, 0);
        castle.addBuilding("Арена");
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));
        castle.showBuildings();
        System.setOut(originalOut);
        String output = outContent.toString();
        assertTrue(output.contains("Арена"), "Вывод списка построек должен содержать добавленное здание «Арена».");
    }

    // Тест 15. Действия нейтрального замка: метод siegeTurn – удаление охранного юнита.
    @org.junit.jupiter.api.Test
    public void testNeutralCastleSiegeTurn() {
        NeutralCastle neutralCastle = new NeutralCastle(2, 2);
        // Вызываем siegeTurn несколько раз – изначально в замке 3–5 охранных юнитов.
        for (int i = 0; i < 5; i++) {
            neutralCastle.siegeTurn();
        }
        assertTrue(neutralCastle.isCaptured(), "После достаточного числа вызовов siegeTurn замок должен считаться захваченным (охрана уничтожена).");
    }

    // Тест 16. Действия нейтрального замка: захват замка (capture).
    @org.junit.jupiter.api.Test
    public void testNeutralCastleCapture() {
        Hero hero = new Hero("Player", 0, 0, map);
        NeutralCastle neutralCastle = new NeutralCastle(2, 2);
        // Уничтожаем охрану
        while (!neutralCastle.isCaptured()) {
            neutralCastle.siegeTurn();
        }
        neutralCastle.capture(hero, map);
        assertTrue(neutralCastle.isPlayerCastle(), "После захвата замок должен стать замком игрока.");
        assertEquals(2, hero.getX(), "После захвата координата X героя должна совпадать с координатой замка.");
        assertEquals(2, hero.getY(), "После захвата координата Y героя должна совпадать с координатой замка.");
    }

    // Тест 17. Отображение игрового поля: обновление ячейки при перемещении героя.
    @org.junit.jupiter.api.Test
    public void testGamemapUpdatePosition() {
        Hero hero = new Hero("Player", 2, 2, map);
        // Перемещаем героя принудительно
        hero.forceMoveTo(3, 3);
        assertEquals(hero.getIcon(), map.getCell(3, 3), "Ячейка карты должна обновиться и отобразить иконку героя после перемещения.");
    }

    // Тест 18. Инициализация карты: проверка установки символов замков.
    @org.junit.jupiter.api.Test
    public void testGamemapInitializationCastles() {
        int rows = 9, cols = 9;
        Gamemap customMap = new Gamemap(rows, cols);
        int mid = cols / 2;
        assertEquals('И', customMap.getCell(0, mid), "На карте в позиции игрока должен стоять символ 'И'.");
        assertEquals('К', customMap.getCell(rows - 1, mid), "На карте в позиции врага должен стоять символ 'К'.");
    }

    // Тест 19. Отображение игрового поля: проверка размещения золотых куч.
    @org.junit.jupiter.api.Test
    public void testPlaceGoldPiles() {
        // Подсчитываем количество '$' до размещения
        int initialGoldCount = 0;
        for (int i = 0; i < map.getRows(); i++){
            for (int j = 0; j < map.getCols(); j++){
                if(map.getCell(i,j) == '$') initialGoldCount++;
            }
        }
        map.placeGoldPiles(3);
        int goldCount = 0;
        for (int i = 0; i < map.getRows(); i++){
            for (int j = 0; j < map.getCols(); j++){
                if(map.getCell(i,j) == '$') goldCount++;
            }
        }
        assertEquals(initialGoldCount + 3, goldCount, "После размещения золотых куч должно добавиться ровно 3 клетки с '$'.");
    }

    // Тест 20. Отображение игрового поля: размещение нейтрального замка.
    @org.junit.jupiter.api.Test
    public void testPlaceNeutralCastle() {
        boolean placed = map.placeNeutralCastle(2, 2);
        assertTrue(placed, "Размещение нейтрального замка должно вернуть true.");
        assertEquals('N', map.getCell(2, 2), "Ячейка, где размещён нейтральный замок, должна содержать символ 'N'.");
    }

    // Тест 21. Отображение игрового поля: корректный расчёт стоимости перемещения по различным типам клеток.
    @org.junit.jupiter.api.Test
    public void testGetMovementCost() {
        // Создаём класс с фиксированным символом клетки для тестирования
        class FixedMap extends Gamemap {
            private final char fixed;
            public FixedMap(char fixed, int rows, int cols) {
                super(rows, cols);
                this.fixed = fixed;
            }
            @Override
            public char getCell(int x, int y) {
                return fixed;
            }
        }
        FixedMap plusMap = new FixedMap('+', 5, 5);
        assertEquals(2, plusMap.getMovementCost(0, 0, 'H'), "Для клетки '+' стоимость перемещения должна быть 2.");
        FixedMap andMap = new FixedMap('&', 5, 5);
        assertEquals(2, andMap.getMovementCost(0, 0, 'H'), "Для клетки '&' стоимость перемещения для героя должна быть 2.");
        FixedMap questionMap = new FixedMap('?', 5, 5);
        assertEquals(4, questionMap.getMovementCost(0, 0, 'H'), "Для клетки '?' стоимость перемещения для героя должна быть 4.");
        FixedMap dotMap = new FixedMap('.', 5, 5);
        assertEquals(3, dotMap.getMovementCost(0, 0, 'H'), "Для клетки '.' стоимость перемещения должна быть 3.");
        FixedMap goldMap = new FixedMap('$', 5, 5);
        assertEquals(3, goldMap.getMovementCost(0, 0, 'H'), "Для клетки '$' стоимость перемещения должна быть 3.");
        FixedMap nMap = new FixedMap('N', 5, 5);
        assertEquals(3, nMap.getMovementCost(0, 0, 'H'), "Для клетки 'N' стоимость перемещения должна быть 3.");
    }

    // Тест 22. Отображение игрового поля: удаление золота с карты.
    @org.junit.jupiter.api.Test
    public void testRemoveGoldAt() {
        map.placeGoldPiles(1);
        int goldX = -1, goldY = -1;
        outer: for (int i = 0; i < map.getRows(); i++){
            for (int j = 0; j < map.getCols(); j++){
                if(map.getCell(i, j) == '$'){
                    goldX = i;
                    goldY = j;
                    break outer;
                }
            }
        }
        assertTrue(goldX != -1, "Должна быть размещена хотя бы одна куча золота.");
        map.removeGoldAt(goldX, goldY);
        assertNotEquals('$', map.getCell(goldX, goldY), "После удаления золота ячейка не должна содержать символ '$'.");
    }

    // Тест 23. Отображение игрового поля: удаление сущности с карты.
    @org.junit.jupiter.api.Test
    public void testRemoveEntityAt() {
        Hero hero = new Hero("Player", 2, 2, map);
        map.removeEntityAt(2, 2);
        assertNotEquals(hero.getIcon(), map.getCell(2, 2), "После удаления сущности ячейка должна восстановиться до исходного состояния.");
    }

    // Тест 24. Логика героя: сбор золота при перемещении на клетку с '$'.
    @org.junit.jupiter.api.Test
    public void testHeroCollectsGold() {
        map.placeGoldPiles(1);
        int goldX = -1, goldY = -1;
        outer:
        for (int i = 0; i < map.getRows(); i++){
            for (int j = 0; j < map.getCols(); j++){
                if(map.getCell(i, j) == '$'){
                    goldX = i;
                    goldY = j;
                    break outer;
                }
            }
        }
        assertTrue(goldX != -1, "Должна быть размещена хотя бы одна куча золота.");
        // Размещаем героя рядом с золотой кучей (если возможно)
        int heroX = goldX, heroY = goldY - 1;
        if (heroY < 0) {
            heroY = goldY + 1;
        }
        Hero hero = new Hero("Player", heroX, heroY, map);
        int initialGold = hero.getGold();
        int dx = 0, dy = (goldY > heroY) ? 1 : -1;
        boolean moved = hero.move(dx, dy);
        assertTrue(moved, "Герой должен суметь переместиться на клетку с золотом.");
        // Согласно логике, при сборе золота начисляется 100 золота и 20 опыта
        assertEquals(initialGold + 100, hero.getGold(), "После сбора золота золото героя должно увеличиться на 100.");
        assertNotEquals('$', map.getCell(goldX, goldY), "После сбора золота клетка должна быть очищена от символа '$'.");
    }

    // Тест 25. Комплексный сценарий: последовательное выполнение нескольких действий.
    @org.junit.jupiter.api.Test
    public void testComprehensiveScenario() {
        Hero hero = new Hero("Player", 2, 2, map);
        Castle castle = new Castle(2, 2);
        // 1. Герой собирает золото.
        map.placeGoldPiles(1);
        int goldX = -1, goldY = -1;
        outer: for (int i = 0; i < map.getRows(); i++){
            for (int j = 0; j < map.getCols(); j++){
                if(map.getCell(i, j) == '$'){
                    goldX = i;
                    goldY = j;
                    break outer;
                }
            }
        }
        if (goldX != -1) {
            // Если золото находится рядом, перемещаем героя, иначе используем forceMoveTo
            if (Math.abs(goldX - hero.getX()) <= 1 && Math.abs(goldY - hero.getY()) <= 1) {
                hero.move(goldX - hero.getX(), goldY - hero.getY());
            } else {
                hero.forceMoveTo(goldX, goldY);
            }
        }
        int goldAfterCollection = hero.getGold();
        // 2. Добавляем в замок здание, необходимое для найма юнита (например, "Сторожевой пост")
        castle.addBuilding("Сторожевой пост");
        // 3. Симулируем наём юнита: списываем стоимость найма и добавляем юнита
        int initialUnitCount = hero.getUnits().size();
        int hireCost = 30;
        hero.addGold(-hireCost);
        hero.addUnit("копейщик");
        // Проверяем, что юнит добавлен и золото уменьшилось на стоимость найма
        assertEquals(initialUnitCount + 1, hero.getUnits().size(), "После найма количество юнитов должно увеличиться.");
        assertEquals(goldAfterCollection - hireCost, hero.getGold(), "Золото героя должно уменьшиться на стоимость найма.");
    }
}
