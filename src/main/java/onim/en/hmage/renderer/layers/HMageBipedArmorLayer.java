package onim.en.hmage.renderer.layers;

import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;

public class HMageBipedArmorLayer<T extends LivingEntity, M extends BipedModel<T>, A extends BipedModel<T>>
    extends BipedArmorLayer<T, M, A> {

  public HMageBipedArmorLayer(IEntityRenderer<T, M> p_i50936_1_, A p_i50936_2_, A p_i50936_3_) {
    super(p_i50936_1_, p_i50936_2_, p_i50936_3_);
  }

}
