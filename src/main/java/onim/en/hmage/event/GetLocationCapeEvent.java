package onim.en.hmage.event;

import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;

public class GetLocationCapeEvent extends Event {

  private AbstractClientPlayerEntity player;
  private ResourceLocation capeLocation;

  public GetLocationCapeEvent(AbstractClientPlayerEntity player) {
    this.player = player;
    this.capeLocation = null;
  }

  public AbstractClientPlayerEntity getPlayer() {
    return this.player;
  }

  public ResourceLocation getCapeLocation() {
    return this.capeLocation;
  }

  public void setCapeLocation(ResourceLocation capeLocation) {
    this.capeLocation = capeLocation;
  }
}
