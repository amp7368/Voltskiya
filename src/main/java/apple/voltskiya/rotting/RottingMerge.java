package apple.voltskiya.rotting;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

class RottingMerge implements Listener {
    RottingMerge(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent event) {
        ClickType type = event.getClick();
        ItemStack cursorItem = event.getCursor();
        ItemStack invItem = event.getCurrentItem();

        // if it's a crafting recipe, dont mess with it
        Inventory destination = event.getInventory();
        if (destination.getType() == InventoryType.CRAFTING || destination.getType() == InventoryType.WORKBENCH) {
            if (event.getSlot() == 0) {
                return;
            }
        }
        // make sure it's not a non vanilla
        boolean cursorItemExists = !(cursorItem == null || cursorItem.getType() == Material.AIR);
        boolean invItemExists = !(invItem == null || invItem.getType() == Material.AIR);

        if (cursorItemExists && invItemExists && cursorItem.getType() == invItem.getType()) {
            ItemMeta cursorMeta = cursorItem.getItemMeta();
            ItemMeta invMeta = invItem.getItemMeta();
            if (cursorMeta == null || invMeta == null) {
                return;
            }
            boolean cursorIsVanilla = cursorMeta.getDisplayName().isEmpty();
            boolean invIsVanilla = invMeta.getDisplayName().isEmpty();
            if (!cursorIsVanilla) {
                Integer vanilla = cursorMeta.getPersistentDataContainer().get(RottingMain.vanilla, PersistentDataType.INTEGER);
                cursorIsVanilla = vanilla != null && vanilla == 1;
                if (!cursorIsVanilla) {
                    return;
                }
            }
            if (!invIsVanilla) {
                Integer vanillaInv = invMeta.getPersistentDataContainer().get(RottingMain.vanilla, PersistentDataType.INTEGER);
                invIsVanilla = vanillaInv != null && vanillaInv == 1;
                if (!invIsVanilla) {
                    return;
                }
            }
        }

        if (type.equals(ClickType.LEFT) || type.equals(ClickType.RIGHT)) {
            if (cursorItemExists && invItemExists && cursorItem.getType() == invItem.getType() && IsRottable.isRottable(invItem.getType())) {
                boolean isAll = type == ClickType.LEFT;
                if (mergeItems(isAll, cursorItem, invItem))
                    event.setCancelled(true);
            }
        } else if (type.equals(ClickType.SHIFT_LEFT) || type.equals(ClickType.SHIFT_RIGHT)) {

            ItemStack currentItem = event.getCurrentItem();
            if (currentItem == null || currentItem.getType() == Material.AIR) {
                return;
            }
            if (destination.getType() == InventoryType.CRAFTING) {
                shiftMovePlayerInv(event);
                return;
            }
            if (destination.equals(event.getClickedInventory())) {
                destination = event.getWhoClicked().getInventory();
            }
            if (currentItem.getAmount() != shiftMove(destination, currentItem)) {
                event.setCancelled(true);
            }
        }
    }

    public static int shiftMove(Inventory destination, @NotNull ItemStack source) {
        boolean isFurnace = RottingMain.furanceTypes.contains(destination.getType());

        // if this is a food that we should worry about
        if (IsRottable.isRottable(source.getType())) {
            ItemStack[] contents = destination.getContents();
            int emptySlot = -1;
            for (int i = 0; i < contents.length; i++) {
                if (isFurnace)
                    if (i == 2)
                        continue;

                ItemStack content = contents[i];
                if (content == null || content.getType() == Material.AIR) {
                    if (emptySlot == -1)
                        emptySlot = i;
                    continue;
                }
                // if these two items are the same type
                if (content.getType().equals(source.getType())) {
                    // try to merge them
                    int leftToStackContent = content.getMaxStackSize() - content.getAmount();
                    if (leftToStackContent == 0) {
                        continue;
                    }
                    RottingDecrement.decrement(content, destination, 0.5);
                    mergeItems(true, source, content);
                    if (source.getAmount() <= 0) {
                        source.setAmount(0);
                        break;
                    }
                }
            }
            if (source.getAmount() != 0) {
                // if there is anything left, put it in an empty slot
                if (destination.getType() == InventoryType.CRAFTING || destination.getType() == InventoryType.PLAYER) {
                } else if (emptySlot != -1) {
                    ItemStack item = contents[emptySlot];
                    if (item == null || item.getType() == Material.AIR) {
                        destination.setItem(emptySlot, new ItemStack(source));
                        source.setAmount(0);
                    }
                }

            }
        }
        return source.getAmount();
    }

