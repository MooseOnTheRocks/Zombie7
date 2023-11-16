package dev.foltz;

import dev.foltz.item.gun.GunStagedItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public abstract class AccuracyCalculator {
    public static float calculateAccuracy(ItemStack stack, PlayerEntity player) {
        if (!(stack.getItem() instanceof GunStagedItem<?> gun)) {
            return 0;
        }

        float maxVelocity = 0.6f;
        float dx = (float) (player.getX() - player.prevX);
        float dy = (float) (player.getX() - player.prevX);
        float dz = (float) (player.getX() - player.prevX);
        float playerSpeed = (float) Math.sqrt(dx*dx + dy*dy + dz*dz);
        float normalizedSpeed = Math.min(playerSpeed, maxVelocity) / maxVelocity;
        float speedWeight = 2.0f;
        float speedFactor = speedWeight * (1 - normalizedSpeed);

        float groundWeight = 0.5f;
        float groundFactor = groundWeight * (player.isOnGround() ? 1f : 0f);

        float crouchWeight = 0.5f;
        float crouchFactor = crouchWeight * (player.isSneaking() ? 1f : 0f);

        float aimWeight = 1.5f;
        float aimFactor = aimWeight * (player.isUsingItem() ? 1f : 0f);

        boolean isFiring = gun.getStageName(stack).equals("firing");
        float recoilFactor = 0.0f;
        if (isFiring && gun.getMaxStageTicks(stack) > 0) {
            float p = gun.getStageTicks(stack) / (float) gun.getMaxStageTicks(stack);
            recoilFactor = -2.0f * (float) Math.pow(1 - p, 0.5);
        }
//
//        System.out.println("STAGE = " + gun.getStageName(stack));
//        System.out.println("TICKS = " + gun.getStageTicks(stack) + " / " + gun.getMaxStageTicks(stack));

//        System.out.println("== FACTORS");
//        System.out.println("  speed =" + speedFactor);
//        System.out.println("  ground=" + groundFactor);
//        System.out.println("  crouch=" + crouchFactor);
//        System.out.println("  aim   =" + crouchFactor);
//        System.out.println("  recoil=" + recoilFactor);
//        System.out.println("recoil=" + recoilFactor + "|speed=" + speedFactor + "|ground=" + groundFactor + "|crouch=" + crouchFactor + "|aim=" + aimFactor);
//        System.out.println("PLAYER SPEED = " + player.getVelocity().length());

        float sumOfFactors = speedFactor + groundFactor + crouchFactor + aimFactor + recoilFactor;
        float sumOfWeights = speedWeight + groundWeight + crouchWeight + aimWeight;
        return 1 - MathHelper.clamp(sumOfFactors / sumOfWeights, 0, 1);
    }
}
