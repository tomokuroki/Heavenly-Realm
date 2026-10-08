package io.github.tomokuroki.heavenlyrealm;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityTravelToDimensionEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

public final class HeavenEvents {
    private static final int ALTAR_Y = 160;
    private static final int FLOWERS_NEEDED = 7;
    private static final String GUIDE_TAG = "heavenly_realm_guide";
    private static final String ALLOWED_EXIT_TAG = "heavenly_realm_allowed_exit";

    private HeavenEvents() {}

    @SubscribeEvent
    public static void preventFatalDamage(LivingDamageEvent.Pre event) {
        if (!(event.getEntity() instanceof ServerPlayer player)
                || !player.level().dimension().equals(HeavenlyRealm.HEAVEN)) {
            return;
        }

        float survivableHealth = player.getHealth() + player.getAbsorptionAmount();
        if (event.getNewDamage() < survivableHealth) return;

        event.setNewDamage(0.0F);
        player.setHealth(player.getMaxHealth());
        ServerLevel heaven = player.getServer().getLevel(HeavenlyRealm.HEAVEN);
        if (heaven != null) sendToAltar(player, heaven, false);
        player.sendSystemMessage(Component.translatable("message.heavenly_realm.no_death"));
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        ServerLevel heaven = player.getServer().getLevel(HeavenlyRealm.HEAVEN);
        if (heaven == null) {
            player.sendSystemMessage(Component.translatable("message.heavenly_realm.missing_dimension"));
            return;
        }

        event.setCanceled(true);
        player.setHealth(player.getMaxHealth());
        player.removeAllEffects();
        player.clearFire();

        if (player.level().dimension().equals(HeavenlyRealm.HEAVEN)) {
            sendToAltar(player, heaven, false);
            player.sendSystemMessage(Component.translatable("message.heavenly_realm.no_escape"));
        } else {
            sendToAltar(player, heaven, true);
            player.sendSystemMessage(Component.translatable("message.heavenly_realm.arrival"));
        }
    }

    @SubscribeEvent
    public static void keepPlayersInsideHeaven(EntityTravelToDimensionEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)
                || !player.level().dimension().equals(HeavenlyRealm.HEAVEN)
                || event.getDimension().equals(HeavenlyRealm.HEAVEN)) {
            return;
        }

        if (player.removeTag(ALLOWED_EXIT_TAG)) return;
        event.setCanceled(true);
        player.sendSystemMessage(Component.translatable("message.heavenly_realm.portal_blocked"));
    }

    @SubscribeEvent
    public static void enforceHeavenRules(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)
                || !player.level().dimension().equals(HeavenlyRealm.HEAVEN)) {
            return;
        }

        if (player.getY() < player.level().getMinBuildHeight() - 8) {
            ServerLevel heaven = player.getServer().getLevel(HeavenlyRealm.HEAVEN);
            if (heaven != null) sendToAltar(player, heaven, false);
            player.sendSystemMessage(Component.translatable("message.heavenly_realm.void_saved"));
            return;
        }

        if (player.tickCount % 100 == 0) {
            int remaining = Math.max(0, FLOWERS_NEEDED - countFlowers(player));
            player.displayClientMessage(Component.translatable(
                    remaining == 0 ? "message.heavenly_realm.quest_ready" : "message.heavenly_realm.quest_status",
                    remaining), true);
        }
    }

    private static void sendToAltar(ServerPlayer player, ServerLevel heaven, boolean greet) {
        buildAltar(heaven);
        player.teleportTo(heaven, 0.5D, ALTAR_Y + 1.0D, 0.5D, 0.0F, 0.0F);
        player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 20 * 20, 0, false, false));
        ensureGuide(heaven);
        if (greet) {
            heaven.playSound(null, BlockPos.containing(0.5D, ALTAR_Y + 1, 0.5D),
                    SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.25F);
        }
    }

    private static void buildAltar(ServerLevel level) {
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int x = -4; x <= 4; x++) {
            for (int z = -4; z <= 4; z++) {
                cursor.set(x, ALTAR_Y, z);
                level.setBlockAndUpdate(cursor, (Math.abs(x) == 4 || Math.abs(z) == 4)
                        ? HeavenlyRealm.SUNSTONE.get().defaultBlockState()
                        : HeavenlyRealm.CLOUD_MARBLE.get().defaultBlockState());
                for (int y = ALTAR_Y + 1; y <= ALTAR_Y + 4; y++) {
                    cursor.set(x, y, z);
                    if (!level.getBlockState(cursor).is(BlockTags.WITHER_IMMUNE)) {
                        level.setBlockAndUpdate(cursor, Blocks.AIR.defaultBlockState());
                    }
                }
            }
        }
    }

    private static void ensureGuide(ServerLevel level) {
        AABB area = new AABB(-16, ALTAR_Y - 4, -16, 16, ALTAR_Y + 12, 16);
        boolean exists = !level.getEntitiesOfClass(AngelEntity.class, area,
                angel -> angel.getTags().contains(GUIDE_TAG)).isEmpty();
        if (exists) return;

        AngelEntity guide = HeavenlyRealm.ANGEL.get().create(level);
        if (guide == null) return;
        guide.moveTo(2.5D, ALTAR_Y + 2.0D, 0.5D, 90.0F, 0.0F);
        guide.setCustomName(Component.translatable("entity.heavenly_realm.guardian_angel"));
        guide.setCustomNameVisible(true);
        guide.setPersistenceRequired();
        guide.addTag(GUIDE_TAG);
        guide.setInvulnerable(true);
        guide.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND,
                new ItemStack(HeavenlyRealm.SKYBELL_ITEM.get()));
        level.addFreshEntity(guide);
    }

    @SubscribeEvent
    public static void onTalkToAngel(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getEntity() instanceof ServerPlayer player)
                || !(event.getTarget() instanceof AngelEntity angel)
                || !angel.getTags().contains(GUIDE_TAG)) {
            return;
        }

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
        if (!player.level().dimension().equals(HeavenlyRealm.HEAVEN)) return;

        int count = countFlowers(player);
        if (count < FLOWERS_NEEDED) {
            player.sendSystemMessage(Component.translatable("message.heavenly_realm.quest",
                    FLOWERS_NEEDED - count));
            return;
        }

        removeFlowers(player, FLOWERS_NEEDED);
        ServerLevel overworld = player.getServer().getLevel(Level.OVERWORLD);
        if (overworld == null) return;
        BlockPos spawn = overworld.getSharedSpawnPos();
        player.addTag(ALLOWED_EXIT_TAG);
        player.teleportTo(overworld, spawn.getX() + 0.5D, spawn.getY() + 1.0D,
                spawn.getZ() + 0.5D, player.getYRot(), player.getXRot());
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 20 * 10, 1));
        player.sendSystemMessage(Component.translatable("message.heavenly_realm.returned"));
        overworld.playSound(null, spawn, SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    private static int countFlowers(ServerPlayer player) {
        int result = 0;
        for (ItemStack stack : player.getInventory().items) {
            if (stack.is(HeavenlyRealm.SKYBELL_ITEM.get())) result += stack.getCount();
        }
        return result;
    }

    private static void removeFlowers(ServerPlayer player, int amount) {
        int remaining = amount;
        for (ItemStack stack : player.getInventory().items) {
            if (remaining == 0) break;
            if (stack.is(HeavenlyRealm.SKYBELL_ITEM.get())) {
                int taken = Math.min(remaining, stack.getCount());
                stack.shrink(taken);
                remaining -= taken;
            }
        }
        player.getInventory().setChanged();
    }
}
