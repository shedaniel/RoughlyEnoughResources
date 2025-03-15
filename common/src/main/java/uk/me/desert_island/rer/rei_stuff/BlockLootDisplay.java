package uk.me.desert_island.rer.rei_stuff;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import uk.me.desert_island.rer.RERUtils;

@Environment(EnvType.CLIENT)
public class BlockLootDisplay extends LootDisplay {
    private final Block inputBlock;

    public BlockLootDisplay(Block block) {
        this.inputBlock = block;
        this.inputStack = RERUtils.fromBlockToItemStack(block);
        this.lootTableId = block.getLootTable().location();
        this.contextType = LootContextParamSets.BLOCK;
    }

    @Override
    public ResourceLocation getLocation() {
        return BuiltInRegistries.BLOCK.getKey(inputBlock);
    }
}
