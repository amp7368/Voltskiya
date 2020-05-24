package apple.voltskiya.bedTeleportScroll;

import net.minecraft.server.v1_15_R1.NBTBase;
import net.minecraft.server.v1_15_R1.NBTTagCompound;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class BedTeleportListener implements Listener {
    private static final String BED_SCROLL = "crafting.bedscroll";
    private static final int SECONDS_FOR_TELEPORT = 8;
    private static final int INCREMENT = 2;
    private JavaPlugin plugin;

    private static final Object teleportationsSync = new Object();
    private static List<UUID> teleportations = new LinkedList<>();
    private static final Random random = new Random();

    public BedTeleportListener(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
    }

    private void bedTeleport(Player player, Location bed, ItemStack itemInHand) {
        UUID uid = player.getUniqueId();
        synchronized (teleportationsSync) {
            if (teleportations.contains(uid))
                // the player is already teleporting
                return;
            teleportations.add(uid);
            // start the teleportation
            incrementalTeleportPlayer(20 / INCREMENT * SECONDS_FOR_TELEPORT, player, bed, itemInHand);
        }
    }

    private void incrementalTeleportPlayer(int sessionsLeft, Player player, Location bed, ItemStack itemInHand) {
        if (sessionsLeft == 0) {
            player.teleport(bed);
            itemInHand.setAmount(itemInHand.getAmount() - 1);
            spawnBedTeleportParticles(bed);
            synchronized (teleportationsSync) {
                teleportations.remove(player.getUniqueId());
            }
        } else {
            boolean contains;
            synchronized (teleportationsSync) {
                contains = teleportations.contains(player.getUniqueId());
            }
            if (contains) {
                spawnBedTeleportParticles(player.getLocation());
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, INCREMENT + 2, 5));
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, INCREMENT + 2, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, INCREMENT + 2, 1));
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> incrementalTeleportPlayer(sessionsLeft - INCREMENT, player, bed, itemInHand), INCREMENT);
            }
        }
    }

    private void spawnBedTeleportParticles(Location loc) {
        World world = loc.getWorld();
        if (world == null) return;
        for (int i = 0; i < 17; i++) {
            double addX = random.nextDouble() * 2 - 1;
            double addY = random.nextDouble() * 4 - 1;
            double addZ = random.nextDouble() * 2 - 1;
            BlockData fallingDustData = Material.BLACK_CONCRETE.createBlockData();
            world.spawnParticle(Particle.FALLING_DUST, loc.clone().add(new Vector(addX, addY, addZ)), 5, fallingDustData);
//            world.spawnParticle(Particle.SQUID_INK, loc.clone().add(new Vector(addX, addY, addZ)), 0);
        }
    }

    @EventHandler
    public void onPlayerDamaged(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        synchronized (teleportationsSync) {
            teleportations.remove(entity.getUniqueId());
        }
    }

    @EventHandler
    public void onTeleport(PlayerInteractEvent event) {
        @NotNull Action action = event.getAction();
        if (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) {
            // this is a right click
            ItemStack itemInHand = event.getItem();
            if (itemInHand == null || itemInHand.getType().isAir())
                return;

            // the item is good
            net.minecraft.server.v1_15_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemInHand);
            NBTTagCompound compound = (nmsStack.hasTag()) ? nmsStack.getTag() : new NBTTagCompound();
            if (compound == null)
                return;

            if (compound.hasKey("Item")) {
                NBTBase itemTags = compound.get("Item");
                if (itemTags == null)
                    return;
                if (itemTags.asString().equals(BED_SCROLL)) {
                    Player player = event.getPlayer();
                    Location bed = player.getBedSpawnLocation();
                    if (bed == null) {
                        player.sendMessage(ChatColor.RED + "You currently have no bed to teleport to");
                        return;
                    }
                    bedTeleport(player, bed, itemInHand);
                }

            }
        }
    }
}
