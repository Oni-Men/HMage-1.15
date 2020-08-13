package onim.en.hmage.module.normal;

import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import onim.en.hmage.module.Module;
import onim.en.hmage.module.ModuleManager;
import onim.en.hmage.module.Modules;

public class FixedFOV extends Module {

  public FixedFOV(ModuleManager manager) {
    super(Modules.FIXED_FOV.getId(), manager);
  }

  @SubscribeEvent
  public void onFOVUpdate(FOVUpdateEvent event) {

    if (this.isEnabled()) {
      event.setNewfov(this.getFovModifier(event.getEntity()));
    }

  }

  /**
   * ステータス効果に影響されないように変更を加えた{@link AbstractClientPlayerEntity#getFovModifier()}のコピー。
   *
   * @param player
   * @return
   */
  public float getFovModifier(PlayerEntity player) {
    float f = 1.0F;
    if (player.abilities.isFlying) {
      f *= 1.1F;
    }

    IAttributeInstance iattributeinstance = player.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
    double base = iattributeinstance.getValue();

    EffectInstance activeSpeedEffect = player.getActivePotionEffect(Effects.SPEED);
    EffectInstance activeSlownessEffect = player.getActivePotionEffect(Effects.SLOWNESS);

    if (activeSpeedEffect != null) {
      base *= 5.0D / (6.0D + activeSpeedEffect.getAmplifier());
    }

    if (activeSlownessEffect != null) {
      base *= 20.0D / (18.0D + activeSlownessEffect.getAmplifier());
    }

    f = (float) ((double) f * ((base / (double) player.abilities.getWalkSpeed() + 1.0D) / 2.0D));
    if (player.abilities.getWalkSpeed() == 0.0F || Float.isNaN(f) || Float.isInfinite(f)) {
      f = 1.0F;
    }

    if (player.isHandActive() && player.getActiveItemStack().getItem() instanceof BowItem) {
      int i = player.getItemInUseMaxCount();
      float f1 = (float) i / 20.0F;
      if (f1 > 1.0F) {
        f1 = 1.0F;
      } else {
        f1 = f1 * f1;
      }

      f *= 1.0F - f1 * 0.15F;
    }

    return f;
  }
}
