package onim.en.hmage;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.multiplayer.ServerData;
import onim.en.hmage.observer.data.AnniGameData;

public class HMageDiscordHandler {

  public static final HMageDiscordHandler INSTANCE = new HMageDiscordHandler();
  private static final String DISCORD_APPLICATION_ID = "724639729651417279";

  private DiscordRPC client;
  private DiscordEventHandlers handlers;

  private HMageDiscordHandler() {
    client = DiscordRPC.INSTANCE;
    handlers = new DiscordEventHandlers();
    client.Discord_Initialize(DISCORD_APPLICATION_ID, handlers, true, "");
  }

  public void clearPresence() {
    client.Discord_ClearPresence();
  }

  public void updatePresenceWithGameInfo(AnniGameData gameInfo) {
    DiscordRichPresence presence = new DiscordRichPresence();

    String name = gameInfo.getMapName();
    if (name == null) {
      name = HMage.getInstance().anniObserverMap.getPlayingServerName().replaceAll("§.", "");
    }

    presence.details = String.format("%s - %s", name, gameInfo.getGamePhase().getText());
    presence.state = gameInfo.getMeTeamColor().getColorName();

    client.Discord_UpdatePresence(presence);
  }

  public void updatePresenceWithNormal() {
    DiscordRichPresence presence = new DiscordRichPresence();

    String state = "-";
    String details = "-";

    ServerData server = Minecraft.getInstance().getCurrentServerData();
    if (server != null) {
      details = server.serverIP.toLowerCase();
    }

    ClientPlayerEntity player = Minecraft.getInstance().player;

    if (player != null) {
      state = player.getName().getString();
    }

    presence.startTimestamp = HMage.startMilliTime / 1000;
    presence.details = details;
    presence.state = state;

    client.Discord_UpdatePresence(presence);
  }
}
