package heroes;

import java.util.ArrayList;
import java.util.List;
import map.Gamemap;

public class Hero {
    // Основные характеристики
    private String name;
    private int level;
    private int experience;
    private List<String> units;

    // Позиционные свойства
    private int x;
    private int y;
    private char icon; // 'H' для героя, 'E' для врага
    private Gamemap gameMap;

    // Золото (общий ресурс)
    private static int gold = 900;

    // Дальность перемещения и очки хода
    private int moveRange = 1;
    private int movementPoints = 10;
    private final int MAX_MOVEMENT_POINTS = 10;

    public Hero(String name, int startX, int startY, Gamemap gameMap) {
        this.name = name;
        this.level = 1;
        this.experience = 0;
        this.units = new ArrayList<>();
        this.x = startX;
        this.y = startY;
        this.gameMap = gameMap;
        this.icon = 'H';
        if (gameMap != null) {
            gameMap.updatePosition(x, y, x, y, icon);
        }
    }

    public Hero(String name) {
        this(name, 0, 0, null);
    }

    public int getMovementPoints() {
        return movementPoints;
    }

    public void resetMovementPoints() {
        movementPoints = MAX_MOVEMENT_POINTS;
    }

    public int getMoveRange() {
        return moveRange;
    }

    public void setMoveRange(int range) {
        this.moveRange = range;
    }

    public boolean move(int dx, int dy) {
        int effectiveDx = dx * moveRange;
        int effectiveDy = dy * moveRange;
        int newX = x + effectiveDx;
        int newY = y + effectiveDy;
        if (gameMap != null && gameMap.canMoveToForHero(newX, newY, icon)) {
            int cost = gameMap.getMovementCost(newX, newY, this.icon);
            if (cost > movementPoints) {
                movementPoints = 0;
                return false;
            }
            movementPoints -= cost;
            char targetCell = gameMap.getCell(newX, newY);
            if (targetCell == '$') {
                addGold(100);
                gameMap.removeGoldAt(newX, newY);
                gainExperience(20);
            }
            gameMap.updatePosition(x, y, newX, newY, icon);
            x = newX;
            y = newY;
            return true;
        }
        return false;
    }

    /**
     * Принудительное перемещение героя на указанную клетку, без проверок.
     */
    public void forceMoveTo(int newX, int newY) {
        if (gameMap != null) {
            gameMap.updatePosition(x, y, newX, newY, icon);
            x = newX;
            y = newY;
        }
    }

    public void gainExperience(int exp) {
        this.experience += exp;
        while (this.experience >= this.level * 100) {
            this.experience -= this.level * 100;
            this.level++;
        }
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public String getName() { return name; }
    public int getLevel() { return level; }
    public int getExperience() { return experience; }

    public List<String> getUnits() {
        return new ArrayList<>(units);
    }

    public int getGold() { return gold; }
    public void addGold(int amount) { gold += amount; }
    public void addUnit(String unit) { this.units.add(unit); }
    public void setLevel(int level) { this.level = level; }
    public void setExperience(int experience) { this.experience = experience; }
    public void setUnits(List<String> units) { this.units = units; }

    public void setIcon(char newIcon) {
        if (newIcon != 'H' && newIcon != 'E') {
            return;
        }
        this.icon = newIcon;
        gameMap.updatePosition(x, y, x, y, icon);
    }

    /**
     * Метод для получения текущей иконки героя.
     */
    public char getIcon() {
        return icon;
    }

    @Override
    public String toString() {
        return "Герой: " + name +
                ", Уровень: " + level +
                ", Опыт: " + experience +
                ", Юниты: " + units +
                ", Золото: " + getGold() +
                ", Дальность перемещения: " + moveRange +
                ", Очки хода: " + movementPoints;
    }
}
