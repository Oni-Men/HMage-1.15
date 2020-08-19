package onim.en.hmage.module;

import net.minecraft.client.Minecraft;
import onim.en.hmage.HMageSettings;
import onim.en.hmage.util.Layout;

public abstract class DrawableModule extends Module implements IDrawable {

  protected int x, y, width, height;
  protected float scale = 1f;
  protected Layout layout = null;

  protected int computedX, computedY, computedWidth, computedHeight;

  public DrawableModule(String moduleId, ModuleManager manager) {
    super(moduleId, manager);
    this.x = this.getDefaultX();
    this.y = this.getDefaultY();
    this.layout = this.getDefaultLayout();
  }

  @Override
  public boolean canDraw() {
    return this.isEnabled();
  }

  @Override
  public void draw(Minecraft mc, float partialTicks) {
    this.draw(mc, partialTicks, false);
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
    Layout layout = this.getLayout();
    this.computedX = this.x;
    int computedWidth = this.computeWidth();
    switch (layout.getLayoutX()) {
    case CENTERX:
      this.computedX += (mc.getMainWindow().getScaledWidth() - computedWidth) / 2;
      break;
    case RIGHT:
      this.computedX += mc.getMainWindow().getScaledWidth() - computedWidth;
      break;
    default:
      break;
    }
    return this.computedX;
  }

  public int computeY(Minecraft mc) {
    this.computedY = this.y;
    Layout layout = this.getLayout();
    int computedHeight = this.computeHeight();
    switch (layout.getLayoutY()) {
    case CENTERY:
      this.computedY += (mc.getMainWindow().getScaledHeight() - computedHeight) / 2;
      break;
    case BOTTOM:
      this.computedY += mc.getMainWindow().getScaledHeight() - computedHeight;
      break;
    default:
      break;
    }
    return this.computedY;
  }

  @Override
  public void load() {
    super.load();

    this.x = HMageSettings.getInt(this.getModuleId() + ".x", x);
    this.y = HMageSettings.getInt(this.getModuleId() + ".y", y);
    this.scale = HMageSettings.getFloat(this.getModuleId() + ".scale", scale);
  }

  @Override
  public void store() {
    super.store();

    HMageSettings.setInt(this.getModuleId() + ".x", x);
    HMageSettings.setInt(this.getModuleId() + ".y", y);
    HMageSettings.setFloat(this.getModuleId() + ".scale", scale);
  }

}
