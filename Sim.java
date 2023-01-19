import java.io.*;
import java.util.*;

public class Sim {

  public static void run_sim(MBTA mbta, Log log) {
    long start = System.currentTimeMillis();
    long end = start + 65 * 1000;

    // make thread for each of the passenger
    for (Passenger passenger: mbta.journeys.keySet()) {
      Thread t = new Thread() {
        public void run() {
          while (!mbta.p_arrived(passenger)) {
            if (System.currentTimeMillis() > end) {
              return;
            }
            Station initial = mbta.journeys.get(passenger).get(passenger.stationId);
            if (initial.whichTrain != null && passenger.onTrain == null) {
              // next journey station is in the train's line
              if (mbta.lines.get(initial.whichTrain).contains(mbta.journeys.get(passenger).get(passenger.stationId+1))) {
                if (passenger.currStation.equals(initial) && mbta.currStation.get(initial.whichTrain).equals(initial)) {
                  log.passenger_boards(passenger, initial.whichTrain, initial);
                  mbta.board_p(passenger, initial, initial.whichTrain);
                }
              }
            }
            if (passenger.stationId < mbta.journeys.get(passenger).size()-1) {
              List<Station> l_station = mbta.lines.get(passenger.onTrain);
              if (initial.whichTrain != null && passenger.onTrain != null &&
                      !l_station.contains(mbta.journeys.get(passenger).get(passenger.stationId + 1))) {
                if (passenger.currStation.equals(initial) && mbta.currStation.get(initial.whichTrain).equals(initial)) {
                  log.passenger_deboards(passenger, initial.whichTrain, initial);
                  mbta.deboard_p(passenger, initial, initial.whichTrain);
                }
              }
            }
          }
          int last_id = mbta.journeys.get(passenger).size();
          Station last = mbta.journeys.get(passenger).get(last_id-1);
          log.passenger_deboards(passenger, passenger.onTrain, last);
          mbta.deboard_p(passenger, last, passenger.onTrain);

        }
      };
      t.start();
    }

    List<Thread> trainThreads = new ArrayList<>();

    // make thread for each of the train
    for (Train train: mbta.lines.keySet()) {
      Thread t = new Thread() {
        public void run() {
          while (!mbta.isDone()) {
            if (System.currentTimeMillis() > end) {
              return;
            }
            try {
              Thread.sleep(50);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
            Station curr = mbta.currStation.get(train);
            Station next = mbta.nextStation(curr, train);


            // wait until next doesn't have train
//            synchronized (next) {
//              while (next.whichTrain != null) {
//                try {
//                  next.wait();
//                } catch (InterruptedException e) {
//                  e.printStackTrace();
//                }
//              }
//            }
//
//            log.train_moves(next.whichTrain, next, mbta.nextStation(next, next.whichTrain));
//            mbta.moveTrain(next, mbta.nextStation(next, next.whichTrain), next.whichTrain);
//
//
//            synchronized (curr) {
//              curr.notifyAll();
//            }
            // move current station to next station
            if (next.whichTrain == null) {
              log.train_moves(train, curr, next);
              mbta.moveTrain(curr, next, train);
            }
          }
        }
      };
      t.start();
      trainThreads.add(t);
    }

    for (Thread t: trainThreads) {
      try {
        t.join();
      } catch (InterruptedException v) {
        v.printStackTrace();
      }
    }
  }

  public static void main(String[] args) throws Exception {
    if (args.length != 1) {
      System.out.println("usage: ./sim <config file>");
      System.exit(1);
    }

    MBTA mbta = new MBTA();
    mbta.loadConfig(args[0]);

    Log log = new Log();

    run_sim(mbta, log);

    String s = new LogJson(log).toJson();
    PrintWriter out = new PrintWriter("log1.json");
    out.print(s);
    out.close();

    mbta.reset();
    mbta.loadConfig(args[0]);
    Verify.verify(mbta, log);
  }
}
