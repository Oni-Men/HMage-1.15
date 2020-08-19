package onim.en.hmage.module.normal;

import java.util.List;

import net.minecraft.client.gui.screen.inventory.CraftingScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import onim.en.hmage.module.Module;
import onim.en.hmage.module.ModuleManager;

public class RecipeBookRemover extends Module {

  public RecipeBookRemover(ModuleManager moduleManager) {
    super("hmage.module.recipe-book-remover", moduleManager);
  }

  @SubscribeEvent
  public void onGuiInit(InitGuiEvent event) {
    if (!this.isEnabled())
      return;

    if (event.getGui() instanceof InventoryScreen || event.getGui() instanceof CraftingScreen) {

      List<Widget> widgets = event.getWidgetList();

      for (int i = 0; i < widgets.size(); i++) {
        if (widgets.get(i) instanceof ImageButton) {
          event.removeWidget(widgets.get(i));
        }
      }
    }
  }

}
