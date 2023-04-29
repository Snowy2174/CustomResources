package plugin.customresources.settings;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import plugin.customresources.CustomResources;
import plugin.customresources.objects.MachineConfig;
import plugin.customresources.objects.MachineTier;

import java.io.File;
import java.util.*;

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
            String name = config.getString(key + ".name");
            String icon = config.getString(key + ".icon");
            String structure = config.getString(key + ".structure");
            double machineCost = config.getDouble(key + ".cost");
            int machineTownLevel = config.getInt(key + ".town_level");
            List<String> machinePreferredBiomes = config.getStringList(key + ".preferred_biomes");

            List<MachineTier> tiers = new ArrayList<>();
            config.getConfigurationSection(key + ".tiers")
                    .getKeys(false)
                    .stream()
                    .map(Integer::parseInt)
                    .sorted()
                    .forEach(tierLevel -> {
                        List<String> tierResources = config.getStringList(key + ".tiers." + tierLevel + ".resources");
                        Map<Material, Integer> tierOutput = new HashMap<>();
                        config.getStringList(key + ".tiers." + tierLevel + ".output")
                                .forEach(outputString -> {
                                    String[] outputParts = outputString.split(" ");
                                    Material outputMaterial = Material.getMaterial(outputParts[0].toUpperCase());
                                    int outputAmount = Integer.parseInt(outputParts[1]);
                                    tierOutput.put(outputMaterial, outputAmount);
                                });
                        List<ItemStack> upgradeMaterials = new ArrayList<>();
                        config.getStringList(key + ".tiers." + tierLevel + ".upgrade_materials")
                                .forEach(upgradeMaterialString -> {
                                    String[] upgradeMaterialParts = upgradeMaterialString.split(" ");
                                    Material upgradeMaterial = Material.getMaterial(upgradeMaterialParts[0].toUpperCase());
                                    int upgradeAmount = Integer.parseInt(upgradeMaterialParts[1]);
                                    upgradeMaterials.add(new ItemStack(upgradeMaterial, upgradeAmount));
                                });
                        int tierUpgradeCost = config.getInt(key + ".tiers." + tierLevel + ".upgrade_cost", 0);
                        Map<Integer, ItemStack> tierOutputMap = new HashMap<>();
                        tierOutputMap.put(tierLevel, (ItemStack) tierOutput);
                        tiers.add(new MachineTier(tierLevel, tierResources, tierOutputMap, upgradeMaterials, tierUpgradeCost));
                    });

            MachineConfig machine = new MachineConfig(name, structure, icon, machineCost, machineTownLevel, machinePreferredBiomes, tiers);
            machines.put(key, machine);
            sendMsg("[CustomResources] Loaded <green>" + name + " <gray>and<green> " + tiers.size() + " <gray>tiers");
        }

        CustomResourceMachineConfig.MACHINES = machines;
        sendMsg("[CustomResources] Loaded <green>" + machines.size() + " <gray>machines");
    }

    public void unload() {
        CustomResourceMachineConfig.MACHINES = null;
    }
}
