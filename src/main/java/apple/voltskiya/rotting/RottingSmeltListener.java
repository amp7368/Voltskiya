package apple.voltskiya.rotting;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Furnace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

class RottingSmeltListener implements Listener {
    public RottingSmeltListener(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onSmeltEvent(FurnaceSmeltEvent event) {
        ItemStack result = event.getResult();
        @NotNull BlockState furnaceBlock = event.getBlock().getState();
        // see if both are edible and there is a furnace

        if (IsRottable.isRottable(result.getType()) && furnaceBlock instanceof Furnace) {
            // both are edible and the same type
            // give the result decay
            ItemMeta resultMeta = result.getItemMeta();
            if (resultMeta == null) {
                // im not dealing with this weird item
                return;
            }
            Furnace furnace = (Furnace) furnaceBlock;
            Inventory inventory = furnace.getInventory();
            ItemStack oldResult = inventory.getItem(2);
            if (oldResult == null || oldResult.getType() == Material.AIR) {
                // make a new item with no rot
                PersistentDataContainer resultContainer = resultMeta.getPersistentDataContainer();
                Long timeToRot = RottingMain.rottingChart.getOrDefault(result.getType().toString(), (long) -1);
                if (timeToRot != -1) {
                    result.setAmount(result.getAmount());
                    RottingDecrement.doGiveFirstRot(result, resultMeta, resultContainer, timeToRot * 1000);
                    inventory.setItem(2, result);
                    event.getSource().setAmount(event.getSource().getAmount()-1);
                    event.setCancelled(true);
                }
            } else {
                //stack it
                if (oldResult.getType() == result.getType()) {
                    PersistentDataContainer resultContainer = resultMeta.getPersistentDataContainer();
                    Long timeToRot = RottingMain.rottingChart.getOrDefault(result.getType().toString(), (long) -1);
                    if (timeToRot != -1) {
                        result.setAmount(result.getAmount() + oldResult.getAmount());
                        RottingDecrement.doGiveFirstRot(result, resultMeta, resultContainer, timeToRot * 1000);
                        inventory.setItem(2, result);
                        event.getSource().setAmount(event.getSource().getAmount()-1);
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}
