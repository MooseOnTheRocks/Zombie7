package dev.foltz.entity.model;

import com.google.common.collect.ImmutableList;
import dev.foltz.entity.Z7GrenadeEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;

public class Z7GrenadeEntityModel extends SinglePartEntityModel<Z7GrenadeEntity> {
    private final ModelPart root;

    public Z7GrenadeEntityModel(ModelPart root) {
        this.root = root;
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("main", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0f, 0.0f, 0.0f, 2.0f, 2.0f, 2.0f).cuboid(0.0f, -4.0f, 0.0f, 2.0f, 2.0f, 2.0f).cuboid(0.0f, 0.0f, -4.0f, 2.0f, 2.0f, 2.0f).cuboid(0.0f, 0.0f, 0.0f, 2.0f, 2.0f, 2.0f).cuboid(2.0f, 0.0f, 0.0f, 2.0f, 2.0f, 2.0f).cuboid(0.0f, 2.0f, 0.0f, 2.0f, 2.0f, 2.0f).cuboid(0.0f, 0.0f, 2.0f, 2.0f, 2.0f, 2.0f), ModelTransform.NONE);
        return TexturedModelData.of(modelData, 64, 32);
    }

    @Override
    public void setAngles(Z7GrenadeEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

    }

    @Override
    public ModelPart getPart() {
        return this.root;
    }
}
