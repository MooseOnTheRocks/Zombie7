package dev.foltz.entity;

import dev.foltz.item.grenade.Z7StickyGrenadeItem;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static dev.foltz.Zombie7.MODID;

public class Z7Entities {
    public static final EntityType<Z7FragGrenadeEntity> FRAG_GRENADE_ENTITY = Registry.register(
        Registries.ENTITY_TYPE,
        new Identifier(MODID, "grenade_frag"),
        FabricEntityTypeBuilder.create().entityFactory(Z7FragGrenadeEntity::new).dimensions(EntityDimensions.fixed(0.5f, 0.5f)).build()
    );

    public static final EntityType<Z7ContactGrenadeEntity> CONTACT_GRENADE_ENTITY = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(MODID, "grenade_contact"),
            FabricEntityTypeBuilder.create().entityFactory(Z7ContactGrenadeEntity::new).dimensions(EntityDimensions.fixed(0.5f, 0.5f)).build()
    );

    public static final EntityType<Z7MolotovGrenadeEntity> MOLOTOV_GRENADE_ENTITY = Registry.register(
        Registries.ENTITY_TYPE,
        new Identifier(MODID, "grenade_molotov"),
        FabricEntityTypeBuilder.create().entityFactory(Z7MolotovGrenadeEntity::new).dimensions(EntityDimensions.fixed(0.5f, 0.5f)).build()
    );

    public static final EntityType<Z7StickyGrenadeEntity> STICKY_GRENADE_ENTITY = Registry.register(
        Registries.ENTITY_TYPE,
        new Identifier(MODID, "grenade_sticky"),
        FabricEntityTypeBuilder.create().entityFactory(Z7StickyGrenadeEntity::new).dimensions(EntityDimensions.fixed(0.5f, 0.5f)).build()
    );

    public static final EntityType<Z7BowlingBallGrenadeEntity> BOWLING_BALL_GRENADE_ENTITY = Registry.register(
        Registries.ENTITY_TYPE,
        new Identifier(MODID, "grenade_bowling_ball"),
        FabricEntityTypeBuilder.create().entityFactory(Z7BowlingBallGrenadeEntity::new).dimensions(EntityDimensions.fixed(0.8f, 0.8f)).build()
    );

    public static final EntityType<Z7BulletBronzeEntity> BULLET_BRONZE_ENTITY = Registry.register(
        Registries.ENTITY_TYPE,
        new Identifier(MODID, "bullet_bronze"),
        FabricEntityTypeBuilder.create().entityFactory(Z7BulletBronzeEntity::new).dimensions(EntityDimensions.fixed(0.15f, 0.15f)).build()
    );

    public static final EntityType<Z7BulletLeadEntity> BULLET_LEAD_ENTITY = Registry.register(
        Registries.ENTITY_TYPE,
        new Identifier(MODID, "bullet_lead"),
        FabricEntityTypeBuilder.create().entityFactory(Z7BulletLeadEntity::new).dimensions(EntityDimensions.fixed(0.15f, 0.15f)).build()
    );

    public static void registerAllEntities() {
    }
}
