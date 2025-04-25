package castle;

import java.util.ArrayList;
import java.util.List;
import heroes.Hero;
import map.Gamemap;

import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;
import java.util.logging.Level;


public class NeutralCastle extends Castle {
    // Создаем статический блок логгера
    private static final Logger LOGGER = Logger.getLogger(NeutralCastle.class.getName());
    static {
        try {
            // Убедимся, что папка logs существует
            java.nio.file.Path logDir = java.nio.file.Paths.get("src", "logs");
            java.nio.file.Files.createDirectories(logDir);
            String logFile = logDir.resolve("neutral_castle.log").toString();
            FileHandler fh = new FileHandler(logFile, true);
            fh.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fh);
            LOGGER.setLevel(Level.INFO);
        } catch (Exception e) {
            System.err.println("Логирование для NeutralCastle не удалось: " + e);
        }
    }


    // Список охранных юнитов (на случайном выборе из набора типов)
    private List<String> guardUnits;
    // Дополнительный бонус к защите
    private int defenseBonus;
    // Флаг захвата замка (если true, замок становится замком игрока)
    private boolean captured;

    public NeutralCastle(int x, int y) {
        super(x, y);
        captured = false;
        defenseBonus = 10;
        guardUnits = new ArrayList<>();
        // Генерируем от 3 до 5 охранных юнитов
        int count = 3 + (int)(Math.random() * 3); // 3, 4 или 5
        String[] possibleUnits = {"Копейщик", "Арбалетчик", "Мечник"};
        for (int i = 0; i < count; i++) {
            int idx = (int)(Math.random() * possibleUnits.length);
            guardUnits.add(possibleUnits[idx]);
        }
        // В нейтральном замке сразу все здания
        addBuilding("Таверна");
        addBuilding("Сторожевой пост");
        addBuilding("Башня арбалетчиков");
        addBuilding("Конюшня");
        addBuilding("Оружейная");
        addBuilding("Арена");
        addBuilding("Собор");
    }

    //Выполнение одного хода осады
    public void siegeTurn() {
        if (!guardUnits.isEmpty()) {
            String defeated = guardUnits.remove(0);
            LOGGER.info("Вы уничтожили охранника нейтрального замка: " + defeated);
        } else {
            LOGGER.info("Охрана нейтрального замка полностью уничтожена!");
        }
    }

    // true, если охрана уничтожена
    public boolean isCaptured() {
        return guardUnits.isEmpty();
    }

    // Захватывает замок
    public void capture(Hero hero, Gamemap gameMap) {
        if (!captured && isCaptured()) {
            captured = true;
            hero.forceMoveTo(getX(), getY());
            // Обновляем ячейку - герой переместился
            gameMap.updatePosition(getX(), getY(), getX(), getY(), hero.getIcon());
            LOGGER.info("Вы захватили нейтральный замок!");
        }
    }

    // Отображает статус осады замка.
    public void displayStatus() {
        System.out.println("Нейтральный замок охраняют " + guardUnits.size() + " юнит(ов).");
        System.out.println("Дополнительный бонус к защите: " + defenseBonus);
    }

    // Отображает список построенных зданий
    @Override
    public void showBuildings() {
        if (!captured) {
            System.out.println("Замок ещё не захвачен. Здания недоступны.");
        } else {
            super.showBuildings();
        }
    }

    // Возвращает true, если замок является замком игрока.
    public boolean isPlayerCastle() {
        return captured;
    }

    @Override
    public String toString() {
        if (captured) {
            return "Замок(" + getX() + ", " + getY() + ")";
        } else {
            return "Нейтральный замок(" + getX() + ", " + getY() + ")";
        }
    }
}
