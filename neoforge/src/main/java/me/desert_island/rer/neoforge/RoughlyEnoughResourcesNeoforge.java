package me.desert_island.rer.neoforge;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import uk.me.desert_island.rer.RoughlyEnoughResources;
import uk.me.desert_island.rer.RoughlyEnoughResourcesClient;

@Mod(value = "roughlyenoughresources")
public class RoughlyEnoughResourcesNeoforge {

    public RoughlyEnoughResourcesNeoforge(IEventBus eventBus, Dist dist) {
        RoughlyEnoughResources.onInitialize();

        if (dist.isClient()) {
            Client.run();
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static class Client {
        public static void run() {
            RoughlyEnoughResourcesClient.onInitializeClient();
        }

    }
}
