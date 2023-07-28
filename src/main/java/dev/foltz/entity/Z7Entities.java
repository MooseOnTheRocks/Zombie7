package dev.foltz.entity;

import dev.foltz.entity.bullet.BulletBronzeEntity;
import dev.foltz.entity.bullet.BulletLeadEntity;
import dev.foltz.entity.grenade.ContactGrenadeEntity;
import dev.foltz.entity.grenade.FragGrenadeEntity;
import dev.foltz.entity.grenade.MolotovGrenadeEntity;
import dev.foltz.entity.grenade.StickyGrenadeEntity;
import dev.foltz.entity.misc.BowlingBallGrenadeEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static dev.foltz.Zombie7.MODID;

public abstract class Z7Entities {
    public static final EntityType<FragGrenadeEntity> FRAG_GRENADE_ENTITY = Registry.register(
        Registries.ENTITY_TYPE,
        new Identifier(MODID, "grenade_frag"),
        FabricEntityTypeBuilder.create().entityFactory(FragGrenadeEntity::new).dimensions(EntityDimensions.fixed(0.5f, 0.5f)).build()
    );

    public static final EntityType<ContactGrenadeEntity> CONTACT_GRENADE_ENTITY = Registry.register(
            Registries.ENTITY_TYPE,
            new Identifier(MODID, "grenade_contact"),
            FabricEntityTypeBuilder.create().entityFactory(ContactGrenadeEntity::new).dimensions(EntityDimensions.fixed(0.5f, 0.5f)).build()
    );

    public static final EntityType<MolotovGrenadeEntity> MOLOTOV_GRENADE_ENTITY = Registry.register(
        Registries.ENTITY_TYPE,
        new Identifier(MODID, "grenade_molotov"),
        FabricEntityTypeBuilder.create().entityFactory(MolotovGrenadeEntity::new).dimensions(EntityDimensions.fixed(0.5f, 0.5f)).build()
    );

    public static final EntityType<StickyGrenadeEntity> STICKY_GRENADE_ENTITY = Registry.register(
        Registries.ENTITY_TYPE,
        new Identifier(MODID, "grenade_sticky"),
        FabricEntityTypeBuilder.create().entityFactory(StickyGrenadeEntity::new).dimensions(EntityDimensions.fixed(0.5f, 0.5f)).build()
    );

    public static final EntityType<BowlingBallGrenadeEntity> BOWLING_BALL_GRENADE_ENTITY = Registry.register(
        Registries.ENTITY_TYPE,
        new Identifier(MODID, "grenade_bowling_ball"),
        FabricEntityTypeBuilder.create().entityFactory(BowlingBallGrenadeEntity::new).dimensions(EntityDimensions.fixed(0.8f, 0.8f)).build()
    );

    public static final EntityType<BulletBronzeEntity> BULLET_BRONZE_ENTITY = Registry.register(
        Registries.ENTITY_TYPE,
        new Identifier(MODID, "bullet_bronze"),
        FabricEntityTypeBuilder.create().entityFactory(BulletBronzeEntity::new).dimensions(EntityDimensions.fixed(0.15f, 0.15f)).build()
    );

    public static final EntityType<BulletLeadEntity> BULLET_LEAD_ENTITY = Registry.register(
        Registries.ENTITY_TYPE,
        new Identifier(MODID, "bullet_lead"),
        FabricEntityTypeBuilder.create().entityFactory(BulletLeadEntity::new).dimensions(EntityDimensions.fixed(0.15f, 0.15f)).build()
    );

    public static void registerAllEntities() {
    }
}
