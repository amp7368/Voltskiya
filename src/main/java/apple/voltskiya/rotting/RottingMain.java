package apple.voltskiya.rotting;

import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class RottingMain {
    public static HashMap<String, Long> rottingChart;
    public static Map<String, String> rotIntoChart;
    public static NamespacedKey lastCheckedKey;
    public static NamespacedKey rottingCountdownKey;
    public static NamespacedKey cooler;
    public static NamespacedKey vanilla;
    private static JavaPlugin plugin;
    public static HashSet<InventoryType> furanceTypes;

    public static void enable(JavaPlugin pl) {
        furanceTypes = new HashSet<>();
        furanceTypes.add(InventoryType.FURNACE);
        furanceTypes.add(InventoryType.BLAST_FURNACE);
        furanceTypes.add(InventoryType.SMOKER);

        plugin = pl;
        IsRottable.initialize();
        getChart();
        lastCheckedKey = new NamespacedKey(pl, TagsNavigate.LAST_CHECKED);
        rottingCountdownKey = new NamespacedKey(pl, TagsNavigate.ROTTING_COUNTDOWN);
        cooler = new NamespacedKey(pl, TagsNavigate.COOLER);
        vanilla = new NamespacedKey(pl, TagsNavigate.VANILLA);
        new RottingListener(pl);
        new RottingMerge(pl);
        new RenameListener(pl);
        new RottingSmeltListener(pl);
        new CoolerPlaceListener(pl);

//        new SlotFinderListener(pl);

        System.out.println("[VoltskiyaApple] [Rotting] enabled");
    }

    private static void getChart() {
        rottingChart = new HashMap<>();
        rotIntoChart = new HashMap<>();

        File file = new File(String.format("%s%s%s", plugin.getDataFolder(), File.separator, TagsNavigate.ROTTING_DIR));
        if (!file.exists())
            if (!file.mkdir()) {
                System.err.println("[VoltskiyaApple] [Rotting] Could not make the directory for rotting");
                return;
            }
        file = new File(String.format("%s%s%s%s%s%s", plugin.getDataFolder(), File.separator, TagsNavigate.ROTTING_DIR, File.separator, TagsNavigate.ROTTING_CHART, ".yml"));
        if (!file.exists())
            try {
                if (!file.createNewFile()) {
                    System.err.println("[VoltskiyaApple] [Rotting] Could not make the rotting chart file");
                    return;
                }
            } catch (IOException e) {
                System.err.println("[VoltskiyaApple] [Rotting] Could not make the rotting chart file");
                return;
            }
        YamlConfiguration configOrig = YamlConfiguration.loadConfiguration(file);
        ConfigurationSection config = configOrig.getConfigurationSection(TagsNavigate.YML_CHART);
        if (config == null) {
            config = configOrig.createSection(TagsNavigate.YML_CHART);
            try {
                configOrig.save(file);
            } catch (IOException ignored) {
            }
        }
        for (String key : config.getKeys(false)) {
            long decayRate = config.getLong(key);
            rottingChart.put(key, decayRate);
        }
        file = new File(String.format("%s%s%s%s%s%s", plugin.getDataFolder(), File.separator, TagsNavigate.ROTTING_DIR, File.separator, TagsNavigate.ROT_INTO_CHART, ".yml"));
        if (!file.exists())
            try {
                if (!file.createNewFile()) {
                    System.err.println("[VoltskiyaApple] [Rotting] Could not make the rot-into chart file");
                    return;
                }
            } catch (IOException e) {
                System.err.println("[VoltskiyaApple] [Rotting] Could not make the rot-into chart file");
                return;
            }
        configOrig = YamlConfiguration.loadConfiguration(file);
        config = configOrig.getConfigurationSection(TagsNavigate.YML_CHART);
        if (config == null) {
            config = configOrig.createSection(TagsNavigate.YML_CHART);
            try {
                configOrig.save(file);
            } catch (IOException ignored) {
            }
        }
        for (String key : config.getKeys(false)) {
            String decayResult = config.getString(key);
            rotIntoChart.put(key, decayResult);
        }
    }
}
