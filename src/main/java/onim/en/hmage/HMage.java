package onim.en.hmage;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ChestScreen;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLConfig;
import onim.en.hmage.gui.screen.AnniServersScreen;
import onim.en.hmage.gui.screen.HMageSettingScreen;
import onim.en.hmage.module.IDrawable;
import onim.en.hmage.module.ModuleManager;
import onim.en.hmage.module.drawable.EquipmentInfoGroup;
import onim.en.hmage.module.drawable.StatusEffect;
import onim.en.hmage.module.drawable.label.CpsCounter;
import onim.en.hmage.module.normal.CustomGuiBackground;
import onim.en.hmage.module.normal.FixedFOV;
import onim.en.hmage.module.normal.RecipeBookRemover;
import onim.en.hmage.observer.AnniChatReciveExecutor;
import onim.en.hmage.observer.AnniObserver;
import onim.en.hmage.observer.AnniObserverMap;
import onim.en.hmage.observer.data.AnniGameData;
import onim.en.hmage.observer.data.AnniPlayerData;
import onim.en.hmage.util.ScreenUtils;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(HMage.MOD_ID)
public class HMage {

  public final static String MOD_ID = "hmage";
  public static final long startMilliTime = System.currentTimeMillis();

  private static HMage instance = null;

  // Directly reference a log4j logger.
  public static final Logger LOGGER = LogManager.getLogger();
  public static Path config;

  public AnniObserverMap anniObserverMap;
  public ModuleManager moduleManager;

  private ITextComponent latestChat = null;

  public HMage() {
    //FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    MinecraftForge.EVENT_BUS.register(this);

    LOGGER.info("Starting initialize");
    this.init();
    LOGGER.info("Finished initializing");

  }

  private void init() {
    instance = this;

    config = Paths.get(FMLConfig.defaultConfigPath()).resolve("./hmage.properties");

    HMageSettings.init(config);

    this.anniObserverMap = AnniObserverMap.getInstance();

    this.moduleManager = new ModuleManager();

    this.moduleManager.register(new FixedFOV(this.moduleManager));
    this.moduleManager.register(new CustomGuiBackground(this.moduleManager));
    this.moduleManager.register(new RecipeBookRemover(this.moduleManager));

    this.moduleManager.register(new StatusEffect(this.moduleManager));

    this.moduleManager.register(new EquipmentInfoGroup(this.moduleManager));

    this.moduleManager.register(new CpsCounter(this.moduleManager));

    HMageSettings.load();

    //HMageが使用するキーバインドをMinecraftに登録
    ClientRegistry.registerKeyBinding(HMageSettings.openSettingsKey);
    ClientRegistry.registerKeyBinding(HMageSettings.showAnniRankingTab);

    //Anniのチャットを処理するスレッドを開始
    AnniChatReciveExecutor.startThread();
  }

  @SubscribeEvent
  public void onInputUpdate(KeyInputEvent event) {
    Minecraft mc = Minecraft.getInstance();

    if (HMageSettings.openSettingsKey.isPressed()) {
      if (mc.currentScreen == null) {
        mc.enqueue(() -> mc.displayGuiScreen(new HMageSettingScreen()));
      }
    }

  }

  @SubscribeEvent
  public void onRenderGameOverlay(final RenderGameOverlayEvent event) {
    if (!HMageSettings.enabled)
      return;

    Minecraft mc = Minecraft.getInstance();

    if (mc.gameSettings.showDebugInfo)
      return;

    if (event.getType() == ElementType.TEXT) {
      this.renderHMageDebugInfo();

      if (HMageSettings.showAnniRankingTab.isKeyDown()) {
        renderAnniRanking(event.getWindow().getScaledWidth(), event.getWindow().getScaledHeight());
      } else if (mc.currentScreen == null) {
        float partialTicks = event.getPartialTicks();
        for (IDrawable drawable : this.moduleManager.getEnabledDrawableModules()) {
          drawable.draw(mc, partialTicks);
        }
      }
    }
  }

