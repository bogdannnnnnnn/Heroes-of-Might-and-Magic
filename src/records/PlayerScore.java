package records;


public class PlayerScore {
    // Очки за различные действия
    public static final int POINTS_PER_GOLD = 1;           // 1 очко за 1 золото
    public static final int POINTS_PER_EXPERIENCE = 2;     // 2 очка за 1 опыт
    public static final int POINTS_PER_LEVEL = 100;        // 100 очков за 1 уровень
    
    // Очки за убийство разных типов юнитов
    public static final int POINTS_KILL_BASIC_UNIT = 50;   // копейщик
    public static final int POINTS_KILL_ARCHER = 75;       // арбалетчик
    public static final int POINTS_KILL_SWORDSMAN = 100;   // мечник
    public static final int POINTS_KILL_RIDER = 125;       // кавалерист
    public static final int POINTS_KILL_PALADIN = 150;     // паладин
    
    // Очки за захват замка
    public static final int POINTS_CAPTURE_CASTLE = 500;
    
    // Счетчики действий
    private int goldCollected;
    private int experienceGained;
    private int levelsGained;
    
    // Счетчики убитых юнитов по типам
    private int killedBasicUnits;
    private int killedArchers;
    private int killedSwordsmen;
    private int killedRiders;
    private int killedPaladins;
    
    // Другие достижения
    private int castlesCaptured;
    
    public PlayerScore() {
        // Изначально все счетчики равны 0
        goldCollected = 0;
        experienceGained = 0;
        levelsGained = 0;
        killedBasicUnits = 0;
        killedArchers = 0;
        killedSwordsmen = 0;
        killedRiders = 0;
        killedPaladins = 0;
        castlesCaptured = 0;
    }
    
    // Добавляет очки за собранное золото
    public void addGoldCollected(int amount) {
        goldCollected += amount;
    }
    
    // Добавляет очки за полученный опыт

    public void addExperienceGained(int amount) {
        experienceGained += amount;
    }
    
    // Добавляет очки за повышение уровня
    public void addLevelsGained(int levels) {
        levelsGained += levels;
    }
    
    // Добавляет очки за убийство
    public void addUnitKilled(String unitType) {
        switch (unitType.toLowerCase()) {
            case "копейщик":
                killedBasicUnits++;
                break;
            case "арбалетчик":
                killedArchers++;
                break;
            case "мечник":
                killedSwordsmen++;
                break;
            case "кавалерист":
                killedRiders++;
                break;
            case "паладин":
                killedPaladins++;
                break;
            default:
                killedBasicUnits++;
        }
    }
    
    //Добавляет очки за захват замка

    public void addCastleCaptured() {
        castlesCaptured++;
    }
    
    // Подсчитывает итоговое количество очков
    public int calculateTotalScore() {
        int totalScore = 0;

        totalScore += goldCollected * POINTS_PER_GOLD;
        totalScore += experienceGained * POINTS_PER_EXPERIENCE;
        totalScore += levelsGained * POINTS_PER_LEVEL;

        totalScore += killedBasicUnits * POINTS_KILL_BASIC_UNIT;
        totalScore += killedArchers * POINTS_KILL_ARCHER;
        totalScore += killedSwordsmen * POINTS_KILL_SWORDSMAN;
        totalScore += killedRiders * POINTS_KILL_RIDER;
        totalScore += killedPaladins * POINTS_KILL_PALADIN;

        totalScore += castlesCaptured * POINTS_CAPTURE_CASTLE;
        
        return totalScore;
    }
    
    // Отчет по статистике
    public String getScoreDetails() {
        StringBuilder details = new StringBuilder();
        
        details.append("Статистика игрока:\n");
        details.append("Собрано золота: ").append(goldCollected).append(" (").append(goldCollected * POINTS_PER_GOLD).append(" очков)\n");
        details.append("Получено опыта: ").append(experienceGained).append(" (").append(experienceGained * POINTS_PER_EXPERIENCE).append(" очков)\n");
        details.append("Повышено уровней: ").append(levelsGained).append(" (").append(levelsGained * POINTS_PER_LEVEL).append(" очков)\n");
        details.append("Убито копейщиков: ").append(killedBasicUnits).append(" (").append(killedBasicUnits * POINTS_KILL_BASIC_UNIT).append(" очков)\n");
        details.append("Убито арбалетчиков: ").append(killedArchers).append(" (").append(killedArchers * POINTS_KILL_ARCHER).append(" очков)\n");
        details.append("Убито мечников: ").append(killedSwordsmen).append(" (").append(killedSwordsmen * POINTS_KILL_SWORDSMAN).append(" очков)\n");
        details.append("Убито кавалеристов: ").append(killedRiders).append(" (").append(killedRiders * POINTS_KILL_RIDER).append(" очков)\n");
        details.append("Убито паладинов: ").append(killedPaladins).append(" (").append(killedPaladins * POINTS_KILL_PALADIN).append(" очков)\n");
        details.append("Захвачено замков: ").append(castlesCaptured).append(" (").append(castlesCaptured * POINTS_CAPTURE_CASTLE).append(" очков)\n");
        details.append("ВСЕГО ОЧКОВ: ").append(calculateTotalScore());
        
        return details.toString();
    }
} 