package buildings;

public class Tavern extends Building {
    public Tavern() {
        super("Таверна", 100);
    }

    @Override
    public void applyEffect() {
        System.out.println("Таверна построена. Теперь можно нанимать Героев.");
    }

    @Override
    public char getIcon() {
        return 'T';
    }
}
