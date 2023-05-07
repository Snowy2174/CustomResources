package plugin.customresources.objects;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

public class MachineTier {

    private final int level;
    private final int storage;
    private final List<String> inputItems;
    private final List<String> outputMaterials;
    private final List<Integer> outputAmounts;
    private final List<String> upgradeMaterials;
    private final double upgradeCost;
    private final int durability;
    private final List<ItemStack> repairMaterials;
    private final double repairCost;

    public MachineTier(int level, int storage, List<String> inputItems, List<String> outputMaterials, List<Integer> outputAmounts,
                       List<String> upgradeMaterials, double upgradeCost, int durability, List<ItemStack> repairMaterials, double repairCost) {
        this.level = level;
        this.storage = storage;
        this.inputItems = inputItems;
        this.outputMaterials = outputMaterials;
        this.outputAmounts = outputAmounts;
        this.upgradeMaterials = upgradeMaterials;
        this.upgradeCost = upgradeCost;
        this.durability = durability;
        this.repairCost = repairCost;
        this.repairMaterials = repairMaterials;
    }


    public int getLevel() {
        return level;
    }

    public int getResourceStorage() {
        return storage;
    }

    public List<String> getInputItems() {
        return inputItems;
    }

    public List<String> getOutputMaterials() {
        return outputMaterials;
    }

    public List<Integer> getOutputAmounts() {
        return outputAmounts;
    }

    public List<String> getUpgradeMaterialTypes() {
        return upgradeMaterials.stream()
                .map(materialString -> materialString.split(":")[0])
                .collect(Collectors.toList());
    }

    public List<Integer> getUpgradeMaterialAmounts() {
        return upgradeMaterials.stream()
                .map(materialString -> Integer.parseInt(materialString.split(":")[1]))
                .collect(Collectors.toList());
    }

    public int getUpgradeMaterialAmount(Material material) {
        for (String upgradeMaterialAmount : upgradeMaterials) {
            String[] parts = upgradeMaterialAmount.split(":");
            if (parts.length == 2) {
                Material storedMaterial = Material.getMaterial(parts[0]);
                if (storedMaterial != null && storedMaterial.equals(material)) {
                    return Integer.parseInt(parts[1]);
                }
            }
        }
        return 0;
    }




    public double getUpgradeCost() {
        return upgradeCost;
    }

    public List<ItemStack> getRepairMaterials() { return repairMaterials; }

    public double getRepairCost() { return repairCost; }

    public int getDurability(){ return durability; }
}
