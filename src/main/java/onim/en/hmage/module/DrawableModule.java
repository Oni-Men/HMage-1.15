package onim.en.hmage.module;

public abstract class DrawableModule extends Module implements IDrawable {

  public DrawableModule(String moduleId, ModuleManager manager) {
    super(moduleId, manager);
  }

  @Override
  public boolean canDraw() {
    return this.isEnabled();
  }
}
