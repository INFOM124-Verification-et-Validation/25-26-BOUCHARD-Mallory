package delft;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.within;
import java.time.temporal.ChronoUnit;

import java.util.*;
import java.util.stream.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.time.*;

class AutoAssignerTest {

    private Student s;
    private Workshop w;
    private ZonedDateTime date1;
    private ZonedDateTime date2;

    private ZonedDateTime date(int year, int month, int day, int hour, int minute) {
        return ZonedDateTime.of(year, month, day, hour, minute, 0, 0, ZoneId.systemDefault());
    }

    @BeforeEach
    void setUp() {
        int studentId = 0;
        String studentName = "NameOfTheStudent";
        String studentEmail = "EmailOfTheStudent";
        s = new Student(studentId, studentName, studentEmail);

        int workshopId = 0;
        String workshopName = "NameOfTheWorkshop";
        date1 = ZonedDateTime.now().plusDays(1);
        date2 = ZonedDateTime.now().plusDays(2);

        Map<ZonedDateTime, Integer> spots = new HashMap<>();
        spots.put(date1, 2);
        spots.put(date2, 0);

        w = new Workshop(workshopId, workshopName, spots);
    }

    @Test
    void getStudentName() {
        assertEquals("NameOfTheStudent", s.getName());
    }

    @Test
    void getStudentEmail() {
        assertEquals("EmailOfTheStudent", s.getEmail());
    }


    @Test
    void testHasAvailableDate_trueWhenSpotsAvailable() {
        assertTrue(w.hasAvailableDate(), "Should have available date since one date has spots left");
    }

    @Test
    void testHasAvailableDate_falseWhenNoSpotsAvailable() {
        Map<ZonedDateTime, Integer> full = new HashMap<>();
        full.put(date1, 0);
        full.put(date2, 0);

        Workshop fullWorkshop = new Workshop(2, "Full Workshop", full);
        assertFalse(fullWorkshop.hasAvailableDate());
    }

    @Test
    void testGetNextAvailableDate_returnsEarliestAvailable() {
        ZonedDateTime expected = date1;
        assertEquals(expected, w.getNextAvailableDate());
    }

    @Test
    void testTakeASpot_decrementsCorrectly() {
        int before = w.getSpotsPerDate().get(date1);
        w.takeASpot(date1);
        int after = w.getSpotsPerDate().get(date1);
        assertEquals(before - 1, after);
    }

    @Test
    void testTakeASpot_onInvalidDate_throwsException() {
        ZonedDateTime invalidDate = ZonedDateTime.now().plusDays(99);

        assertThrows(NullPointerException.class, () -> w.takeASpot(invalidDate));
    }

    @Test
    void testGettersReturnExpectedValues() {
        assertEquals("NameOfTheWorkshop", w.getName());
        assertNotNull(w.getSpotsPerDate());
    }

    @Test
    void testAssign_studentsAssignedUntilNoSpotsLeft() {
        // Arrange
        AutoAssigner assigner = new AutoAssigner();

        Student s1 = new Student(1, "Alice", "a@x.com");
        Student s2 = new Student(2, "Bob", "b@x.com");
        Student s3 = new Student(3, "Charlie", "c@x.com");
        List<Student> students = List.of(s1, s2, s3);

        // Workshop avec 2 places
        Map<ZonedDateTime, Integer> spots = new HashMap<>();
        ZonedDateTime date = ZonedDateTime.now().plusDays(1);
        spots.put(date, 2);
        Workshop workshop = new Workshop(10, "Test Workshop", spots);

        // Act
        AssignmentsLogger log = assigner.assign(students, List.of(workshop));

        // Assert
        // Deux étudiants assignés (2 spots)
        assertThat(log.getAssignments())
                .hasSize(2)
                .allSatisfy(msg -> assertThat(msg).contains("Test Workshop"));
        // Un étudiant non assigné (plus de place)
        assertThat(log.getErrors())
                .hasSize(1)
                .first().toString()
                .contains("no spots");

        // Vérifie que les places du workshop ont bien décrémenté à 0
        assertThat(workshop.getSpotsPerDate().get(date)).isEqualTo(0);
    }

    @Test
    void testAssign_allStudentsFailWhenNoAvailableDate() {
        AutoAssigner assigner = new AutoAssigner();

        // Workshop sans aucune place
        Map<ZonedDateTime, Integer> noSpots = new HashMap<>();
        noSpots.put(ZonedDateTime.now().plusDays(1), 0);
        Workshop fullWorkshop = new Workshop(99, "Full", noSpots);

        Student s1 = new Student(1, "A", "a@x.com");
        Student s2 = new Student(2, "B", "b@x.com");
        List<Student> students = List.of(s1, s2);

        AssignmentsLogger log = assigner.assign(students, List.of(fullWorkshop));

        assertThat(log.getAssignments()).isEmpty();
        assertThat(log.getErrors()).hasSize(2);
    }

    @Test
    void testAssign_multipleWorkshopsAssignmentsDistributed() {
        AutoAssigner assigner = new AutoAssigner();

        ZonedDateTime d1 = ZonedDateTime.now().plusDays(1);
        ZonedDateTime d2 = ZonedDateTime.now().plusDays(2);

        Map<ZonedDateTime, Integer> map1 = Map.of(d1, 1);
        Map<ZonedDateTime, Integer> map2 = Map.of(d2, 1);

        Workshop w1 = new Workshop(1, "Java", new HashMap<>(map1));
        Workshop w2 = new Workshop(2, "Python", new HashMap<>(map2));

        List<Student> students = List.of(
                new Student(1, "Alpha", "a@x.com"),
                new Student(2, "Beta", "b@x.com"),
                new Student(3, "Gamma", "c@x.com")
        );

        AssignmentsLogger log = assigner.assign(students, List.of(w1, w2));

        // Deux étudiants assignés (1 par workshop), un échoue
        assertThat(log.getAssignments()).hasSize(2);
        assertThat(log.getErrors()).hasSize(4);

        // Les deux workshops devraient être pleins
        assertThat(w1.getSpotsPerDate().get(d1)).isEqualTo(0);
        assertThat(w2.getSpotsPerDate().get(d2)).isEqualTo(0);
    }

}

