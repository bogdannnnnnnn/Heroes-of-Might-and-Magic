package test.java;

import static org.junit.jupiter.api.Assertions.*;

import battle.Battle;
import map.Gamemap;
import org.junit.jupiter.api.*;
import heroes.Hero;
import records.RecordManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

class MapTest {
    private Gamemap map;
    private Hero player;

    @BeforeEach
    void setUp() {
        map = new Gamemap(5, 5);
        player = new Hero("H", 0, 0, map);
    }

    @Test
    void cannotMoveOutOfBounds() {
        assertFalse(player.move(-1,0));
        assertEquals(0, player.getX());
        assertEquals(0, player.getY());
    }

    @Test
    void cannotMoveToObstacle() {
        map.addObstacle(2, 3);
        player.forceMoveTo(2, 2);
        assertFalse(player.move(0, 1));
    }

    @Test
    void updatePositionAfterForceMove() {
        player.forceMoveTo(3, 3);
        assertEquals(player.getIcon(), map.getCell(3, 3));
    }

    @Test
    void placeCastlesOnMap() {
        Gamemap custom = new Gamemap(9, 9);
        int mid = custom.getCols() / 2;
        assertEquals('И', custom.getCell(0, mid));
        assertEquals('К', custom.getCell(8, mid));
    }

    @Test
    void placeAndRemoveGold() {
        boolean fl= false;
        map.placeGoldPiles(3);
        assertEquals( 3, count(map, '$'));
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++){
                if (map.getCell(i, j) == '$') {
                    map.removeGoldAt(i, j);
                    fl = true;
                    break;
                }
            }
            if(fl) break;
        }
        assertEquals( 2, count(map, '$'));
    }

    private int count(Gamemap m, char ch){
        int c=0;
        for(int i=0;i<m.getRows();i++){
            for(int j=0;j<m.getCols();j++) {
                if (m.getCell(i, j) == ch) c++;
            }
        }
        return c;
    }
    @Test
    void placeNeutralCastle() {
        assertTrue(map.placeNeutralCastle(2, 2),
                "Метод должен вернуть true");
        assertEquals('N', map.getCell(2, 2));
    }

    @Test
    void removeEntityRestoresCell() {
        player.forceMoveTo(1, 1);
        map.removeEntityAt(1, 1);
        assertNotEquals(player.getIcon(), map.getCell(1, 1));
    }

     class BattleTest {
        private Gamemap map;
        private Hero player, enemy;

        @BeforeEach
        void setUp() {
            map = new Gamemap(5, 5);
            player = new Hero("BBE", 0, 0, map);
            enemy  = new Hero("TEA", 1, 1, map);
        }

        @Test
        public void playerWinsIfEnemyHasNoUnits(){
            player.addUnit("мечник");
            assertTrue(Battle.startBattle(player,enemy));
        }

        @Test
        public void botWinsIfPlayerHasNoUnits(){
            enemy.addUnit("мечник");
            assertFalse(Battle.startBattle(player,enemy));
        }

        @Test
        public void unitPowerValue(){
            assertEquals(10,Battle.getUnitPower("копейщик"));
            assertEquals(15, Battle.getUnitPower("арбалетчик"));
            assertEquals(20, Battle.getUnitPower("мечник"));
            assertEquals(25, Battle.getUnitPower("кавалерист"));
            assertEquals(30, Battle.getUnitPower("паладин"));
            assertEquals(10, Battle.getUnitPower("NoName"));
        }

        @Test
        public void outputRemoveMassageWhileBattle(){
            player.addUnit("NoName");
            enemy.addUnit("паладин");

            var outContent = new ByteArrayOutputStream();
            var originalContent=System.out;
            System.setOut(new PrintStream(outContent));

            Battle.startBattle(player,enemy);
            System.setOut(originalContent);
        }
    }

    public static class RecordTest {
        public static void main(String[] args) {
            try {
                // Инициализируем RecordManager
                RecordManager.init();
                System.out.println("Инициализация успешна");

                // Сохраняем несколько записей
                RecordManager.update("player1", "map1", 100);
                RecordManager.update("player2", "map1", 200);
                RecordManager.update("player3", "map1", 300);
                System.out.println("Записи сохранены");

                // Загружаем все записи
                List<RecordManager.Record> records = RecordManager.loadAll();
                System.out.println("\nЗагруженные записи (" + records.size() + "):");
                for (RecordManager.Record r : records) {
                    System.out.println("Игрок: " + r.player + ", Карта: " + r.mapName + ", Очки: " + r.score);
                }

                // Обновляем запись
                RecordManager.update("player1", "map2", 400);
                System.out.println("\nОбновлена запись для player1");

                // Загружаем записи снова
                records = RecordManager.loadAll();
                System.out.println("\nОбновленные записи (" + records.size() + "):");
                for (RecordManager.Record r : records) {
                    System.out.println("Игрок: " + r.player + ", Карта: " + r.mapName + ", Очки: " + r.score);
                }

                System.out.println("\nТест завершен успешно");
            } catch (IOException e) {
                System.err.println("Ошибка: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
