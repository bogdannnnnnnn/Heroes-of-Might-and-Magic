package buildings;

public abstract class Building {
    protected String name;
    protected int cost;

    public Building(String name, int cost) {
        this.name = name;
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public int getCost() {
        return cost;
    }

    public abstract void applyEffect();
    public abstract char getIcon();
}
