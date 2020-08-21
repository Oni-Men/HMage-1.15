package onim.en.hmage.module.drawable;

import java.awt.Dimension;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import onim.en.hmage.module.DrawableModule;
import onim.en.hmage.module.ModuleManager;

public abstract class LabelModule extends DrawableModule {

  protected String text;

  private Dimension cachedSize;
  private int cacheId = -1;

  public LabelModule(String moduleId, ModuleManager manager) {
    super(moduleId, manager);
  }

  @Override
  public void draw(Minecraft mc, float partialTicks, boolean layoutMode) {
    if (text != null && !text.isEmpty()) {

      this.computePosition(mc);
      this.computeSize();

      RenderSystem.pushMatrix();
      RenderSystem.translatef(computedPoint.x, computedPoint.y, 0);

      AbstractGui.fill(0, 0, computedSize.width, computedSize.height, 0x33333333);

      RenderSystem.translatef(getPaddingX() * this.scale, (getPaddingY()) * this.scale, 0);
      RenderSystem.scalef(this.scale, this.scale, 1.0F);

      mc.fontRenderer.drawStringWithShadow(text, 0, 0, 0xffffff);

      RenderSystem.popMatrix();
    }
  }

  protected abstract int getPaddingX();

  protected abstract int getPaddingY();

  protected abstract String getText();

  @Override
  public void computeSize() {
    if (cachedSize == null) {
      cachedSize = new Dimension(0, 0);
    }
    if (text == null) {
      cachedSize.setSize(0, 0);
      return;
    }

    if (text.isEmpty()) {
      cachedSize.setSize(0, 0);
      return;
    }

    if (cacheId == text.hashCode()) { return; }

    Minecraft mc = Minecraft.getInstance();

    int width = mc.fontRenderer.getStringWidth(text) + 2 * getPaddingX();
    int height = mc.fontRenderer.FONT_HEIGHT + 2 * getPaddingY();

    cachedSize.setSize(width, height);
    cacheId = text.hashCode();
  }

  //  @Override
  //  public int computeWidth() {
  //    if (this.getText().hashCode() != hash) {
  //      this.computedWidth = Minecraft.getInstance().fontRenderer.getStringWidth(this.getText());
  //      this.computedWidth += 2 * this.getPaddingX();
  //      this.hash = this.getText().hashCode();
  //    }
  //    return this.computedWidth;
  //  }
  //
  //  @Override
  //  public int computeHeight() {
  //    if (this.getText().hashCode() != hash) {
  //      this.computedHeight = Minecraft.getInstance().fontRenderer.FONT_HEIGHT + 2 * this.getPaddingY();
  //    }
  //    return this.computedHeight;
  //  }

}
