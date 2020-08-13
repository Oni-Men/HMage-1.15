package onim.en.hmage;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLConfig;
import onim.en.hmage.module.IDrawable;
import onim.en.hmage.module.ModuleManager;
import onim.en.hmage.module.drawable.StatusEffect;
import onim.en.hmage.module.normal.CustomGuiBackground;
import onim.en.hmage.module.normal.FixedFOV;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(HMage.MOD_ID)
public class HMage {

  public final static String MOD_ID = "hmage";

  public static boolean enabled = true;

  // Directly reference a log4j logger.
  public static final Logger LOGGER = LogManager.getLogger();
  public static Path config;

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
    config = Paths.get(FMLConfig.defaultConfigPath()).resolve("./hmage.properties");

    HMageSettings.init(config);

    this.moduleManager = new ModuleManager();

    this.moduleManager.register(new FixedFOV(this.moduleManager));
    this.moduleManager.register(new CustomGuiBackground(this.moduleManager));

    this.moduleManager.register(new StatusEffect(this.moduleManager));
  }

  @SubscribeEvent
  public void onRenderGameOverlay(final RenderGameOverlayEvent event) {
    if (!enabled)
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
  public void onReciveChat(ClientChatReceivedEvent event) {

  }
}
