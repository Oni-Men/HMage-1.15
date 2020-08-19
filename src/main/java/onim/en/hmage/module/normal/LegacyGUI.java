package onim.en.hmage.module.normal;

import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import onim.en.hmage.module.Module;
import onim.en.hmage.module.ModuleManager;

public class LegacyGUI extends Module {

  public LegacyGUI(ModuleManager manager) {
    super("hmage.module.legacy-gui", manager);
  }

  @SubscribeEvent
  public void onGuiInit(InitGuiEvent event) {
    //SlotのxPosとyPosがfinalなので再代入許されない
    //    Screen gui = event.getGui();
    //    boolean enabled = this.isEnabled();
    //
    //    if (gui instanceof ContainerScreen) {
    //      Container container = ScreenUtils.getContainer((ContainerScreen<?>) gui);
    //      List<Slot> inventorySlots = container.inventorySlots;
    //
    //      if (gui instanceof InventoryScreen) {
    //
    //        for (int i = 0; i < inventorySlots.size(); i++) {
    //          Slot slot = inventorySlots.get(i);
    //
    //          if (slot instanceof CraftingResultSlot) {
    //
    //            slot.xPos = enabled ? 144 : 154;
    //            slot.yPos = enabled ? 36 : 28;
    //
    //            for (int m = 0; m < 2; ++m) {
    //              for (int n = 0; n < 2; ++n) {
    //                Slot craft = inventorySlots.get(i + n + m * 2 + 1);
    //                craft.xPos = (enabled ? 88 : 98) + n * 18;
    //                craft.yPos = (enabled ? 26 : 18) + m * 18;
    //              }
    //            }
    //            break;
    //          }
    //        }
    //      }
    //
    //      if (gui instanceof BrewingStandScreen) {
    //        inventorySlots.get(0).yPos = enabled ? 46 : 51;
    //        inventorySlots.get(1).yPos = enabled ? 53 : 58;
    //        inventorySlots.get(2).yPos = enabled ? 46 : 51;
    //      }
    //
    //      if (gui instanceof EnchantmentScreen) {
    //        inventorySlots.get(0).xPos = enabled ? 25 : 15;
    //        inventorySlots.get(1).xPos = enabled ? -25 : 35;
    //      }
    //
    //    }

  }

}
