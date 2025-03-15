package me.desert_island.rer.neoforge;

import net.minecraft.core.registries.Registries;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.RegisterEvent;
import uk.me.desert_island.rer.RerDataComponents;
import uk.me.desert_island.rer.RoughlyEnoughResources;

@Mod(value = "roughlyenoughresources")
public class RoughlyEnoughResourcesNeoforge {

    public RoughlyEnoughResourcesNeoforge(IEventBus eventBus, Dist dist) {
        RoughlyEnoughResources.onInitialize();
        eventBus.addListener(this::registerEvent);
    }

    public void registerEvent(RegisterEvent event) {
        event.register(Registries.DATA_COMPONENT_TYPE, helper -> RerDataComponents.init());
    }
}
