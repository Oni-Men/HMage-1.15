package onim.en.hmage.module;

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

}
