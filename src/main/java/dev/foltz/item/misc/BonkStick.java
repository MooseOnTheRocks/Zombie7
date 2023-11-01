package dev.foltz.item.misc;

import dev.foltz.status.ConcussionStatusEffect;
import dev.foltz.status.Z7StatusEffects;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class BonkStick extends Item {
    public BonkStick() {
        super(new FabricItemSettings().maxCount(1));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        user.addStatusEffect(new StatusEffectInstance(Z7StatusEffects.STATUS_EFFECT_CONCUSSION, ConcussionStatusEffect.SHORT_DURATION));
        return TypedActionResult.success(user.getStackInHand(hand), true);
    }
}
