package delft;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import java.util.*;
import java.util.stream.*;

import static java.util.Collections.*;
import static org.assertj.core.api.Assertions.*;
import static delft.Field.*;
import static delft.Property.*;
import static delft.SportsHallPlanner.planHalls;
import static org.junit.jupiter.api.Assertions.*;

class SportsHallPlannerTests {
    private static Request request1;
    private static Request request2;

    private static SportsHall sportsHall1;
    private static SportsHall sportsHall2;

    @BeforeEach
    void setUp() {
        request1 = new Request(
                Set.of(HAS_RESTAURANT, CLOSE_PUBLIC_TRANSPORT),
                TENNIS,
                6
        );

        request2 =  new Request(
                Set.of(NEAR_CITY_CENTRE),
                BADMINTON,
                2
        );

        sportsHall1 = new SportsHall(
                Set.of(HAS_RESTAURANT,  CLOSE_PUBLIC_TRANSPORT),
                Map.of(TENNIS, 7, BADMINTON, 1)
        );

        sportsHall2 = new SportsHall(
                Set.of(NEAR_CITY_CENTRE, HAS_RESTAURANT, CLOSE_PUBLIC_TRANSPORT),
                Map.of(BASKETBALL, 1)
        );
    }

    @Test
    void getRequestProperties() {
        Set<Property> properties = request1.getProperties();
        assertEquals(2, properties.size());
        assertTrue(properties.contains(HAS_RESTAURANT));
        assertTrue(properties.contains(CLOSE_PUBLIC_TRANSPORT));
        assertFalse(properties.contains(NEAR_CITY_CENTRE));
    }

    @Test
    void getRequestFieldType() {
        assertEquals(BADMINTON, request2.getRequiredFieldType());
    }

    @Test
    void getRequestMinNumberOfFields() {
        assertEquals(2, request2.getMinNumberOfFields());
    }

    @Test
    void canFullFillRequestImpossible() {
        assertFalse(sportsHall2.canFulfillRequest(request1));
    }

    @Test
    void canFullFillRequestPossible() {
        assertTrue(sportsHall1.canFulfillRequest(request1));
    }

    @Test
    void planHallsExceptionDuplicationHalls() {
        List<Request> requests = List.of(request1, request2);
        List<SportsHall> halls = List.of(sportsHall1, sportsHall1, sportsHall2);
        assertThatThrownBy(() -> SportsHallPlanner.planHalls(requests, halls))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("duplicate elements");

    }

    @Test
    void planHallsEmptyRequest() {
        List<Request> requests = List.of();
        List<SportsHall> halls = List.of(sportsHall1, sportsHall2);

        Map<SportsHall, Request> planning = SportsHallPlanner.planHalls(requests, halls);

        assertTrue(planning.isEmpty());
    }
}
