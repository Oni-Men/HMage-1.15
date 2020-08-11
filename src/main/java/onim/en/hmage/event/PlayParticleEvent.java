package onim.en.hmage.event;

import net.minecraft.network.play.server.SSpawnParticlePacket;
import net.minecraftforge.eventbus.api.Event;

public class PlayParticleEvent extends Event {

  private SSpawnParticlePacket particle;

  public PlayParticleEvent(SSpawnParticlePacket particle) {
    this.particle = particle;
  }

  @Override
  public boolean isCancelable() {
    return true;
  }

  public SSpawnParticlePacket getParticle() {
    return particle;
  }
}
