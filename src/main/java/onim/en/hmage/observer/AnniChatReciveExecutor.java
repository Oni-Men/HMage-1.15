package onim.en.hmage.observer;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import onim.en.hmage.HMage;
import onim.en.hmage.observer.data.AnniKillType;
import onim.en.hmage.observer.data.AnniTeamColor;
import onim.en.hmage.observer.data.ClassType;

public class AnniChatReciveExecutor {

  /** キルログのパターン */
  private static Pattern killChatPattern = Pattern
      .compile("§(.)(.+)§(.)\\((.+)\\)§7 (shot|killed) §(.)(.+)§(.)\\((.+)\\)§r.*");

  /** ネクサスダメージのパターン */
  private static Pattern nexusChatPattern = Pattern
      .compile("§(?<attackColor>.)(?<attacker>.+)§7 has damaged the §(?<damageColor>.)(.+) team's nexus!.*");
  private static Pattern nexusChatPattern2 = Pattern
      .compile("§(?<damageColor>.)(.+) team's§7 nexus is under attack by §(?<attackColor>.)(?<attacker>.+).*");

  /** 拠点落ちた時のパターン */
  private static String AA = "(§.▒§r){10} §r";
  private static Pattern destroyedTeam = Pattern.compile(AA + "§(?<color>.).+ team's§r");
  private static Pattern teamWins = Pattern.compile(AA + "§(?<color>.).+§r§7 team wins!§r");

  private static List<Pattern> nexusChatPatterns = Arrays.asList(nexusChatPattern, nexusChatPattern2);

  /** 職業変更のパターン */
  private static Pattern changeJobPattern = Pattern.compile("\\[Class\\] (?<job>.+) Selected");

  private static BlockingQueue<ChatReciveTask> blockingQueue = new LinkedBlockingDeque<>();

  private static ChatParseRunner target = null;

  /**
   * チャットを受け取ったときの処理。
   *
   * @param textComponent チャット
   */
  public static void onReceiveChat(ITextComponent textComponent, ChatType type) {
    blockingQueue.add(new ChatReciveTask(textComponent.getFormattedText(), type));
  }

  public synchronized static void startThread() {
    if (target != null) { return; }

    target = new ChatParseRunner();
    Thread thread = new Thread(target);
    thread.setDaemon(true);
    thread.start();
  }

  public static ChatParseRunner getTarget() {
    return target;
  }

  static public class ChatParseRunner implements Runnable {

    private long lastExecuteTime = -1;

    @Override
    public void run() {
      while (true) {
        try {
          lastExecuteTime = System.currentTimeMillis();

          ChatReciveTask task = blockingQueue.poll(200, TimeUnit.SECONDS);
          if (task == null) {
            continue;
          }
          String message = task.getMessage();

          AnniObserver anniObserver = HMage.getInstance().anniObserverMap.getAnniObserver();
          if (anniObserver == null) {
            continue;
          }

          if (task.getChatType() == ChatType.CHAT) {

            //パターンにマッチすることを確認
            Matcher matcher = killChatPattern.matcher(message);
            if (matcher.matches()) {
              //キル数を加算
              AnniTeamColor killerTeam = AnniTeamColor.findByColorCode(matcher.group(1));
              AnniTeamColor deadTeam = AnniTeamColor.findByColorCode(matcher.group(6));
              AnniKillType killType = matcher.group(5).equalsIgnoreCase("shot") ? AnniKillType.SHOT
                  : AnniKillType.MELEE;
              anniObserver.getGameInfo().addKillCount(matcher.group(2), killerTeam, matcher.group(7), deadTeam,
                  killType);
              continue;
            }

          } else if (task.getChatType() == ChatType.GAME_INFO) {

            Matcher matcher = null;
            //パターンに一致するかどうかを調べる
            for (Pattern pattern : nexusChatPatterns) {
              Matcher temp = pattern.matcher(message);
              if (temp.matches()) {
                matcher = temp;
                break;
              }
            }

            //一致しない場合
            if (matcher == null) {
              continue;
            }

            //ネクダメを加算
            AnniTeamColor attackerColor = AnniTeamColor.findByColorCode(matcher.group("attackColor"));
            AnniTeamColor damageColor = AnniTeamColor.findByColorCode(matcher.group("damageColor"));
            anniObserver.getGameInfo().addNexusDamageCount(matcher.group("attacker"),
                attackerColor, damageColor);
          } else if (task.getChatType() == ChatType.SYSTEM) {

            //パターンにマッチすることを確認
            Matcher matcher = changeJobPattern.matcher(message);
            if (matcher.matches()) {
              //変更後の職業を設定
              ClassType classType = ClassType.getClassTypeFromName(matcher.group("job"));
              if (classType != null) {
                anniObserver.getGameInfo().setClassType(classType);
              }
              continue;
            }

            matcher = destroyedTeam.matcher(message);

            if (matcher.matches()) {
              AnniTeamColor destroyedTeam = AnniTeamColor.findByColorCode(matcher.group("color"));
              anniObserver.getGameInfo().nexusDestroyed(destroyedTeam);
              continue;
            }

            matcher = teamWins.matcher(message);

            if (matcher.matches()) {
              AnniTeamColor winTeam = AnniTeamColor.findByColorCode(matcher.group("color"));
              anniObserver.getGameInfo().teamWins(winTeam);
              continue;
            }

          }

        } catch (Throwable e) {
          HMage.LOGGER.error("Error:", e);
        }
      }
    }

    public long getLastExecuteTime() {
      return lastExecuteTime;
    }
  }

  static class ChatReciveTask {

    private final String message;

    private final ChatType chatType;

    public ChatReciveTask(String message, ChatType chatType) {
      this.message = message;
      this.chatType = chatType;
    }

    public String getMessage() {
      return message;
    }

    public ChatType getChatType() {
      return chatType;
    }

  }
}
