import java.io.BufferedReader;
import java.util.*;
import java.io.*;
import com.google.gson.*;

public class MBTA {

  // Creates an initially empty simulation
  public MBTA() { }

  public Map<Train, List<Station>> lines = new HashMap<>();
  public Map<Passenger, List<Station>> journeys = new HashMap<>();
  public Map<Train, Station> currStation = new HashMap<>();

  // Adds a new transit line with given name and stations
  public void addLine(String name, List<String> stations) {
    Train t = Train.make(name);
    if (!lines.containsKey(t)) {
      List<Station> sList = new ArrayList<>();
      for (String s : stations) {
        Station st = Station.make(s);
        sList.add(st);
      }

      if (!stations.isEmpty()) {
        Station s = Station.make(stations.get(0));
        s.whichTrain = t;
        currStation.put(t, s);
      }

      lines.put(t, sList);
    }
  }

  // Adds a new planned journey to the simulation
  public void addJourney(String name, List<String> stations) {
    Passenger p = Passenger.make(name);
    if (!journeys.containsKey(p)) {
      List<Station> sList = new ArrayList<>();
      for (String s : stations) {
        Station st = Station.make(s);
        sList.add(st);
      }

      if (!stations.isEmpty()) {
        p.currStation = sList.get(0);
      }

      journeys.put(p, sList);
    }
  }

  // Return normally if initial simulation conditions are satisfied, otherwise
  // raises an exception
  public void checkStart() {
    for (Map.Entry<Train, Station> entry: currStation.entrySet()) {
      Station s = lines.get(entry.getKey()).get(0);
      if (!s.equals(entry.getValue())) {
        throw new NullPointerException();
      }
    }

    for (Map.Entry<Passenger, List<Station>> entry: journeys.entrySet()) {
      Station s = entry.getKey().currStation;
      if (!s.equals(entry.getValue().get(0))) {
        throw new NullPointerException();
      }
    }
  }

  // Return normally if final simulation conditions are satisfied, otherwise
  // raises an exception
  public void checkEnd() {
    if (!isDone()) {
      throw new NullPointerException();
    }
  }

  // reset to an empty simulation
  public void reset() {
    for (List<Station> s_list : lines.values()) {
      for (Station s: s_list) {
        s.whichTrain = null;
      }
    }

    for (Train t: lines.keySet()) {
      t.reverse = false;
    }

    for (Passenger p: journeys.keySet()) {
      p.onTrain = null;
      p.currStation = null;
      p.stationId = 0;
    }

    lines.clear();
    journeys.clear();
    currStation.clear();
  }

  // adds simulation configuration from a file
  public void loadConfig(String filename) {
    try {
      BufferedReader br = new BufferedReader(new FileReader(filename));
      Gson gson = new Gson();
      Configuration c = gson.fromJson(br, Configuration.class);
      for (Map.Entry<String, List<String>> entry: c.lines.entrySet()) {
        addLine(entry.getKey(), entry.getValue());
      }

      for (Map.Entry<String, List<String>> entry: c.trips.entrySet()) {
        addJourney(entry.getKey(), entry.getValue());
      }

    } catch (FileNotFoundException e) {
      throw new NullPointerException();
    }
  }

  public boolean isDone() {
    for (Passenger p: journeys.keySet()) {
      if (!p_arrived(p) || journeys.get(p).size() <= 1) {
        return false;
      }
    }
    return true;
  }

  public void moveTrain(Station s1, Station s2, Train t) {
    // check that no train in destination
//    if (s2.whichTrain != null) {
//      throw new RuntimeException();
//    }

    // check current station is at s1
    if (!currStation.containsKey(t)) {
      throw new NullPointerException();
    }

    if (currStation.get(t).equals(s1)) {
      int index = lines.get(t).indexOf(s1);
      int size = lines.get(t).size();

      if (index > 0 && index < size-1) {
        if (!t.reverse && lines.get(t).get(index+1).equals(s2)) {
          currStation.put(t, s2);
        } else if (t.reverse && lines.get(t).get(index-1).equals(s2)) {
          currStation.put(t, s2);
        } else {
          throw new NullPointerException();
        }
      } else if (index == 0) {
        if (lines.get(t).get(index+1).equals(s2)) {
          currStation.put(t, s2);
          t.reverse = false;
        } else {
          throw new NullPointerException();
        }
      } else if (index == size-1) {
        if (lines.get(t).get(index-1).equals(s2)) {
          currStation.put(t, s2);
          t.reverse = true;
        } else {
          throw new NullPointerException();
        }
      } else {
        throw new NullPointerException();
      }
    } else {
      throw new NullPointerException();
    }

    // change every passengers' current station
    for (Passenger p : journeys.keySet()) {
      if (p.currStation.equals(s1) && p.onTrain != null && p.onTrain.equals(t)) {
        p.currStation = s2;
        if (p.stationId < journeys.get(p).size()-1 && p.currStation.equals(journeys.get(p).get(p.stationId+1))) {
          p.stationId += 1;
        }
      }
    }

    s1.whichTrain = null;
    s2.whichTrain = t;
  }

  public Station nextStation(Station curr, Train train) {
    int curr_id = lines.get(train).indexOf(curr);
    Station next = null;
    if (!train.reverse) {
      if (curr_id == lines.get(train).size()-1) {
        next = lines.get(train).get(curr_id-1);
      } else {
        next = lines.get(train).get(curr_id+1);
      }
    } else {
      if (curr_id == 0) {
        next = lines.get(train).get(curr_id+1);
      } else {
        next = lines.get(train).get(curr_id-1);
      }
    }

    return next;
  }

  public void board_p (Passenger p, Station s, Train t) {
    // p has arrived at destination
    if (!journeys.containsKey(p)) {
      throw new NullPointerException();
    }
    int journey_len = journeys.get(p).size();
    if (journeys.get(p).get(journey_len-1) == s) {
      System.out.println("1");
      throw new RuntimeException();
    }

    // Passenger is not on train
//    if (p.onTrain != null) {
//      System.out.println("2");
//      throw new RuntimeException();
//    }

    // Curr station == Passenger curr station
    if (!currStation.containsKey(t) || p.currStation == null) {
      throw new NullPointerException();
    }

    if (!currStation.get(t).equals(s) || !p.currStation.equals(s)) {
      throw new NullPointerException();
    }

    p.onTrain = t;
  }

  public void deboard_p(Passenger p, Station s, Train t) {
    // p cannot deboard at start
    if (!journeys.containsKey(p)) {
      throw new NullPointerException();
    }
    if (journeys.get(p).get(p.stationId) != s) {
      throw new RuntimeException();
    }

    // Passenger is not on train
    if (p.onTrain == null) {
      System.out.println("3");
      throw new RuntimeException();
    }

    // Curr station == Passenger curr station
    if (!currStation.containsKey(t) || p.currStation == null) {
      throw new NullPointerException();
    }

    if (!currStation.get(t).equals(s) || !p.currStation.equals(s)) {
      throw new NullPointerException();
    }

    p.onTrain = null;
  }

  public boolean p_arrived(Passenger p) {
    int last_id = journeys.get(p).size()-1;
    return p.currStation.equals(journeys.get(p).get(last_id));
  }
}
