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
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main extends JavaPlugin {

    private FileConfiguration config = getConfig();
    private static NMS nmsHandler;
    public static Map<Material, Map<String, String>> blocksConfig;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        // Check is a new version exists
        VersionChecker.init(this);

        // Initialize NMS - Stop the plugin if it's an incompatible NMS version.
        nmsHandler = RegisterNMS.init(this);
        if (nmsHandler == null) {
            this.getLogger().info("Disabling CompressedBlocks");
            this.setEnabled(false);

        } else {
            ConfigurationSection compressibleConfig = config.getConfigurationSection("compressible");

            this.getLogger().info(compressibleConfig.toString());
            for (Map.Entry<String, Object> entry : compressibleConfig.getValues(false).entrySet()) {
                Material material = Material.valueOf(entry.getKey());
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
    }


    @Override
    public void onDisable() {
        //Fired when the server stops and disables all plugins
    }

}
