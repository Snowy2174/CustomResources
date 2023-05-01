package plugin.customresources.util;

import com.palmergames.util.TimeMgmt;
import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
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

    public static void createHologram(Machine machine, Location location, Boolean ready){
        String id = String.valueOf(machine.getId());

        if (holograms.containsKey(id)) {
            // If a hologram with the same ID exists, stop
            return;
        }
        HolographicDisplaysAPI api = HolographicDisplaysAPI.get(CustomResources.getPlugin());
        Hologram machineHolo = api.createHologram(location.add(0.0, 3, 0.0));

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

    private static void appendHologram(Hologram machineHolo, Machine machine, MachineConfig config) {
        ItemStack icon = new ItemStack(Material.getMaterial(config.getIcon()));

        machineHolo.getLines().appendItem(icon);
        machineHolo.getLines().appendText(ChatColor.GREEN + "" + machine.getState() + " " + ChatColor.GRAY + "" + config.getName());
        machineHolo.getLines().appendText(ChatColor.GRAY + "Tier [" + ChatColor.GOLD + machine.getTier() + ChatColor.GRAY + "]");
        machineHolo.getLines().appendText("");
    }

    public static void appendInfoHologram(Hologram machineHolo, MachineTier tierConfig) {
        machineHolo.getLines().appendText(ChatColor.GRAY + "Produces " + ChatColor.GREEN + tierConfig.getOutputMaterials().get(0) + ChatColor.GRAY + "x" + ChatColor.GREEN + tierConfig.getOutputAmounts().get(0));
        machineHolo.getLines().appendText(ChatColor.GOLD + "[" + TimeMgmt.countdownTimeHoursRaw(TimeMgmt.townyTime(true)) + "h Remaining]");
    }

    public static void appendReadyHologram(Hologram machineHolo, MachineTier tierConfig) {
        machineHolo.getLines().appendText(ChatColor.GRAY + "Produced " + ChatColor.GOLD + tierConfig.getOutputMaterials().get(0) + "x" + tierConfig.getOutputAmounts().get(0));
        machineHolo.getLines().appendText(ChatColor.GOLD + "[ Ready to Collect ]");
    }

    public static void removeHologramTask(Hologram hologram, String id){

        BukkitScheduler scheduler = getServer().getScheduler();
        BukkitTask task = scheduler.runTaskLaterAsynchronously(CustomResources.getPlugin(), new BukkitRunnable() {
            @Override
            public void run() {
                hologram.delete();
                holograms.remove(id);
            }
        }, 60 * 20L);}

    public static void removeHologram(String id) {
        holograms.get(id).delete();
        holograms.remove(id);
    }
}
