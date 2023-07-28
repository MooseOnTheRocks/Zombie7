package dev.foltz.entity;

import dev.foltz.Z7Util;
import dev.foltz.Zombie7;
import dev.foltz.entity.grenade.FragGrenadeEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class FragGrenadeEntityRenderer extends EntityRenderer<FragGrenadeEntity> {
    private static final Identifier TEXTURE_PRIMED = new Identifier(Zombie7.MODID, "textures/entity/grenade/frag/primed.png");
    private static final Identifier TEXTURE_INACTIVE = new Identifier(Zombie7.MODID, "textures/entity/grenade/frag/bowling_ball.png");

    public FragGrenadeEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }


    private static void vertex(VertexConsumer buffer, Matrix4f matrix, Matrix3f normalMatrix, int light, float x, int y, int u, int v) {
        buffer.vertex(matrix, x - 0.5f, (float)y - 0.5f, 0.0f).color(255, 255, 255, 255).texture(u, v).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normalMatrix, 0.0f, 1.0f, 0.0f).next();
    }

    @Override
    public void render(FragGrenadeEntity grenadeEntity, float yaw, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light) {
        if (grenadeEntity.age < 2 && this.dispatcher.camera.getFocusedEntity().squaredDistanceTo(grenadeEntity) < 12.25) {
            return;
        }

        int ticksToExplode = grenadeEntity.getFuseTime();
        float s;

        if (!grenadeEntity.isActive()) {
            s = 1.0f;
        }
        else {
            final int tickLimitBulge = Z7Util.ticksFromSeconds(3);
            final int tickLimitExploding = Z7Util.ticksFromSeconds(0.75f);
            float theta = (float) tickLimitBulge - ticksToExplode;
            float p = theta % 20 >= 10 ? 1 : -1;
            if (ticksToExplode > tickLimitBulge) {
                s = 1.0f;
            }
            else if (ticksToExplode < tickLimitExploding) {
                s = MathHelper.map(ticksToExplode, tickLimitExploding, 0, 1.0f, 1.4f);
            }
            else if (p > 0) {
                s = 1.1f;
            }
            else {
                s = 1.0f;
            }
        }

        s *= 0.4;
        matrixStack.push();
        matrixStack.scale(s, s, s);
        matrixStack.translate(0, 0.5f, 0);
        matrixStack.multiply(this.dispatcher.getRotation());
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f));
        MatrixStack.Entry entry = matrixStack.peek();
        Matrix4f matrix4f = entry.getPositionMatrix();
        Matrix3f matrix3f = entry.getNormalMatrix();
        VertexConsumer vertexConsumer = vertexConsumerProvider
                .getBuffer(RenderLayer.getEntityCutout(getTexture(grenadeEntity)));
        vertex(vertexConsumer, matrix4f, matrix3f, light, 0.0f, 0, 0, 1);
        vertex(vertexConsumer, matrix4f, matrix3f, light, 1.0f, 0, 1, 1);
        vertex(vertexConsumer, matrix4f, matrix3f, light, 1.0f, 1, 1, 0);
        vertex(vertexConsumer, matrix4f, matrix3f, light, 0.0f, 1, 0, 0);
        matrixStack.pop();

        super.render(grenadeEntity, yaw, tickDelta, matrixStack, vertexConsumerProvider, light);
    }

    @Override
    public Identifier getTexture(FragGrenadeEntity grenadeEntity) {
        if (grenadeEntity.isActive()) {
            return TEXTURE_PRIMED;
        }
        return TEXTURE_INACTIVE;
    }
}
