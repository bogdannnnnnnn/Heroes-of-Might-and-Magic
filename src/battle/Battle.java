package battle;

import heroes.Hero;
import records.PlayerScore;
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
        
        // Отслеживаем убитых юнитов для очков
        PlayerScore playerScore = player.getPlayerScore();
        int startLevel = player.getLevel();
        int startExp = player.getExperience();

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
                
                // Добавляем очки за убийство юнита
                if (playerScore != null) {
                    playerScore.addUnitKilled(lostUnit);
                    
                    // Даем игроку опыт за убийство
                    int expGained = getUnitPower(lostUnit);
                    player.gainExperience(expGained);
                }
            }

            // Вывод текущего состояния армий
            System.out.println("Осталось юнитов у игрока: " + playerUnits.size() +
                    ", у врага: " + enemyUnits.size());
            System.out.println("----------------------------");
        }

        boolean playerWins = !playerUnits.isEmpty();
        
        // Если игрок победил, обновляем его очки и добавляем опыт
        if (playerWins && playerScore != null) {
            // Даем дополнительный опыт за победу
            int bonusExp = 50;
            player.gainExperience(bonusExp);
            
            // Рассчитываем силу врага для награды
            int enemyPower = 0;
            for (String unit : enemyUnits) {
                enemyPower += getUnitPower(unit);
            }
            
            // Добавляем доп. золото за победу
            int goldReward = 100 + (enemyPower / 2);
            player.addGold(goldReward);
            
            // Обновляем очки игрока за собранное золото
            playerScore.addGoldCollected(goldReward);
            
            // Обновляем очки за полученный опыт
            int totalExpGained = player.getExperience() - startExp;
            if (player.getLevel() > startLevel) {
                // Если повысился уровень - добавляем очки за уровни
                int levelsGained = player.getLevel() - startLevel;
                playerScore.addLevelsGained(levelsGained);
                // Корректируем опыт с учетом уровня
                totalExpGained += startLevel * 100 * levelsGained;
            }
            playerScore.addExperienceGained(totalExpGained);
            
            System.out.println("Сражение окончено! Игрок побеждает.");
            System.out.println("Получено: " + goldReward + " золота, " + (bonusExp + totalExpGained) + " опыта");
        } else {
            System.out.println("Сражение окончено! Враг побеждает.");
        }
        
        // Обновляем армии героев
        player.clearUnits();
        for (String unit : playerUnits) {
            player.addUnit(unit);
        }
        
        enemy.clearUnits();
        for (String unit : enemyUnits) {
            enemy.addUnit(unit);
        }
        
        return playerWins;
    }
}
