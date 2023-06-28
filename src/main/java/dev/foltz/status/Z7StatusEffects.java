package dev.foltz.status;

import dev.foltz.Zombie7;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Z7StatusEffects {
    public static final UUID CONCUSSION_ID = UUID.fromString("37ca822b-b45b-48db-b53a-56f904293e67");
    public static final UUID CONCUSSION_LONG_ID = UUID.fromString("37ca822b-b45b-48db-b53a-56f904293e67");
    public static final UUID BROKEN_BONE_ID = UUID.fromString("43b8fca5-2be7-4228-93b7-0aeb6d77ce7e");

    private static final Map<String, StatusEffect> ALL_STATUS_EFFECTS = new HashMap<>();
    public static final Z7HealingStatusEffect STATUS_EFFECT_HEALING = registerStatusEffect("healing", new Z7HealingStatusEffect());
    public static final StatusEffect STATUS_EFFECT_BLEEDING = registerStatusEffect("bleeding", new Z7BleedingStatusEffect().addAttributeModifier(
            EntityAttributes.GENERIC_MOVEMENT_SPEED, CONCUSSION_LONG_ID.toString(), -0.1f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL
    ));
    public static final StatusEffect STATUS_EFFECT_BLEEDING_LONG = registerStatusEffect("bleeding_long", new Z7BleedingLongStatusEffect());
    public static final StatusEffect STATUS_EFFECT_INFECTION = registerStatusEffect("infection", new Z7InfectionStatusEffect());
    public static final StatusEffect STATUS_EFFECT_CONCUSSION_LONG = registerStatusEffect("concussion_long", new Z7ConcussionLongStatusEffect().addAttributeModifier(
            EntityAttributes.GENERIC_MOVEMENT_SPEED, CONCUSSION_LONG_ID.toString(), -0.1f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL
    ));
    public static final StatusEffect STATUS_EFFECT_CONCUSSION = registerStatusEffect("concussion", new Z7ConcussionStatusEffect().addAttributeModifier(
            EntityAttributes.GENERIC_MOVEMENT_SPEED, CONCUSSION_ID.toString(), -0.2f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL
    ));
    public static final StatusEffect STATUS_EFFECT_BROKEN_BONE = registerStatusEffect("broken_bone", new Z7BrokenBoneStatusEffect().addAttributeModifier(
            EntityAttributes.GENERIC_MOVEMENT_SPEED, BROKEN_BONE_ID.toString(), -0.3f, EntityAttributeModifier.Operation.MULTIPLY_TOTAL
    ));


    private static <T extends StatusEffect> T registerStatusEffect(String name, T effect) {
        ALL_STATUS_EFFECTS.put(name, effect);
        return effect;
    }


    public static void registerAllStatusEffects() {
        for (var entry : ALL_STATUS_EFFECTS.entrySet()) {
            var identifier = new Identifier(Zombie7.MODID, entry.getKey());
            Registry.register(Registries.STATUS_EFFECT, identifier, entry.getValue());
        }
    }
}
