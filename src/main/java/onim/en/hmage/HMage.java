package onim.en.hmage;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ChestScreen;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLConfig;
import onim.en.hmage.gui.AnniServersScreen;
import onim.en.hmage.module.IDrawable;
import onim.en.hmage.module.ModuleManager;
import onim.en.hmage.module.drawable.StatusEffect;
import onim.en.hmage.module.normal.CustomGuiBackground;
import onim.en.hmage.module.normal.FixedFOV;
import onim.en.hmage.observer.AnniChatReciveExecutor;
import onim.en.hmage.observer.AnniObserver;
import onim.en.hmage.observer.AnniObserverMap;
import onim.en.hmage.util.GuiScreenUtils;

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

  public HMage() {
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    MinecraftForge.EVENT_BUS.register(this);

    LOGGER.info("Starting initialize");
    this.init();
    LOGGER.info("Finished initializing");

  }

  private void setup(final FMLCommonSetupEvent event) {
  }

  private void init() {
    instance = this;

    config = Paths.get(FMLConfig.defaultConfigPath()).resolve("./hmage.properties");

    HMageSettings.init(config);

    this.anniObserverMap = AnniObserverMap.getInstance();

    this.moduleManager = new ModuleManager();

    this.moduleManager.register(new FixedFOV(this.moduleManager));
    this.moduleManager.register(new CustomGuiBackground(this.moduleManager));

    this.moduleManager.register(new StatusEffect(this.moduleManager));
  }

  @SubscribeEvent
  public void onRenderGameOverlay(final RenderGameOverlayEvent event) {
    if (!HMageSettings.enabled)
      return;

    Minecraft mc = Minecraft.getInstance();

    if (mc.gameSettings.showDebugInfo)
      return;

    if (mc.currentScreen == null && event.getType() == ElementType.TEXT) {
      float partialTicks = event.getPartialTicks();
      for (IDrawable drawable : this.moduleManager.getEnabledDrawableModules()) {
        drawable.draw(mc, partialTicks);
      }
    }
  }

  @SubscribeEvent
  public void onInitGuiEvent(InitGuiEvent event) {
    if (!(event.getGui() instanceof ChestScreen)) { return; }
    ChestScreen gui = (ChestScreen) event.getGui();

    if (gui instanceof AnniServersScreen) { return; }

    IInventory chestInventory = GuiScreenUtils.getChestInventory(gui);

    if (chestInventory == null) { return; }

    ITextComponent chestDisplayName = gui.getTitle();
    if (chestDisplayName.getFormattedText().startsWith(GuiScreenUtils.SELEC_SERVER)) {

      Minecraft mc = Minecraft.getInstance();
      mc.displayGuiScreen(new AnniServersScreen((ChestContainer) chestInventory, mc.player.inventory, gui.getTitle()));
    }
  }

  @SubscribeEvent
  public void onReciveChat(ClientChatReceivedEvent event) {
    if (HMageSettings.enabled) {
      AnniChatReciveExecutor.onReceiveChat(event.getMessage(), event.getType());
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

  public static HMage getInstance() {
    return instance;
  }
}
