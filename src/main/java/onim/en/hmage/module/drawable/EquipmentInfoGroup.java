package onim.en.hmage.module.drawable;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.inventory.EquipmentSlotType;
import onim.en.hmage.module.DrawableModule;
import onim.en.hmage.module.ModuleManager;
import onim.en.hmage.util.Layout;

public class EquipmentInfoGroup extends DrawableModule {

  private Map<EquipmentSlotType, EquipmentInfo> mapEquipmentInfo;
  private EquipmentInfo previousAddedInfo;

  public EquipmentInfoGroup(ModuleManager manager) {
    super("hmage.module.equipment-info-group", manager);

    this.mapEquipmentInfo = new HashMap<>();

    this.addEquipmentInfo(manager, EquipmentSlotType.HEAD);
    this.addEquipmentInfo(manager, EquipmentSlotType.CHEST);
    this.addEquipmentInfo(manager, EquipmentSlotType.LEGS);
    this.addEquipmentInfo(manager, EquipmentSlotType.FEET);
    this.addEquipmentInfo(manager, EquipmentSlotType.MAINHAND);
    this.addEquipmentInfo(manager, EquipmentSlotType.OFFHAND);
  }

  private void addEquipmentInfo(ModuleManager manager, EquipmentSlotType slotType) {
    EquipmentInfo info = new EquipmentInfo(manager, slotType);
    if (previousAddedInfo != null) {
      info.getLayout().bottom().left();
      info.setAnchor(previousAddedInfo);
    }
    previousAddedInfo = info;
    this.mapEquipmentInfo.put(slotType, info);
  }

  @Override
  public void draw(Minecraft mc, float partialTicks, boolean layoutMode) {
    for (EquipmentInfo info : mapEquipmentInfo.values()) {
      if (info.canDraw()) {
        info.draw(mc, partialTicks, layoutMode);
      }
    }
  }

  @Override
  public Layout getDefaultLayout() {
    return Layout.getLayout();
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
  public void computeSize() {

  }

}
