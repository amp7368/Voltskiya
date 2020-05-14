package apple.voltskiya.rotting.hopper;

import apple.voltskiya.rotting.RottingMain;
import apple.voltskiya.rotting.RottingMerge;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Hopper;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RottingHopper {
    protected static Map<Location, HopperDone> hoppers = new HashMap<>(100);
    public static final Object moveSyncObject = new Object();

    protected static Map<UUID, HopperDone> minecartHopperDone = new HashMap<>(10);
    public static final Object moveMinecartSyncObject = new Object();

    private static void dealWithHopperMove(Location locationOfHopper) {
        HopperDone hopperDone;
        if (hoppers.containsKey(locationOfHopper)) {
            hopperDone = hoppers.get(locationOfHopper);
            if (hopperDone.hopperDone) {
                // if this hopper completed it's transaction, don't try to do anything more
                return;
            }
        } else {
            // don't try to do something because the transaction was somehow already completed?
            return;
        }

        // we know for sure that this (or more) event exists.

        dealWithMove(hopperDone);
    }

    private static void dealWithMove(HopperDone hopperDone) {
        while (!hopperDone.firstWaveCheckEvents.isEmpty()) {
            // if there are no more items in the list to check, just leave

            InventoryMoveItemEvent event = hopperDone.firstWaveCheckEvents.remove(0);
            ItemStack oldItem = event.getItem();
            int oldAmount = oldItem.getAmount();
            InventoryHolder initiator = event.getInitiator().getHolder();
            if (initiator instanceof Hopper) {
                // see if the item was moved
                ItemStack[] oldSourceContents = event.getSource().getContents();
                boolean itemWasMoved = true;
                for (ItemStack content : oldSourceContents)
                    if (content != null && !content.getType().isAir())
                        if (content.equals(oldItem)) {
                            // we have the item that should have been attempted to be moved.
                            // if it has an incorrect amount, then don't check the next one,
                            // because the next one might have the correct amount
                            // and it wouldn't have been the item that was moved.
                            if (content.getAmount() == oldAmount) {
                                // remember to try to move the item
                                itemWasMoved = false;
                                break;
                            }
                        }
                if (itemWasMoved) {
                    // if the item was moved then this hopper is done
                    hopperDone.clear();
                    return;
                }
            }
            // You should have checked that this was a hopper earlier
        }
        hopperDone.clearFirst();
        // we made it here so item was not moved
        // actually move the item if we can
        dealWithHopperMoveSecond(hopperDone);
    }

    private static void dealWithHopperMoveSecond(HopperDone hopperDone) {
        while (!hopperDone.secondWaveMergeEvents.isEmpty()) {
            // if there are no more items in the list to check, just leave

            InventoryMoveItemEvent event = hopperDone.secondWaveMergeEvents.remove(0);
            Inventory destination = event.getDestination();
            if (RottingMain.furanceTypes.contains(destination.getType())) {
                ItemStack[] destinationContents = destination.getContents();
                Location sourceLocation = event.getSource().getLocation();
                Location destinationLocation = event.getDestination().getLocation();
                hopperDone.clear();
                if (sourceLocation == null || destinationLocation == null)
                    // idk how this happened, but just ignore it.
                    return;
                if (sourceLocation.getBlockY() != destinationLocation.getBlockY()) {
                    // then put it in the 0th slot
                    if (RottingMerge.pushItem(destination, event.getItem(), destinationContents[0], 0))
                        // then stop
                        return;
                } else {
                    // then put it in the 1th slot
                    if (RottingMerge.pushItem(destination, event.getItem(), destinationContents[1], 1))
                        // then stop
                        return;
                }
            } else if (destination.getType() == InventoryType.BREWING) {
                if (RottingMerge.pushItem(destination, event.getItem(), destination.getContents()[3], 3))
                    // then stop
                    return;
            } else {
                if (RottingMerge.pushItem(destination, event.getItem())) {
                    // then stop
                    hopperDone.clear();
                    return;
                }
            }
        }

    }

    public static void dealWithMinecartMove(UUID uuidOfCart) {
        HopperDone hopperDone;
        if (minecartHopperDone.containsKey(uuidOfCart)) {
            hopperDone = minecartHopperDone.get(uuidOfCart);
            if (hopperDone.hopperDone) {
                // if this hopper completed it's transaction, don't try to do anything more
                return;
            }
        } else {
            // don't try to do something because the transaction was somehow already completed?
            return;
        }

        // we know for sure that this (or more) event exists.
        dealWithMove(hopperDone);

    }

    public static void addEvent(JavaPlugin plugin, Location key, InventoryMoveItemEvent event) {
        synchronized (moveSyncObject) {
            HopperDone keyValue = hoppers.getOrDefault(key, null);
            if (keyValue == null) {
                hoppers.put(key, new HopperDone(event));
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> RottingHopper.dealWithHopperMove(key), 0);
            } else {
                if (keyValue.firstWaveCheckEvents.isEmpty())
                    // if there doesn't exist one already, do this
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> RottingHopper.dealWithHopperMove(key), 0);
                keyValue.add(event);
                keyValue.hopperDone = false;
            }
        }
    }

    public static void addEvent(JavaPlugin plugin, UUID key, InventoryMoveItemEvent event) {
        synchronized (moveMinecartSyncObject) {
            HopperDone keyValue = minecartHopperDone.getOrDefault(key, null);
            if (keyValue == null) {
                minecartHopperDone.put(key, new HopperDone(event));
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> RottingHopper.dealWithMinecartMove(key), 0);
            } else {
                if (keyValue.firstWaveCheckEvents.isEmpty())
                    // if there doesn't exist one already, do this
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> RottingHopper.dealWithMinecartMove(key), 0);
                keyValue.add(event);
                keyValue.hopperDone = false;
            }
        }
    }
}
