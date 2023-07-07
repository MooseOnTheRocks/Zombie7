package dev.foltz.item.grenade;

import com.google.common.collect.Multimap;
import dev.foltz.Z7Util;
import dev.foltz.entity.Z7BulletEntity;
import dev.foltz.entity.Z7GrenadeEntity;
import dev.foltz.item.Z7ComplexItem;
import dev.foltz.item.ammo.Z7AmmoItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static dev.foltz.item.grenade.Z7FragGrenadeItem.*;

public abstract class Z7GrenadeItem extends Z7ComplexItem implements Z7IGrenadelike {
    public static final int GLOBAL_STAGE_PRIMED = -1;
    public static final int GLOBAL_STAGE_DEFAULT = 0;

    protected final Map<Integer, Integer> maxUsagePerStage;

    public Z7GrenadeItem(int maxCount, Map<Integer, Integer> maxUsagePerStage) {
        super(new FabricItemSettings().maxCount(maxCount));
        this.maxUsagePerStage = Map.copyOf(maxUsagePerStage);
    }

    public abstract List<? extends Z7GrenadeEntity> createGrenadeEntities(LivingEntity player, ItemStack stack);

    @Override
    public ItemStack tickShoot(ItemStack stack, LivingEntity entity) {
        if (!(stack.getItem() instanceof Z7IGrenadelike grenadelike)) {
            return stack;
        }

        return stack;
    }

    @Override
    public boolean canShoot(ItemStack stack, LivingEntity entity) {
        if (!(stack.getItem() instanceof Z7IGrenadelike grenadelike)) {
            return false;
        }

        return true;
    }

    @Override
    public boolean isInFiringCooldown(ItemStack stack) {
        if (!(stack.getItem() instanceof Z7IGrenadelike grenadelike)) {
            return false;
        }

        return false;
    }

    @Override
    public ItemStack beginReload(ItemStack stack, LivingEntity entity) {
        setGrenadeStage(stack, GLOBAL_STAGE_DEFAULT);
        resetUsageStage(stack);
        System.out.println("Cancelling grenade.");
        return stack;
    }

    @Override
    public ItemStack tickReload(ItemStack stack, LivingEntity entity) {
        return stack;
    }

    @Override
    public ItemStack endReload(ItemStack stack, LivingEntity entity) {
        return stack;
    }

    @Override
    public boolean isReloading(ItemStack stack) {
        return false;
    }

    @Override
    public int getMaxUsageTicks(ItemStack stack) {
        if (!(stack.getItem() instanceof Z7IGrenadelike grenadelike)) {
            return 0;
        }

        return maxUsagePerStage.getOrDefault(getGrenadeStage(stack), 0);
    }

    @Override
    public boolean canReload(ItemStack stack, LivingEntity entity) {
        if (!(stack.getItem() instanceof Z7IGrenadelike grenadelike)) {
            return false;
        }

        int grenadeStage = getGrenadeStage(stack);
        return grenadeStage != GLOBAL_STAGE_DEFAULT && grenadeStage != GLOBAL_STAGE_PRIMED;
    }

    public boolean isPrimed(ItemStack stack) {
        return getGrenadeStage(stack) == GLOBAL_STAGE_PRIMED;
    }

    public boolean isActive(ItemStack stack) {
        return getGrenadeStage(stack) == STAGE_PRIME_ON_RELEASE;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient) {
            if (isReloading(stack)) {
                tickReloadInventory(stack, world, entity, slot, selected);
            }
            else {
                tickShootInventory(stack, world, entity, slot, selected);
            }
        }
    }

    @Override
    public boolean isShooting(ItemStack stack, LivingEntity entity) {
        return false;
    }

    @Override
    public boolean isReadyToFire(ItemStack stack) {
        return false;
    }

    @Override
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

        setGrenadeStage(stack, GLOBAL_STAGE_DEFAULT);
        resetUsageStage(stack);

        if (!(entity instanceof PlayerEntity player && player.getAbilities().creativeMode)) {
            stack.decrement(1);
        }

        return true;
    }

    // -- Vanilla

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        int usedTime = getMaxUseTime(stack) - remainingUseTicks;
        if (getGrenadeStage(stack) != GLOBAL_STAGE_PRIMED && usedTime < Z7Util.ticksFromSeconds(0.5f)) {
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
    public int getItemBarStep(ItemStack stack) {
        return 13;
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return getGrenadeStage(stack) != GLOBAL_STAGE_DEFAULT;
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        final float red = 0.0f;
        final float orange = 0.1f;
        final float green = 0.33f;
        final float yellow = 1.0f;

        if (getGrenadeStage(stack) == GLOBAL_STAGE_PRIMED) {
            float color;
            if (getMaxUsageTicks(stack) == 0) {
                color = orange;
            }
            else {
                float p = getUsageStage(stack) / (float) getMaxUsageTicks(stack);
                color = MathHelper.lerp(p, orange, red);
            }
            return MathHelper.hsvToRgb(color, 1.0f, 1.0f);
        }
        else if (getGrenadeStage(stack) == STAGE_PRIME_ON_RELEASE) {
            return MathHelper.hsvToRgb(yellow, 1.0f, 1.0f);
        }
        else if (isReadyToFire(stack)) {
            return MathHelper.hsvToRgb(orange, 1.0f, 1.0f);
        }
        else {
            return MathHelper.hsvToRgb(green, 1.0f, 1.0f);
        }
    }

    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        return false;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
    }

    @Override
    public boolean allowNbtUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
        return oldStack.getItem() != newStack.getItem();
    }
}
