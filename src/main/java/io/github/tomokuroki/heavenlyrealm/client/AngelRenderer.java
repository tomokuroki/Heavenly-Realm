package io.github.tomokuroki.heavenlyrealm.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import io.github.tomokuroki.heavenlyrealm.AngelEntity;
import io.github.tomokuroki.heavenlyrealm.HeavenlyRealm;

public final class AngelRenderer extends MobRenderer<AngelEntity, AngelModel> {
    private static final ResourceLocation TEXTURE = HeavenlyRealm.id("textures/entity/angel.png");

    public AngelRenderer(EntityRendererProvider.Context context) {
        super(context, new AngelModel(context.bakeLayer(HeavenClient.ANGEL_LAYER)), 0.55F);
    }

    @Override
    public ResourceLocation getTextureLocation(AngelEntity entity) {
        return TEXTURE;
    }

    @Override
    protected int getBlockLightLevel(AngelEntity entity, BlockPos pos) {
        return 15;
    }
}
