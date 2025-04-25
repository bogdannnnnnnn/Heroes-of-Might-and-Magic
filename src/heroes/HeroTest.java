package heroes;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import map.Gamemap;

class HeroTest {
    private Gamemap map;
    private Hero player;

    @BeforeEach
    void setUp() {
        map = new Gamemap(5, 5);
        player = new Hero("BBE", 2, 2, map);
    }

    @Test void gainsExperienceAndLevelsUp() {
        int lvl = player.getLevel();
        player.gainExperience(lvl * 100);
        assertEquals(lvl+1, player.getLevel());
    }

    @Test void goldManagement() {
        int g0 = player.getGold();
        player.addGold(200);
        assertEquals(g0+200, player.getGold());
        player.addGold(-100);
        assertEquals(g0+100, player.getGold());
    }

    @Test void forceMoveToWorks() {
        player.forceMoveTo(4,4);
        assertEquals(4, player.getX());
        assertEquals(4, player.getY());
    }

    @Test void collectsGoldMove() {
        map.placeGoldPiles(1);
        int startY=0;
        int gx=-1, gy=-1;
        for(int i=0;i<5;i++) {
            for(int j=0;j<5;j++){
                if(map.getCell(i,j)=='$'){
                    gx=i; gy=j;
                    break;
                }
            }
        }
        assertTrue(gx >= 0);
        if(gy>0){
            startY = gy-1;
        }
        else {
            startY = gy+1;
        }

        player.forceMoveTo(gx, startY);
        int goldBefore = player.getGold();
        assertTrue(player.move(0, (gy-startY)));
        assertEquals(goldBefore + 100, player.getGold());
        assertNotEquals('$', map.getCell(gx, gy));
    }
}
