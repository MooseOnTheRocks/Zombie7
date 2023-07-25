package dev.foltz.item.gun;

import dev.foltz.entity.Z7BulletEntity;
import dev.foltz.item.ammo.Z7AmmoItem;
import dev.foltz.item.stage.*;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
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
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GunStagedItem extends StagedItem {
    public static final String AMMO_LIST = "AmmoList";

    public final Z7AmmoItem.AmmoCategory ammoCategory;
    public final int maxAmmoCapacity;

    public GunStagedItem(int maxDurability, Z7AmmoItem.AmmoCategory ammoCategory, int maxAmmoCapacity, Map<String, StageBuilder> stagesMap) {
        super(new FabricItemSettings().maxDamage(maxDurability), new StagedItemGraphBuilder(stagesMap).build());
        this.ammoCategory = ammoCategory;
        this.maxAmmoCapacity = maxAmmoCapacity;
    }

    public GunStagedItem(int maxDurability, Z7AmmoItem.AmmoCategory ammoCategory, int maxAmmoCapacity, StagedItemGraph graph) {
        super(new FabricItemSettings().maxDamage(maxDurability), graph);
        this.ammoCategory = ammoCategory;
        this.maxAmmoCapacity = maxAmmoCapacity;
    }

    public static StagedItemEventHandler<GunStagedItem> tryFireReset(String stageShooting) {
        return view -> {
            if (view.isPressingShoot() && view.item.hasAmmoInGun(view.stack)) {
                view.item.setStageTicks(view.stack, 0);
                return doFire().handleEvent(view);
            }
            return view.stageId;
        };
    }

    public static StagedItemEventHandler<GunStagedItem> tryShootOrReloadInit(String stageShooting, String stageReloading) {
        return view -> {
//            System.out.println("tryShootOrReloadInit: " + view.isPressingShoot() + " | " + view.isPressingReload());
            if (view.isPressingShoot() || view.isPressingReload()) {
                return tryShootOrReload(stageShooting, stageReloading).handleEvent(view);
            }
            else {
                return view.stageId;
            }
        };
    }


    public static StagedItemEventHandler<GunStagedItem> tryShootOrReload(String stageShooting, String stageReloading) {
        return view -> {
            if (view.isPressingShoot() && view.item.hasAmmoInGun(view.stack)) {
                return stageShooting;
            }
            else {
                return tryReload(stageReloading).handleEvent(view);
            }
        };
    }

    public static StagedItemEventHandler<GunStagedItem> tryReloadInit(String stageReloading) {
        return view -> {
            if ((view.isPressingShoot() || view.isPressingReload()) && view.item.hasRoomInGun(view.stack) && view.item.hasAmmoInInventory(view.entity)) {
                return stageReloading;
            }
            else {
                return view.stageId;
            }
        };
    }

    public static StagedItemEventHandler<GunStagedItem> tryReload(String stageReloading) {
        return view -> {
            if ((view.isPressingShoot() || view.isPressingReload()) && view.item.hasRoomInGun(view.stack) && view.item.hasAmmoInInventory(view.entity)) {
                return stageReloading;
            }
            else {
                return doCancel(view.stageId).handleEvent(view);
            }
        };
    }

    public static StagedItemEventHandler<GunStagedItem> tryReloadWholeClip(String stageDefault) {
        return view -> {
            var stack = view.stack;

            if (view.entity instanceof PlayerEntity player) {
                for (int i = view.item.getAmmoInGun(stack).size(); i < view.item.getMaxAmmoCapacity(stack); i++) {
                    int slot = view.item.ammoCategory.findAmmoSlot(player);
                    if (slot == -1) {
                        break;
                    }
                    ItemStack ammoStack = player.getInventory().getStack(slot);
                    view.item.loadOneBullet(stack, ammoStack, !player.getAbilities().creativeMode);
                }
            }
            else {
                for (int i = view.item.getAmmoInGun(stack).size(); i < view.item.getMaxAmmoCapacity(stack); i++) {
                    view.item.loadOneBullet(stack, view.item.ammoCategory.defaultItem.get().getDefaultStack(), false);
                }
            }

            view.item.playSoundReloadEnd(stack, view.entity);
            return stageDefault;
        };
    }

    public static StagedItemEventHandler<GunStagedItem> tryReloadOneBullet(String stageDefault) {
        return view -> {
            var stack = view.stack;

            if (!(view.entity instanceof PlayerEntity player)) {
                return stageDefault;
            }

            int usageTicks = view.item.getStageTicks(stack);
            int maxStageTicks = view.item.getMaxStageTicks(stack);
            int loadingAmmoStage = (int) (view.item.getMaxAmmoCapacity(stack) * (usageTicks / (float) maxStageTicks));

            // No available bullets to load.
            int slot = view.item.ammoCategory.findAmmoSlot(player);
            if (slot == -1) {
                return doCancel(stageDefault).handleEvent(view);
            }

            // Loading final bullet.
            if (usageTicks >= maxStageTicks) {
                ItemStack ammoStack = player.getInventory().getStack(slot);
                view.item.loadOneBullet(stack, ammoStack, !player.getAbilities().creativeMode);
                view.item.playSoundReloadEnd(stack, player);
                return stageDefault;
            }
            // Loading a bullet.
            else if (loadingAmmoStage > view.item.getAmmoInGun(stack).size()) {
                ItemStack ammoStack = player.getInventory().getStack(view.item.ammoCategory.findAmmoSlot(player));
                view.item.loadOneBullet(stack, ammoStack, !player.getAbilities().creativeMode);
                view.item.playSoundReloadStep(stack, player);
            }

            return view.stageId;
        };
    }

    public static StagedItemEventHandler<GunStagedItem> doFire() {
        return view -> {
            view.item.shootGun(view.stack, view.world, view.entity);
            view.item.playSoundFireBegin(view.stack, view.entity);
            return view.stageId;
        };
    }

    public static StagedItemEventHandler<GunStagedItem> doCancel(String stageDefault) {
        return view -> {
            view.item.playSoundReloadCancel(view.stack, view.entity);
            // Note: Could cancel pressingShoot as well.
            view.playerState.setPressingReload(false);
            return stageDefault;
        };
    }


    public static StagedItemEventHandler<GunStagedItem> doReady(String stageReady) {
        return view -> {
            view.item.playSoundReadyToFire(view.stack, view.entity);
            return stageReady;
        };
    }

    public void playSoundFireBegin(ItemStack stack, Entity entity) {
        entity.world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS, 0.3f, 2.0f);
    }

    public void playSoundPreFireStep(ItemStack stack, Entity entity) {
        entity.world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.PLAYERS, 0.25f, 4.0f);
    }

    public void playSoundReadyToFire(ItemStack stack, Entity entity) {
        entity.world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.PLAYERS, 0.25f, 8.0f);
    }

    public void playSoundReloadBegin(ItemStack stack, Entity entity) {
        entity.world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ITEM_ARMOR_EQUIP_IRON, SoundCategory.PLAYERS, 0.5f, 3.0f);
    }

    public void playSoundReloadStep(ItemStack stack, Entity entity) {
        entity.world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.PLAYERS, 0.5f, 1.0f);
    }

    public void playSoundReloadEnd(ItemStack stack, Entity entity) {
        entity.world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.PLAYERS, 0.5f, 1.0f);
        entity.world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, SoundCategory.PLAYERS, 0.5f, 1.0f);
    }

    public void playSoundReloadCancel(ItemStack stack, Entity entity) {
        entity.world.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.ITEM_CROSSBOW_LOADING_END, SoundCategory.PLAYERS, 0.5f, 3.0f);
    }


    
    public boolean hasAmmoInInventory(Entity entity) {
        return entity instanceof PlayerEntity player && ammoCategory.findAmmoSlot(player) != -1;
    }

    public boolean hasAmmoInGun(ItemStack stack) {
        return !getAmmoInGun(stack).isEmpty();
    }

    public boolean hasRoomInGun(ItemStack stack) {
        return getAmmoInGun(stack).size() < getMaxAmmoCapacity(stack);
    }

    public List<ItemStack> getAmmoInGun(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        if (nbt == null || !nbt.contains(AMMO_LIST)) {
            return List.of();
        }
        else {
            return nbt.getList(AMMO_LIST, NbtElement.COMPOUND_TYPE)
                .stream()
                .map(e -> (NbtCompound) e)
                .map(ItemStack::fromNbt).toList();
        }
    }



    public int getMaxAmmoCapacity(ItemStack stack) {
        return maxAmmoCapacity;
    }


    public ItemStack loadOneBullet(ItemStack gun, ItemStack bulletStack, boolean takeFromBulletStack) {
        if (!hasRoomInGun(gun)) {
            return gun;
        }
        List<ItemStack> ammoInGun = new ArrayList<>(getAmmoInGun(gun));
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

    public ItemStack popNextBullet(ItemStack gun) {
        if (!hasAmmoInGun(gun)) {
            return ItemStack.EMPTY;
        }

        List<ItemStack> ammoInGun = new ArrayList<>(getAmmoInGun(gun));
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


    public boolean shootGun(ItemStack itemStack, World world, Entity entity) {
        if (!(entity instanceof PlayerEntity player)) {
            return false;
        }

        System.out.println("Shooting gun!");

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

    public boolean isBroken(ItemStack stack) {
        return stack.getDamage() >= stack.getMaxDamage();
    }

    public float getModifiedBulletDamage(ItemStack gunStack, ItemStack bulletStack, float damage) {
        return damage;
    }

    public float getModifiedBulletSpeed(ItemStack gunStack, ItemStack bulletStack, float speed) {
        return speed;
    }

    public float getModifiedBulletBaseRange(ItemStack gunStack, ItemStack bulletStack, float range) {
        return range;
    }

    public float getModifiedBulletAccuracy(ItemStack gunStack, ItemStack bulletStack, float accuracy) {
        return accuracy;
    }

    // -- Vanilla

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return !(getStageName(stack).equals("default") && getAmmoInGun(stack).size() == 0);
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        var stage = getStage(stack);
        float p = stage.barProgress(stack);
        if (p < 0) {
            p = (float) getAmmoInGun(stack).size() / (float) getMaxAmmoCapacity(stack);
            if (getAmmoInGun(stack).size() == 1 && p < 1f/13f) {
                return 1;
            }
            else {
                return Math.round(13f * p);
            }
        }
        else {
            return Math.round(13f * p);
        }
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
        if (stack.getDamage() > 0){
            tooltip.add(MutableText.of(Text.of("Durability: " + (stack.getMaxDamage() - stack.getDamage()) + "/" + stack.getMaxDamage()).getContent()).formatted(Formatting.GRAY, Formatting.BOLD));
        }

//        tooltip.add(MutableText.of(Text.of("Gun Stage (" + getStageId(stack) + ") = " + stagesGraph.nameFromId(getStageId(stack))).getContent()));

        var ammoList = getAmmoInGun(stack);
        if (getMaxAmmoCapacity(stack) == 0);
        else if (ammoList.isEmpty()) {
            tooltip.add(MutableText.of(Text.of("Ammo 0/" + getMaxAmmoCapacity(stack)).getContent()).formatted(Formatting.GRAY));
        }
        else {
            tooltip.add(MutableText.of(Text.of("Ammo " + ammoList.size() + "/" + getMaxAmmoCapacity(stack) + ":").getContent()).formatted(Formatting.GRAY));
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

            compactList.forEach(a -> tooltip.add(MutableText.of(Text.of("  " + a.getCount() + "x ").getContent()).append(Text.translatable(a.getTranslationKey())).formatted(Formatting.DARK_GRAY)));
        }
    }

    @Override
    public boolean allowNbtUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
        return oldStack.getItem() != newStack.getItem();
    }
}
