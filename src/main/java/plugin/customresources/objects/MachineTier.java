package plugin.customresources.objects;

import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class MachineTier {

    private final int level;
    private final List<String> requiredResources;
    private final List<String> outputMaterials;
    private final List<Integer> outputAmounts;
    private final List<ItemStack> upgradeMaterials;
    private final int upgradeCost;

    public MachineTier(int level, List<String> requiredResources, List<String> outputMaterials, List<Integer> outputAmounts,
                       List<ItemStack> upgradeMaterials, int upgradeCost) {
        this.level = level;
        this.requiredResources = requiredResources;
        this.outputMaterials = outputMaterials;
        this.outputAmounts = outputAmounts;
        this.upgradeMaterials = upgradeMaterials;
        this.upgradeCost = upgradeCost;
    }


    public int getLevel() {
        return level;
    }

    public List<String> getRequiredResources() {
        return requiredResources;
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
