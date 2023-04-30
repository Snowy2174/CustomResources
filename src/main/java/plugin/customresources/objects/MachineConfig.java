package plugin.customresources.objects;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public class MachineConfig {
    private final String name;
    private final String structure;
    private final String icon;
    private final double cost;
    private final int townLevel;
    private final List<String> preferredBiomes;
    private final List<MachineTier> tiers;

    public MachineConfig(String name, String structure, String icon, double cost, int townLevel, List<String> preferredBiomes, List<MachineTier> tiers) {
        this.name = name;
        this.structure = structure;
        this.icon = icon;
        this.cost = cost;
        this.townLevel = townLevel;
        this.preferredBiomes = preferredBiomes;
        this.tiers = tiers;
    }

    public String getName() {
        return name;
    }

    public String getStructure() {
        return structure;
    }

    public double getCost() {
        return cost;
    }

    public int getTownLevel() {
        return townLevel;
    }

    public List<String> getPreferredBiomes() {
        return preferredBiomes;
    }

    public List<MachineTier> getTiers() {
        return tiers;
    }

    public String getIcon() {
        return icon;
    }
}