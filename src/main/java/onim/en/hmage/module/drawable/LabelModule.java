package onim.en.hmage.module.drawable;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import onim.en.hmage.module.DrawableModule;
import onim.en.hmage.module.ModuleManager;

public abstract class LabelModule extends DrawableModule {

  private int hash = -1;

  public LabelModule(String moduleId, ModuleManager manager) {
    super(moduleId, manager);
  }

  @Override
  public void draw(Minecraft mc, float partialTicks, boolean layoutMode) {
    String text = this.getText();
    if (text != null && !text.isEmpty()) {

      RenderSystem.pushMatrix();
      RenderSystem.translatef(computeX(mc), computeY(mc), 0);

      AbstractGui.fill(0, 0, this.computeWidth(), this.computeHeight(), 0x33333333);

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
  public int computeWidth() {
    if (this.getText().hashCode() != hash) {
      this.computedWidth = Minecraft.getInstance().fontRenderer.getStringWidth(this.getText());
      this.computedWidth += 2 * this.getPaddingX();
      this.hash = this.getText().hashCode();
    }
    return this.computedWidth;
  }

  @Override
  public int computeHeight() {
    if (this.getText().hashCode() != hash) {
      this.computedHeight = Minecraft.getInstance().fontRenderer.FONT_HEIGHT + 2 * this.getPaddingY();
    }
    return this.computedHeight;
  }

}
