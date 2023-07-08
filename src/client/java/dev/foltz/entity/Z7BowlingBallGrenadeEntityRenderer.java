package dev.foltz.entity;

import dev.foltz.Z7Util;
import dev.foltz.Zombie7;
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

public class Z7BowlingBallGrenadeEntityRenderer extends EntityRenderer<Z7BowlingBallGrenadeEntity> {
    private static final Identifier TEXTURE = new Identifier(Zombie7.MODID, "textures/entity/grenade/bowling_ball/default.png");

    private static final Identifier[] BOWLING_TEXTURES = generateBowlingTextures();

    public Z7BowlingBallGrenadeEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
    }

    private static Identifier[] generateBowlingTextures() {
        Identifier[] array = new Identifier[22];
        array[array.length - 1] = new Identifier(Zombie7.MODID, "textures/entity/grenade/bowling_ball/backside.png");
        for (int i = 0; i <= 20; i++) {
            array[i] = new Identifier(Zombie7.MODID, "textures/entity/grenade/bowling_ball/" + i + ".png");
        }
        return array;
    }

    private static void vertex(VertexConsumer buffer, Matrix4f matrix, Matrix3f normalMatrix, int light, float x, int y, int u, int v) {
        buffer.vertex(matrix, x - 0.5f, (float)y - 0.5f, 0.0f).color(255, 255, 255, 255).texture(u, v).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(normalMatrix, 0.0f, 1.0f, 0.0f).next();
    }

    @Override
    public void render(Z7BowlingBallGrenadeEntity grenadeEntity, float yaw, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light) {
        if (grenadeEntity.age < 2 && this.dispatcher.camera.getFocusedEntity().squaredDistanceTo(grenadeEntity) < 12.25) {
            return;
        }


        float s = 0.8f;

//        s *= 0.4;
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
    public Identifier getTexture(Z7BowlingBallGrenadeEntity bowlingBallEntity) {
        int index = (int) (Math.floor(bowlingBallEntity.distanceTraveled * 8f)) % 34;
        if (index > BOWLING_TEXTURES.length - 1) {
            index = BOWLING_TEXTURES.length - 1;
        }

        return BOWLING_TEXTURES[index];
    }
}
