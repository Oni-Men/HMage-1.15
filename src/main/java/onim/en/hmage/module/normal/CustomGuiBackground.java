package onim.en.hmage.module.normal;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import onim.en.hmage.event.DrawWorldBackgroundEvent;
import onim.en.hmage.module.Module;
import onim.en.hmage.module.ModuleManager;

public class CustomGuiBackground extends Module {

  public CustomGuiBackground(ModuleManager manager) {
    super("hmage-module-custom-gui-bg", manager);
  }

  @SubscribeEvent
  public void onDrawWorldBackground(DrawWorldBackgroundEvent event) {
    Minecraft mc = Minecraft.getInstance();

    if (mc == null)
      return;

    if (!isEnabled() || mc.world == null) { return; }
    event.setCanceled(true);

    double w = (double) mc.getMainWindow().getScaledWidth();
    double h = (double) mc.getMainWindow().getScaledHeight();

    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder builder = tessellator.getBuffer();

    RenderSystem.pushMatrix();
    RenderSystem.enableBlend();
    RenderSystem.disableAlphaTest();
    RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
        GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
    RenderSystem.shadeModel(7425);
    RenderSystem.disableTexture();
    RenderSystem.color4f(1F, 1F, 1F, 1F);

    int color = 0x44cc88ee;

    float alpha = (float) (color >> 24 & 255) / 255F;
    float red = (float) (color >> 16 & 255) / 255F;
    float green = (float) (color >> 8 & 255) / 255F;
    float blue = (float) (color & 255) / 255F;

    builder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

    builder.pos(w, 0, 0).color(red, green, blue, alpha).endVertex();
    builder.pos(0, 0, 0).color(red, green, blue, alpha).endVertex();
    builder.pos(0, h, 0).color(red, green, blue, alpha).endVertex();
    builder.pos(w, h, 0).color(red, green, blue, alpha).endVertex();

    tessellator.draw();

    RenderSystem.shadeModel(7424);
    RenderSystem.popMatrix();
    RenderSystem.enableAlphaTest();
    RenderSystem.disableBlend();
    RenderSystem.enableTexture();
  }
}
