package onim.en.hmage.event;

import net.minecraftforge.eventbus.api.Event;

public class DrawWorldBackgroundEvent extends Event {

  public DrawWorldBackgroundEvent() {
  }

  @Override
  public boolean isCancelable() {
    return true;
  }
}
