package onim.en.hmage.gui.screen;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;
import onim.en.hmage.HMageSettings;

public class HMageSettingScreen extends Screen {

  public HMageSettingScreen() {
    super(new StringTextComponent("HMage Settings"));
  }

  @Override
  protected void init() {
    this.addButton(
        new Button(this.width / 2 - 50, 64, 100, 20, this.getButtonText("HMage", HMageSettings.enabled), (b) -> {
          HMageSettings.toggleEnabled();
          b.setMessage(this.getButtonText("HMage", HMageSettings.enabled));
    }));
  }

  @Override
  public void render(int mouseX, int mouseY, float partialTicks) {
    this.renderBackground();
    this.drawCenteredString(this.font, this.title.getFormattedText(), this.width / 2, 15, 16777215);
    super.render(mouseX, mouseY, partialTicks);
  }

  @Override
  public void onClose() {
    super.onClose();

    HMageSettings.store();
  }

  private String getButtonText(String title, boolean b) {
    return String.format("%s : %s", title, (b ? "Enabled" : "Disabled"));
  }
}
