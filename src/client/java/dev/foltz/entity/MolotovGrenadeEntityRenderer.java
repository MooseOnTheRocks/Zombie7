package dev.foltz.entity;

import dev.foltz.Zombie7;
import dev.foltz.entity.grenade.MolotovGrenadeEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class MolotovGrenadeEntityRenderer extends EntityRenderer<MolotovGrenadeEntity> {
    private static final Identifier TEXTURE_PRIMED = new Identifier(Zombie7.MODID, "textures/entity/grenade/molotov/lit.png");
    private static final Identifier TEXTURE_INACTIVE = new Identifier(Zombie7.MODID, "textures/entity/grenade/molotov/bowling_ball.png");

    public MolotovGrenadeEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }


    private static void vertex(VertexConsumer buffer, Matrix4f matrix, Matrix3f normalMatrix, int light, float x, int y, int u, int v) {
        buffer.vertex(matrix, x - 0.5f, (float)y - 0.5f, 0.0f).color(255, 255, 255, 255).texture(u, v).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normalMatrix, 0.0f, 1.0f, 0.0f).next();
    }

    @Override
    public void render(MolotovGrenadeEntity grenadeEntity, float yaw, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light) {
        if (grenadeEntity.age < 2 && this.dispatcher.camera.getFocusedEntity().squaredDistanceTo(grenadeEntity) < 12.25) {
            return;
        }

        float s = 1.0f;

        s *= 0.4;
        matrixStack.push();
        matrixStack.scale(s, s, s);
        matrixStack.translate(0, 0.5f, 0);
        matrixStack.multiply(this.dispatcher.getRotation());
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f));
        float theta = 0f;
        if (grenadeEntity.isMoving()) {
            theta = 0.5f * (grenadeEntity.getWorld().getTime() / 20f) * 360f;
        }
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(theta));
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
    public Identifier getTexture(MolotovGrenadeEntity grenadeEntity) {
        if (grenadeEntity.isActive()) {
            return TEXTURE_PRIMED;
        }
        return TEXTURE_INACTIVE;
    }
}
