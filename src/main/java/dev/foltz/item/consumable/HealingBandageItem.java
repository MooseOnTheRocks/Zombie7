package dev.foltz.item.consumable;

import dev.foltz.Z7Util;
import dev.foltz.status.Z7StatusEffects;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
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
import java.util.function.Supplier;

public class HealingBandageItem extends Item {
    public static final Supplier<StatusEffectInstance> EFFECT_HEALING = () -> new StatusEffectInstance(Z7StatusEffects.STATUS_EFFECT_HEALING, 15 * 20);

    public HealingBandageItem() {
        super(new FabricItemSettings());
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        PlayerEntity playerEntity;
        PlayerEntity playerEntity2 = playerEntity = user instanceof PlayerEntity ? (PlayerEntity)user : null;
        if (playerEntity instanceof ServerPlayerEntity) {
            Criteria.CONSUME_ITEM.trigger((ServerPlayerEntity)playerEntity, stack);
        }
        if (!world.isClient) {
            user.removeStatusEffect(Z7StatusEffects.STATUS_EFFECT_BLEEDING);
            user.removeStatusEffect(Z7StatusEffects.STATUS_EFFECT_BLEEDING_LONG);
            user.addStatusEffect(EFFECT_HEALING.get());
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
        return Z7Util.ticksFromSeconds(3);
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
//        return UseAction.BRUSH;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (user.hasStatusEffect(Z7StatusEffects.STATUS_EFFECT_BLEEDING) || user.getMaxHealth() != user.getHealth()) {
            return ItemUsage.consumeHeldItem(world, user, hand);
        }
        else {
            user.sendMessage(Text.of("Unable to use bandages: not bleeding and full health."), true);
            return TypedActionResult.pass(user.getStackInHand(hand));
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(MutableText.of(Text.of("Soothing to the touch.").getContent()).formatted(Formatting.GRAY));
        tooltip.add(ScreenTexts.EMPTY);
        tooltip.add(Text.translatable("tooltip.whenApplied").formatted(Formatting.DARK_PURPLE));

        MutableText mutableText = Text.translatable(EFFECT_HEALING.get().getTranslationKey());
        mutableText = Text.translatable("potion.withDuration", mutableText, StatusEffectUtil.getDurationText(EFFECT_HEALING.get(), 1));
        tooltip.add(mutableText.formatted(EFFECT_HEALING.get().getEffectType().getCategory().getFormatting()));

        tooltip.add(Text.translatable("attribute.modifier.plus.0", ItemStack.MODIFIER_FORMAT.format(2), Text.translatable(net.minecraft.entity.effect.StatusEffects.INSTANT_HEALTH.getTranslationKey())).formatted(Formatting.BLUE));
        tooltip.add(Text.translatable("attribute.modifier.take.1", ItemStack.MODIFIER_FORMAT.format(100), Text.translatable(Z7StatusEffects.STATUS_EFFECT_BLEEDING.getTranslationKey())).formatted(Formatting.BLUE));
    }
}
