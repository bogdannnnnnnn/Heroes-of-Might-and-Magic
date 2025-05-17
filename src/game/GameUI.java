package game;

import java.util.Scanner;
import heroes.Hero;
import castle.Castle;
import map.Gamemap;

public class GameUI {
    private Scanner scanner = new Scanner(System.in);

    public void printWelcomeMessage() {
        System.out.println("Добро пожаловать в стратегию в стиле Heroes of Might and Magic!");
    }

    private void printSeparator(String title) {
        System.out.println(title);
    }

    public void printNewRound() {
        printSeparator("Новый раунд");
    }

    public void printEnemyTurn() {
        printSeparator("Ход противника");
    }

    public void printEnemyTurnEnd() {
        System.out.println("Ход противника завершен.");
    }

    public void printNoHero() {
        System.out.println("У вас нет героя!");
    }

    public void printNewHeroPrompt() {
        System.out.println("Так как в замке построена Таверна, введите 'm' для найма нового героя:");
    }

    public void printHeroHired(String heroName) {
        System.out.println("Нанят новый герой: " + heroName);
    }

    public void printHeroNotHired() {
        System.out.println("Новый герой не нанят. Ожидание команды...");
    }

    public void printNoTavernNoHero() {
        System.out.println("В замке нет Таверны, поэтому нанять нового героя невозможно. Ожидание команды...");
    }

    public void printCastleBuildingsHeader() {
        System.out.println("Ваши здания в замке:");
    }

    public void printCommandOptions() {
        System.out.println("Введите команду:");
        System.out.println("Для перемещения: w, s, a, d или их комбинация (wa, sd и т.д.)");
        System.out.println("b - действия в замке, h - найм юнитов, ~ - сохранить, e - завершить ход, q - выход");
    }

    public void printSaveSuccess() {
        System.out.println("Игра успешно сохранена.");
    }

    public void printSaveError(String err) {
        System.out.println("Ошибка сохранения: " + err);
    }

    public void printAutoSaveError(String err) {
        System.out.println("Ошибка автосохранения: " + err);
    }

    public void printVictoryAndRecordSaved() {
        System.out.println("Вы завоевали вражеский замок! Победа!");
        System.out.println("Ваш результат сохранён в таблице рекордов.");
    }

    public void printExitGame() {
        System.out.println("Выход из игры.");
    }

    public void printEndTurn() {
        System.out.println("Вы завершили ход.");
    }

    public void printStableActive() {
        System.out.println("Конюшня активна: ваша дальность перемещения увеличена!");
    }

    public void printNotInCastle() {
        System.out.println("Вы не находитесь в своем замке!");
    }

    public void printHireNotInCastle() {
        System.out.println("Для найма юнитов вы должны находиться в своем замке!");
    }

    public void printCannotHireHeroAlive() {
        System.out.println("Найм нового героя возможен только после смерти основного героя!");
    }

    public void printInvalidDirection() {
        System.out.println("Неверное направление. Используйте w, s, a, d или их комбинации для диагонали.");
    }

    public void printOutOfBoundsMove() {
        System.out.println("Нельзя выйти за пределы карты!");
    }

    public void printSteppingOnEnemy() {
        System.out.println("Вы пытаетесь шагнуть на клетку, занятую врагом!");
    }

    public void printNeutralCastleReached() {
        System.out.println("Вы достигли нейтрального замка! Осада начинается.");
    }

    public void printSiegeOptions() {
        System.out.println("Введите 'a' для атаки или 'e' для завершения осады на этот ход:");
    }

    public void printSiegeEnded() {
        System.out.println("Осада замка завершена на этот ход.");
    }

    public void printInvalidSiegeCommand() {
        System.out.println("Неверная команда в осаде.");
    }

    public void printNeutralCastleCaptured() {
        System.out.println("Вы захватили нейтральный замок!");
    }

    public void printMoveNotPossible() {
        System.out.println("Перемещение невозможно!");
    }

    public void printVictoryMessage() {
        System.out.println("Вы завоевали вражеский замок! Победа!");
    }

    public void printHeroesCollision(int x, int y) {
        System.out.println("Столкновение! Координаты: (" + x + ", " + y + ")");
    }

