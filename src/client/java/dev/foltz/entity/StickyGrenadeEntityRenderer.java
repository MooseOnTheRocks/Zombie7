package dev.foltz.entity;

import dev.foltz.Z7Util;
import dev.foltz.Zombie7;
import dev.foltz.entity.grenade.StickyGrenadeEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class StickyGrenadeEntityRenderer extends EntityRenderer<StickyGrenadeEntity> {
    private static final Identifier TEXTURE_PRIMED = new Identifier(Zombie7.MODID, "textures/entity/grenade/sticky/primed.png");
    private static final Identifier TEXTURE_INACTIVE = new Identifier(Zombie7.MODID, "textures/entity/grenade/sticky/bowling_ball.png");

    public StickyGrenadeEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }


    private static void vertex(VertexConsumer buffer, Matrix4f matrix, Matrix3f normalMatrix, int light, float x, int y, int u, int v) {
        buffer.vertex(matrix, x - 0.5f, (float)y - 0.5f, 0.0f).color(255, 255, 255, 255).texture(u, v).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normalMatrix, 0.0f, 1.0f, 0.0f).next();
    }

    @Override
    public void render(StickyGrenadeEntity grenadeEntity, float yaw, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light) {
        if (grenadeEntity.age < 2 && this.dispatcher.camera.getFocusedEntity().squaredDistanceTo(grenadeEntity) < 12.25) {
            return;
        }

        int ticksToExplode = grenadeEntity.getFuseTime();
        float s;

        if (!grenadeEntity.isActive()) {
            s = 1.0f;
        }
        else {
            final int tickLimitExploding = Z7Util.ticksFromSeconds(3f);
            if (ticksToExplode > tickLimitExploding) {
                s = 1.0f;
            }
            else {
                s = MathHelper.map(ticksToExplode, tickLimitExploding, 0, 1.0f, 1.4f);
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
    public Identifier getTexture(StickyGrenadeEntity grenadeEntity) {
        if (grenadeEntity.isActive()) {
            return TEXTURE_PRIMED;
        }
        return TEXTURE_INACTIVE;
    }
}
