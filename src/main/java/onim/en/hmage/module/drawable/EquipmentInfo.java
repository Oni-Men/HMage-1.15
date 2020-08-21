package onim.en.hmage.module.drawable;

import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import onim.en.hmage.module.DrawableModule;
import onim.en.hmage.module.ModuleManager;
import onim.en.hmage.util.Layout;

public class EquipmentInfo extends DrawableModule {

  private final static Map<EquipmentSlotType, ItemStack> mapItemStackForLayoutMode;
  static {
    mapItemStackForLayoutMode = new HashMap<>();
    mapItemStackForLayoutMode.put(EquipmentSlotType.MAINHAND, new ItemStack(Item.getItemById(276)));
    mapItemStackForLayoutMode.put(EquipmentSlotType.OFFHAND, new ItemStack(Item.getItemById(360)));
    mapItemStackForLayoutMode.put(EquipmentSlotType.FEET, new ItemStack(Item.getItemById(313)));
    mapItemStackForLayoutMode.put(EquipmentSlotType.LEGS, new ItemStack(Item.getItemById(312)));
    mapItemStackForLayoutMode.put(EquipmentSlotType.CHEST, new ItemStack(Item.getItemById(311)));
    mapItemStackForLayoutMode.put(EquipmentSlotType.HEAD, new ItemStack(Item.getItemById(310)));
  }

  private final ItemStack itemForLayoutMode;
  private EquipmentSlotType slotType;
  private ItemStack equipmentItem = null;

  private int cacheId = 0;

  public EquipmentInfo(ModuleManager manager, EquipmentSlotType slotType) {
    super("hmage.module.equipment-info", manager);
    this.slotType = slotType;
    this.itemForLayoutMode = mapItemStackForLayoutMode.get(slotType);
  }

  @Override
  public void computeSize() {
    if (computedSize == null) {
      computedSize = new Dimension(0, 0);
    }

    if (cacheId == equipmentItem.hashCode()) { return; }


    if (equipmentItem == null) {
      computedSize.setSize(0, 0);
      cacheId = 0;
      return;
    }

    if (equipmentItem.isEmpty()) {
      computedSize.setSize(0, 0);
      cacheId = equipmentItem.hashCode();
      return;
    }

    Minecraft mc = Minecraft.getInstance();
    String itemText = getTextForItemStack(equipmentItem);

    int width = 20 + mc.fontRenderer.getStringWidth(itemText);
    int height = 20;

    computedSize.setSize(width, height);
    cacheId = equipmentItem.hashCode();
  }

  @Override
  public void draw(Minecraft mc, float partialTicks, boolean layoutMode) {
    PlayerEntity player = mc.player;
    FontRenderer fontRenderer = mc.fontRenderer;

    this.syncEquipmentSlotItemStack(player);

    ItemStack itemStackForRender = layoutMode ? this.itemForLayoutMode : this.equipmentItem;

    this.computePosition(mc);
    this.computeSize();

    if (Item.getIdFromItem(itemStackForRender.getItem()) == 0)
      return;

    RenderSystem.pushMatrix();
    RenderSystem.translatef(computedPoint.x, computedPoint.y, 0);
    RenderSystem.scalef(this.scale, this.scale, 1.0F);

    int color = this.getDurabilityColor(itemStackForRender);
    String text = this.getTextForItemStack(itemStackForRender);
    int stringWidth = fontRenderer.getStringWidth(text);

    if (layout.isRight() && !layout.isHorizontal()) {
      drawLightedItem(mc, itemStackForRender, stringWidth + 2, 2);
      fontRenderer.drawStringWithShadow(text, 0, 10 - mc.fontRenderer.FONT_HEIGHT / 2, color);
    } else {
      drawLightedItem(mc, itemStackForRender, 2, 2);
      fontRenderer.drawStringWithShadow(text, 20, 10 - mc.fontRenderer.FONT_HEIGHT / 2, color);
    }

    RenderSystem.popMatrix();
    RenderSystem.disableRescaleNormal();
    RenderSystem.disableBlend();
  }

  private void syncEquipmentSlotItemStack(PlayerEntity player) {
    this.equipmentItem = player.getItemStackFromSlot(this.slotType);
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

}
