package buildings;

public class Arena extends Building {
    public Arena() {
        super("Арена", 90);
    }

    @Override
    public void applyEffect() {
        System.out.println("Арена построена. Теперь можно нанимать кавалеристов.");
    }

    @Override
    public char getIcon() {
        return 'R';
    }
}
