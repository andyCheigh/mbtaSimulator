import java.util.*;

public class Station extends Entity {
  private Station(String name) { super(name); }

  private static Map<String, Station> stationMap = new HashMap();

  public Train whichTrain = null;

  public static Station make(String name) {
    if (stationMap.containsKey(name)) {
      return stationMap.get(name);
    }

    Station newStation = new Station(name);
    stationMap.put(name, newStation);
    return newStation;
  }
}
