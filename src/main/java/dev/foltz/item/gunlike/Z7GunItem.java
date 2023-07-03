package dev.foltz.item.gunlike;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import dev.foltz.entity.Z7BulletEntity;
import dev.foltz.item.Z7ComplexItem;
import dev.foltz.item.ammo.Z7AmmoItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
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

public abstract class Z7GunItem extends Z7ComplexItem implements Z7GunlikeItem{
    public static final String USAGE_STAGE = "UsageStage";
    public static final String GUN_STAGE = "GunStage";
    public static final String LAST_FIRED_TIME = "LastFiredTime";
    public static final String RELOAD_STAGE = "ReloadStage";
    public static final String AMMO_LIST = "AmmoList";
    public final Z7AmmoItem.AmmoCategory ammoCategory;
    public final int firingTicks;
    public final int reloadingTicks;
    public final int ammoCapacity;
    private final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;

    public Z7GunItem(int maxDurability, int firingTicks, int reloadingTicks, int ammoCapacity, Z7AmmoItem.AmmoCategory ammoCategory) {
        super(new FabricItemSettings().maxDamageIfAbsent(maxDurability));
        this.ammoCategory = ammoCategory;
        this.firingTicks = firingTicks;
        this.reloadingTicks = reloadingTicks;
        this.ammoCapacity = ammoCapacity;

        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier", 1d, EntityAttributeModifier.Operation.ADDITION));
        builder.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Weapon modifier", -2d, EntityAttributeModifier.Operation.ADDITION));
        this.attributeModifiers = builder.build();
    }

    @Override
    public boolean isBroken(ItemStack stack) {
        return stack.getDamage() >= stack.getMaxDamage();
    }

    // -- Shooting

    @Override
    public boolean shoot(ItemStack itemStack, World world, LivingEntity entity) {
        if (!(entity instanceof PlayerEntity player)) {
            return false;
        }

        boolean isCreative = player.getAbilities().creativeMode;
        ItemStack ammoStack = popNextBullet(itemStack);

        if (ammoStack.isEmpty()) {
            if (isCreative) {
                ammoStack = ammoCategory.defaultItem.getDefaultStack();
            }
            else {
                return false;
            }
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

        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 0.5f, 2.0f);
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

    public abstract boolean isReadyToFire(ItemStack stack);

    public boolean isFiring(World world, ItemStack stack) {
        NbtCompound nbt = stack.hasNbt() ? stack.getNbt() : new NbtCompound();
        long lastTime = nbt.contains(LAST_FIRED_TIME) ? nbt.getLong(LAST_FIRED_TIME) : 0;
        long diff = world.getTime() - lastTime;
        return diff <= firingTicks;
    }

    public int getFiringTicks(World world, ItemStack stack) {
        if (!isFiring(world, stack)) {
            return 0;
        }

        NbtCompound nbt = stack.getOrCreateNbt();
        long lastTime = nbt.contains(LAST_FIRED_TIME) ? nbt.getLong(LAST_FIRED_TIME) : 0;
        int diff = (int) (world.getTime() - lastTime);
//        System.out.println("FIRING TICKS DIFF = " + diff);
        return diff;
    }

    public ItemStack resetLastFiredTime(World world, ItemStack stack) {
        NbtCompound nbt = stack.hasNbt() ? stack.getNbt() : new NbtCompound();
        nbt.putLong(LAST_FIRED_TIME, world.getTime());
        stack.setNbt(nbt);
        return stack;
    }

    // -- Gun stage

    public int getGunStage(ItemStack stack) {
        NbtCompound nbt = stack.hasNbt() ? stack.getNbt() : new NbtCompound();
        return nbt.contains(GUN_STAGE) ? nbt.getInt(GUN_STAGE) : 0;
    }

    public ItemStack setGunStage(ItemStack stack, int stage) {
        NbtCompound nbt = stack.hasNbt() ? stack.getNbt() : new NbtCompound();
        nbt.putInt(GUN_STAGE, stage);
        stack.setNbt(nbt);
        return stack;
    }

    // -- Usage stage: time left-click is held for (scaled to relative stage usage time),
    //                 clamped to [0, 1] in client-side ModelPredicates.

    public int getMaxUsageTicks(ItemStack stack) {
        return 0;
    }

    public int getUsageTicks(ItemStack stack) {
        NbtCompound nbt = stack.hasNbt() ? stack.getNbt() : new NbtCompound();
        return nbt.contains(USAGE_STAGE) ? nbt.getInt(USAGE_STAGE) : 0;
    }

    public ItemStack updateUsageTicks(ItemStack stack) {
        NbtCompound nbt = stack.hasNbt() ? stack.getNbt() : new NbtCompound();
        int v = nbt.contains(USAGE_STAGE) ? nbt.getInt(USAGE_STAGE) + 1 : 1;
        nbt.putInt(USAGE_STAGE, v);
        stack.setNbt(nbt);
        return stack;
    }

    public ItemStack resetUsageTicks(ItemStack stack) {
        NbtCompound nbt = stack.hasNbt() ? stack.getNbt() : new NbtCompound();
        nbt.putInt(USAGE_STAGE, 0);
        nbt.putInt(RELOAD_STAGE, 0);
        stack.setNbt(nbt);
        return stack;
    }

    // -- Reloading

    @Override
    public ItemStack beginReload(ItemStack stack, World world, LivingEntity entity) {
        if (!(entity instanceof PlayerEntity player) || getGunStage(stack) != 0) {
            return stack;
        }

        if (!canReload(stack, player)) {
            world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.BLOCK_DISPENSER_FAIL, SoundCategory.PLAYERS, 0.8f, 2.0f);
            return resetUsageTicks(stack);
        }

        tryReloading(player, stack);
        return stack;
    }

