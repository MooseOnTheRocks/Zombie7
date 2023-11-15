package dev.foltz;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public abstract class AccuracyCalculator {
    public static float calculateAccuracy(ItemStack stack, PlayerEntity player) {
        float maxVelocity = 0.6f;
        float dx = (float) (player.getX() - player.prevX);
        float dy = (float) (player.getX() - player.prevX);
        float dz = (float) (player.getX() - player.prevX);
        float playerSpeed = (float) Math.sqrt(dx*dx + dy*dy + dz*dz);
        float normalizedSpeed = Math.min(playerSpeed, maxVelocity) / maxVelocity;
        float speedWeight = 1.5f;
        float speedFactor = speedWeight * (1 - normalizedSpeed);

        float groundWeight = 0.5f;
        float groundFactor = groundWeight * (player.isOnGround() ? 1f : 0f);

        float crouchWeight = 0.5f;
        float crouchFactor = crouchWeight * (player.isSneaking() ? 1f : 0f);

        float aimWeight = 1.0f;
        float aimFactor = aimWeight * (player.isUsingItem() ? 1f : 0f);

//        System.out.println("== FACTORS");
//        System.out.println("  speed =" + speedFactor);
//        System.out.println("  ground=" + groundFactor);
//        System.out.println("  crouch=" + crouchFactor);
//        System.out.println("  aim   =" + crouchFactor);
//        System.out.println("speed=" + speedFactor + "|ground=" + groundFactor + "|crouch=" + crouchFactor + "|aim=" + aimFactor);
//        System.out.println("PLAYER SPEED = " + player.getVelocity().length());

        float sumOfFactors = speedFactor + groundFactor + crouchFactor + aimFactor;
        float sumOfWeights = speedWeight + groundWeight + crouchWeight + aimWeight;
        return 1 - sumOfFactors / sumOfWeights;
    }
}
