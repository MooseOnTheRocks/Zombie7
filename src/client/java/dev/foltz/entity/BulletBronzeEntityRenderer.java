package dev.foltz.entity;

import dev.foltz.Zombie7;
import dev.foltz.Zombie7Client;
import dev.foltz.entity.bullet.BulletBronzeEntity;
import dev.foltz.entity.model.BulletBronzeEntityModel;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class BulletBronzeEntityRenderer extends EntityRenderer<BulletBronzeEntity> {
    private static final Identifier TEXTURE = new Identifier(Zombie7.MODID, "textures/entity/bullet/bronze.png");
    private final BulletBronzeEntityModel model;

    public BulletBronzeEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.model = new BulletBronzeEntityModel(context.getPart(Zombie7Client.MODEL_BULLET_BRONZE_LAYER));
    }

    @Override
    public void render(BulletBronzeEntity bulletEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        matrixStack.push();
        matrixStack.translate(0.0f, 0.0f, 0.0f);
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MathHelper.lerp((float)g, (float)bulletEntity.prevYaw, (float)bulletEntity.getYaw()) - 90.0f));
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(MathHelper.lerp((float)g, (float)bulletEntity.prevPitch, (float)bulletEntity.getPitch())));
        this.model.setAngles(bulletEntity, g, 0.0f, -0.1f, 0.0f, 0.0f);
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(this.model.getLayer(TEXTURE));
        this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, 1.0f, 1.0f, 1.0f, 1.0f);
        matrixStack.pop();
        super.render(bulletEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    @Override
    public Identifier getTexture(BulletBronzeEntity grenadeEntity) {
        return TEXTURE;
    }
}
