package me.desert_island.rer.neoforge;

import uk.me.desert_island.rer.RoughlyEnoughResourcesClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(value = "roughlyenoughresources", dist = Dist.CLIENT)
public class RERNeoForgeClient {

    public RERNeoForgeClient(final IEventBus eventBus) {
        RoughlyEnoughResourcesClient.onInitializeClient();
    }
}
