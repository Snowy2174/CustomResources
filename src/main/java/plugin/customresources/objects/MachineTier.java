package plugin.customresources.objects;

import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class MachineTier {

    private final int level;
    private final List<String> requiredResources;
    private final Map<String, Integer> output;
    private final List<ItemStack> upgradeMaterials;
    private final int upgradeCost;

    public MachineTier(int level, List<String> requiredResources, Map<String, Integer> output,
                       List<ItemStack> upgradeMaterials, int upgradeCost) {
        this.level = level;
        this.requiredResources = requiredResources;
        this.output = output;
        this.upgradeMaterials = upgradeMaterials;
        this.upgradeCost = upgradeCost;
    }

    public int getLevel() {
        return level;
    }

    public List<String> getRequiredResources() {
        return requiredResources;
    }

    public Map<String, Integer> getOutput() {
        return output;
    }

    public List<ItemStack> getUpgradeMaterials() {
        return upgradeMaterials;
    }

    public int getUpgradeCost() {
        return upgradeCost;
    }
}
