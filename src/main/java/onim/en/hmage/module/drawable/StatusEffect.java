package onim.en.hmage.module.drawable;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.texture.PotionSpriteUploader;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectUtils;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import onim.en.hmage.module.DrawableModule;
import onim.en.hmage.module.ModuleManager;
import onim.en.hmage.module.Modules;
import onim.en.hmage.util.Layout;

public class StatusEffect extends DrawableModule {

  private List<EffectInstance> potionEffectList = Lists.newArrayList();

  private int widthHashCode = 0, cachedWidth = 0, heightHashCode = 0, cachedHeight = 0;

  public StatusEffect(ModuleManager manager) {
    super(Modules.STATUS_EFFECT.getId(), manager);
  }

  @Override
  public Layout getDefaultLayout() {
    return Layout.getLayout().top().right();
  }

  @Override
  public int getDefaultX() {
    return 0;
  }

  @Override
  public int getDefaultY() {
    return 0;
  }

  @Override
  public int computeWidth() {
    if (potionEffectList == null) { return 0; }
    if (widthHashCode != potionEffectList.hashCode()) {
      int width = 0;
      for (EffectInstance effectinstance : potionEffectList) {
        int durationStringWidth = Minecraft.getInstance().fontRenderer
            .getStringWidth(EffectUtils.getPotionDurationString(effectinstance, 1F));
        if (this.getLayout().isHorizontal()) {
          width += 20 + durationStringWidth;
        } else {
          width = Math.max(width, 20 + durationStringWidth);
        }
      }
      cachedWidth = width;
      widthHashCode = potionEffectList.hashCode();
    }
    return this.cachedWidth;
  }

  @Override
  public int computeHeight() {
    if (this.potionEffectList == null || this.getLayout().isHorizontal()) { return 20; }
    if (heightHashCode != potionEffectList.hashCode()) {
      cachedHeight = potionEffectList.stream()
          .filter(p -> p.getEffectInstance().isShowIcon())
          .collect(Collectors.toList())
          .size() * (20);
      heightHashCode = potionEffectList.hashCode();
    }
    return cachedHeight;
  }

  @Override
  public void draw(Minecraft mc, float partialTicks, boolean layoutMode) {
    FontRenderer fontRenderer = mc.fontRenderer;
    PlayerEntity player = mc.player;

    potionEffectList.clear();
    potionEffectList.addAll(Ordering.natural().reverse().sortedCopy(player.getActivePotionEffects()));

    if (potionEffectList.isEmpty())
      return;

    RenderSystem.pushMatrix();

    Layout position = this.getLayout();
    int x = this.computeX(mc);
    int y = this.computeY(mc);

    PotionSpriteUploader potionspriteuploader = mc.getPotionSpriteUploader();

    for (EffectInstance effectinstance : potionEffectList) {
      if (!effectinstance.isShowIcon())
        continue;

      Effect effect = effectinstance.getPotion();

      TextureAtlasSprite textureatlassprite = potionspriteuploader.getSprite(effect);
      mc.getTextureManager().bindTexture(textureatlassprite.getAtlasTexture().getTextureLocation());

      String text = EffectUtils.getPotionDurationString(effectinstance, 1.0F);
      int textWidth = fontRenderer.getStringWidth(text);

      RenderSystem.enableBlend();
      RenderSystem.enableAlphaTest();
      RenderSystem.color4f(1F, 1F, 1F, 1F);

      if (position.isRight() && !position.isHorizontal()) {
        StatusEffect.innerBlit(x - textWidth, y, 18, 18, 0, textureatlassprite);
        fontRenderer.drawStringWithShadow(text, x - textWidth - 20, y + 10 - fontRenderer.FONT_HEIGHT / 2, 0xffffff);
      } else {
        StatusEffect.innerBlit(x, y, 18, 18, 0, textureatlassprite);
        fontRenderer.drawStringWithShadow(text, x + 20, y + 10 - fontRenderer.FONT_HEIGHT / 2, 0xffffff);
      }

      if (position.isHorizontal()) {
        x += 20 + textWidth;
      } else {
        y += 20;
      }

    }
    RenderSystem.popMatrix();
  }

  @SubscribeEvent
  public void onRenderPotionIcons(RenderGameOverlayEvent event) {
    if (this.isEnabled() && event.getType() == ElementType.POTION_ICONS) {
      event.setCanceled(true);
    }
  }

  protected static void innerBlit(int x, int y, int w, int h, int z, TextureAtlasSprite sprite) {
    BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
    bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
    bufferbuilder.pos((double) x, (double) y + h, (double) z).tex(sprite.getMinU(), sprite.getMaxV()).endVertex();
    bufferbuilder.pos((double) x + w, (double) y + h, (double) z).tex(sprite.getMaxU(), sprite.getMaxV()).endVertex();
    bufferbuilder.pos((double) x + w, (double) y, (double) z).tex(sprite.getMaxU(), sprite.getMinV()).endVertex();
    bufferbuilder.pos((double) x, (double) y, (double) z).tex(sprite.getMinU(), sprite.getMinV()).endVertex();
    bufferbuilder.finishDrawing();
    RenderSystem.enableAlphaTest();
    WorldVertexBufferUploader.draw(bufferbuilder);
 }
}
