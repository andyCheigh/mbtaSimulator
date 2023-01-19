import java.util.*;

public class Train extends Entity {
  private Train(String name) { super(name); }

  private static Map<String, Train> trainMap = new HashMap();

  public boolean reverse = false;

  public static Train make(String name) {

    if (trainMap.containsKey(name)) {
      return trainMap.get(name);
    }

    Train newTrain = new Train(name);
    trainMap.put(name, newTrain);

    return newTrain;
  }
}
