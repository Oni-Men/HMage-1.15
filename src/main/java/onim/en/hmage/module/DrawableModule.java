package onim.en.hmage.module;

import net.minecraft.client.Minecraft;
import onim.en.hmage.HMageSettings;
import onim.en.hmage.util.Layout;

public abstract class DrawableModule extends Module implements IDrawable {

  private int x, y, width, height;
  private Layout layout = null;

  public DrawableModule(String moduleId, ModuleManager manager) {
    super(moduleId, manager);
    this.x = 0;
    this.y = 0;
    this.width = this.computeWidth();
    this.height = this.computeHeight();
  }

  @Override
  public boolean canDraw() {
    return this.isEnabled();
  }

  public abstract Layout getDefaultLayout();

  public abstract int getDefaultX();

  public abstract int getDefaultY();

  public abstract int computeWidth();

  public abstract int computeHeight();

  public Layout getLayout() {
    if (layout == null) {
      this.layout = Layout
          .getLayout(HMageSettings.getInt(this.getModuleId() + ".layout", this.getDefaultLayout().getCode()));
    }
    return this.layout;
  }

  public int computeX(Minecraft mc) {
    int x = this.x;
    Layout layout = this.getLayout();
    switch (layout.getLayoutX()) {
    case CENTERX:
      x += mc.getMainWindow().getScaledWidth() / 2;
      break;
    case RIGHT:
      x += mc.getMainWindow().getScaledWidth();
      break;
    default:
      break;
    }
    return x;
  }

  public int computeY(Minecraft mc) {
    int y = this.y;
    Layout layout = this.getLayout();
    switch (layout.getLayoutY()) {
    case CENTERY:
      y += mc.getMainWindow().getScaledHeight() / 2;
      break;
    case BOTTOM:
      y += mc.getMainWindow().getScaledHeight();
      break;
    default:
      break;
    }
    return y;
  }
}
