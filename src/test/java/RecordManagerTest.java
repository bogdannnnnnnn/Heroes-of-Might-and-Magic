package test.java;

import records.RecordManager;
import records.RecordManager.Record;
import org.junit.jupiter.api.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

 class RecordManagerTest {
    private static final File RECORDS_FILE = new File("records.csv");
    
    @BeforeEach
    public void setup() throws IOException {
        if (RECORDS_FILE.exists()) {
            RECORDS_FILE.delete();
        }
        RecordManager.init();
    }
    
    @AfterEach
    public void cleanup() {
        if (RECORDS_FILE.exists()) {
            RECORDS_FILE.delete();
        }
    }
    
    @Test
    public void testInit() {
        assertTrue(RECORDS_FILE.exists());
    }
    
    @Test
    public void testAddRecord() throws IOException {
        RecordManager.update("player1", "map1", 100);
        
        List<Record> records = RecordManager.loadAll();
        assertEquals(1, records.size());
        assertEquals("player1", records.get(0).player);
        assertEquals(100, records.get(0).score);
        assertEquals("map1", records.get(0).mapName);
    }
    
    @Test
    public void testUpdateRecord() throws IOException {
        // Добавляем запись
        RecordManager.update("player1", "map1", 100);
        
        // Обновляем с лучшим показателем
        RecordManager.update("player1", "map2", 150);
        
        List<Record> records = RecordManager.loadAll();
        assertEquals(1, records.size());
        assertEquals("player1", records.get(0).player);
        assertEquals(150, records.get(0).score);
        assertEquals("map2", records.get(0).mapName);
    }
    
    @Test
    public void testNoUpdateWithLowerScore() throws IOException {
        // Добавляем запись
        RecordManager.update("player1", "map1", 100);
        
        // Пытаемся обновить с худшим показателем
        RecordManager.update("player1", "map2", 50);
        
        List<Record> records = RecordManager.loadAll();
        assertEquals(1, records.size());
        assertEquals("player1", records.get(0).player);
        assertEquals(100, records.get(0).score);
        assertEquals("map1", records.get(0).mapName);
    }
    
    @Test
    public void testLimitToTop5() throws IOException {
        // Добавляем 6 записей с разными игроками
        for (int i = 1; i <= 6; i++) {
            RecordManager.update("player" + i, "map", i * 10);
        }
        
        List<Record> records = RecordManager.loadAll();
        assertEquals(5, records.size());
        
        // Проверяем, что первые 5 игроков сохранены (в порядке убывания очков)
        assertEquals("player6", records.get(0).player);
        assertEquals("player5", records.get(1).player);
        assertEquals("player4", records.get(2).player);
        assertEquals("player3", records.get(3).player);
        assertEquals("player2", records.get(4).player);
        
        // Игрок с наименьшим показателем (player1) должен быть удален
        for (Record r : records) {
            assertNotEquals("player1", r.player);
        }
    }
    
    @Test
    public void testNoDuplicates() throws IOException {
        RecordManager.update("player1", "map1", 100);
        RecordManager.update("player2", "map2", 200);
        RecordManager.update("player1", "map3", 300); // Обновление игрока player1
        
        List<Record> records = RecordManager.loadAll();
        assertEquals(2, records.size());
        
        // player1 должен быть с обновленными данными и стоять на первом месте
        assertEquals("player1", records.get(0).player);
        assertEquals(300, records.get(0).score);
        assertEquals("map3", records.get(0).mapName);
    }
} 