package battle;

import heroes.Hero;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Battle {
    // Метод для получения условной силы юнита по его названию.
    public static int getUnitPower(String unitType) {
        switch (unitType.toLowerCase()) {
            case "копейщик":
                return 10;
            case "арбалетчик":
                return 15;
            case "мечник":
                return 20;
            case "кавалерист":
                return 25;
            case "паладин":
                return 30;
            default:
                return 10;
        }
    }

    /**
     * Симуляция боя между героями.
     * @param player герой игрока
     * @param enemy враг
     * @return true, если побеждает игрок (враг погибает), false – если побеждает враг (герой игрока погибает)
     */
    public static boolean startBattle(Hero player, Hero enemy) {
        List<String> playerUnits = new ArrayList<>(player.getUnits());
        List<String> enemyUnits = new ArrayList<>(enemy.getUnits());
        Random rand = new Random();

        System.out.println("Начинается сражение на поле боя!");
        while (!playerUnits.isEmpty() && !enemyUnits.isEmpty()) {
            int playerPower = 0;
            for (String unit : playerUnits) {
                playerPower += getUnitPower(unit);
            }

            int enemyPower = 0;
            for (String unit : enemyUnits) {
                enemyPower += getUnitPower(unit);
            }

            double totalPower = playerPower + enemyPower;
            double chancePlayerLoses = enemyPower / totalPower;
            double roll = rand.nextDouble();

            if (roll < chancePlayerLoses) {
                // Игрок теряет один случайный юнит
                int index = rand.nextInt(playerUnits.size());
                String lostUnit = playerUnits.remove(index);
                System.out.println("Игрок теряет " + lostUnit + "!");
            } else {
                // Враг теряет один случайный юнит
                int index = rand.nextInt(enemyUnits.size());
                String lostUnit = enemyUnits.remove(index);
                System.out.println("Враг теряет " + lostUnit + "!");
            }

            // Вывод текущего состояния армий
            System.out.println("Осталось юнитов у игрока: " + playerUnits.size() +
                    ", у врага: " + enemyUnits.size());
            System.out.println("----------------------------");
        }

        if (playerUnits.isEmpty()) {
            System.out.println("Сражение окончено! Враг побеждает.");
            return false;
        } else {
            System.out.println("Сражение окончено! Игрок побеждает.");
            return true;
        }
    }
}
