package uk.me.desert_island.rer.rei_stuff;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class WorldGenRecipe implements Recipe<Inventory> {
    public ItemStack output;

    public WorldGenRecipe(ItemStack output) {
        this.output = output;
    }

    @Override
    public ItemStack craft(Inventory arg0) {
        return null;
    }

    @Override
    public boolean fits(int arg0, int arg1) {
        return false;
    }

    @Override
    public Identifier getId() {
        return null;
    }

    @Override
    public ItemStack getOutput() {
        return output;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return null;
    }

    @Override
    public RecipeType<?> getType() {
        return null;
    }

    @Override
    public boolean matches(Inventory arg0, World arg1) {
        return false;
    }

}