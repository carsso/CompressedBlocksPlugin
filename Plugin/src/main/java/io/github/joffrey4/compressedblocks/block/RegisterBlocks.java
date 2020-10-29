package io.github.joffrey4.compressedblocks.block;

import io.github.joffrey4.compressedblocks.Main;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public class RegisterBlocks {

    public static Map<Material, ItemStack> registeredBlocks;

    public static void init(Main plugin) {
        if (registeredBlocks == null) {
            registeredBlocks = new HashMap<>();
        }

        for (Map.Entry<Material, Map<String, String>> block : Main.blocksConfig.entrySet()) {
            Material material = block.getKey();
            Map<String, String> blockConfig = block.getValue();
            String name = blockConfig.get("name");
            ItemStack compressedBlock = register(new BlockCompressed(material, name, plugin));
            registeredBlocks.put(material, compressedBlock);
        }
    }

    private static ItemStack register(BlockCompressed block) {
        // Create a stack and get its meta
        ItemStack itemStack = block.getItemStack();
        ItemMeta meta = itemStack.getItemMeta();

        // Write the DisplayName & the Lore
        meta.setDisplayName(ChatColor.GOLD + block.getDisplayName());
        meta.setLore(block.getLore());

        itemStack.setItemMeta(meta);
        return itemStack;
    }


    public static ItemStack getByName(String name) {
        return RegisterBlocks.registeredBlocks.get(Material.valueOf(name));
    }

    public static ItemStack getCompressedBlock(String name, Player player) {
        if (player != null && player.hasPermission("compressedblocks.compress")) {
            return RegisterBlocks.getByName(name).clone();
        }
        return new ItemStack(Material.AIR);
    }

    public static ItemStack getUnCompressedBlocks(String name, Player player) {
        if (player != null && player.hasPermission("compressedblocks.uncompress")) {
            if(RegisterBlocks.getByName(name).getType() != Material.AIR) {
                ItemStack itemStack = new ItemStack(Material.valueOf(name));
                itemStack.setAmount(9);
                return itemStack;
            }
        }
        return new ItemStack(Material.AIR);
    }
}
