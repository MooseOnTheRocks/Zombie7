package dev.foltz;

import dev.foltz.item.Z7Items;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelProviderException;
import net.fabricmc.fabric.api.client.model.ModelResourceProvider;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class Z7ModelResourceProvider implements ModelResourceProvider {
    public static final Identifier PISTOL_DEAGLE = new Identifier(Zombie7.MODID, "item/gun/deagle/default");

    // TODO: Use ModelLoadingPlugin instead: https://github.com/FabricMC/fabric/tree/1.20.2/fabric-model-loading-api-v1/src/testmodClient/java/net/fabricmc/fabric/test/model/loading
    @Override
    public @Nullable UnbakedModel loadModelResource(Identifier resourceId, ModelProviderContext context) throws ModelProviderException {
        if (resourceId.getNamespace().equals(Zombie7.MODID)) {
            System.out.println("loadModelResource for " + resourceId);
        }

        return null;
    }
}
