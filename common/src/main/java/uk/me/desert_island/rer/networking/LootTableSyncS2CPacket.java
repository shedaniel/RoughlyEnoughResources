package uk.me.desert_island.rer.networking;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;
import uk.me.desert_island.rer.RerJsonUtils;
import uk.me.desert_island.rer.RoughlyEnoughResources;
import java.util.HashMap;
import java.util.Map;

public record LootTableSyncS2CPacket(Map<ResourceLocation, JsonElement> lootJson) implements CustomPacketPayload {

    public static final Type<LootTableSyncS2CPacket> TYPE = new Type<>(RoughlyEnoughResources.SEND_LOOT_INFO);

    public static final StreamCodec<RegistryFriendlyByteBuf, JsonElement> BINARY_JSON = StreamCodec.of(
        LootTableSyncS2CPacket::writeOptimizedJson,
        RerJsonUtils::readJson
    );

    public static LootTableSyncS2CPacket of(Map<ResourceLocation, LootTable> lootTables, RegistryAccess registryAccess) {
        Map<ResourceLocation, JsonElement> map = new HashMap<>();

        lootTables.forEach((location, lootTable) -> {
            var json = LootTable.CODEC.encodeStart(RegistryOps.create(JsonOps.INSTANCE, registryAccess), Holder.direct(lootTable))
                .getOrThrow(string -> new IllegalStateException("Unable to encode LootTable within RER Loot Table Sync: " + string));

            map.put(location, json);
        });

        return new LootTableSyncS2CPacket(map);
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, LootTableSyncS2CPacket> CODEC = StreamCodec.composite(
        ByteBufCodecs.map(HashMap::new, ResourceLocation.STREAM_CODEC, BINARY_JSON),
        LootTableSyncS2CPacket::lootJson,
        LootTableSyncS2CPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void writeOptimizedJson(RegistryFriendlyByteBuf buf, JsonElement element) {
        JsonElement optimizedJson = RerJsonUtils.optimiseTable(element);
        RerJsonUtils.writeJson(buf, optimizedJson);
    }
}
