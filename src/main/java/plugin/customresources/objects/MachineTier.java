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
    private final int upgradeCost;

    public MachineTier(int level, int storage, List<String> inputItems, List<String> outputMaterials, List<Integer> outputAmounts,
                       List<ItemStack> upgradeMaterials, int upgradeCost) {
        this.level = level;
        this.storage = storage;
        this.inputItems = inputItems;
        this.outputMaterials = outputMaterials;
        this.outputAmounts = outputAmounts;
        this.upgradeMaterials = upgradeMaterials;
        this.upgradeCost = upgradeCost;
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

    public int getUpgradeCost() {
        return upgradeCost;
    }
}
