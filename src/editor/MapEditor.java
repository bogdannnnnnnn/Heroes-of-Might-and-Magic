package editor;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;


public class MapEditor {
    private static final File MAPS_DIR = new File("maps");
    
    // Создает карту maps
    static {
        if (!MAPS_DIR.exists()) {
            MAPS_DIR.mkdirs();
        }
    }

    // Создает папку player в maps
    public static File getPlayerMapsDir(String playerName) {
        File playerMapsDir = new File(MAPS_DIR, playerName);
        if (!playerMapsDir.exists()) {
            playerMapsDir.mkdirs();
        }
        return playerMapsDir;
    }

    // Вывод списка карт
    public static List<String> listMapNames(String playerName) {
        File dir;
        if (playerName != null) {
            dir = getPlayerMapsDir(playerName);
        } else {
            dir = MAPS_DIR;
        }
        String[] arr = dir.list((d, name) -> name.endsWith(".csv"));
        if (arr == null) {
            return List.of();
        }
        return Arrays.stream(arr).collect(Collectors.toList());
    }

    public static List<String> listMapNames() {
        return listMapNames(null);
    }
}
