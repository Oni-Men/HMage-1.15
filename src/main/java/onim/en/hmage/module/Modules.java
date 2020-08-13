package onim.en.hmage.module;

public enum Modules {

  FIXED_FOV("fixed-fov"),
  CUSTOM_GUI_BG("custom-gui-bg"),
  STATUS_EFFECT("status-effect"),
  EQUIPMENT_INFO("equipment-info"),
  HIT_COLOR("hit-color");

  private final String name;
  private final String id;

  private Modules(String name) {
    this.name = name;
    this.id = "hmage.module." + name;
  }

  public String getName() {
    return this.name;
  }

  public String getId() {
    return this.id;
  }

}
