package onim.en.hmage.module;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import net.minecraftforge.common.MinecraftForge;

public class ModuleManager {

  private final Map<String, Module> moduleMap;
  private final Map<String, IDrawable> drawableModules;
  private Collection<Module> enabledModules;
  private Collection<Module> disabledModules;

  private Collection<IDrawable> enabledDrawables;

  public ModuleManager() {
    moduleMap = new HashMap<>();
    drawableModules = new HashMap<>();
  }

  public void register(Module module) {
    if (module == null)
      return;
    this.moduleMap.computeIfAbsent(module.getModuleId(), k -> {
      MinecraftForge.EVENT_BUS.register(module);
      return module;
    });
    if (module instanceof IDrawable) {
      this.drawableModules.computeIfAbsent(module.getModuleId(), k -> {
        return (IDrawable) module;
      });
    }
    this.updateModules();
  }

  public void loadAllSettings() {
    this.moduleMap.values().forEach(m -> m.load());
  }

  public void storeAllSettings() {
    this.moduleMap.values().forEach(m -> m.store());
  }

  public Collection<Module> getEnabledModules() {
    return this.enabledModules;
  }

  public Collection<Module> getDisabledModules() {
    return this.disabledModules;
  }

  public Collection<IDrawable> getEnabledDrawableModules() {
    return this.enabledDrawables;
  }

  public void updateModules() {
    enabledModules = this.moduleMap.values().stream().filter(m -> m.isEnabled()).collect(Collectors.toList());
    disabledModules = this.moduleMap.values().stream().filter(m -> !m.isEnabled()).collect(Collectors.toList());

    enabledDrawables = this.drawableModules.values()
        .stream()
        .filter(m -> m.canDraw())
        .collect(Collectors.toList());
  }
}
