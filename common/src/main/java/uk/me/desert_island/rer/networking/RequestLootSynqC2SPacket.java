package uk.me.desert_island.rer.networking;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;
import uk.me.desert_island.rer.RoughlyEnoughResources;

public class RequestLootSynqC2SPacket implements CustomPacketPayload {

    private RequestLootSynqC2SPacket() {
    }

    public static final Type<RequestLootSynqC2SPacket> TYPE = new Type<>(RoughlyEnoughResources.ASK_SYNC_INFO);
    public static final RequestLootSynqC2SPacket INSTANCE = new RequestLootSynqC2SPacket();
    public static final StreamCodec<RegistryFriendlyByteBuf, RequestLootSynqC2SPacket> CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
