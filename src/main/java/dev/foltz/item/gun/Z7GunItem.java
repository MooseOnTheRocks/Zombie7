package dev.foltz.item.gun;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import dev.foltz.entity.Z7BulletEntity;
import dev.foltz.item.Z7ComplexItem;
import dev.foltz.item.Z7IReloadable;
import dev.foltz.item.Z7IShootable;
import dev.foltz.item.ammo.Z7AmmoItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.stat.Stats;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class Z7GunItem extends Z7ComplexItem implements Z7IGunlike {
    private final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;
    protected final Z7IReloadable reloadingBehavior;
    protected final Z7IShootable shootingBehavior;

    public Z7GunItem(int maxDurability, Z7IReloadable reloadingBehavior, Z7IShootable shootingBehavior) {
        super(new FabricItemSettings().maxDamageIfAbsent(maxDurability));
        this.reloadingBehavior = reloadingBehavior;
        this.shootingBehavior = shootingBehavior;

        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier", 1d, EntityAttributeModifier.Operation.ADDITION));
        builder.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Weapon modifier", -2d, EntityAttributeModifier.Operation.ADDITION));
        this.attributeModifiers = builder.build();
    }

    @Override
    public boolean isReadyToFire(ItemStack stack) {
        return shootingBehavior.isReadyToFire(stack);
    }

    @Override
    public boolean isInFiringCooldown(ItemStack stack) {
        return shootingBehavior.isInFiringCooldown(stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (isReloading(stack)) {
            reloadingBehavior.tickReloadInventory(stack, world, entity, slot, selected);
        }
        else {
            shootingBehavior.tickShootInventory(stack, world, entity, slot, selected);
        }
    }

    @Override
    public boolean canShoot(ItemStack stack, LivingEntity entity) {
        return shootingBehavior.canShoot(stack, entity);
    }

    @Override
    public boolean isShooting(ItemStack stack, LivingEntity entity) {
        return shootingBehavior.isShooting(stack, entity);
    }

    @Override
    public ItemStack beginShoot(ItemStack stack, LivingEntity entity) {
        return shootingBehavior.beginShoot(stack, entity);
    }

    @Override
    public ItemStack tickShoot(ItemStack stack, LivingEntity entity) {
        return shootingBehavior.tickShoot(stack, entity);
    }

    @Override
    public ItemStack endShoot(ItemStack stack, LivingEntity entity) {
        return shootingBehavior.endShoot(stack, entity);
    }

    @Override
    public boolean canReload(ItemStack stack, LivingEntity entity) {
        return reloadingBehavior.canReload(stack, entity);
    }

    @Override
    public boolean isReloading(ItemStack stack) {
        return reloadingBehavior.isReloading(stack);
    }

    @Override
    public ItemStack beginReload(ItemStack stack, LivingEntity entity) {
        return reloadingBehavior.beginReload(stack, entity);
    }

    @Override
    public ItemStack tickReload(ItemStack stack, LivingEntity entity) {
        return reloadingBehavior.tickReload(stack, entity);
    }

    @Override
    public ItemStack endReload(ItemStack stack, LivingEntity entity) {
        return reloadingBehavior.endReload(stack, entity);
    }

    // -- Shooting

    @Override
    public boolean shootGun(ItemStack itemStack, World world, LivingEntity entity) {
        if (!(entity instanceof PlayerEntity player)) {
            return false;
        }

        boolean isCreative = player.getAbilities().creativeMode;
        ItemStack ammoStack = popNextBullet(itemStack);

        if (ammoStack.isEmpty()) {
            return false;
        }

        if (!world.isClient) {
            createBulletEntities(player, itemStack, ammoStack).forEach(world::spawnEntity);

            if (!isCreative) {
                int damage = itemStack.getDamage() + 1;
                if (damage > itemStack.getMaxDamage()) {
                    damage = itemStack.getMaxDamage();
                }
                itemStack.setDamage(damage);
                if (isBroken(itemStack)) {
                    player.incrementStat(Stats.BROKEN.getOrCreateStat(itemStack.getItem()));
                    player.sendToolBreakStatus(Hand.MAIN_HAND);
                }
            }
        }
        else {
            spawnBulletParticles(player, itemStack, ammoStack);
        }

        player.incrementStat(Stats.USED.getOrCreateStat(this));

        return true;
    }

    public List<? extends Z7BulletEntity> createBulletEntities(PlayerEntity player, ItemStack gunStack, ItemStack ammoStack) {
        if (ammoStack.getItem() instanceof Z7AmmoItem ammoItem) {
            return ammoItem.createBulletEntities(player, gunStack, ammoStack);
        }
        return List.of();
    }

    public void spawnBulletParticles(PlayerEntity player, ItemStack gunStack, ItemStack ammoStack) {
        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();

        double yaw = player.getYaw();
        double theta = (yaw / 180d) * Math.PI;
        double d = Math.cos(theta);
        double f = Math.sin(theta);

        double di = 1;
        for (int i = 0; i < 5; ++i) {
            double g = 0.05 * (double)i;
            player.world.addParticle(ParticleTypes.SMOKE, x + di * d, y, z + di * f, d * g, 0.01f, f * g);
        }

        for (int i = 0; i < 3; ++i) {
            double g = 0.01 * (double)i;
            player.world.addParticle(ParticleTypes.LARGE_SMOKE, x + di * d, y, z + di * f, d * g, 0.01f, f * g);
        }
    }



    // -- Vanilla

    @Override
    public int getItemBarStep(ItemStack stack) {
        float p = 0f;
        if (isReloading(stack)) {
            p = (float) getUsageStage(stack) / (float) getMaxUsageTicks(stack);
        }
        else {
            p = (float) getAmmoInGun(stack).size() / (float) getAmmoCapacity();
            if (getAmmoInGun(stack).size() == 1 && p < 1f/13f) {
                p = 1f/13f;
            }
        }

        return isBroken(stack) ? 13 : Math.round(13f * p);
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return stack.hasNbt() && stack.getNbt().contains(USAGE_STAGE);
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        final float red = 0.0f;
        final float green = 0.33f;
        final float yellow = 1.0f;
        final float orange = 0.1f;

        if (stack.getMaxDamage() == stack.getDamage()) {
            return MathHelper.hsvToRgb(red, 1.0f, 1.0f);
        }
        else if (isReloading(stack)) {
            return MathHelper.hsvToRgb(yellow, 1.0f, 1.0f);
        }
        else if (isReadyToFire(stack) || isInFiringCooldown(stack)) {
            return MathHelper.hsvToRgb(orange, 1.0f, 1.0f);
        }
        else {
            return MathHelper.hsvToRgb(green, 1.0f, 1.0f);
        }
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND
                ? this.attributeModifiers
                : super.getAttributeModifiers(slot);
    }

    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        return false;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (isBroken(stack)) {
            tooltip.add(MutableText.of(Text.of("Broken").getContent()).formatted(Formatting.RED, Formatting.BOLD));
        }
        else if (stack.getDamage() > 0){
            tooltip.add(MutableText.of(Text.of("Durability: " + (stack.getMaxDamage() - stack.getDamage()) + "/" + stack.getMaxDamage()).getContent()).formatted(Formatting.GRAY, Formatting.BOLD));
        }

//        tooltip.add(MutableText.of(Text.of("Gun Stage = " + getGunStage(stack)).getContent()));

        var ammoList = getAmmoInGun(stack);
        if (ammoList.isEmpty()) {
            tooltip.add(MutableText.of(Text.of("Ammo 0/" + getAmmoCapacity()).getContent()).formatted(Formatting.GRAY));
        }
        else {
            tooltip.add(MutableText.of(Text.of("Ammo " + ammoList.size() + "/" + getAmmoCapacity() + ":").getContent()).formatted(Formatting.GRAY));
            var compactList = new ArrayList<ItemStack>();
            for (ItemStack ammoStack : ammoList) {
                if (compactList.isEmpty()) {
                    compactList.add(ammoStack);
                    continue;
                }

                var top = compactList.get(compactList.size() - 1);
                if (ItemStack.canCombine(top, ammoStack)) {
                    top = top.copyWithCount(top.getCount() + ammoStack.getCount());
                    compactList.set(compactList.size() - 1, top);
                }
                else {
                    compactList.add(ammoStack);
                }
            }
            compactList.forEach(a -> {
                int count = a.getCount();
                MutableText text = MutableText.of(Text.of("  " + count + "x ").getContent()).append(Text.translatable(a.getTranslationKey()));
                tooltip.add(text.formatted(Formatting.DARK_GRAY));
            });
        }
    }

    @Override
    public boolean allowNbtUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
        return oldStack.getItem() != newStack.getItem();
    }
}
