package onim.en.hmage.observer;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class AnniObserverMap {

  @Nullable
  private static AnniObserverMap instance = null;
  @Nullable
  private String playingServerName;
  private final Map<String, AnniObserver> anniObserverMap;
  private static File historyDataDir;

  public static AnniObserverMap getInstance() {
    //    if (instance == null) {
    //      historyDataDir = new File(HMage.modConfigurationDirectory, "anni");
    //      historyDataDir.mkdir();
    //      instance = new AnniObserverMap();
    //    }
    return instance;
  }

  private AnniObserverMap() {
    anniObserverMap = new HashMap<>();
  }
}
