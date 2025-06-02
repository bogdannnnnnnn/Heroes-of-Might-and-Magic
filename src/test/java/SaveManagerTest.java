package test.java;

import heroes.Hero;
import map.Gamemap;
import save.SaveManager;
import org.junit.jupiter.api.*;
import java.io.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

 class SaveManagerTest {
    private static final String TEST_PLAYER = "testPlayer";
    private static final File SAVE_DIR = new File("save", TEST_PLAYER);
    private static final File AUTO_SAVE_FILE = new File(SAVE_DIR, "autosave.csv");
    
    @BeforeEach
    public void setup() {
        // Очищаем тестовую директорию
        if (SAVE_DIR.exists()) {
            for (File file : SAVE_DIR.listFiles()) {
                file.delete();
            }
        } else {
            SAVE_DIR.mkdirs();
        }
        SaveManager.init(TEST_PLAYER);
    }
    
    @AfterEach
    public void cleanup() {
        if (SAVE_DIR.exists()) {
            for (File file : SAVE_DIR.listFiles()) {
                file.delete();
            }
            SAVE_DIR.delete();
        }
    }
    
    @Test
    public void testInit() {
        assertTrue(SAVE_DIR.exists());
    }
    
    @Test
    public void testManualSave() {
        String testData = "Player;testPlayer;Map;testMap;Score;100\n";
        SaveManager.manualSave(testData);
        
        // Проверяем, что файл сохранения создан
        List<File> saves = SaveManager.listSaves();
        assertEquals(1, saves.size());
        
        // Проверяем содержимое файла
        String loadedData = SaveManager.loadSave(saves.get(0));
        assertEquals(testData, loadedData);
    }
    
    @Test
    public void testAutoSave() {
        String testData = "Player;testPlayer;Map;testMap;Score;100\n";
        SaveManager.autoSave(testData);
        
        // Проверяем, что файл автосохранения создан
        assertTrue(AUTO_SAVE_FILE.exists());
        
        // Проверяем содержимое файла
        String loadedData = SaveManager.loadSave(AUTO_SAVE_FILE);
        assertEquals(testData, loadedData);
    }
    
    @Test
    public void testListSaves()  {
        // Создаем несколько файлов сохранений
        String testData1 = "Player1;testPlayer1;Map1;testMap1;Score;100\n";
        String testData2 = "Player2;testPlayer2;Map2;testMap2;Score;200\n";
        SaveManager.manualSave(testData1);
        SaveManager.manualSave(testData2);
        SaveManager.autoSave(testData1);
        
        // Проверяем, что в списке только ручные сохранения
        List<File> saves = SaveManager.listSaves();
        assertEquals(2, saves.size());
        
        // Проверяем, что в директории 3 файла (2 ручных + 1 авто)
        assertEquals(3, SAVE_DIR.listFiles().length);
    }
 }
     class HeroTest {
        private Gamemap map;
        private Hero player;

        @BeforeEach
        void setUp() {
            map = new Gamemap(5, 5);
            player = new Hero("BBE", 2, 2, map);
        }

        @Test
        void gainsExperienceAndLevelsUp() {
            int lvl = player.getLevel();
            player.gainExperience(lvl * 100);
            assertEquals(lvl+1, player.getLevel());
        }

        @Test
        void goldManagement() {
            int g0 = player.getGold();
            player.addGold(200);
            assertEquals(g0+200, player.getGold());
            player.addGold(-100);
            assertEquals(g0+100, player.getGold());
        }

        @Test void forceMoveToWorks() {
            player.forceMoveTo(4,4);
            assertEquals(4, player.getX());
            assertEquals(4, player.getY());
        }

        @Test void collectsGoldMove() {
            map.placeGoldPiles(1);
            int startY=0;
            int gx=-1, gy=-1;
            for(int i=0;i<5;i++) {
                for(int j=0;j<5;j++){
                    if(map.getCell(i,j)=='$'){
                        gx=i; gy=j;
                        break;
                    }
                }
            }
            assertTrue(gx >= 0);
            if(gy>0){
                startY = gy-1;
            }
            else {
                startY = gy+1;
            }

            player.forceMoveTo(gx, startY);
            int goldBefore = player.getGold();
            assertTrue(player.move(0, (gy-startY)));
            assertEquals(goldBefore + 100, player.getGold());
            assertNotEquals('$', map.getCell(gx, gy));
        }
    }
