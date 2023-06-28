package dev.foltz.item;

import dev.foltz.status.Z7ConcussionStatusEffect;
import dev.foltz.status.Z7StatusEffect;
import dev.foltz.status.Z7StatusEffects;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;

public class Z7BonkStick extends Item {
    public Z7BonkStick() {
        super(new FabricItemSettings().maxCount(1));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        user.addStatusEffect(new StatusEffectInstance(Z7StatusEffects.STATUS_EFFECT_CONCUSSION, Z7ConcussionStatusEffect.SHORT_DURATION));
        return TypedActionResult.success(user.getStackInHand(hand), true);
    }
}
