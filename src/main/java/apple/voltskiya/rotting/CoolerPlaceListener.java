package apple.voltskiya.rotting;

import net.minecraft.server.v1_15_R1.NBTBase;
import net.minecraft.server.v1_15_R1.NBTTagCompound;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.v1_15_R1.block.CraftBlock;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class CoolerPlaceListener implements Listener {
    private JavaPlugin plugin;

    public CoolerPlaceListener(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        if (item == null || item.getType() != Material.CHEST)
            return;

        // the item is good
        net.minecraft.server.v1_15_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
        NBTTagCompound compound = (nmsStack.hasTag()) ? nmsStack.getTag() : new NBTTagCompound();
        if (compound == null)
            return;

        if (compound.hasKey("Item")) {
            NBTBase itemTags = compound.get("Item");
            if (itemTags == null)
                return;
            if (itemTags.asString().equals(TagsNavigate.COOLER_ITEM)) {
                // this should be a cooler that we place
                @NotNull Block placed = event.getBlockPlaced();
                BlockState state = placed.getState();
                if (state instanceof Chest) {
                    if (isChestNearby(placed.getLocation())) {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(ChatColor.RED + "You cannot place a cooler next to another chest.");
                        return;
                    }
                    @NotNull PersistentDataContainer blockContainer = ((Chest) state).getPersistentDataContainer();
                    blockContainer.set(RottingMain.cooler, PersistentDataType.INTEGER, 1);
                    ((Chest) state).setCustomName("Cooler");
                    state.update();
                }
            } else if (itemTags.asString().equals(TagsNavigate.FREEZER_ITEM)) {
                // this should be a cooler that we place
                @NotNull Block placed = event.getBlockPlaced();
                BlockState state = placed.getState();
                if (state instanceof Chest) {
                    if (isChestNearby(placed.getLocation())) {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(ChatColor.RED + "You cannot place a freezer next to another chest.");
                        return;
                    }
                    @NotNull PersistentDataContainer blockContainer = ((Chest) state).getPersistentDataContainer();
                    blockContainer.set(RottingMain.cooler, PersistentDataType.INTEGER, 2);
                    ((Chest) state).setCustomName("Freezer");
                    state.update();
                }
            }
        }
    }

    private static boolean isChestNearby(Location chestLoc) {
        World world = chestLoc.getWorld();
        if (world == null)
            return true;
        int x = chestLoc.getBlockX();
        int y = chestLoc.getBlockY();
        int z = chestLoc.getBlockZ();
        return (world.getBlockAt(x + 1, y, z).getType() == Material.CHEST ||
                world.getBlockAt(x - 1, y, z).getType() == Material.CHEST ||
                world.getBlockAt(x, y, z + 1).getType() == Material.CHEST ||
                world.getBlockAt(x, y, z - 1).getType() == Material.CHEST);

    }
}
