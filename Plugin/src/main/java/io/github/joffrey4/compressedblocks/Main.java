package io.github.joffrey4.compressedblocks;

import io.github.joffrey4.compressedblocks.api.NMS;
import io.github.joffrey4.compressedblocks.block.RegisterBlocks;
import io.github.joffrey4.compressedblocks.command.RegisterCommand;
import io.github.joffrey4.compressedblocks.event.RegisterEvent;
import io.github.joffrey4.compressedblocks.nms.RegisterNMS;
import io.github.joffrey4.compressedblocks.recipe.RegisterRecipes;
import io.github.joffrey4.compressedblocks.util.VersionChecker;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_16_R2.inventory.CraftShapedRecipe;
import org.bukkit.craftbukkit.v1_16_R2.inventory.CraftShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class Main extends JavaPlugin {

    private FileConfiguration config;
    private static NMS nmsHandler;
    public static Map<Material, Map<String, String>> blocksConfig;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        config = getConfig();

        blocksConfig = new HashMap<>();

        // Check is a new version exists
        VersionChecker.init(this);

        // Initialize NMS - Stop the plugin if it's an incompatible NMS version.
        nmsHandler = RegisterNMS.init(this);
        if (nmsHandler == null) {
            this.getLogger().info("Disabling CompressedBlocks");
            this.setEnabled(false);
            return;
        }

        ConfigurationSection compressibleConfig = config.getConfigurationSection("compressible");

        // This block was modified by @cindyker for The Minecats community, implemented by @AeSix
        // original cause for non-operation was invalid item names in the config.
        this.getLogger().info(compressibleConfig.toString());
        for (Map.Entry<String, Object> entry : compressibleConfig.getValues(false).entrySet()) {
            Material material;

            try {
                material = Material.valueOf(entry.getKey());
            } catch (Exception e) {
                this.getLogger().severe("Error in Config! Material: " + entry.getKey() + " is not a real material.");
                continue;  //Go to next item to check.
            }
            Map<String, String> blockConfig = new HashMap<>();
            blockConfig.put("name", config.getString("compressible." + entry.getKey() + ".name"));
            blockConfig.put("lore", config.getString("compressible." + entry.getKey() + ".lore"));
            blockConfig.put("texture", config.getString("compressible." + entry.getKey() + ".texture"));
            blocksConfig.put(material, blockConfig);
        }

        // Initialize blocks & recipes
        RegisterBlocks.init(this);
        RegisterRecipes.init(this);

        // Initialize plugin mechanics
        RegisterEvent.init(this, nmsHandler);

        // Initialize commands
        RegisterCommand.init(this);
    }


    @Override
    public void onDisable() {
        //Fired when the server stops and disables all plugins
    }

}
