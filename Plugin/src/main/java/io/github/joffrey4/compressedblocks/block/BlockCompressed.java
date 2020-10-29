package io.github.joffrey4.compressedblocks.block;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.github.joffrey4.compressedblocks.Main;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.lang.reflect.Field;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.UUID;

public class BlockCompressed {

    private static Material material;
    private static String displayName;
    private static String materialName;
    private static String typeName;
    private static FileConfiguration config;

    public BlockCompressed(Material material, Main plugin) {
        BlockCompressed.config = plugin.getConfig();
        BlockCompressed.material = material;

        // Name of the type of the block (ex: Oak_Wood)
        BlockCompressed.typeName = material.toString();

        // Name of the compressed block item (ex: Compressed Oak Wood)
        BlockCompressed.displayName = setDisplayName();

        // Name of the compressed block material
        BlockCompressed.materialName = setMaterialName();
    }

    private String setDisplayName() {
        String displayName = config.getString("compressible." + typeName + ".name");

        if(displayName == null || displayName.isEmpty()) {
            displayName = typeName;
        }

        return ChatColor.translateAlternateColorCodes('&', displayName);
    }

    private String setMaterialName() {
        String materialName = config.getString("compressible." + typeName + ".material-name");

        if(materialName == null || materialName.isEmpty()) {
            materialName = typeName;
        }

        return ChatColor.translateAlternateColorCodes('&', materialName);
    }

    public ItemStack getItemStack() {

        // Get the skin image and initialize an itemStack (skull)
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);

        String skinURL = "http://textures.minecraft.net/texture/";
        String texture = config.getString("compressible." + typeName + ".texture");
        if (texture == null || texture.isEmpty()) {
            return skull;
        } else {
            skinURL += config.getString("compressible." + typeName + ".texture");
        }

        // Get the skull metadata and initialize a texture profile
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        GameProfile profile = new GameProfile(UUID.fromString("069a79f4-44e9-4726-a5be-fca90e38aaf5"), null);
        profile.getProperties().put("textures", new Property("textures", Base64Coder.encodeString("{textures:{SKIN:{url:\"" + skinURL + "\"}}}")));
        profile.getProperties().put("compBlocksName", new Property("compBlocksName", typeName));

        // Set the texture profile to the skull metadata
        Field profileField;
        try {
            profileField = skullMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(skullMeta, profile);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }

        // Save the metadata on the skull and return it
        skull.setItemMeta(skullMeta);
        return skull;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<String> getLore() {
        List<String> lore;

        if (Objects.equals(config.getString("compressible." + typeName + ".lore"), "default")) {
            lore = config.getStringList("default.lore");
        } else {
            lore = config.getStringList("compressible." + typeName + ".lore");
        }

        for (final ListIterator<String> i = lore.listIterator(); i.hasNext();) {
            String line = i.next();

            if (line.contains("&type")) {
                line = line.replace("&type", materialName);
            }
            i.set(ChatColor.translateAlternateColorCodes('&', line));
        }
        return lore;
    }
}
