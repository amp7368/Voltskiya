package apple.voltskiya.rotting;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

class RottingDecrement {
    private static final long WATCH_TIME = 30000;
    public static Map<Inventory, Integer> openInventories = new HashMap<>();
    /*
    A cooler has a integer identifier of 1
     */

    /**
     * decrements a food and tries to rot that item
     *
     * @param item       the item that is rotting
     * @param inventory  the inventory the item is rotting in
     * @param multiplier the decay multiplier (based on container)
     * @return whether the item spoils soon
     */
    public static boolean decrement(@NotNull ItemStack item, Inventory inventory, double multiplier) {
        ItemMeta im = item.getItemMeta();
        if (im == null) {
            return false;
        }
        boolean isVanilla = im.getDisplayName().isEmpty();
        if (!isVanilla) {
            Integer vanilla = im.getPersistentDataContainer().get(RottingMain.vanilla, PersistentDataType.INTEGER);
            isVanilla = vanilla != null && vanilla == 1;
        }
        // see if the item is a normal item that should be decayed
        if (isVanilla) {
            PersistentDataContainer container = im.getPersistentDataContainer();
            Long lastChecked = container.get(RottingMain.lastCheckedKey, PersistentDataType.LONG);
            Long oldRottingCountdown = container.get(RottingMain.rottingCountdownKey, PersistentDataType.LONG);
            if (lastChecked == null || oldRottingCountdown == null || oldRottingCountdown == -1)
                return false;
            long decrement = (long) ((System.currentTimeMillis() - lastChecked) * multiplier);
            long newRottingCountdown = oldRottingCountdown - decrement;
            if (newRottingCountdown <= 0) {
                Long timeToRot = RottingMain.rottingChart.getOrDefault(item.getType().toString(), (long) -1);

                int amountLost = -1 + (int) (newRottingCountdown / timeToRot / 1000);

                amountLost = Math.max(0, Math.min(item.getAmount(), amountLost * -1));
                Material material = Material.getMaterial(RottingMain.rotIntoChart.getOrDefault(item.getType().toString(), "AIR"));
                if (material != null && material != Material.AIR) {
                    ItemStack rotten = new ItemStack(material);
                    rotten.setAmount(amountLost);
                    inventory.addItem(rotten);
                }
                item.setAmount(item.getAmount() - amountLost);
                if (item.getAmount() > 0) {
                    container.set(RottingMain.lastCheckedKey, PersistentDataType.LONG, System.currentTimeMillis());
                    container.set(RottingMain.rottingCountdownKey, PersistentDataType.LONG, timeToRot * 1000);
                    List<String> lore = getLore(timeToRot * 1000);
                    im.setLore(lore);
                    item.setItemMeta(im);
                    return timeToRot * 1000 < WATCH_TIME;
                } else {
                    inventory.remove(item);
                }
                return false;
            } else {
                doGiveFirstRot(item, im, container, newRottingCountdown);
                return newRottingCountdown < WATCH_TIME;
            }
        }

        return false;
    }