    public static void shiftMovePlayerInv(InventoryClickEvent event) {
        Inventory clickedInventory = event.getClickedInventory();
        if (clickedInventory == null)
            // i don't care if you click outside the inventory
            return;

        ItemStack source = event.getCurrentItem();
        if (source == null || source.getType() == Material.AIR || !IsRottable.isRottable(source.getType()))
            // i don't care if they're not shiftclicking anything
            return;
        /*
        0 is for hotbar
        1 is for inventory
        2 is for crafting / armor / shield
         */
        int sourceType;
        if (clickedInventory.getType() == InventoryType.CRAFTING) {
            sourceType = 2;
        } else if (clickedInventory.getType() == InventoryType.PLAYER) {
            int slot = event.getSlot();
            if (slot <= 8 && slot >= 0) {
                sourceType = 0;
            } else if (slot >= 9 && slot <= 35) {
                sourceType = 1;
            } else if (slot >= 36 && slot <= 39) {
                sourceType = 1;
            } else if (slot == 40) {
                sourceType = 1;
            } else
                // i don't care about this weird inventory slot
                return;
        } else
            // this is some weird inventory?
            return;
        // we have a source

        PlayerInventory inventory = event.getWhoClicked().getInventory();
        @NotNull ItemStack[] contents = inventory.getContents();

        int lower;
        int upper;
        switch (sourceType) {
            case 0:
                lower = 10;
                upper = 36;
                break;
            case 1:
                lower = 0;
                upper = 9;
                break;
            case 2:
                lower = 0;
                upper = 36;
                break;
            default:
                return;
        }
        for (int i = lower; i <= upper; i++) {
            ItemStack content = contents[i];
            if (content == null || content.getType() == Material.AIR)
                continue;
            // if these two items are the same type
            if (content.getType().equals(source.getType())) {
                // try to merge them
                int leftToStackContent = content.getMaxStackSize() - content.getAmount();
                if (leftToStackContent == 0) {
                    continue;
                }
                mergeItems(true, source, content);
                if (source.getAmount() <= 0) {
                    source.setAmount(0);
                    break;
                }
            }
        }
        if (source.getAmount() != 0) {
            // if there is anything left, put it in an empty slot
            for (int i = lower; i < upper; i++) {
                ItemStack item = contents[i];
                if (item == null || item.getType() == Material.AIR) {
                    inventory.setItem(i, new ItemStack(source));
                    source.setAmount(0);
                    break;
                }
            }
        }
        event.setCancelled(true);
    }

    public static boolean mergeItems(boolean isAll, ItemStack cursorItem, ItemStack invItem) {
        // then try and merge them.
        ItemMeta cursorMeta = cursorItem.getItemMeta();
        ItemMeta invMeta = invItem.getItemMeta();

        if (cursorMeta == null || invMeta == null) {
            // ignore this event
            return false;
        }
        // check if one is vanilla. if it is, ignore this event
        boolean cursorItemExists = !(cursorItem == null || cursorItem.getType() == Material.AIR);
        boolean invItemExists = !(invItem == null || invItem.getType() == Material.AIR);
        if (cursorItemExists && invItemExists && cursorItem.getType() == invItem.getType()) {
            boolean cursorIsVanilla = cursorMeta.getDisplayName().isEmpty();
            boolean invIsVanilla = invMeta.getDisplayName().isEmpty();
            if (!cursorIsVanilla) {
                Integer vanilla = cursorMeta.getPersistentDataContainer().get(RottingMain.vanilla, PersistentDataType.INTEGER);
                cursorIsVanilla = vanilla != null && vanilla == 1;
                if (!cursorIsVanilla) return false;
            }
            if (!invIsVanilla) {
                Integer vanilla = invMeta.getPersistentDataContainer().get(RottingMain.vanilla, PersistentDataType.INTEGER);
                invIsVanilla = vanilla != null && vanilla == 1;
                if (!invIsVanilla) return false;
            }
        } else {
            return false;
        }

        @NotNull PersistentDataContainer cursorContainer = cursorMeta.getPersistentDataContainer();
        @NotNull PersistentDataContainer invContainer = invMeta.getPersistentDataContainer();

        Long cursorCountdown = cursorContainer.get(RottingMain.rottingCountdownKey, PersistentDataType.LONG);
        Long invCountdown = invContainer.get(RottingMain.rottingCountdownKey, PersistentDataType.LONG);

        if (cursorCountdown == null) {
            Long timeToRot = RottingMain.rottingChart.getOrDefault(cursorItem.getType().toString(), (long) -1);
            if (timeToRot == -1)
                // ignore this event
                return false;
            RottingDecrement.doGiveFirstRot(cursorItem, cursorMeta, cursorContainer, timeToRot * 1000);
            cursorCountdown = cursorContainer.get(RottingMain.rottingCountdownKey, PersistentDataType.LONG);
            if (cursorCountdown == null)
                // ignore
                return false;

        }
        if (invCountdown == null) {
            Long timeToRot = RottingMain.rottingChart.getOrDefault(invItem.getType().toString(), (long) -1);
            if (timeToRot == -1)
                // ignore this event
                return false;
            RottingDecrement.doGiveFirstRot(invItem, invMeta, invContainer, timeToRot * 1000);
            invCountdown = invContainer.get(RottingMain.rottingCountdownKey, PersistentDataType.LONG);
            if (invCountdown == null)
                // ignore
                return false;
        }
        int cursorCount = cursorItem.getAmount();
        if (!isAll)
            cursorCount = 1;
        int invCount = invItem.getAmount();

        int maxStackSize = invItem.getMaxStackSize();

        // num of items being moved
        int numToMove = Math.min(maxStackSize - invCount, cursorCount);

        long newCountdown = Math.min(invCountdown, cursorCountdown);

        // set the item in the inventory
        invItem.setAmount(invCount + numToMove);
        List<String> lore = new ArrayList<>();
        if (newCountdown == -1) {
            // shouldn't ever happen
            newCountdown = -2;
        }
        invMeta.setLore(RottingDecrement.getLore(newCountdown));
        invContainer.set(RottingMain.lastCheckedKey, PersistentDataType.LONG, System.currentTimeMillis());
        invContainer.set(RottingMain.rottingCountdownKey, PersistentDataType.LONG, newCountdown);
        invItem.setItemMeta(invMeta);

        // set the amount of the item in the cursor
        cursorItem.setAmount(cursorItem.getAmount() - numToMove);
        return true;
    }

    public static void mergeItemAfter(Inventory destination, ItemStack item) {
        for (ItemStack content : destination.getContents()) {
            if (content != null) // content can be null!
                if (item.getType().equals(content.getType()) && item.getItemMeta() != null &&
                        item.getItemMeta().equals(content.getItemMeta())) {
                    shiftMove(destination, content);
                }
        }


    }

}