package io.github.tomokuroki.heavenlyrealm.client;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import io.github.tomokuroki.heavenlyrealm.HeavenlyRealm;

@EventBusSubscriber(modid = HeavenlyRealm.MOD_ID, value = Dist.CLIENT)
public final class HeavenClient {
    public static final ModelLayerLocation ANGEL_LAYER =
            new ModelLayerLocation(HeavenlyRealm.id("angel"), "main");

    private HeavenClient() {}

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ANGEL_LAYER, AngelModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(HeavenlyRealm.ANGEL.get(), AngelRenderer::new);
    }
}
