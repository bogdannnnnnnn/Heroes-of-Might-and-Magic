package buildings;

public class ArcherTower extends Building {
    public ArcherTower() {
        super("Башня арбалетчиков", 75);
    }

    @Override
    public void applyEffect() {
        System.out.println("Башня арбалетчиков построена. Теперь можно нанимать арбалетчиков.");
    }

    @Override
    public char getIcon() {
        return 'A';
    }
}