    /**
     * decrements everything in the inventory every second
     *
     * @param inventory the inventory being decremented
     * @param plugin    the plugin
     */
    public static void dealWithOpenInventory(Inventory inventory, JavaPlugin plugin, boolean isPlayerInv) {
        if (openInventories.containsKey(inventory)) {
            if (openInventories.get(inventory) == 0) {
                // this inv is no longer open.
                openInventories.remove(inventory);
                return;
            } // otherwise continue
        } else {
            // just leave. we're clearly no longer needed
            return;
        }

        double multiplier;
        InventoryHolder holder = inventory.getHolder();
        if (holder instanceof Player) {
            multiplier = 1;
        } else if (holder instanceof Chest) {
            Chest chest = (Chest) holder;
            Integer cooler = chest.getPersistentDataContainer().get(RottingMain.cooler, PersistentDataType.INTEGER);
            if (cooler == null) {
                multiplier = 0.5;
            } else if (cooler == 1) {
                multiplier = 0.333;
            } else if (cooler == 2) {
                multiplier = 0.25;
            } else {
                multiplier = 0.5;
            }
        } else {
            multiplier = 0.5;
        }

        boolean spoilsSoon = false;
        ItemStack[] inventoryContents = inventory.getContents();
        for (ItemStack item : inventoryContents) {
            if (item == null || item.getType() == Material.AIR)
                continue;
            @NotNull Material type = item.getType();
            if (IsRottable.isRottable(type)) {
                ItemMeta im = item.getItemMeta();
                if (im == null) {
                    continue;
                }
                PersistentDataContainer container = im.getPersistentDataContainer();
                if (container.has(RottingMain.lastCheckedKey, PersistentDataType.LONG) && container.has(RottingMain.rottingCountdownKey, PersistentDataType.LONG)) {
                    // then deal with decrementing the lore
                    if (RottingDecrement.decrement(item, inventory, multiplier)) {
                        spoilsSoon = true;
                    }
                } else {
                    // then put lore on it according to the chart
                    Long timeToRot = RottingMain.rottingChart.getOrDefault(item.getType().toString(), (long) -1);
                    if (timeToRot != -1) {
                        boolean isVanilla = im.getDisplayName().isEmpty();
                        if (!isVanilla) {
                            Integer vanilla = im.getPersistentDataContainer().get(RottingMain.vanilla, PersistentDataType.INTEGER);
                            isVanilla = vanilla != null && vanilla == 1;
                        }
                        if (isVanilla) {
                            doGiveFirstRot(item, im, container, timeToRot * 1000);
                            if (timeToRot * 1000 < WATCH_TIME)
                                spoilsSoon = true;
                        }
                    }
                }
            }
        }
        if (isPlayerInv) {
            if (spoilsSoon) {
                // then watch it
                // same as don't watch it for now.. might be changed later
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> dealWithOpenInventory(inventory, plugin, true), 20 * WATCH_TIME / 1000);
            } else {
                // don't watch it
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> dealWithOpenInventory(inventory, plugin, true), 20 * WATCH_TIME / 1000);
            }
        } else {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> dealWithOpenInventory(inventory, plugin, false), 40);
        }
    }

    public static void doGiveFirstRot(ItemStack item, ItemMeta im, PersistentDataContainer container, long millis) {
        List<String> lore = getLore(millis);
        im.setLore(lore);
        // say when you last checked the rotting
        container.set(RottingMain.lastCheckedKey, PersistentDataType.LONG, System.currentTimeMillis());
        container.set(RottingMain.rottingCountdownKey, PersistentDataType.LONG, millis);
        item.setItemMeta(im);
    }

    protected static List<String> getLore(long countDownMillis) {
        long day = TimeUnit.MILLISECONDS.toDays(countDownMillis);
        long hours = TimeUnit.MILLISECONDS.toHours(countDownMillis) - (day * 24);
        long minute = TimeUnit.MILLISECONDS.toMinutes(countDownMillis) - (TimeUnit.MILLISECONDS.toHours(countDownMillis) * 60);
        long second = TimeUnit.MILLISECONDS.toSeconds(countDownMillis) - (TimeUnit.MILLISECONDS.toMinutes(countDownMillis) * 60);

        StringBuilder string = new StringBuilder();
        int check = 0;

        boolean started = false;
        string.append("Spoils in ");
        if (day != 0) {
            check = 4;
            started = true;
            string.append(day);
            if (day == 1) {
                string.append(" day, ");
            } else
                string.append(" days, ");
        }
        if (started || hours != 0) {
            if (check != 4) check = 3;
            started = true;
            string.append(hours);
            if (hours == 1)
                string.append(" hour, ");
            else
                string.append(" hours, ");
        }
        if ((started || minute != 0) && check != 4) {
            started = true;
            string.append(minute);
            if (minute == 1)
                string.append(" minute, ");
            else
                string.append(" minutes, ");
        }
        if ((started || second != 0) && check != 4 && check != 3) {
            string.append(second);
            if (second == 1)
                string.append(" second");
            else
                string.append(" seconds");
        }

        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.DARK_GRAY + string.toString());
        return lore;
    }

}
