package apple.voltskiya.hostile;

import net.minecraft.server.v1_15_R1.EntityLiving;
import org.bukkit.Bukkit;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

class HostileTarget {
    public static void target(Mob mob, Player player, JavaPlugin plugin) {
        System.out.println(mob.getName() + " targeting player");
        EntityLiving living = (EntityLiving) mob;


        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> target(mob, player, plugin), 20);
    }
}
