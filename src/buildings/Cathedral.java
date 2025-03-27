package buildings;

public class Cathedral extends Building {
    public Cathedral() {
        super("Собор", 110);
    }

    @Override
    public void applyEffect() {
        System.out.println("Собор построен. Теперь можно нанимать паладинов.");
    }

    @Override
    public char getIcon() {
        return 'D';
    }
}
