package onim.en.hmage.module.drawable.label;

import java.util.LinkedList;
import java.util.Queue;

import org.lwjgl.glfw.GLFW;

import net.minecraftforge.client.event.InputEvent.MouseInputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import onim.en.hmage.module.ModuleManager;
import onim.en.hmage.module.drawable.LabelModule;
import onim.en.hmage.util.Layout;

public class CpsCounter extends LabelModule {

  private final CPSCounter counter;

  public CpsCounter(ModuleManager manager) {
    super("hmage.module.cps-counter", manager);
    this.counter = new CPSCounter();
  }

  @SubscribeEvent
  public void onMouseInputEvent(MouseInputEvent event) {
    if (!isEnabled())
      return;

    if (event.getAction() != GLFW.GLFW_PRESS)
      return;

    if (event.getButton() != GLFW.GLFW_MOUSE_BUTTON_1)
      return;

    this.counter.clicked();
  }

  @Override
  public Layout getDefaultLayout() {
    return Layout.getLayout().centerx().bottom();
  }

  @Override
  public int getDefaultX() {
    return 0;
  }

  @Override
  public int getDefaultY() {
    return -36;
  }

  @Override
  protected int getPaddingX() {
    return 2;
  }

  @Override
  protected int getPaddingY() {
    return 1;
  }

  @Override
  protected String getText() {
    if (this.counter == null) { return "-- CPS"; }
    return String.format("%d CPS", this.counter.getCurrentCPS());
  }

  static class CPSCounter {
    private final Queue<Long> clicks = new LinkedList<>();

    public int getCurrentCPS() {
      long now = System.currentTimeMillis();
      while (!clicks.isEmpty() && clicks.peek() < now) {
        clicks.remove();
      }
      return clicks.size();
    }

    public void clicked() {
      this.clicks.add(System.currentTimeMillis() + 1000L);
    }
  }
}
