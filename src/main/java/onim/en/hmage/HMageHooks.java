package onim.en.hmage;

import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.network.play.server.SSpawnParticlePacket;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import onim.en.hmage.event.DrawWorldBackgroundEvent;
import onim.en.hmage.event.GetLocationCapeEvent;
import onim.en.hmage.event.PlayParticleEvent;

public class HMageHooks {

  public static boolean onRenderBackground() {
    return MinecraftForge.EVENT_BUS.post(new DrawWorldBackgroundEvent());
  }

  public static ResourceLocation onGetLocationCape(AbstractClientPlayerEntity player) {
    GetLocationCapeEvent event = new GetLocationCapeEvent(player);
    MinecraftForge.EVENT_BUS.post(event);
    return event.getCapeLocation();
  }

  public static boolean onhandleParticles(SSpawnParticlePacket particle) {
    PlayParticleEvent event = new PlayParticleEvent(particle);
    MinecraftForge.EVENT_BUS.post(event);
    return event.isCanceled();
  }
}
