package io.github.tomokuroki.heavenlyrealm;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(HeavenlyRealm.MOD_ID)
public final class HeavenlyRealm {
    public static final String MOD_ID = "heavenly_realm";
    public static final ResourceKey<Level> HEAVEN = ResourceKey.create(
            Registries.DIMENSION, id("heaven"));

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MOD_ID);
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, MOD_ID);
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    public static final DeferredBlock<Block> CLOUD_MARBLE = BLOCKS.registerSimpleBlock("cloud_marble",
            BlockBehaviour.Properties.of().mapColor(MapColor.SNOW).strength(1.2F, 4.0F)
                    .sound(SoundType.WOOL).lightLevel(state -> 3));
    public static final DeferredBlock<Block> SUNSTONE = BLOCKS.registerSimpleBlock("sunstone",
            BlockBehaviour.Properties.of().mapColor(MapColor.GOLD).strength(2.0F, 6.0F)
                    .sound(SoundType.AMETHYST).lightLevel(state -> 12));
    public static final DeferredBlock<SkybellBlock> SKYBELL = BLOCKS.register("skybell",
            () -> new SkybellBlock(MobEffects.REGENERATION, 7.0F,
                    BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).noCollission()
                            .instabreak().sound(SoundType.GRASS).lightLevel(state -> 7)));
    public static final DeferredBlock<HeavenGrassBlock> HEAVEN_GRASS = BLOCKS.register("heaven_grass",
            () -> new HeavenGrassBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN)
                    .replaceable().noCollission().instabreak().sound(SoundType.GRASS)
                    .offsetType(BlockBehaviour.OffsetType.XYZ).lightLevel(state -> 3)));

    public static final DeferredHolder<EntityType<?>, EntityType<AngelEntity>> ANGEL = ENTITIES.register("angel",
            () -> EntityType.Builder.of(AngelEntity::new, MobCategory.CREATURE)
                    .sized(0.8F, 2.35F).eyeHeight(2.05F).clientTrackingRange(10)
                    .build(id("angel").toString()));

    public static final DeferredItem<BlockItem> CLOUD_MARBLE_ITEM =
            ITEMS.registerSimpleBlockItem("cloud_marble", CLOUD_MARBLE);
    public static final DeferredItem<BlockItem> SUNSTONE_ITEM =
            ITEMS.registerSimpleBlockItem("sunstone", SUNSTONE);
    public static final DeferredItem<BlockItem> SKYBELL_ITEM =
            ITEMS.registerSimpleBlockItem("skybell", SKYBELL, new Item.Properties());
    public static final DeferredItem<BlockItem> HEAVEN_GRASS_ITEM =
            ITEMS.registerSimpleBlockItem("heaven_grass", HEAVEN_GRASS, new Item.Properties());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> HEAVEN_TAB =
            TABS.register("heaven", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.heavenly_realm"))
                    .icon(() -> SKYBELL_ITEM.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(CLOUD_MARBLE_ITEM.get());
                        output.accept(SUNSTONE_ITEM.get());
                        output.accept(SKYBELL_ITEM.get());
                        output.accept(HEAVEN_GRASS_ITEM.get());
                    }).build());

    public HeavenlyRealm(IEventBus modBus, ModContainer container) {
        BLOCKS.register(modBus);
        ITEMS.register(modBus);
        ENTITIES.register(modBus);
        TABS.register(modBus);
        modBus.addListener(HeavenlyRealm::onEntityAttributes);
        NeoForge.EVENT_BUS.register(HeavenEvents.class);
    }

    private static void onEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(ANGEL.get(), AngelEntity.createAttributes().build());
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
