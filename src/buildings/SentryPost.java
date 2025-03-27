package buildings;

public class SentryPost extends Building {
    public SentryPost() {
        super("Сторожевой пост", 50);
    }

    @Override
    public void applyEffect() {
        System.out.println("Сторожевой пост построен. Теперь можно нанимать копейщиков.");
    }

    @Override
    public char getIcon() {
        return 'P';
    }
}
