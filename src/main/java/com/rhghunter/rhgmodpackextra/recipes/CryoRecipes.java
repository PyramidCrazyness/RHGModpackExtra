package com.rhghunter.rhgmodpackextra.recipes;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import java.util.HashMap;
import java.util.Map;

public class CryoRecipes {
    private static final Map<ItemStack, ItemStack> recipes = new HashMap<ItemStack, ItemStack>();

    public static void init() {
        // Define your recipes here: Input -> Output
        addRecipe(new ItemStack(Items.water_bucket), new ItemStack(Blocks.ice));
        addRecipe(new ItemStack(Items.lava_bucket), new ItemStack(Blocks.obsidian));
        addRecipe(new ItemStack(Items.slime_ball), new ItemStack(Items.snowball));
    }

    public static void addRecipe(ItemStack input, ItemStack output) {
        recipes.put(input, output);
    }

    public static ItemStack getResult(ItemStack input) {
        if (input == null) return null;
        for (Map.Entry<ItemStack, ItemStack> entry : recipes.entrySet()) {
            if (entry.getKey().isItemEqual(input)) {
                return entry.getValue().copy();
            }
        }
        return null;
    }
}
