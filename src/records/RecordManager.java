package records;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;


public class RecordManager {
    private static final int MAX_RECORDS = 5;
    private static final File RECORDS_DIR = new File("records");
    private static final File RECORDS_FILE = new File("records.csv");
    
    static {
        if (!RECORDS_DIR.exists()) {
            RECORDS_DIR.mkdirs();
        }
    }

    // Возвращает файл с рекордами для конкретного игрока
    private static File getPlayerRecordFile(String playerName) {
        return new File(RECORDS_DIR, playerName + "_records.csv");
    }

    public static void init() throws IOException {
        // Инициализируем директорию
        if (!RECORDS_DIR.exists()) {
            RECORDS_DIR.mkdirs();
        }
        
        // Инициализируем файл для совместимости с тестами
        if (!RECORDS_FILE.exists()) {
            File dir = RECORDS_FILE.getParentFile();
            if (dir != null && !dir.exists()) {
                dir.mkdirs();
            }
            try (PrintWriter pw = new PrintWriter(
                    new OutputStreamWriter(new FileOutputStream(RECORDS_FILE), StandardCharsets.UTF_8))) {
                // создаем пустой файл
            }
        }
    }

    // Обновляет рекорд игрока для конкретной карты
    public static void update(String player, String mapName, int score) throws IOException {
        updatePlayerRecord(player, mapName, score);
        
        updateLegacyRecords(player, mapName, score);
    }
    
    // Метод для обновления записей в файле конкретного игрока
    private static void updatePlayerRecord(String player, String mapName, int score) throws IOException {
        File recordFile = getPlayerRecordFile(player);
        
        // Создаем файл, если его нет
        if (!recordFile.exists()) {
            File dir = recordFile.getParentFile();
            if (dir != null && !dir.exists()) {
                dir.mkdirs();
            }
            try (PrintWriter pw = new PrintWriter(
                    new OutputStreamWriter(new FileOutputStream(recordFile), StandardCharsets.UTF_8))) {
            }
        }
        // Обновление рекорда
        List<Record> records = loadPlayerRecords(player);
        Optional<Record> existing = records.stream().filter(r -> r.mapName.equals(mapName)).findFirst();
        
        if (existing.isPresent()) {
            Record rec = existing.get();
            if (score <= rec.score) {
                return;
            }
            rec.score = score;
        } else {
            records.add(new Record(player, score, mapName));
        }
        
        // Сортировка и усечение списка до MAX_RECORDS
        records = records.stream().sorted(Comparator.comparingInt((Record r) -> r.score).reversed()).limit(MAX_RECORDS)
                .collect(Collectors.toList());
        
        // Запись обратно в файл
        try (PrintWriter pw = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(recordFile), StandardCharsets.UTF_8))) {
            for (Record r : records) {
                pw.println(r.player + ";" + r.score + ";" + r.mapName);
            }
        }
    }
    
    // Метод обновления записи в общем файле (для совместимости с тестом)
    private static void updateLegacyRecords(String player, String mapName, int score) throws IOException {
        List<Record> records = loadLegacyRecords();
        Optional<Record> existing = records.stream()
                .filter(r -> r.player.equals(player))
                .findFirst();
                
        if (existing.isPresent()) {
            Record rec = existing.get();
            if (score <= rec.score) {
                return; // нет улучшения
            }
            rec.score = score;
            rec.mapName = mapName;
        } else {
            records.add(new Record(player, score, mapName));
        }
        
        // Сортировка и усечение списка до MAX_RECORDS
        records = records.stream()
                .sorted(Comparator.comparingInt((Record r) -> r.score).reversed())
                .limit(MAX_RECORDS)
                .collect(Collectors.toList());
                
        // Запись обратно в файл
        try (PrintWriter pw = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(RECORDS_FILE), StandardCharsets.UTF_8))) {
            for (Record r : records) {
                pw.println(r.player + ";" + r.score + ";" + r.mapName);
            }
        }
    }

    // Загружает все рекорды игрока
    public static List<Record> loadPlayerRecords(String player) throws IOException {
        File recordFile = getPlayerRecordFile(player);
        List<Record> records = new ArrayList<>();
        
        if (!recordFile.exists()) {
            return records;
        }
        
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(recordFile), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null && !line.isBlank()) {
                String[] tokens = line.split(";");
                if (tokens.length >= 3) {
                    String playerName = tokens[0];
                    int score = Integer.parseInt(tokens[1]);
                    String mapName = tokens[2];
                    records.add(new Record(playerName, score, mapName));
                }
            }
        }
        return records;
    }
    
    // Загружает записи из общего файла (для совместимости с тестами)
    private static List<Record> loadLegacyRecords() throws IOException {
        List<Record> records = new ArrayList<>();
        if (!RECORDS_FILE.exists()) {
            return records;
        }
        
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(RECORDS_FILE), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null && !line.isBlank()) {
                String[] tokens = line.split(";");
                if (tokens.length >= 3) {
                    String playerName = tokens[0];
                    int score = Integer.parseInt(tokens[1]);
                    String mapName = tokens[2];
                    records.add(new Record(playerName, score, mapName));
                }
            }
        }
        return records;
    }

    // Загружает все рекорды всех игроков
    public static List<Record> loadAll() throws IOException {
        if (RECORDS_FILE.exists()) {
            return loadLegacyRecords();
        }
        
        List<Record> allRecords = new ArrayList<>();
        
        if (!RECORDS_DIR.exists()) {
            return allRecords;
        }
        
        File[] recordFiles = RECORDS_DIR.listFiles((dir, name) -> name.endsWith("_records.csv"));
        if (recordFiles != null) {
            for (File file : recordFiles) {
                String playerName = file.getName().replace("_records.csv", "");
                allRecords.addAll(loadPlayerRecords(playerName));
            }
        }
        
        // Сортируем по убыванию очков
        allRecords.sort(Comparator.comparingInt((Record r) -> r.score).reversed());
        
        return allRecords;
    }

    // Получает топ рекордов по всем игрокам
    public static List<Record> getTopRecords(int limit) throws IOException {
        return loadAll().stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    // Класс для хранения информации о рекорде
    public static class Record {
        public String player;
        public int score;
        public String mapName;

        public Record(String player, int score, String mapName) {
            this.player = player;
            this.score = score;
            this.mapName = mapName;
        }
    }
}
