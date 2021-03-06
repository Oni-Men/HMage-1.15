package onim.en.hmage.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.Container;
import net.minecraftforge.coremod.api.ASMAPI;
import onim.en.hmage.observer.data.AnniPlayerData;

public class ScreenUtils {

  private static final String GRAY = ChatFormatting.GRAY.toString();
  private static final String BLACK = ChatFormatting.BLACK.toString();
  public static final String SELECT_SERVER = MessageFormat.format("{0}|{1}Select Server{0}|", GRAY, BLACK);

  public static <T extends Container> Container getContainer(ContainerScreen<T> chest) {

    String mapField = ASMAPI.mapField("field_147002_h");

    for (Field field : chest.getClass().getSuperclass().getDeclaredFields()) {
      try {

        int mod = field.getModifiers();

        if (!Modifier.isProtected(mod) || !Modifier.isFinal(mod)) {
          continue;
        }

        if (!field.getName().equals(mapField)) {
          continue;
        }

        field.setAccessible(true);
        Container container = (Container) field.get(chest);

        return container;
      } catch (IllegalArgumentException | IllegalAccessException e) {
        e.printStackTrace();
      }
    }

    return null;
  }

  public static int drawRankingLeft(String title, List<AnniPlayerData> ranking, FontRenderer fr, int top, int left,  Function<AnniPlayerData, String> valueGetter) {
    int color = 0xFFFFFF;
    fr.drawStringWithShadow(title, left, top, color);
    left += 8;
    top += 12;

    if (ranking.isEmpty()) {
      fr.drawStringWithShadow("No Result", left, top, color);
      top += 10;
      return top;
    }

    int offset = getMaxPlayerNameWidth(fr, ranking);

    int rankerNumberWidth = fr.getStringWidth(ranking.size() + ".");

    AnniPlayerData data;
    String rankerNumber, rankerName, rankerValue;
    for (int i = 0, len = ranking.size(); i < len; i++) {
      data = ranking.get(i);
      rankerNumber = (i + 1) + ".";
      rankerName = String.format("%s%s",data.getTeamColor().getColorCode(), data.getPlayerName());
      rankerValue = valueGetter.apply(data);
      fr.drawStringWithShadow(rankerNumber, left, top, color);
      fr.drawStringWithShadow(rankerName, left + rankerNumberWidth, top, color);
      fr.drawStringWithShadow(rankerValue, left + rankerNumberWidth + offset + 8, top, color);
      top += 10;
    }

    return top;
  }

  private static void drawStringRight(FontRenderer f, String text, int x, int y, int color) {
    f.drawString(text, x - f.getStringWidth(text), y, color);
  }

  public static int drawRankingRight(String title, List<AnniPlayerData> ranking, FontRenderer fr, int top, int right,
      Function<AnniPlayerData, String> valueGetter) {
    int color = 0xFFFFFF;
    drawStringRight(fr, title, right, top, color);
    top += 12;
    right -= 8;

    if (ranking.isEmpty()) {
      drawStringRight(fr, "No Result", right, top, color);
      top += 10;
      return top;
    }

    int offset = getMaxPlayerNameWidth(fr, ranking);

    int rankerValueOffset = ranking.stream()
        .map(d -> fr.getStringWidth(valueGetter.apply(d)))
        .sorted((a, b) -> b - a)
        .findAny().orElse(0);

    AnniPlayerData data;
    String rankerNumber, rankerName, rankerValue;
    for (int i = 0, len = ranking.size(); i < len; i++) {
      data = ranking.get(i);
      rankerNumber = (i + 1) + ".";
      rankerName = String.format(" %s%s", data.getTeamColor().getColorCode(), data.getPlayerName());
      rankerValue = valueGetter.apply(data);
      drawStringRight(fr, rankerValue, right, top, color);
      fr.drawStringWithShadow(rankerName, right - offset - rankerValueOffset - 8, top, color);
      drawStringRight(fr, rankerNumber, right - rankerValueOffset - offset - 8,
          top,
          color);
      top += 10;
    }

    return top;
  }

  public static int drawRanking(String title, List<AnniPlayerData> ranking, FontRenderer fr, int top,
      int left, Function<AnniPlayerData, String> value) {
    return drawRankingLeft(title, ranking, fr, top, left, value);
  }

  public static int getMaxPlayerNameWidth(FontRenderer font, Collection<AnniPlayerData> datas) {
    return datas.stream()
        .map(d -> font.getStringWidth(d.getPlayerName()))
        .sorted((a, b) -> b - a)
        .findAny()
        .orElse(0);
    //    int width = 0;
    //    for (AnniPlayerData data : datas) {
    //      int nameWidth = font.getStringWidth(data.getPlayerName());
    //      if (nameWidth > width) {
    //        width = nameWidth;
    //      }
    //    }
    //    return width;
  }
}
