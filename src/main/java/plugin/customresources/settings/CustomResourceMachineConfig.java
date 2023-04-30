package plugin.customresources.settings;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import plugin.customresources.CustomResources;
import plugin.customresources.objects.MachineConfig;
import plugin.customresources.objects.MachineTier;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static com.palmergames.bukkit.towny.TownyMessaging.sendMsg;

public class CustomResourceMachineConfig {

    private final CustomResources plugin;
    private final File configFile;

    public static HashMap<String, MachineConfig> MACHINES;

    public CustomResourceMachineConfig(CustomResources plugin) {
        this.plugin = plugin;
        this.configFile = new File(plugin.getDataFolder(), "machines.yml");
    }

    public void load() {
        HashMap<String, MachineConfig> machines = new HashMap<>();
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        for (String key : config.getKeys(false)) {
            MachineConfig machine = createMachineFromConfig(config, key);
            machines.put(key, machine);
            sendMsg("[CustomResources] Loaded <green>" + machine.getName() + " <gray>and<green> " + machine.getTiers().size() + " <gray>tiers");
        }

        CustomResourceMachineConfig.MACHINES = machines;
        sendMsg("[CustomResources] Loaded <green>" + machines.size() + " <gray>machines");
    }

    private MachineConfig createMachineFromConfig(YamlConfiguration config, String key) {
        String name = config.getString(key + ".name");
        String icon = config.getString(key + ".icon");
        String structure = config.getString(key + ".structure");
        double machineCost = config.getDouble(key + ".cost");
        int machineTownLevel = config.getInt(key + ".town_level");
        List<String> machinePreferredBiomes = config.getStringList(key + ".preferred_biomes");

        List<MachineTier> tiers = createTiersFromConfig(config, key);

        return new MachineConfig(name, structure, icon, machineCost, machineTownLevel, machinePreferredBiomes, tiers);
    }

    private List<MachineTier> createTiersFromConfig(YamlConfiguration config, String key) {
        return config.getConfigurationSection(key + ".tiers")
                .getKeys(false)
                .stream()
                .map(Integer::parseInt)
                .sorted()
                .map(tierLevel -> createTierFromConfig(config, key, tierLevel))
                .collect(Collectors.toList());
    }

    private MachineTier createTierFromConfig(YamlConfiguration config, String key, int tierLevel) {
        List<String> tierResources = config.getStringList(key + ".tiers." + tierLevel + ".resources");
        Map<String, Integer> tierOutput = config.getStringList(key + ".tiers." + tierLevel + ".output")
                .stream()
                .map(outputString -> outputString.split(" "))
                .collect(Collectors.toMap(outputParts -> outputParts[0].toUpperCase(), outputParts -> Integer.parseInt(outputParts[1])));

        // Store output material name and amount separately
        List<String> outputMaterialNames = new ArrayList<>();
        List<Integer> outputMaterialAmounts = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : tierOutput.entrySet()) {
            outputMaterialNames.add(entry.getKey());
            outputMaterialAmounts.add(entry.getValue());
        }

        List<ItemStack> upgradeMaterials = config.getStringList(key + ".tiers." + tierLevel + ".upgrade_materials")
                .stream()
                .map(upgradeMaterialString -> upgradeMaterialString.split(" "))
                .map(upgradeMaterialParts -> {
                    Material upgradeMaterial = Material.getMaterial(upgradeMaterialParts[0].toUpperCase());
                    int upgradeAmount = Integer.parseInt(upgradeMaterialParts[1]);
                    return new ItemStack(upgradeMaterial, upgradeAmount);
                })
                .collect(Collectors.toList());
        int tierUpgradeCost = config.getInt(key + ".tiers." + tierLevel + ".upgrade_cost", 0);

        return new MachineTier(tierLevel, tierResources, outputMaterialNames, outputMaterialAmounts, upgradeMaterials, tierUpgradeCost);
    }



    public void unload() {
        CustomResourceMachineConfig.MACHINES = null;
    }
}
