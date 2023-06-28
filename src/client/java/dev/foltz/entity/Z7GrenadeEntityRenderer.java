package dev.foltz.entity;

import dev.foltz.Zombie7;
import dev.foltz.Zombie7Client;
import dev.foltz.entity.model.Z7GrenadeEntityModel;
import dev.foltz.item.Z7Items;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class Z7GrenadeEntityRenderer extends EntityRenderer<Z7GrenadeEntity> {
//    private static final Identifier TEXTURE = new Identifier(Zombie7.MODID, "textures/entity/grenade/grenade.png");
//    private final Z7GrenadeEntityModel model;
    private final ItemRenderer itemRenderer;
    private final float scale;
    private final boolean lit;

    public Z7GrenadeEntityRenderer(EntityRendererFactory.Context context, float scale, boolean lit) {
        super(context);
//        this.model = new Z7GrenadeEntityModel(context.getPart(Zombie7Client.MODEL_GRENADE_LAYER));
        this.itemRenderer = context.getItemRenderer();
        this.scale = scale;
        this.lit = lit;
    }

    @Override
    protected int getBlockLight(Z7GrenadeEntity entity, BlockPos pos) {
        return this.lit ? 15 : super.getBlockLight(entity, pos);
    }


    @Override
    public void render(Z7GrenadeEntity grenadeEntity, float yaw, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light) {
        if (((Entity)grenadeEntity).age < 2 && this.dispatcher.camera.getFocusedEntity().squaredDistanceTo(grenadeEntity) < 12.25) {
            return;
        }
        matrixStack.push();
        matrixStack.scale(this.scale, this.scale, this.scale);
        matrixStack.multiply(this.dispatcher.getRotation());
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f));
        this.itemRenderer.renderItem(Z7Items.ITEM_GRENADE.getDefaultStack(), ModelTransformationMode.GROUND, light, OverlayTexture.DEFAULT_UV, matrixStack, vertexConsumerProvider, ((Entity)grenadeEntity).world, grenadeEntity.getId());
        matrixStack.pop();
        super.render(grenadeEntity, yaw, tickDelta, matrixStack, vertexConsumerProvider, light);
    }

    @Override
    public Identifier getTexture(Z7GrenadeEntity grenadeEntity) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
    }
}