    public void printEnemySteppingOnPlayer() {
        System.out.println("Враг пытается шагнуть на вашу клетку!");
    }

    public void printDefeatMessage() {
        System.out.println("Враг завоевал ваш замок! Вы проиграли!");
    }

    public void printBattleStarted() {
        System.out.println("Бой начался!");
    }

    public void printPlayerPosition(int x, int y) {
        System.out.println("Позиция игрока: (" + x + ", " + y + ")");
    }

    public void printNoHeroTargetCastle() {
        System.out.println("У игрока нет героя, цель – замок.");
    }

    public void printEnemyPosition(int x, int y) {
        System.out.println("Позиция врага: (" + x + ", " + y + ")");
    }

    public void printBattleVictory() {
        System.out.println("Сражение окончено! Игрок побеждает!");
        System.out.println("Вы завоевали врага, игра окончена.");
    }

    public void printHeroDiedInBattle() {
        System.out.println("Ваш герой погиб в сражении!");
    }

    public void printTavernOfferNewHero() {
        System.out.println("Так как в замке построена Таверна, вы можете нанять нового героя.");
    }

    public void printEnterMForNewHero() {
        System.out.println("Введите 'm' для найма нового героя:");
    }

    public void printHeroNotHiredSkip() {
        System.out.println("Новый герой не нанят. Ваш ход пропущен.");
    }

    public void printNoTavernHeroDied() {
        System.out.println("В замке нет Таверны, поэтому нанять нового героя невозможно. Ваш герой погиб.");
    }

    public void printCastleMenuOptions(Hero hero, Castle castle) {
        System.out.println("=== Меню замка ===");
        System.out.println("Ваше золото: " + hero.getGold());
        System.out.println("Замок: " + castle);
        System.out.println("Выберите действие:");
        System.out.println("1. Построить здание");
        System.out.println("2. Показать построенные здания");
        System.out.println("0. Выход из меню замка");
    }

    public void printInvalidSelection() {
        System.out.println("Неверный выбор. Попробуйте снова.");
    }

    public void printBuildingMenu(Hero hero) {
        System.out.println("=== Меню строительства в замке ===");
        System.out.println("Ваше золото: " + hero.getGold());
        System.out.println("Выберите здание для постройки:");
        System.out.println("1. Таверна (100 золота)");
        System.out.println("2. Сторожевой пост (50 золота)");
        System.out.println("3. Башня арбалетчиков (75 золота)");
        System.out.println("4. Конюшня (120 золота)");
        System.out.println("5. Оружейная (80 золота)");
        System.out.println("6. Арена (90 золота)");
        System.out.println("7. Собор (110 золота)");
        System.out.println("0. Отмена");
    }

    public void printNotEnoughGoldBuild(String buildingName) {
        System.out.println("Недостаточно золота для постройки " + buildingName + "!");
    }

    public void printHireMenu(Hero hero, Castle castle) {
        System.out.println("=== Меню найма юнитов ===");
        System.out.println("Ваше золото: " + hero.getGold());
        System.out.println("Доступные варианты найма:");
        if (castle.hasBuilding("Сторожевой пост")) {
            System.out.println("1. Копейщик (30 золота)");
        }
        if (castle.hasBuilding("Башня арбалетчиков")) {
            System.out.println("2. Арбалетчик (40 золота)");
        }
        if (castle.hasBuilding("Оружейная")) {
            System.out.println("3. Мечник (50 золота)");
        }
        if (castle.hasBuilding("Арена")) {
            System.out.println("4. Кавалерист (60 золота)");
        }
        if (castle.hasBuilding("Собор")) {
            System.out.println("5. Паладин (70 золота)");
        }
        System.out.println("0. Выход из меню найма");
    }

    public void printHireUnavailable(String unitName, String buildingName) {
        System.out.println("Найм " + unitName + " невозможен, отсутствует " + buildingName + "!");
    }

    public void printNotEnoughGoldHire(String unitName) {
        System.out.println("Недостаточно золота для найма " + unitName + "!");
    }

    public void printUnitHired(String unitName, int remainingGold) {
        System.out.println("Нанят " + unitName + ". Ваше золото теперь: " + remainingGold);
    }

}