    @Override
    public ItemStack tickReload(ItemStack stack, World world, LivingEntity entity) {
        if (!(entity instanceof PlayerEntity player) || getGunStage(stack) != 0) {
            return stack;
        }

        if (!canReload(stack, player)) {
            return resetUsageTicks(stack);
        }

        int reloadStage = getReloadStage(stack);
        boolean isNewReloadStage = ((ammoCapacity * reloadStage) / reloadingTicks) != ((ammoCapacity * (reloadStage - 1)) / reloadingTicks);

        if (!isNewReloadStage) {
            return updateReloadTicks(stack);
        }

        int ammoSlot = findAmmoSlot(player, ammoCategory);
        ItemStack ammoStack = player.getInventory().getStack(ammoSlot);
        stack = loadOneBullet(stack, ammoStack, !player.getAbilities().creativeMode);

        // Gun not fully loaded, but no more ammo available.
        if (!hasAmmoInInventory(player)) {
            world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.BLOCK_DISPENSER_DISPENSE, SoundCategory.PLAYERS, 1.0f, 2.0f);
            world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.BLOCK_DISPENSER_FAIL, SoundCategory.PLAYERS, 0.8f, 1.0f);
            return resetUsageTicks(stack);
        }
        // Gun fully loaded.
        else if (getAmmoInGun(stack).size() >= ammoCapacity) {
            world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.BLOCK_DISPENSER_DISPENSE, SoundCategory.PLAYERS, 0.8f, 2.0f);
            world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, SoundCategory.PLAYERS, 0.2f, 3f);
            return resetUsageTicks(stack);
        }
        else {
            world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.BLOCK_DISPENSER_DISPENSE, SoundCategory.PLAYERS, 1.0f, 2.0f);
            return updateReloadTicks(stack);
        }
    }

    @Override
    public ItemStack endReload(ItemStack stack, World world, LivingEntity entity) {
        return resetUsageTicks(stack);
    }

    public ItemStack updateReloadTicks(ItemStack stack) {
        NbtCompound nbt = stack.getOrCreateNbt();
        int v = nbt.contains(RELOAD_STAGE) ? nbt.getInt(RELOAD_STAGE) + 1 : 1;
        nbt.putInt(RELOAD_STAGE, v);
        stack.setNbt(nbt);
        return stack;
    }

    @Override
    public boolean isReloading(ItemStack stack) {
        NbtCompound nbt = stack.hasNbt() ? stack.getNbt() : new NbtCompound();
        int reloadStage = nbt.contains(RELOAD_STAGE) ? nbt.getInt(RELOAD_STAGE) : 0;
        return reloadStage > 0;
    }

    public ItemStack setReloadStage(ItemStack stack, int stage) {
        NbtCompound nbt = stack.hasNbt() ? stack.getNbt() : new NbtCompound();
        nbt.putInt(RELOAD_STAGE, stage);
        stack.setNbt(nbt);
        return stack;
    }

    public int getReloadStage(ItemStack stack) {
        NbtCompound nbt = stack.hasNbt() ? stack.getNbt() : new NbtCompound();
        return nbt.contains(RELOAD_STAGE) ? nbt.getInt(RELOAD_STAGE) : 0;
    }

    public int getInitialReloadStage(ItemStack stack) {
        return Math.round(MathHelper.lerp((float) getAmmoInGun(stack).size() / (float) ammoCapacity, 0.0f, reloadingTicks));
    }

    public boolean tryReloading(PlayerEntity player, ItemStack stack) {
        if (isReloading(stack)) {
            return true;
        }
        else if (canReload(stack, player)) {
            resetUsageTicks(stack);
            setGunStage(stack, 0);
//            setReloadStage(stack, getReloadStage(stack) + 1);
            // If gun has ammo in it already,
            // set reload stage accordingly.
//            int reloadStage = getAmmoInGun(stack).size() / reloadingTicks;
            int reloadStage = getInitialReloadStage(stack);
            setReloadStage(stack, reloadStage + 1);
            return true;
        }
        else {
            return false;
        }
    }

    public int findAmmoSlot(PlayerEntity player, Z7AmmoItem.AmmoCategory ammoCategory) {
        if (player.getMainHandStack().isIn(ammoCategory.tag)) {
            return player.getInventory().selectedSlot;
        }
        else if (player.getOffHandStack().isIn(ammoCategory.tag)) {
            return PlayerInventory.OFF_HAND_SLOT;
        }
        else {
            for (int slot = 0; slot < player.getInventory().main.size(); slot++) {
                ItemStack stack = player.getInventory().getStack(slot);
                if (stack.isIn(ammoCategory.tag)) {
                    return slot;
                }
            }
        }
        return -1;
    }

    public boolean hasAmmoInInventory(PlayerEntity player) {
        return findAmmoSlot(player, ammoCategory) != -1;
    }

    @Override
    public boolean canReload(ItemStack stack, PlayerEntity player) {
        return getAmmoInGun(stack).size() < ammoCapacity && hasAmmoInInventory(player);
    }

    @Override
    public boolean hasAmmoInGun(ItemStack stack) {
        return !getAmmoInGun(stack).isEmpty();
    }

    public List<ItemStack> getAmmoInGun(ItemStack stack) {
        NbtCompound nbt = stack.hasNbt() ? stack.getNbt() : new NbtCompound();
        if (!nbt.contains(AMMO_LIST)) {
            return List.of();
        }

        List<ItemStack> ammoList = nbt.getList(AMMO_LIST, NbtElement.COMPOUND_TYPE)
                .stream()
                .map(e -> (NbtCompound) e)
                .map(ItemStack::fromNbt).toList();

        return ammoList;
    }

    public ItemStack popNextBullet(ItemStack gun) {
        List<ItemStack> ammoInGun = new ArrayList<>(getAmmoInGun(gun));
        if (ammoInGun.isEmpty()) {
            return ItemStack.EMPTY;
        }
        else {
            ItemStack bullet = ammoInGun.get(0);
            ammoInGun.remove(0);
            NbtCompound nbt = gun.getOrCreateNbt();
            NbtList list = new NbtList();
            for (ItemStack stack : ammoInGun) {
                NbtCompound ammoNbt = new NbtCompound();
                stack.writeNbt(ammoNbt);
                list.add(ammoNbt);
            }
            nbt.put(AMMO_LIST, list);
            gun.setNbt(nbt);
            return bullet;
        }
    }

    public ItemStack loadOneBullet(ItemStack gun, ItemStack bulletStack, boolean takeFromBulletStack) {
        List<ItemStack> ammoInGun = new ArrayList<>(getAmmoInGun(gun));
        if (ammoInGun.size() >= ammoCapacity) {
            return gun;
        }
        else {
            ItemStack bullet = takeFromBulletStack ? bulletStack.split(1) : bulletStack.copyWithCount(1);
            ammoInGun.add(bullet);
            NbtCompound nbt = gun.getOrCreateNbt();
            NbtList list = new NbtList();
            for (ItemStack stack : ammoInGun) {
                NbtCompound ammoNbt = new NbtCompound();
                stack.writeNbt(ammoNbt);
                list.add(ammoNbt);
            }
            nbt.put(AMMO_LIST, list);
            gun.setNbt(nbt);
            return gun;
        }
    }


    // -- Vanilla


    @Override
    public int getItemBarStep(ItemStack stack) {
        float p = isReloading(stack)
                ? (float) getReloadStage(stack) / (float) reloadingTicks
                : (float) getAmmoInGun(stack).size() / (float) ammoCapacity;

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
        else if (isReadyToFire(stack)) {
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

        var ammoList = getAmmoInGun(stack);
        if (ammoList.isEmpty()) {
            tooltip.add(MutableText.of(Text.of("Ammo 0/" + ammoCapacity).getContent()).formatted(Formatting.GRAY));
        }
        else {
            tooltip.add(MutableText.of(Text.of("Ammo 0/" + ammoCapacity + ":").getContent()).formatted(Formatting.GRAY));
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
