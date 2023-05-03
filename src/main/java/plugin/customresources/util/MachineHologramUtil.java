package plugin.customresources.util;

import com.palmergames.util.TimeMgmt;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import plugin.customresources.CustomResources;
import plugin.customresources.objects.Machine;
import plugin.customresources.objects.MachineConfig;
import plugin.customresources.objects.MachineTier;

import java.util.HashMap;
import java.util.Map;

import static org.bukkit.Bukkit.getServer;
import static plugin.customresources.settings.CustomResourcesMachineConfig.MACHINES;

public class MachineHologramUtil {

    private static final Map<String, Hologram> holograms = new HashMap<>();

    public static void createHologram(Machine machine, Boolean ready) {
        String id = String.valueOf(machine.getId());
        Location location = machine.getLocation().add(0.0, 3.0, 0.0);

        if (holograms.containsKey(id)) {
            // If a hologram with the same ID exists, stop
            return;
        } else {
            HolographicDisplaysAPI api = HolographicDisplaysAPI.get(CustomResources.getPlugin());
            Hologram machineHolo = api.createHologram(location);

            MachineConfig config = MACHINES.get(machine.getType());
            MachineTier tierConfig = config.getTiers().get(machine.getTier());

            appendHologram(machineHolo, machine, config);

            if (ready) {
                appendReadyHologram(machineHolo, tierConfig);
            } else {
                appendInfoHologram(machineHolo, tierConfig);
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

    public static void appendInfoHologram(Hologram machineHolo, MachineTier tierConfig) {

        String line1 = "&7Produces &a" + tierConfig.getOutputMaterials().get(0) + "&7x&a" + tierConfig.getOutputAmounts().get(0);
        String line2 = "&6[ " + TimeMgmt.countdownTimeHoursRaw(TimeMgmt.townyTime(true)) + "h Remaining ]";

        machineHolo.getLines().appendText(ChatColor.translateAlternateColorCodes( '&',line1));
        machineHolo.getLines().appendText(ChatColor.translateAlternateColorCodes('&', line2));
    }

    public static void appendReadyHologram(Hologram machineHolo, MachineTier tierConfig) {
        String line1 = "&7Produced &6" + tierConfig.getOutputMaterials().get(0) + "&7x&6" + tierConfig.getOutputAmounts().get(0);
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
