package io.github.joffrey4.compressedblocks.recipe;

import io.github.joffrey4.compressedblocks.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import io.github.joffrey4.compressedblocks.block.RegisterBlocks;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.Map;

public class RegisterRecipes {

    public static void init(Main plugin) {
        NamespacedKey namespacedKey = new NamespacedKey(plugin, "receipes");

        Server server = Bukkit.getServer();

        for (Map.Entry<Material, ItemStack> registeredBlock : RegisterBlocks.registeredBlocks.entrySet()) {
            Material rawMaterial = registeredBlock.getKey();
            ItemStack compressedBlock = registeredBlock.getValue();
            server.addRecipe(Compressing(namespacedKey, compressedBlock, rawMaterial));
            server.addRecipe(UnCompressing(namespacedKey, new ItemStack(rawMaterial), compressedBlock.getType()));
        }
    }

    private static ShapedRecipe Compressing(NamespacedKey key, ItemStack itemResult, Material ingredient) {
        ShapedRecipe shapedRecipe = new ShapedRecipe(key, itemResult);
        shapedRecipe.shape("III", "III", "III");
        shapedRecipe.setIngredient('I', ingredient);
        return shapedRecipe;
    }

    private static ShapelessRecipe UnCompressing(NamespacedKey key, ItemStack itemResult, Material ingredient) {
        ShapelessRecipe shapelessRecipe = new ShapelessRecipe(key, itemResult);
        shapelessRecipe.addIngredient(ingredient);
        return shapelessRecipe;
    }
}
