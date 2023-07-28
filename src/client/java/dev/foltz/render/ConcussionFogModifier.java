package dev.foltz.render;

import dev.foltz.status.StatusEffects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;

@Environment(value= EnvType.CLIENT)
public  class ConcussionFogModifier implements BackgroundRenderer.StatusEffectFogModifier {
    @Override
    public StatusEffect getStatusEffect() {
        return StatusEffects.STATUS_EFFECT_CONCUSSION;
    }

    @Override
    public void applyStartEndModifier(BackgroundRenderer.FogData fogData, LivingEntity entity, StatusEffectInstance effect, float viewDistance, float tickDelta) {
//        float f;
//        float f2 = f = effect.isInfinite() ? 5.0f : MathHelper.lerp((float)Math.min(1.0f, (float)effect.getDuration() / 20.0f), (float)viewDistance, (float)5.0f);
//        float f = MathHelper.lerp(Math.min(1.0f, (float) effect.getDuration() / 20.0f), viewDistance, 5.0f);
        int secondsLeftBeginDiminishing = 5;
        int s = secondsLeftBeginDiminishing * 20;
        float n = 0.5f;
        float m = 3.8f;
        float d = 7f;
        float f = effect.isDurationBelow(s - 1)
                ? d + (ConcussionLongFogModifier.FOG_DIST_FACTOR * 1.15f - d) * (float) Math.pow((1 - Math.pow(effect.getDuration(), n) / Math.pow(s, n)), m)
                : d;

//        float f = 16.0f;
        if (fogData.fogType == BackgroundRenderer.FogType.FOG_SKY) {
            fogData.fogStart = 0.0f;
            fogData.fogEnd = f * 0.8f;
        } else {
            fogData.fogStart = f * 0.25f;
            fogData.fogEnd = f;
        }
    }
}