  @SubscribeEvent
  public void onInitGuiEvent(InitGuiEvent event) {

    if (!(event.getGui() instanceof ChestScreen)) { return; }
    ChestScreen gui = (ChestScreen) event.getGui();

    if (gui instanceof AnniServersScreen) { return; }

    ChestContainer chestInventory = (ChestContainer) ScreenUtils.getContainer(gui);

    if (chestInventory == null) { return; }

    ITextComponent chestDisplayName = gui.getTitle();
    if (chestDisplayName.getFormattedText().startsWith(ScreenUtils.SELECT_SERVER)) {

      StringTextComponent title = new StringTextComponent(ChatFormatting.BLUE + "|Server Selector|");

      Minecraft mc = Minecraft.getInstance();
      mc.displayGuiScreen(
          new AnniServersScreen((ChestContainer) chestInventory, mc.player.inventory, title));
    }
  }

  @SubscribeEvent
  public void onClientTick(ClientTickEvent event) {
    if (HMageSettings.enabled) {
      AnniObserver anniObserver = this.anniObserverMap.getAnniObserver();
      if (anniObserver != null) {
        anniObserver.onClientTick(event);
      }
    }
  }

  @SubscribeEvent
  public void onReciveChat(ClientChatReceivedEvent event) {
    if (HMageSettings.enabled) {
      AnniChatReciveExecutor.onReceiveChat(event.getMessage(), event.getType());
    }
    if (event.getType() == ChatType.GAME_INFO) {
      this.latestChat = event.getMessage();
    }
  }

  @SubscribeEvent
  public void onBossOverlayRender(RenderGameOverlayEvent.BossInfo event) {
    if (HMageSettings.enabled) {
      AnniObserver anniObserver = this.anniObserverMap.getAnniObserver();
      if (anniObserver != null) {
        anniObserver.onBossOverlayRender(event);
      }
    }
  }

  private void renderAnniRanking(int width, int height) {
    AnniObserver anniObserver = this.anniObserverMap.getAnniObserver();

    if (anniObserver == null)
      return;

    Minecraft mc = Minecraft.getInstance();

    AnniGameData gameInfo = anniObserver.getGameInfo();

    List<AnniPlayerData> killRanking = gameInfo.getTotalKillRanking(10);
    List<AnniPlayerData> nexusRanking = gameInfo.getNexusRanking(10);

    RenderSystem.disableLighting();

    ScreenUtils.drawRankingLeft("Kills in this Game", killRanking, mc.fontRenderer, 4, 4,
        d -> String.format("%dK", d.getTotalKillCount()));

    ScreenUtils.drawRankingRight("Nexus damage in this Game", nexusRanking, mc.fontRenderer, 4, width - 4,
        d -> String.format("%dD", d.getNexusDamageCount()));
  }

  private void renderHMageDebugInfo() {
    Minecraft mc = Minecraft.getInstance();
    AnniObserver observer = this.anniObserverMap.getAnniObserver();

    if (observer != null && observer.bossInfoMap != null) {
      List<String> list = observer.bossInfoMap.values().stream().map(b -> b.getName().getFormattedText())
          .collect(Collectors.toList());
      mc.fontRenderer.drawString(String.join(",", list), 4, 2, 0xffffff);
    }

    if (mc.world != null) {
      Scoreboard scoreboard = mc.world.getScoreboard();

      if (scoreboard != null) {
        List<String> list = scoreboard.getTeams().stream().map(t -> t.getDisplayName().getFormattedText())
            .collect(Collectors.toList());
        mc.fontRenderer.drawString(String.join(",", list), 4, 12,
            0xffffff);

        ScoreObjective objective = scoreboard.getObjectiveInDisplaySlot(1);
        if (objective != null) {
          mc.fontRenderer.drawString(scoreboard.getObjectiveInDisplaySlot(1).getDisplayName().getFormattedText(), 4, 22,
              0xffffff);
        }
      }
    }

    if (this.latestChat != null) {
      String text = this.latestChat.getFormattedText();
      mc.fontRenderer.drawString(text.replace('\u00a7', '%'), 4, 32, 0xffffff);
    }
  }

  public static HMage getInstance() {
    return instance;
  }
}
