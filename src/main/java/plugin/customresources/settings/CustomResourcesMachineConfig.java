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

import static plugin.customresources.CustomResources.info;

public class CustomResourcesMachineConfig {

    private final CustomResources plugin;
    public static HashMap<String, MachineConfig> MACHINES;

    public CustomResourcesMachineConfig(CustomResources plugin) {
        this.plugin = plugin;
    }

    public static void load() {
        HashMap<String, MachineConfig> machines = new HashMap<>();
        File file = new File(CustomResources.getPlugin().getDataFolder(), "machines.yml" );
        if (!file.exists()){
            CustomResources.getPlugin().saveResource("machines.yml", false);
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        for (String key : config.getKeys(false)) {
            MachineConfig machine = createMachineFromConfig(config, key);
            machines.put(key, machine);
            info("[CustomResources] Loaded <green>" + machine.getName() + " <gray>and<green> " + machine.getTiers().size() + " <gray>tiers");
        }

        CustomResourcesMachineConfig.MACHINES = machines;
        info("[CustomResources] Loaded <green>" + machines.size() + " <gray>machines");
    }

    private static MachineConfig createMachineFromConfig(YamlConfiguration config, String key) {
        String name = config.getString(key + ".name");
        String icon = config.getString(key + ".icon");
        String structure = config.getString(key + ".structure");
        double machineCost = config.getDouble(key + ".cost");
        int townMachineryLevel = config.getInt(key + ".town_machinery_level");
        List<String> machinePreferredBiomes = config.getStringList(key + ".preferred_biomes");

        List<MachineTier> tiers = createTiersFromConfig(config, key);

        return new MachineConfig(name, structure, icon, machineCost, townMachineryLevel, machinePreferredBiomes, tiers);
    }

    private static List<MachineTier> createTiersFromConfig(YamlConfiguration config, String key) {
        return Objects.requireNonNull(config.getConfigurationSection(key + ".tiers"))
                .getKeys(false)
                .stream()
                .map(Integer::parseInt)
                .sorted()
                .map(tierLevel -> createTierFromConfig(config, key, tierLevel))
                .collect(Collectors.toList());
    }

    private static MachineTier createTierFromConfig(YamlConfiguration config, String key, int tierLevel) {
        int tierDurability = config.getInt(key + ".tiers." + tierLevel + ".durability", 1);
        int tierResourceStorage = config.getInt(key + ".tiers." + tierLevel + ".storage", 3);
        List<String> tierInputs = config.isSet(key + ".tiers." + tierLevel + ".input")
                ? config.getStringList(key + ".tiers." + tierLevel + ".input")
                : new ArrayList<>();

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

        List<String> upgradeMaterials = config.getStringList(key + ".tiers." + tierLevel + ".upgrade_materials")
                .stream()
                .map(upgradeMaterialString -> upgradeMaterialString.split(" "))
                .map(upgradeMaterialParts -> upgradeMaterialParts[0].toUpperCase() + ":" + upgradeMaterialParts[1])
                .collect(Collectors.toList());

        double tierUpgradeCost = config.getInt(key + ".tiers." + tierLevel + ".upgrade_cost", 0);

        List<ItemStack> tierRepairMaterials = config.getStringList(key + ".tiers." + tierLevel + ".repair_materials")
                .stream()
                .map(upgradeMaterialString -> upgradeMaterialString.split(" "))
                .map(upgradeMaterialParts -> {
                    Material repairMaterial = Material.getMaterial(upgradeMaterialParts[0].toUpperCase());
                    int repairAmount = Integer.parseInt(upgradeMaterialParts[1]);
                    return new ItemStack(repairMaterial, repairAmount);
                })
                .collect(Collectors.toList());
        double tierRepairCost = config.getInt(key + ".tiers." + tierLevel + ".repair_cost", 0);

        return new MachineTier(tierLevel, tierResourceStorage, tierInputs, outputMaterialNames, outputMaterialAmounts, upgradeMaterials, tierUpgradeCost, tierDurability, tierRepairMaterials, tierRepairCost);
    }

    public static List<String> getAllMachineNames() {
        if (MACHINES == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(MACHINES.keySet());
    }


    public static void unload() {
        CustomResourcesMachineConfig.MACHINES = null;
    }

    public static void reload() {
        unload();
        load();
    }

}
