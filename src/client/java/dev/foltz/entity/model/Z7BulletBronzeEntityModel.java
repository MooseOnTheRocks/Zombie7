package dev.foltz.entity.model;

import dev.foltz.entity.Z7BulletBronzeEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;

public class Z7BulletBronzeEntityModel extends SinglePartEntityModel<Z7BulletBronzeEntity> {
    private final ModelPart root;

    public Z7BulletBronzeEntityModel(ModelPart root) {
        this.root = root;
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("main",
            ModelPartBuilder.create()
                    .uv(0, 0)
                    // Small bullet model
                    .cuboid(-2f, 0.5f, -0.5f, 4f, 1.0f, 1.0f),
                    // Big bullet model
//                    .cuboid(-3f, 1.0f, -1.0f, 6f, 2.0f, 2.0f),
                ModelTransform.NONE);
        return TexturedModelData.of(modelData, 64, 32);
    }

    @Override
    public void setAngles(Z7BulletBronzeEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

    }

    @Override
    public ModelPart getPart() {
        return this.root;
    }
}
