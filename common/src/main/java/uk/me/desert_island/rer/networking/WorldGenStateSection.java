package uk.me.desert_island.rer.networking;

import it.unimi.dsi.fastutil.ints.Int2LongArrayMap;
import it.unimi.dsi.fastutil.ints.Int2LongMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.function.Consumer;

/**
 * @author blodhgarm
 */
@ApiStatus.Experimental
public record WorldGenStateSection(Block block, Int2LongMap longValues) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<WorldGenStateSection> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("rer", "world_gen_state_block_section"));

    public static final StreamCodec<RegistryFriendlyByteBuf, WorldGenStateSection> CODEC = StreamCodec.composite(
        ByteBufCodecs.registry(Registries.BLOCK), WorldGenStateSection::block,
        ByteBufCodecs.map(Int2LongArrayMap::new, ByteBufCodecs.VAR_INT, ByteBufCodecs.VAR_LONG), WorldGenStateSection::longValues,
        WorldGenStateSection::new
    );

    public static void encodeSections(Block block, AtomicLongArray atomicLongArray, Consumer<WorldGenStateSection> consumer) {
        WorldGenStateSection currentSection = new WorldGenStateSection(block, new Int2LongArrayMap());

        long currentTotalBytes = 0;

        for (int i = 0; i < atomicLongArray.length(); i++) {
            var amount = atomicLongArray.get(i);

            if (currentTotalBytes + (amount * Long.BYTES) >= 1000000) {
                consumer.accept(currentSection);

                currentSection = new WorldGenStateSection(block, new Int2LongArrayMap());

                currentTotalBytes = 0;
            }

            currentSection.longValues().put(i, amount);

            currentTotalBytes += (amount * Long.BYTES);
        }
    }

    public static void decodeSection(WorldGenStateSection section, AtomicLongArray atomicLongArray){
        for (var entry : section.longValues().int2LongEntrySet()) {
            atomicLongArray.set(entry.getIntKey(), entry.getLongValue());
        }
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}