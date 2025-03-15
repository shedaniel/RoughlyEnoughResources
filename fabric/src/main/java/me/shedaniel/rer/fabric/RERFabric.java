package me.shedaniel.rer.fabric;

import uk.me.desert_island.rer.RerDataComponents;
import uk.me.desert_island.rer.RoughlyEnoughResources;
import net.fabricmc.api.ModInitializer;

public class RERFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        RoughlyEnoughResources.onInitialize();
        RerDataComponents.init();
    }
}
