package save;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

public class SaveManager {
    private static String playerName;
    private static File saveDir;
    private static File autoSaveFile;
    // Создание дириктории и autosave
    public static void init(String name) {
        playerName = name;
        saveDir = new File("save", playerName);
        if (!saveDir.exists()) saveDir.mkdirs();
        autoSaveFile = new File(saveDir, "autosave.csv");
    }
    //
    public static void manualSave(String stateCsv) {
        try {
            String ts = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss").format(new Date());
            File target = new File(saveDir, playerName + "_" + ts + ".csv");
            try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(target), StandardCharsets.UTF_8))) {
                pw.print(stateCsv);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Перезапись autosave
    public static void autoSave(String stateCsv) {
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(autoSaveFile), StandardCharsets.UTF_8))) {
            pw.print(stateCsv);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Показ сохранений кроме autosave
    public static List<File> listSaves() {
        File[] files = saveDir.listFiles((d, n) -> n.endsWith(".csv") && !n.equals("autosave.csv"));
        if (files == null) return List.of();
        return Arrays.asList(files);
    }
    // Выгрузка сейва
    public static String loadSave(File file) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
