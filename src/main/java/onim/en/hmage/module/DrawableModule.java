package onim.en.hmage.module;

import java.awt.Dimension;
import java.awt.Point;

import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import onim.en.hmage.HMageSettings;
import onim.en.hmage.util.Layout;

public abstract class DrawableModule extends Module implements IDrawable {

  private Point translate;
  protected Point computedPoint;
  protected Dimension computedSize;
  protected float scale = 1f;
  protected boolean layoutMode = false;
  protected Layout layout = null;

  protected DrawableModule anchor = null;

  public DrawableModule(String moduleId, ModuleManager manager) {
    super(moduleId, manager);
    this.translate = new Point(this.getDefaultX(), this.getDefaultY());
    this.layout = this.getDefaultLayout();
  }

  @Override
  public boolean canDraw() {
    return this.isEnabled();
  }

  @Override
  public void draw(Minecraft mc, float partialTicks) {
    if (this.canDraw()) {
      this.draw(mc, partialTicks, this.layoutMode);
    }
  }

  public void enterLayoutMode() {
    this.layoutMode = true;
  }

  public void leaveLayoutMode() {
    this.layoutMode = false;
  }

  public void setAnchor(DrawableModule anchor) {
    this.anchor = anchor;
  }

  public DrawableModule getAnchor() {
    return this.anchor;
  }

  public abstract Layout getDefaultLayout();

  public abstract int getDefaultX();

  public abstract int getDefaultY();

  public abstract void computeSize();

  public Layout getLayout() {
    if (layout == null) {
      int i = HMageSettings.getInt(this.getKeyLayout(), this.layout.getCode());
      this.layout = Layout.getLayout(i);
    }
    return this.layout;
  }

  public void computePosition(Minecraft mc) {
    Layout layout = this.getLayout();
    MainWindow mainWindow = mc.getMainWindow();

    if (this.computedPoint == null) {
      this.computedPoint = new Point();
    }

    this.computedPoint.setLocation(translate);
    this.computeSize();

    if (this.anchor != null && this.anchor.anchor != this) {
      anchor.computePosition(mc);
      anchor.computeSize();
    }

    int mx = 0, my = 0;

    switch (layout.getLayoutX()) {
    case LEFT:
      if (this.anchor != null) {
        mx += this.anchor.computedPoint.x;
      }
      break;
    case CENTER:
      if (this.anchor == null) {
        mx += (mainWindow.getScaledWidth() - computedSize.width) / 2;
      } else {
        mx += this.anchor.computedPoint.x - this.computedSize.width / 2;
      }
      break;
    case RIGHT:
      if (this.anchor == null) {
        mx += mainWindow.getScaledWidth() - computedSize.width;
      } else {
        mx += this.anchor.computedPoint.x + this.anchor.computedSize.width;
      }
      break;
    default:
      break;
    }

    switch (layout.getLayoutY()) {
    case TOP:
      if (this.anchor != null) {
        my -= this.anchor.computedSize.height;
      }
      break;
    case CENTER:
      if (this.anchor == null) {
        my += (mainWindow.getScaledHeight() - computedSize.height) / 2;
      } else {
        my += this.anchor.computedPoint.y + anchor.computedSize.height / 2 + computedSize.height / 2;
      }
      break;
    case BOTTOM:
      if (this.anchor == null) {
        my += mainWindow.getScaledHeight() - computedSize.height;
      } else {
        my += this.anchor.computedPoint.y + this.computedSize.height;
      }
      break;
    default:
      break;
    }

    this.computedPoint.move(mx, my);
  }

  protected String getKeyTranslateX() {
    return this.getModuleId() + ".x";
  }

  protected String getKeyTranslateY() {
    return this.getModuleId() + ".y";
  }

  protected String getKeyLayout() {
    return this.getModuleId() + ".layout";
  }

  protected String getKeyScale() {
    return this.getModuleId() + ".scale";
  }

  @Override
  public void load() {
    super.load();

    int x = HMageSettings.getInt(this.getKeyTranslateX(), translate.x);
    int y = HMageSettings.getInt(this.getKeyTranslateY(), translate.y);
    translate.setLocation(x, y);

    this.scale = HMageSettings.getFloat(this.getKeyScale(), scale);

    int i = HMageSettings.getInt(this.getKeyLayout(), layout.getCode());
    this.layout = Layout.getLayout(i);
  }

  @Override
  public void store() {
    super.store();

    HMageSettings.setInt(this.getKeyTranslateX(), translate.x);
    HMageSettings.setInt(this.getKeyTranslateY(), translate.y);
    HMageSettings.setFloat(this.getKeyScale(), scale);
    HMageSettings.setInt(this.getKeyLayout(), this.layout.getCode());
  }

}
