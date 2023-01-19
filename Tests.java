import static org.junit.Assert.*;
import org.junit.*;
import java.util.*;

public class Tests {
  @Test public void testPass() {
    assertTrue("true should be true", true);
  }
  @Test public void testTrain() {
    MBTA m = new MBTA();

    Passenger p1 = Passenger.make("Andy");
    Passenger p2 = Passenger.make("Cheigh");

    List<String> red = List.of("Davis", "Harvard", "Kendall", "Park", "Downtown Crossing",
            "South Station", "Broadway", "Andrew", "JFK");
    m.addLine("red", red);
    List<String> orange = List.of("Ruggles", "Back Bay", "Tufts Medical Center", "Chinatown",
            "Downtown Crossing", "State", "North Station",  "Sullivan");
    m.addLine("orange", orange);

    List<String> andyJourney = List.of("Davis", "Harvard");
    List<String> cheighJourney = List.of("Ruggles", "Back Bay");
    m.addJourney("Andy", andyJourney);
    m.addJourney("Cheigh", cheighJourney);

    Train rline = Train.make("red");
    Train oline = Train.make("orange");

    Station davis = Station.make("Davis");
    Station harvard = Station.make("Harvard");
    Station ruggles = Station.make("Ruggles");
    Station bb = Station.make("Back Bay");

    Log log = new Log();
    log.passenger_boards(p1, rline, davis);
    log.train_moves(rline, davis, harvard);
    log.passenger_boards(p2, oline, ruggles);
    log.train_moves(oline, ruggles, bb);
    log.passenger_deboards(p1, rline, harvard);
    log.passenger_deboards(p2, oline, bb);
    Verify.verify(m, log);
  }
}
