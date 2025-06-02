package castle;

import heroes.Hero;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import map.Gamemap;


public class NeutralCastle extends Castle {
    // Создаем статический блок логгера
    private static final Logger LOGGER = Logger.getLogger(NeutralCastle.class.getName());

    static {

    }

    private List<String> guardUnits;
    private int defenseBonus;
    private boolean captured;

    public NeutralCastle(int x, int y) {
        super(x, y);
        System.out.println("static2");
        System.out.println("static1");
        try {
            java.nio.file.Path logDir = java.nio.file.Paths.get( "logs");
            java.nio.file.Files.createDirectories(logDir);
            String logFile = logDir.resolve("neutral_castle.log").toString();
            FileHandler fh = new FileHandler(logFile, true);
            fh.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fh);
            LOGGER.setLevel(Level.ALL);
        } catch (Exception e) {
            System.err.println("Логирование для NeutralCastle не удалось: " + e);
        }
        captured = false;
        defenseBonus = 10;
        guardUnits = new ArrayList<>();
        
        LOGGER.fine("Создан нейтральный замок в позиции (" + x + ", " + y + ")");
        
        // Генерируем юнитов
        int count = 3 + (int)(Math.random() * 3);
        String[] possibleUnits = {"Копейщик", "Арбалетчик", "Мечник"};
        for (int i = 0; i < count; i++) {
            int idx = (int)(Math.random() * possibleUnits.length);
            guardUnits.add(possibleUnits[idx]);
        }
        
        LOGGER.config("Сгенерировано " + count + " охранников для нейтрального замка");
        
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
            LOGGER.fine("Вы уничтожили охранника нейтрального замка: " + defeated);
        } else {
            LOGGER.warning("Охрана нейтрального замка полностью уничтожена!");
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
            gameMap.updatePosition(getX(), getY(), getX(), getY(), hero.getIcon());
            LOGGER.severe("Вы захватили нейтральный замок!");
        }
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
