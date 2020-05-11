package apple.voltskiya.powertool;

import org.bukkit.plugin.java.JavaPlugin;

public class PowerToolMain {
    public static void enable(JavaPlugin plugin) {
        new PowerToolCommand(plugin);
        new PowerToolClearAllCommand(plugin);
        new PowerToolClearCommand(plugin);
        new PowerToolListCommand(plugin);
        new PowerToolListener(plugin);
        System.out.println("[VoltskiyaApple] [PowerTool] enabled");
    }
}
