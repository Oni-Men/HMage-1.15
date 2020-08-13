package onim.en.hmage.observer;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nonnull;

import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ClientBossInfo;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.BossInfo;
import net.minecraft.world.BossInfo.Color;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.TickEvent.Phase;
import onim.en.hmage.HMage;
import onim.en.hmage.HMageDiscordHandler;
import onim.en.hmage.observer.data.AnniGameData;
import onim.en.hmage.observer.data.AnniTeamColor;
import onim.en.hmage.observer.data.GamePhase;
import onim.en.hmage.util.ShotbowUtils;

public class AnniObserver {
  private static final String MAP_PREFIX = ChatFormatting.GOLD.toString() + ChatFormatting.BOLD.toString() + "Map: ";

  private static final String VOTING_TEXT = ChatFormatting.GREEN + "/vote [map name] to vote";

  private Minecraft mc;
  private Map<UUID, BossInfo> bossInfoMap = null;

  private int tickLeftWhileNoAnniScoreboard = 0;

  @Nonnull
  private final AnniGameData gameInfo;

  public AnniObserver(Minecraft mcIn) {
    this.mc = mcIn;
    this.gameInfo = new AnniGameData();
  }

  public AnniGameData getGameInfo() {
    return this.gameInfo;
  }

  public void onJoinGame() {
    this.tickLeftWhileNoAnniScoreboard = 0;
    HMageDiscordHandler.INSTANCE.updatePresenceWithGameInfo(gameInfo);
  }

  public void onLeaveGame() {
    this.tickLeftWhileNoAnniScoreboard = 0;

    if (gameInfo.getGamePhase().getValue() > 0 && gameInfo.getMeTeamColor() != AnniTeamColor.NO_JOIN) {
      //gameInfoを保存
      File historyDataDir = AnniObserverMap.getHistoryDataDir();
      Gson gson = new Gson();
      String json = gson.toJson(gameInfo);
      try {
        Files.write(json, new File(historyDataDir, gameInfo.getGameTimestamp() + ".txt"), StandardCharsets.UTF_8);
      } catch (IOException e) {
        e.printStackTrace();
        //握りつぶす
      }

      //昔の情報を削除
      File[] listFiles = historyDataDir.listFiles(f -> f.getName().endsWith(".txt"));
      Arrays.stream(listFiles).sorted((f1, f2) -> f2.compareTo(f1)).skip(20).forEach(f -> f.delete());

    }
    HMageDiscordHandler.INSTANCE.clearPresence();
  }

  public void updatePresence() {
    HMageDiscordHandler.INSTANCE.updatePresenceWithGameInfo(gameInfo);
  }

  @OnlyIn(Dist.CLIENT)
  public void onClientTick(ClientTickEvent event) {
    if (event.phase != Phase.END) { return; }

    //ゲーム中でないならリセット
    if (mc.ingameGUI == null) {
      HMage.getInstance().anniObserverMap.unsetAnniObserver();
      return;
    }
    if (mc.world == null) {
      tickLeftWhileNoAnniScoreboard++;
      if (tickLeftWhileNoAnniScoreboard > 100) {
        HMage.getInstance().anniObserverMap.unsetAnniObserver();
      }
      return;
    }

    Scoreboard scoreboard = mc.world.getScoreboard();
    //Anniをプレイ中かどうか確認
    if (scoreboard != null && isAnniScoreboard(scoreboard)) {
      tickLeftWhileNoAnniScoreboard = 0;

      AnniTeamColor previousTeamColor = gameInfo.getMeTeamColor();
      AnniTeamColor nextTeamColor = AnniTeamColor.NO_JOIN;

      ScorePlayerTeam team = scoreboard.getPlayersTeam(gameInfo.getMePlayerData().getPlayerName());

      if (team != null) {
        nextTeamColor = AnniTeamColor.findByTeamName(team.getDisplayName().getString().replaceFirst("§.", ""));
      }

      if (previousTeamColor != nextTeamColor) {
        gameInfo.getMePlayerData().setTeamColor(nextTeamColor);
        updatePresence();
      }

    } else {
      tickLeftWhileNoAnniScoreboard++;
      if (tickLeftWhileNoAnniScoreboard > 100) {
        HMage.getInstance().anniObserverMap.unsetAnniObserver();
        return;
      }
    }

    GamePhase previousPhase = this.gameInfo.getGamePhase(), nextPhase = GamePhase.UNKNOWN;
    //フェーズを取得
    if (bossInfoMap != null) {
      for (BossInfo bossInfo : bossInfoMap.values()) {
        //フェーズを表示するボスバーは青色なので
        if (bossInfo.getColor() == Color.BLUE) {
          String name = bossInfo.getName().getUnformattedComponentText();
          nextPhase = GamePhase.getGamePhasebyText(name);
          break;
        }
      }
    }
    if (previousPhase == null || previousPhase.getValue() != nextPhase.getValue()) {
      this.gameInfo.setGamePhase(nextPhase);
      updatePresence();
    }

    //Mapを取得
    if (gameInfo.getMapName() == null && scoreboard != null) {
      String previousMapName = gameInfo.getMapName();
      String nextMapName = getMapFromScoreboard(scoreboard);
      if (previousMapName == null || !previousMapName.equals(nextMapName)) {
        gameInfo.setMapName(nextMapName);
        updatePresence();
      }
    }
  }

