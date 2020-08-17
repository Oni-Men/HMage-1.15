package onim.en.hmage.observer;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.loading.FMLConfig;
import onim.en.hmage.HMage;
import onim.en.hmage.observer.data.GamePhase;

public class AnniObserverMap {

  @Nullable
  private static AnniObserverMap instance = null;
  @Nullable
  private String playingServerName;
  private final Map<String, AnniObserver> anniObserverMap;
  private static File historyDataDir;

  public static AnniObserverMap getInstance() {
    if (instance == null) {
      historyDataDir = new File(FMLConfig.defaultConfigPath(), "anni");
      historyDataDir.mkdir();
      instance = new AnniObserverMap();
    }
    return instance;
  }

  private AnniObserverMap() {
    anniObserverMap = new HashMap<>();
  }

  public void setAnniObserver(String serverName, GamePhase phase) {
    setAnniObserver(serverName, phase, false);
  }

  public void setAnniObserver(String serverName, GamePhase phase, boolean force) {
    playingServerName = serverName;

    boolean canPutNewObserver = false;

    if (anniObserverMap.containsKey(serverName)) {
      AnniObserver anniObserver = anniObserverMap.get(serverName);
      if (anniObserver != null) {
        if (anniObserver.getGameInfo().getGamePhase().getValue() > phase.getValue()) {
          HMage.LOGGER.info("detected game was changed, before phase:" + anniObserver.getGameInfo().getGamePhase()
              + ", next phase:" + phase);
          canPutNewObserver = true;
        } else {
          HMage.LOGGER.info("detect the game was changed, before phase:" + anniObserver.getGameInfo().getGamePhase()
              + ", next phase:" + phase);
        }
      }
    } else {
      canPutNewObserver = true;
    }

    if (force || canPutNewObserver) {
      HMage.LOGGER.info("New observer was created.");
      anniObserverMap.put(serverName, new AnniObserver(Minecraft.getInstance()));
    }

    getAnniObserver().onJoinGame();
    HMage.LOGGER.info("Observe annihilation game: " + playingServerName);
  }

  public void unsetAnniObserver() {
    HMage.LOGGER.info("Stop observing game: " + playingServerName);
    getAnniObserver().onLeaveGame();
    this.playingServerName = null;
  }

  @Nullable
  public AnniObserver getAnniObserver() {
    if (this.playingServerName == null)
      return null;
    return anniObserverMap.get(playingServerName);
  }

  public static File getHistoryDataDir() {
    return historyDataDir;
  }

  public String getPlayingServerName() {
    return playingServerName;
  }
}
