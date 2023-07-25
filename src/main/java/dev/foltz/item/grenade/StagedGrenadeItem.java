package dev.foltz.item.grenade;

import dev.foltz.Z7Util;
import dev.foltz.entity.Z7GrenadeEntity;
import dev.foltz.item.stage.StageBuilder;
import dev.foltz.item.stage.StagedItemEventHandler;
import dev.foltz.item.stage.StagedItemGraphBuilder;
import dev.foltz.item.stage.StagedItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public abstract class StagedGrenadeItem extends StagedItem<StagedGrenadeItem> {
    public StagedGrenadeItem(int maxStackCount, Map<String, StageBuilder<StagedGrenadeItem>> stagesMap) {
        super(new FabricItemSettings().maxCount(maxStackCount), new StagedItemGraphBuilder<>(stagesMap).build());
    }

    public void playSoundReloadCancel(ItemStack stack, Entity entity) {
        entity.world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ITEM_CROSSBOW_LOADING_END, SoundCategory.PLAYERS, 0.5f, 3.0f);
    }

    public abstract List<? extends Z7GrenadeEntity> createGrenadeEntities(LivingEntity entity, ItemStack stack);

    public boolean throwGrenade(ItemStack stack, World world, LivingEntity entity, float speed) {
        var grenades = createGrenadeEntities(entity, stack);
        for (var grenade : grenades) {
            ItemStack throwStack = new ItemStack(stack.getItem());
            grenade.setItemStack(throwStack);
            grenade.setOwner(entity);
            grenade.setPosition(entity.getX(), entity.getEyeY() - grenade.getHeight() / 2f, entity.getZ());
            grenade.setVelocity(entity, entity.getPitch(), entity.getYaw(), 0.0f, speed);
            world.spawnEntity(grenade);
        }

        entity.playSound(SoundEvents.ENTITY_ENDER_PEARL_THROW, 1.0f, -3f);
        setStageId(stack, 0);
        resetStageTicks(stack);

        if (!(entity instanceof PlayerEntity player && player.getAbilities().creativeMode)) {
            stack.decrement(1);
        }

        return true;
    }

    public static StagedItemEventHandler<StagedGrenadeItem> doCancel(String stageDefault) {
        return view -> {
            view.item.playSoundReloadCancel(view.stack, view.entity);
            // Note: Could cancel pressingShoot as well.
            view.playerState.setPressingReload(false);
            return stageDefault;
        };
    }

    public static StagedItemEventHandler<StagedGrenadeItem> doReady(String stageDefault) {
        return view -> {
            view.item.playSoundReloadCancel(view.stack, view.entity);
            // Note: Could cancel pressingShoot as well.
            view.playerState.setPressingReload(false);
            return stageDefault;
        };
    }


    // -- Vanilla

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        int usedTime = getMaxUseTime(stack) - remainingUseTicks;
        if (usedTime < Z7Util.ticksFromSeconds(0.25f)) {
            return;
        }

        final int fullThrowTime = Z7Util.ticksFromSeconds(4f);
        float throwSpeed = MathHelper.map(Math.min(usedTime, fullThrowTime), 0, fullThrowTime, 0.5f, 2.5f);

        throwGrenade(stack, world, user, throwSpeed);

    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        super.usageTick(world, user, stack, remainingUseTicks);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        user.setCurrentHand(hand);
        return TypedActionResult.consume(user.getStackInHand(hand));
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 72000;
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return !getStageName(stack).equals("default");
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        var stage = getStage(stack);
        float p = stage.barProgress(stack);
        if (p < 0) {
            p = 1;
        }
        return Math.round(13f * p);
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        return getStage(stack).barColor(stack);
    }

    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        return false;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(MutableText.of(Text.of("Grenade!").getContent()).formatted(Formatting.GRAY, Formatting.ITALIC));
//        tooltip.add(MutableText.of(Text.of("  - Soap").getContent()).formatted(Formatting.GRAY, Formatting.BOLD));
    }

    @Override
    public boolean allowNbtUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
        return oldStack.getItem() != newStack.getItem();
    }
}
