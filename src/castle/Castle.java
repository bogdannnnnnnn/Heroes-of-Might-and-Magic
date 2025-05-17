package castle;

import buildings.Building;
import java.util.ArrayList;
import java.util.List;

public class Castle {
    protected int x;
    protected int y;
    private List<String> buildings;
    private boolean allBuildingsBuilt;

    public Castle(int x, int y) {
        this.x = x;
        this.y = y;
        buildings = new ArrayList<>();
        allBuildingsBuilt = false;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void addBuilding(Object building) {
        // Если объект является Building, используем его название через getName(), иначе toString()
        if (building instanceof Building) {
            buildings.add(((Building) building).getName());
        } else {
            buildings.add(building.toString());
        }
    }

    public void addBuilding(String buildingName) {
        buildings.add(buildingName);
    }

    public List<String> getBuildingsNames() {
        return new ArrayList<>(buildings);
    }

    public void showBuildings() {
        if (buildings.isEmpty()) {
            System.out.println("В замке пока нет построек.");
        } else {
            System.out.println("Построенные здания:");
            for (String b : buildings) {
                System.out.println("- " + b);
            }
        }
    }

    public boolean hasBuilding(String buildingName) {
        return buildings.contains(buildingName);
    }

    public void setAllBuildingsBuilt(boolean built) {
        allBuildingsBuilt = built;
    }

    public boolean isAllBuildingsBuilt() {
        return allBuildingsBuilt;
    }

    @Override
    public String toString() {
        return "Замок(" + x + ", " + y + ")";
    }
}
