package onim.en.hmage.module;

import onim.en.hmage.HMageSettings;

public abstract class Module {

  private final String moduleId;
  private final ModuleManager manager;
  private boolean enabled = true;

  public Module(String moduleId, ModuleManager manager) {
    this.moduleId = moduleId;
    this.manager = manager;
  }

  public String getModuleId() {
    return this.moduleId;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
    this.manager.updateModules();
  }

  public boolean isEnabled() {
    return this.enabled;
  }

  public void load() {
    this.setEnabled(HMageSettings.getBoolean(this.getModuleId() + ".enabled", enabled));
  }

  public void store() {
    HMageSettings.setBoolean(this.getModuleId() + ".enabled", enabled);
  }
}
