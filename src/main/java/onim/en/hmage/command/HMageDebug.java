package onim.en.hmage.command;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import onim.en.hmage.HMage;
import onim.en.hmage.observer.AnniObserver;
import onim.en.hmage.observer.data.AnniGameData;

public class HMageDebug {

  public static void register(CommandDispatcher<CommandSource> dispatcher) {
    dispatcher.register(Commands.literal("dhmage").executes(c -> {
      return displayObserverInformation(c.getSource());
    }));
  }

  private static int displayObserverInformation(CommandSource source) {
    AnniObserver observer = HMage.getInstance().anniObserverMap.getAnniObserver();

    if (observer == null) {
      source.sendFeedback(new StringTextComponent("Observer is null"), true);
      return 0;
    }

    AnniGameData gameInfo = observer.getGameInfo();

    source.sendFeedback(new StringTextComponent(gameInfo.getMapName()), true);
    source.sendFeedback(new StringTextComponent(gameInfo.getGamePhase().getText()), true);
    source.sendFeedback(new StringTextComponent(gameInfo.getMeTeamColor().getColoredName()), true);
    source.sendFeedback(new StringTextComponent(String.valueOf(gameInfo.getMeleeKillCount())), true);
    source.sendFeedback(new StringTextComponent(String.valueOf(gameInfo.getNexusAttackCount())), true);

    return 1;
  }
}
