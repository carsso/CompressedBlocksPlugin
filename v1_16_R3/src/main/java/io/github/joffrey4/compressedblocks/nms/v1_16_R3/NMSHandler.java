package io.github.joffrey4.compressedblocks.nms.v1_16_R3;

import com.mojang.authlib.GameProfile;
import io.github.joffrey4.compressedblocks.api.NMS;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_16_R3.block.CraftBlockEntityState;
import org.bukkit.craftbukkit.v1_16_R3.block.CraftSkull;
import net.minecraft.server.v1_16_R3.TileEntity;
import org.bukkit.event.block.BlockBreakEvent;

import java.lang.reflect.Field;

public class NMSHandler implements NMS {
    public <T extends TileEntity> T getTE(CraftBlockEntityState<T> cbs) {
        try {
            Field f = CraftBlockEntityState.class.getDeclaredField("tileEntity");
            f.setAccessible(true);
            return (T) f.get(cbs);
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public ImmutableTriple<Boolean, Location, String> eventOnBreak (BlockBreakEvent event) {
        BlockState block = event.getBlock().getState();

        // Check if the block is an instance of Player Skull
        if (block.getData().getItemType() == Material.LEGACY_SKULL_ITEM || block.getData().getItemType() == Material.PLAYER_HEAD) {
            CraftSkull skull = (CraftSkull) block;
            GameProfile skullProfile = getTE(skull).gameProfile;

            // Check if the skull is a compressed block
            if (skullProfile != null && skullProfile.getProperties().containsKey("compBlocksName")) {

                // Drop a stack of the uncompressed blocks
                Location location = event.getBlock().getLocation().add(0.5F, 0.5F, 0.5F);
                String dropTypeName = skullProfile.getProperties().get("compBlocksName").iterator().next().getValue();

                return new ImmutableTriple<>(true, location, dropTypeName);
            }
        }
        return new ImmutableTriple<>(false, null, null);
    }
}
