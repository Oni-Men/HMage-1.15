package onim.en.hmage.module.drawable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import onim.en.hmage.module.DrawableModule;
import onim.en.hmage.module.ModuleManager;
import onim.en.hmage.util.Layout;

public class EquipmentInfo extends DrawableModule {

  private static final List<ItemStack> armorListForLayoutMode = new ArrayList<>();
  static {
    armorListForLayoutMode.add(new ItemStack(Item.getItemById(276)));
    armorListForLayoutMode.add(new ItemStack(Item.getItemById(360)));
    armorListForLayoutMode.add(new ItemStack(Item.getItemById(313)));
    armorListForLayoutMode.add(new ItemStack(Item.getItemById(312)));
    armorListForLayoutMode.add(new ItemStack(Item.getItemById(311)));
    armorListForLayoutMode.add(new ItemStack(Item.getItemById(310)));
  }

  private List<ItemStack> armorList = Lists.newArrayList();

  public EquipmentInfo(ModuleManager manager) {
    super("hmage.module.equipment-info", manager);
  }

  @Override
  public void draw(Minecraft mc, float partialTicks, boolean layoutMode) {
    PlayerEntity player = mc.player;
    FontRenderer fontRenderer = mc.fontRenderer;

    armorList.clear();
    if (layoutMode) {
      armorList.addAll(armorListForLayoutMode);
    } else {
      //手持ちのアイテム
      armorList.add(player.getHeldItem(Hand.OFF_HAND));
      armorList.add(player.getHeldItem(Hand.MAIN_HAND));
      //装備
      Iterator<ItemStack> iterator = player.getArmorInventoryList().iterator();
      while (iterator.hasNext()) {
        armorList.add(iterator.next());
      }
    }

    Collections.reverse(armorList);
    Layout layout = getLayout();

    int x = 0;
    int y = 0;

    RenderSystem.pushMatrix();
    RenderSystem.translatef(this.computeX(mc), this.computeY(mc), 0);
    RenderSystem.scalef(this.scale, this.scale, 1.0F);

    for (ItemStack armor : armorList) {

      if (Item.getIdFromItem(armor.getItem()) == 0)
        continue;

      int color = getDurabilityColor(armor);
      String text = getTextForItemStack(armor);
      int stringWidth = fontRenderer.getStringWidth(text);

      if (layout.isRight() && !layout.isHorizontal()) {
        drawLightedItem(mc, armor, x + stringWidth + 2, y + 2);
        fontRenderer.drawStringWithShadow(text, x, y + 10 - mc.fontRenderer.FONT_HEIGHT / 2, color);
      } else {
        drawLightedItem(mc, armor, x + 2, y + 2);
        fontRenderer.drawStringWithShadow(text, x + 20, y + 10 - mc.fontRenderer.FONT_HEIGHT / 2, color);
      }

      if (layout.isHorizontal()) {
        x += 20 + stringWidth;
      } else {
        y += 20;
      }

    }

    RenderSystem.popMatrix();
    RenderSystem.disableRescaleNormal();
    RenderSystem.disableBlend();
  }

  private void drawLightedItem(Minecraft mc, ItemStack stack, int x, int y) {
    RenderHelper.enableStandardItemLighting();
    mc.getItemRenderer().renderItemAndEffectIntoGUI(stack, x, y);
    RenderHelper.disableStandardItemLighting();
  }

  public String getTextForItemStack(ItemStack stack) {
    boolean itemStackDamageable = stack.isDamageable();
    if (itemStackDamageable) {
      return String.valueOf(stack.getMaxDamage() - stack.getDamage());

    } else if (stack.isStackable()) {
      return String.valueOf(stack.getCount());
    } else {
      return "";
    }
  }

  public int getDurabilityColor(ItemStack stack) {
    if (!stack.isDamageable())
      return 0xffffff;

    int durability = (int) (255 - 255 * ((float) stack.getDamage() / (float) stack.getMaxDamage()));
    int red = 0xff;
    int green = durability;
    int blue = (int) (durability * ((float) durability / 256));

    return (red << 16) | (green << 8) | blue;
  }

  @Override
  public Layout getDefaultLayout() {
    return Layout.getLayout().top().left();
  }

  @Override
  public int getDefaultX() {
    return 2;
  }

  @Override
  public int getDefaultY() {
    return 2;
  }

  @Override
  public int computeWidth() {
    return 0;
  }

  @Override
  public int computeHeight() {
    return 0;
  }

}
