package nl.tudelft.jpacman.level;

import nl.tudelft.jpacman.npc.Ghost;
import nl.tudelft.jpacman.npc.ghost.GhostFactory;
import nl.tudelft.jpacman.sprite.EmptySprite;
import nl.tudelft.jpacman.sprite.PacManSprites;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PlayerCollisionsTest {

    private PacManSprites pacManSprites = new PacManSprites();
    private PlayerFactory playerFactory = new PlayerFactory(pacManSprites);
    private GhostFactory ghostFactory = new GhostFactory(pacManSprites);
    private PlayerCollisions playerCollisions = new PlayerCollisions();
    Player player;
    Ghost ghost;
    Pellet pellet;

    @BeforeEach
    void setUp() {
        player = playerFactory.createPacMan();
        ghost = ghostFactory.createClyde();
        pellet = new Pellet(0, new EmptySprite());
    }

    @Test
    public void PlayerColideGhost() {
        playerCollisions.collide(player, ghost);
        assertFalse(player.isAlive());
    }

    @Test
    public void PlayerColidePellet() {
        playerCollisions.collide(player, pellet);
        assertTrue(player.isAlive());
    }

    @Test
    public void GhostCollidePlayer() {
        playerCollisions.collide(ghost, player);
        assertFalse(player.isAlive());
    }

    @Test
    public void GhostCollidePellet() {
        playerCollisions.collide(ghost, pellet);
        assertTrue(player.isAlive());
    }
}
