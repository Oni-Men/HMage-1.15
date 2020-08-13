package onim.en.hmage.gui;

import java.util.List;

import net.minecraft.client.gui.screen.inventory.ChestScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import onim.en.hmage.HMage;
import onim.en.hmage.observer.data.GamePhase;
import onim.en.hmage.util.NBTUtils;

public class AnniServersScreen extends ChestScreen {

  public AnniServersScreen(ChestContainer p_i51095_1_, PlayerInventory p_i51095_2_, ITextComponent p_i51095_3_) {
    super(p_i51095_1_, p_i51095_2_, p_i51095_3_);
  }

  @Override
  protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type) {

    if (slotIn == null) {
      super.handleMouseClick(slotIn, slotId, mouseButton, type);
      return;
    }

    ItemStack stack = slotIn.getStack();
    boolean isServerSelectAction = type == ClickType.PICKUP &&
        slotIn != null &&
        stack != null &&
        Item.getIdFromItem(stack.getItem()) != 0;

    if (isServerSelectAction) {
      HMage.getInstance().anniObserverMap.setAnniObserver(stack.getDisplayName().getString(), getPhaseFromStack(stack));
    }
    super.handleMouseClick(slotIn, slotId, mouseButton, type);
  }

  private GamePhase getPhaseFromStack(ItemStack stack) {
    List<String> lore = NBTUtils.getLore(stack);
    if (lore != null && lore.size() == 3)
      return GamePhase.getGamePhasebyText(lore.get(2));
    return GamePhase.UNKNOWN;
  }
}
