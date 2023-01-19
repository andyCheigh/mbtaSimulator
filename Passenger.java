import java.util.*;

public class Passenger extends Entity {
  private Passenger(String name) { super(name); }
  private static Map<String, Passenger> passengerMap = new HashMap();
  public Train onTrain = null;
  public Station currStation = null;
  public int stationId = 0;

  public static Passenger make(String name) {
    if (passengerMap.containsKey(name)) {
      return passengerMap.get(name);
    }

    Passenger newPassenger = new Passenger(name);
    passengerMap.put(name, newPassenger);
    return newPassenger;
  }
}
