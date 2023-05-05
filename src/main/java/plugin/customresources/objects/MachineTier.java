package plugin.customresources.objects;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public class MachineTier {

    private final int level;
    private final int storage;
    private final List<String> inputItems;
    private final List<String> outputMaterials;
    private final List<Integer> outputAmounts;
    private final List<ItemStack> upgradeMaterials;
    private final double upgradeCost;
    private final int durability;
    private final List<ItemStack> repairMaterials;
    private final double repairCost;

    public MachineTier(int level, int storage, List<String> inputItems, List<String> outputMaterials, List<Integer> outputAmounts,
                       List<ItemStack> upgradeMaterials, double upgradeCost, int durability, List<ItemStack> repairMaterials, double repairCost) {
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

    public List<ItemStack> getUpgradeMaterials() {
        return upgradeMaterials;
    }

    public double getUpgradeCost() {
        return upgradeCost;
    }

    public List<ItemStack> getRepairMaterials() { return upgradeMaterials; }

    public double getRepairCost() { return repairCost; }

    public int getDurability(){ return durability; }
}
