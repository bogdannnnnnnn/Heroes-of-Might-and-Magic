package battle;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import heroes.Hero;
import map.Gamemap;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class BattleTest {
    private Gamemap map;
    private Hero player, enemy;

    @BeforeEach
    void setUp() {
        map = new Gamemap(5, 5);
        player = new Hero("BBE", 0, 0, map);
        enemy  = new Hero("TEA", 1, 1, map);
    }

    @Test
    public void playerWinsIfEnemyHasNoUnits(){
        player.addUnit("мечник");
        assertTrue(Battle.startBattle(player,enemy));
    }

    @Test
    public void botWinsIfPlayerHasNoUnits(){
        enemy.addUnit("мечник");
        assertFalse(Battle.startBattle(player,enemy));
    }

    @Test
    public void unitPowerValue(){
        assertEquals(10,Battle.getUnitPower("копейщик"));
        assertEquals(15, Battle.getUnitPower("арбалетчик"));
        assertEquals(20, Battle.getUnitPower("мечник"));
        assertEquals(25, Battle.getUnitPower("кавалерист"));
        assertEquals(30, Battle.getUnitPower("паладин"));
        assertEquals(10, Battle.getUnitPower("NoName"));
    }

    @Test
    public void outputRemoveMassageWhileBattle(){
        player.addUnit("NoName");
        enemy.addUnit("паладин");

        var outContent = new ByteArrayOutputStream();
        var originalContent=System.out;
        System.setOut(new PrintStream(outContent));

        Battle.startBattle(player,enemy);
        System.setOut(originalContent);
    }
}
