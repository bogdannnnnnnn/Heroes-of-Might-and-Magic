package test.java;

import editor.MapEditor;
import map.Gamemap;
import org.junit.jupiter.api.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

 class MapEditorTest {
    private static final String TEST_MAP_NAME = "test_map.csv";
    private static final File TEST_MAP_FILE = new File("maps", TEST_MAP_NAME);
    
    @BeforeEach
    public void setup() throws IOException {
        File mapsDir = new File("maps");
        if (!mapsDir.exists()) {
            mapsDir.mkdirs();
        }
        if (TEST_MAP_FILE.exists()) {
            TEST_MAP_FILE.delete();
        }
        
        // Создаем тестовую карту для тестов
        Gamemap map = new Gamemap(10, 10);
        map.saveToFile(TEST_MAP_FILE.getPath());
    }
    
    @AfterEach
    public void cleanup() {
        if (TEST_MAP_FILE.exists()) {
            TEST_MAP_FILE.delete();
        }
    }
    
    @Test
    public void testMapSaveAndLoad() throws IOException {
        // Проверяем, что файл карты создан
        assertTrue(TEST_MAP_FILE.exists());
        
        // Загружаем карту из файла
        Gamemap loadedMap = new Gamemap(TEST_MAP_FILE.getPath());
        assertEquals(10, loadedMap.getRows());
        assertEquals(10, loadedMap.getCols());
    }
    
    @Test
    public void testMapEditing() throws IOException {
        // Загружаем карту
        Gamemap map = new Gamemap(TEST_MAP_FILE.getPath());
        
        // Добавляем препятствие
        map.addObstacle(3, 3);
        
        // Сохраняем изменения
        map.saveToFile(TEST_MAP_FILE.getPath());
        
        // Загружаем обновленную карту
        Gamemap updatedMap = new Gamemap(TEST_MAP_FILE.getPath());
        assertEquals('#', updatedMap.getCell(3, 3));
    }
    
    @Test
    public void testMapDelete() {
        assertTrue(TEST_MAP_FILE.exists());
        
        // Удаляем карту
        assertTrue(TEST_MAP_FILE.delete());
        assertFalse(TEST_MAP_FILE.exists());
    }
    
    @Test
    public void testListMapNames() throws IOException {
        // Создаем еще одну тестовую карту
        File secondMapFile = new File("maps", "test_map2.csv");
        try {
            Gamemap map = new Gamemap(8, 8);
            map.saveToFile(secondMapFile.getPath());
            
            // Получаем список карт
            List<String> maps = MapEditor.listMapNames();
            
            // Проверяем, что наши тестовые карты есть в списке
            assertTrue(maps.contains(TEST_MAP_NAME));
            assertTrue(maps.contains("test_map2.csv"));
        } finally {
            if (secondMapFile.exists()) {
                secondMapFile.delete();
            }
        }
    }
}