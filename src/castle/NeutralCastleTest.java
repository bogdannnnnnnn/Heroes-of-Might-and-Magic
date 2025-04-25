package castle;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import heroes.Hero;
import map.Gamemap;

class NeutralCastleTest {
    private NeutralCastle neutralCastle;
    private Gamemap map;
    private Hero player;

    @BeforeEach
    void setUp() {
        map = new Gamemap(5, 5);
        neutralCastle = new NeutralCastle(2, 2);
        player = new Hero("BBE", 0, 0, map);
    }

    @Test
    void siegeEventuallyCaptures() {
        for (int i = 0; i < 5; i++) {
            neutralCastle.siegeTurn();
        }
        assertTrue(neutralCastle.isCaptured());
    }

    @Test
    void captureTransfersOwnershipAndMovesHero() {
        while (!neutralCastle.isCaptured()) {
            neutralCastle.siegeTurn();
        }
        neutralCastle.capture(player, map);
        assertTrue(neutralCastle.isPlayerCastle());
        assertEquals(2, player.getX());
        assertEquals(2, player.getY());
    }
}

