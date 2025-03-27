package buildings;

public class Stable extends Building {
    public Stable() {
        super("Конюшня", 120);
    }

    @Override
    public void applyEffect() {
        System.out.println("Конюшня построена. Дальность перемещения героев увеличена.");
    }

    @Override
    public char getIcon() {
        return 'C';
    }
}
