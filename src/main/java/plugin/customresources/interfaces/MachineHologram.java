package plugin.customresources.interfaces;

import com.palmergames.util.TimeMgmt;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import plugin.customresources.CustomResources;
import plugin.customresources.objects.Machine;
import plugin.customresources.objects.MachineConfig;
import plugin.customresources.objects.MachineTier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static plugin.customresources.settings.CustomResourcesMachineConfig.MACHINES;

public class MachineHologram {

    private static final Map<String, Hologram> holograms = new HashMap<>();

    public static void createHologram(Machine machine) {
        String id = String.valueOf(machine.getId());
        Location location = machine.getLocation().clone().add(0.0, 2.0, 0.0);

        if (holograms.containsKey(id)) {
            // If a hologram with the same ID exists, stop
        } else {
            HolographicDisplaysAPI api = HolographicDisplaysAPI.get(CustomResources.getPlugin());
            Hologram machineHolo = api.createHologram(location);

            MachineConfig config = MACHINES.get(machine.getType());

            appendHologram(machineHolo, machine, config);

            if (machine.getStoredResourcesInteger() > 0) {
                appendReadyHologram(machineHolo, machine.getTierConfig(), machine.getResourceType());
            } else if (machine.getState() == Machine.CustomResourcesMachineState.Upgrading) {
              appendUpgradeHologram(machineHolo, machine.getTierConfig(), machine);
            } else {
                appendInfoHologram(machineHolo, machine.getTierConfig(), machine.getResourceType());
                removeHologramTask(machineHolo, id);
            }

            holograms.put(id, machineHolo);
        }
    }

    private static void appendHologram(Hologram machineHolo, Machine machine, MachineConfig config) {
        ItemStack icon = new ItemStack(Material.getMaterial(config.getIcon()));

        String line1 = "&a" + machine.getState() + "&7 " + config.getName();
        String line2 = "&7Tier [ &6" + machine.getTier() + " &7]";

        machineHolo.getLines().appendItem(icon);
        machineHolo.getLines().appendText(ChatColor.translateAlternateColorCodes( '&',line1));
        machineHolo.getLines().appendText(ChatColor.translateAlternateColorCodes('&', line2));
        machineHolo.getLines().appendText("");
    }

    public static void appendInfoHologram(Hologram machineHolo, MachineTier tierConfig, Integer resource) {

        String line1 = "&7Produces &a" + tierConfig.getOutputMaterials().get(resource) + "&7x&a" + tierConfig.getOutputAmounts().get(resource);
        String line2 = "&6[ " + TimeMgmt.countdownTimeHoursRaw(TimeMgmt.townyTime(true)) + "h Remaining ]";

        machineHolo.getLines().appendText(ChatColor.translateAlternateColorCodes( '&',line1));
        machineHolo.getLines().appendText(ChatColor.translateAlternateColorCodes('&', line2));
    }
    private static void appendUpgradeHologram(Hologram machineHolo, MachineTier tierConfig, Machine machine) {
        String line1 = "&7Upgrading: &a" + tierConfig.getUpgradeMaterialTypes() + "&7x&a" + tierConfig.getUpgradeMaterialAmounts();
        machineHolo.getLines().appendText(ChatColor.translateAlternateColorCodes('&', line1));

        List<String> upgradeMaterials = tierConfig.getUpgradeMaterialTypes();
        for (String material : upgradeMaterials) {
            String line2 = "&7Remaining: &6" + material + machine.getStoredMaterialAmount(Material.valueOf(material))+ " &7/&6 " + tierConfig.getUpgradeMaterialAmount(Material.valueOf(material));
            machineHolo.getLines().appendText(ChatColor.translateAlternateColorCodes('&', line2));
        }
    }

    public static void appendReadyHologram(Hologram machineHolo, MachineTier tierConfig, Integer resource) {
        String line1 = "&7Produced &6" + tierConfig.getOutputMaterials().get(resource) + "&7x&6" + tierConfig.getOutputAmounts().get(resource);
        String line2 = "&6[ Ready to Collect! ]";

        machineHolo.getLines().appendText(ChatColor.translateAlternateColorCodes( '&',line1));
        machineHolo.getLines().appendText(ChatColor.translateAlternateColorCodes('&', line2));
    }

    public static void removeHologramTask(Hologram hologram, String id) {
        BukkitScheduler scheduler = Bukkit.getScheduler();
        BukkitTask task = scheduler.runTaskLater(CustomResources.getPlugin(), () -> {
            hologram.delete();
            holograms.remove(id);
        }, 60 * 20L);
    }


    public static void removeHologram(String id) {
        holograms.get(id).delete();
        holograms.remove(id);
    }
}
