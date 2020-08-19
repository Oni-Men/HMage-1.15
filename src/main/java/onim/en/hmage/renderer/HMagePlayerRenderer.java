package onim.en.hmage.renderer;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.entity.Entity;

public class HMagePlayerRenderer extends PlayerRenderer{

  public HMagePlayerRenderer(EntityRendererManager renderManager, boolean useSmallArms) {
    super(renderManager, useSmallArms);

    for (int i = 0; i < layerRenderers.size(); i++) {
      LayerRenderer<? extends Entity, ? extends EntityModel<?>> layerRenderer = layerRenderers.get(i);
      if (layerRenderer instanceof BipedArmorLayer) {
        //setLayer(i, new HMageLayerBipedArmor(this));
        //} else if (layerRenderer instanceof LayerCape) {
        //setLayer(i, new HMageLayerCape(this));
      }
    }
  }

}
