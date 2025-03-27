package buildings;

public class WeaponBuilding extends Building {
    public WeaponBuilding() {
        super("Оружейная", 80);
    }

    @Override
    public void applyEffect() {
        System.out.println("Оружейная построена. Теперь можно нанимать мечников.");
    }

    @Override
    public char getIcon() {
        return 'W';
    }
}
