package uk.me.desert_island.rer;

import com.google.common.collect.Lists;
import dev.architectury.networking.NetworkManager;
import dev.architectury.utils.GameInstance;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.loot.LootDataType;
import net.minecraft.world.level.storage.loot.LootTable;
import uk.me.desert_island.rer.networking.*;
import java.util.*;

public class RoughlyEnoughResources {
    public static final ResourceLocation SEND_WORLD_GEN_STATE_START = ResourceLocation.fromNamespaceAndPath("roughlyenoughresources", "swds_start");
    public static final ResourceLocation SEND_WORLD_GEN_STATE_CHUNK = ResourceLocation.fromNamespaceAndPath("roughlyenoughresources", "swds_chunk");
    public static final ResourceLocation SEND_WORLD_GEN_STATE_DONE = ResourceLocation.fromNamespaceAndPath("roughlyenoughresources", "swds_done");
    public static final ResourceLocation SEND_LOOT_INFO = ResourceLocation.fromNamespaceAndPath("roughlyenoughresources", "sli");
    public static final ResourceLocation ASK_SYNC_INFO = ResourceLocation.fromNamespaceAndPath("roughlyenoughresources", "asi");

    public static final int MIN_WORLD_Y = -64;
    public static final int MAX_WORLD_Y = 320;
    public static final int WORLD_HEIGHT = MAX_WORLD_Y - MIN_WORLD_Y;

    public static void onInitialize() {
        RerDataComponents.init();
        RERUtils.LOGGER.info("RoughlyEnoughPacketSize?  Possibly.");
        NetworkManager.registerReceiver(NetworkManager.c2s(), RequestLootSynqC2SPacket.TYPE, RequestLootSynqC2SPacket.CODEC, (packet, context) -> {
            context.queue(() -> sendLootToPlayers(GameInstance.getServer(), Collections.singletonList((ServerPlayer) context.getPlayer())));
        });
    }

    public static void sendLootToPlayers(MinecraftServer server, List<ServerPlayer> players) {
        ReloadableServerRegistries.Holder lootManager = server.reloadableRegistries();
        List<ResourceLocation> names = Lists.newArrayList(lootManager.getKeys(LootDataType.TABLE.registryKey()));

        Map<ResourceLocation, LootTable> lootMap = new HashMap<>();
        int size = 50;
        for (int i = 0; i < names.size(); i += size) {
            int end = Math.min(names.size(), i + size);
            for (int j = i; j < end; j++) {
                ResourceLocation identifier = names.get(j);
                LootTable table = lootManager.getLootTable(ResourceKey.create(Registries.LOOT_TABLE, identifier));
                lootMap.put(identifier, table);
            }
            for (ServerPlayer player : players) {
                NetworkManager.sendToPlayer(player, LootTableSyncS2CPacket.of(lootMap, player.registryAccess()));
            }
        }
    }

}
