package apple.voltskiya.respawn;

import org.bukkit.configuration.ConfigurationSection;

public class Coord {
    public int x;
    public int z;

    public Coord(ConfigurationSection config) {
        x = config.getInt("x");
        z = config.getInt("z");
    }
}
