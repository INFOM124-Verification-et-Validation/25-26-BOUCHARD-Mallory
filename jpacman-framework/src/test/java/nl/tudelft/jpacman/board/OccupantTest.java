package nl.tudelft.jpacman.board;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test suite to confirm that {@link Unit}s correctly (de)occupy squares.
 *
 * @author Jeroen Roosen 
 *
 */
class OccupantTest {

    /**
     * The unit under test.
     */
    private Unit unit;

    /**
     * Resets the unit under test.
     */
    @BeforeEach
    void setUp() {
        unit = new BasicUnit();
    }

    /**
     * Asserts that a unit has no square to start with.
     */
    @Test
    void noStartSquare() {
        assertThat(unit.hasSquare()).isFalse();
    }

    /**
     * Tests that the unit indeed has the target square as its base after
     * occupation.
     */
    @Test
    void testOccupy() {
        Square square = new BasicSquare();

        assertThat(unit.hasSquare()).isFalse();

        unit.occupy(square);

        assertThat(unit.hasSquare()).isTrue();
        assertThat(unit.getSquare()).isEqualTo(square);
    }

    /**
     * Test that the unit indeed has the target square as its base after
     * double occupation.
     */
    @Test
    void testReoccupy() {
        Square fstSquare = new BasicSquare();
        Square sndSquare = new BasicSquare();

        assertThat(unit.hasSquare()).isFalse();

        unit.occupy(fstSquare);

        assertThat(unit.hasSquare()).isTrue();
        assertThat(unit.getSquare()).isEqualTo(fstSquare);
        assertThat(unit.getSquare()).isNotEqualTo(sndSquare);

        unit.occupy(sndSquare);

        assertThat(unit.hasSquare()).isTrue();
        assertThat(unit.getSquare()).isEqualTo(sndSquare);
        assertThat(unit.getSquare()).isNotEqualTo(fstSquare);
    }
}
