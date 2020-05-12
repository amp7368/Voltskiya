package apple.voltskiya.rotting;

import org.bukkit.Bukkit;
import org.bukkit.block.Hopper;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

class RottingListener implements Listener {
    private JavaPlugin plugin;

    RottingListener(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (event.getInventory() instanceof AnvilInventory) {
            return;
        }
        if (RottingDecrement.openInventories.containsKey(event.getInventory())) {
            RottingDecrement.openInventories.put(event.getInventory(), RottingDecrement.openInventories.get(event.getInventory()) + 1);
        } else {
            RottingDecrement.openInventories.put(event.getInventory(), 1);
            RottingDecrement.dealWithOpenInventory(event.getInventory(), plugin, false);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (RottingDecrement.openInventories.containsKey(event.getInventory())) {
            RottingDecrement.openInventories.put(event.getInventory(), RottingDecrement.openInventories.get(event.getInventory()) - 1);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        RottingDecrement.openInventories.put(event.getPlayer().getInventory(), 1);
        RottingDecrement.dealWithOpenInventory(event.getPlayer().getInventory(), plugin, true);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        RottingDecrement.openInventories.remove(event.getPlayer().getInventory());
    }

    @EventHandler
    public void onInventoryMove(InventoryMoveItemEvent event) {
        System.out.println(event.getItem().getType() + " is being moved");
        InventoryHolder initiator = event.getInitiator().getHolder();
        if (initiator instanceof Hopper) {
            RottingHopper.hopperDone.put(((Hopper) initiator).getBlock().getLocation(), false);
            ItemStack item = event.getItem();
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> RottingHopper.dealWithHopperMove(event, item, item.getAmount()), 0);
        } else if (initiator instanceof HopperMinecart) {
            RottingHopper.minecartHopperDone.put(((HopperMinecart) initiator).getUniqueId(), false);
            ItemStack item = event.getItem();
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> RottingHopper.dealWithHopperMove(event, item, item.getAmount()), 0);
        }
    }

    @EventHandler
    public void onHopperPickup(InventoryPickupItemEvent event) {
        // deal with a hopper picking up an item
        int left = RottingMerge.shiftMove(event.getInventory(), event.getItem().getItemStack());
        Item item = event.getItem();
        ItemStack is = item.getItemStack();
        if (left == 0) {
            event.getItem().remove();
            event.setCancelled(true);
        } else {
            if (is.getAmount() != left) {
                is.setAmount(left);
                item.setItemStack(is);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityPickup(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player) {
            // deal with an entity picking up an entity
            Inventory inventory = ((Player) event.getEntity()).getInventory();
            int left = RottingMerge.shiftMove(inventory, event.getItem().getItemStack());
            if (left <= 0) {
                event.getItem().remove();
                event.setCancelled(true);
            } else if (event.getItem().getItemStack().getAmount() != left) {
                Item item = event.getItem();
                ItemStack is = item.getItemStack();
                is.setAmount(left);
                item.setItemStack(is);
                event.setCancelled(true);
            }
        }
    }
}
