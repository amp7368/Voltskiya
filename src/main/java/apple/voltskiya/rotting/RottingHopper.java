package apple.voltskiya.rotting;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Hopper;
import org.bukkit.entity.minecart.HopperMinecart;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

class RottingHopper {
    protected static Map<Location, Boolean> hopperDone = new HashMap<>();
    protected static Map<UUID, Boolean> minecartHopperDone = new HashMap<>();
    public static final Object moveMinecartSyncObject = new Object();
    public static final Object moveSyncObject = new Object();

    public synchronized static void dealWithHopperMove(InventoryMoveItemEvent event, ItemStack oldItem, int oldAmount) {
        InventoryHolder initiator = event.getInitiator().getHolder();
        if (initiator instanceof HopperMinecart) {
            // do something with the hopper todo
            System.out.println(((HopperMinecart) initiator).getLocation().toString() + " was a hopper minecart");
            printU(minecartHopperDone);
            synchronized (moveMinecartSyncObject) {

            }
        } else if (initiator instanceof Hopper) {
            synchronized (moveSyncObject) {
                // test
                /*
                I need this sync object in case someone gets the object and uses the wrong reference.
                This shouldn't impact performance by anything really as the calls are called one after the other. (I believe)
                sync object > synchronized map in this case
                */

                // do something with the hopper todo

                Location location = ((Hopper) initiator).getLocation();

                if (hopperDone.getOrDefault(location, false)) {
                    // if this hopper completed it's transaction, don't try to do anything more
                    return;
                }

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
                                Bukkit.broadcastMessage(content.getType() + " is in the inventory");
                                // remember to try to move the item
                                itemWasMoved = false;
                                break;
                            }else{
                                break;
                            }
                        }
                if (itemWasMoved) {
                    // the item was moved then this hopper is done
                    hopperDone.put(location, true);
                    return;
                }
                // move on to the second wave (this is to make sure that no item was moved)


            }
        }
    }

    public static void print(ItemStack[] contents) {
        System.out.println("--------------");
        for (ItemStack i : contents) {
            if (i != null && !i.getType().isAir())
                System.out.println(i.getType());
        }
        System.out.println("--------------");
    }

    private static void print(Map<Location, Boolean> map) {
        for (Location loc : map.keySet()) {
            System.out.println(loc.toString());
        }
    }

    private static void printU(Map<UUID, Boolean> map) {
        for (UUID loc : map.keySet()) {
            System.out.println(loc.toString());
        }
    }
}
