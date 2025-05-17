package test.java;

import static org.junit.jupiter.api.Assertions.*;

import buildings.Tavern;
import castle.Castle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

class TavernTest {
    @Test void tavernApplyEffectPrintsMessage() {
        var out    = new ByteArrayOutputStream();
        var orig   = System.out;
        System.setOut(new PrintStream(out));

        Tavern t = new Tavern();
        t.applyEffect();

        System.setOut(orig);
        assertTrue(out.toString().contains("Таверна построена"));
    }
}
class BuildingTest {
    private Castle castle;

    @BeforeEach
    void setUp() {
        castle = new Castle(0, 0);
    }

    @Test
    void addAndShowBuildings() {
        castle.addBuilding("Арена");
        assertTrue(castle.hasBuilding("Арена"));

        var out = new ByteArrayOutputStream();
        var orig = System.out;
        System.setOut(new PrintStream(out));
        castle.showBuildings();

        System.setOut(orig);
        assertTrue(out.toString().contains("Арена"));
    }
}