  public void onRecieveChat(ClientChatReceivedEvent event) {

    if (!ShotbowUtils.isShotbow(mc))
      return;

    ITextComponent message = event.getMessage();

    if (message.getUnformattedComponentText().isEmpty())
      return;

    //チャットを元に処理を実行
    AnniChatReciveExecutor.onReceiveChat(message, event.getType());
  }

  private boolean isAnniScoreboard(Scoreboard scoreboard) {
    ScoreObjective scoreobjective = scoreboard.getObjectiveInDisplaySlot(1);

    if (scoreobjective == null) { return false; }
    String displayName = scoreobjective.getDisplayName().getString();

    //Voteの場合
    if (displayName.contentEquals(VOTING_TEXT)) { return true; }

    //試合中の場合
    if (displayName.contains(MAP_PREFIX)) { return true; }

    return false;
  }

  private String getMapFromScoreboard(Scoreboard scoreboard) {
    ScoreObjective scoreobjective = scoreboard.getObjectiveInDisplaySlot(1);

    if (scoreobjective == null) { return null; }
    String displayName = scoreobjective.getDisplayName().getString();

    if (displayName.equals(VOTING_TEXT)) {
      gameInfo.setGamePhase(GamePhase.STARTING);
      return null;
    }

    if (!displayName.contains(MAP_PREFIX)) { return null; }

    return displayName.replace(MAP_PREFIX, "");
  }

  public void onBossOverlayRender(RenderGameOverlayEvent.BossInfo event) {
    if (this.bossInfoMap == null) {
      this.bossInfoMap = Maps.newHashMap();
    }
    ClientBossInfo bossInfo = event.getBossInfo();
    this.bossInfoMap.put(bossInfo.getUniqueId(), bossInfo);
  }

  //  @SuppressWarnings("unchecked")
  //  private Map<UUID, BossInfo> getBossInfoMap(BossOverlayGui bossOverlay) {
  //    if (bossOverlay != null) {
  //
  //      Field[] declaredFields = bossOverlay.getClass().getDeclaredFields();
  //
  //      for (Field field : declaredFields) {
  //
  //        int modifiers = field.getModifiers();
  //
  //        if (!Modifier.isPrivate(modifiers) || !Modifier.isFinal(modifiers))
  //          continue;
  //
  //        if (!Map.class.isAssignableFrom(field.getType()))
  //          continue;
  //
  //        try {
  //          field.setAccessible(true);
  //          return (Map<UUID, BossInfo>) field.get(bossOverlay);
  //        } catch (IllegalArgumentException | IllegalAccessException e) {
  //          e.printStackTrace();
  //        }
  //      }
  //    }
  //    return null;
  //  }
}
