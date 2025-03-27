package heroes;

import java.util.List;

public class HeroUI {

    // Вывод подробной информации о герое
    public void displayHeroInfo(Hero hero) {
        System.out.println("Герой: " + hero.getName());
        System.out.println("Позиция: (" + hero.getX() + ", " + hero.getY() + ")");
        System.out.println("Уровень: " + hero.getLevel());
        System.out.println("Опыт: " + hero.getExperience());
        System.out.println("Золото: " + hero.getGold());
        System.out.println("Очки хода: " + hero.getMovementPoints());
        System.out.println("Дальность перемещения: " + hero.getMoveRange());

        List<String> units = hero.getUnits();
        if (units.isEmpty()) {
            System.out.println("Юниты: отсутствуют");
        } else {
            System.out.println("Юниты: " + String.join(", ", units));
        }
    }

    // Метод для отображения результата сброса очков хода
    public void printResetMovement(int newMovementPoints) {
        System.out.println("Очки хода восстановлены: " + newMovementPoints);
    }

    // Метод для отображения результата перемещения
    public void printMoveResult(boolean success, int remainingPoints) {
        if (success) {
            System.out.println("Перемещение выполнено. Осталось очков хода: " + remainingPoints);
        } else {
            System.out.println("Перемещение невозможно или недостаточно очков хода!");
        }
    }

    // Метод для вывода информации о сборе золота
    public void printGoldCollected(int goldAdded, int totalGold) {
        System.out.println("Собрана копилка золота! +" + goldAdded + " золота. Всего золота: " + totalGold);
    }

    // Метод для отображения полученного опыта
    public void printExperienceGained(int expGained, int totalExp) {
        System.out.println("Получено " + expGained + " опыта. Всего опыта: " + totalExp);
    }

    // Метод для отображения повышения уровня
    public void printLevelUp(int newLevel) {
        System.out.println("Поздравляем! Вы достигли нового уровня: " + newLevel);
    }
}
