package dev.foltz.item.consumable;

import dev.foltz.status.Z7StatusEffects;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SplintItem extends Item {
//    public static final StatusEffectInstance EFFECT_HEALTH = new StatusEffectInstance(StatusEffects.INSTANT_HEALTH, 1, 1);
//    public static final int HEAL_AMOUNT = 5; // Number of half-hearts.

    public SplintItem() {
        super(new FabricItemSettings().maxCount(5));
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        PlayerEntity playerEntity = user instanceof PlayerEntity ? (PlayerEntity)user : null;
        if (playerEntity instanceof ServerPlayerEntity) {
            Criteria.CONSUME_ITEM.trigger((ServerPlayerEntity)playerEntity, stack);
        }
        if (!world.isClient) {
            user.removeStatusEffect(Z7StatusEffects.STATUS_EFFECT_BROKEN_BONE);
//            user.heal(HEAL_AMOUNT);
//            user.addStatusEffect(new StatusEffectInstance(Z7StatusEffects.STATUS_EFFECT_HEALING, 280));
        }
        if (playerEntity != null) {
            playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
            if (!playerEntity.getAbilities().creativeMode) {
                stack.decrement(1);
            }
        }
        user.emitGameEvent(GameEvent.EAT);
        return stack;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 20;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.EAT;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (user.hasStatusEffect(Z7StatusEffects.STATUS_EFFECT_BROKEN_BONE)) {
            return ItemUsage.consumeHeldItem(world, user, hand);
        }
        else {
            user.sendMessage(Text.of("Unable to use splint: no broken bones."), true);
            return TypedActionResult.pass(user.getStackInHand(hand));
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
//        PotionUtil.buildTooltip(statusEffects, tooltip, 1);
        tooltip.add(MutableText.of(Text.of("Mend those breaks!").getContent()).formatted(Formatting.GRAY));
        tooltip.add(ScreenTexts.EMPTY);
        tooltip.add(Text.translatable("tooltip.whenApplied").formatted(Formatting.DARK_PURPLE));
//        tooltip.add(Text.translatable("attribute.modifier.plus.1", ItemStack.MODIFIER_FORMAT.format(HEAL_AMOUNT), Text.translatable(StatusEffects.INSTANT_HEALTH.getTranslationKey())).formatted(Formatting.BLUE));
        tooltip.add(Text.translatable("attribute.modifier.take.1", ItemStack.MODIFIER_FORMAT.format(100), Text.translatable(Z7StatusEffects.STATUS_EFFECT_BROKEN_BONE.getTranslationKey())).formatted(Formatting.BLUE));
    }
}
