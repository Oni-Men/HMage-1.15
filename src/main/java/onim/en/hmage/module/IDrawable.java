package onim.en.hmage.module;

import net.minecraft.client.Minecraft;

public interface IDrawable {

  public void draw(Minecraft mc, float partialTicks);

  public void draw(Minecraft mc, float partialTicks, boolean layoutMode);

  public boolean canDraw();
}
