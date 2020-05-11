package apple.voltskiya.hostile;

import org.bukkit.plugin.java.JavaPlugin;

public class HostileMain {
    public static void enable(JavaPlugin plugin){
        new HostileListener(plugin);
    }
}
