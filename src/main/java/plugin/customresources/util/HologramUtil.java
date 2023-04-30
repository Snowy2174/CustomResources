package plugin.customresources.util;

import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import org.bukkit.Location;
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

public class HologramUtil {

    private static Map<String, Hologram> holograms = new HashMap<>();
    public static void createInfoHologram(Machine machine, Location location){

        String id = String.valueOf(machine.getId());
        
        HolographicDisplaysAPI api = HolographicDisplaysAPI.get(CustomResources.getPlugin());
        Hologram machineInfo = api.createHologram(location.add(0.0, 1, 0.0));

        MachineConfig config = MACHINES.get(machine.getType());
        MachineTier tierConfig = config.getTiers().get(machine.getTier());

        machineInfo.getLines().appendItem(new ItemStack(config.getIcon()));
        machineInfo.getLines().appendText("&a" + machine.getState() + " &7" + config.getName());
        machineInfo.getLines().appendText("&7Tier [ " + machine.getTier() + "]");
        machineInfo.getLines().appendText("");
        machineInfo.getLines().appendText("&7Produces &a" + tierConfig.getOutputMaterials().get(0) + "&7x&a" + tierConfig.getOutputAmounts().get(0) );
        machineInfo.getLines().appendText("&6[%townyadvanced_time_until_new_day_hours_raw%:%townyadvanced_time_until_new_day_minutes_raw% Remaining]");

        holograms.put(String.valueOf(machine.getId()), machineInfo);

        removeHologramTask(machineInfo, id);

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



    public static void createReadyHologram(Machine machine, Location location){

        HolographicDisplaysAPI api = HolographicDisplaysAPI.get(CustomResources.getPlugin());
        Hologram machineInfo = api.createHologram(location.add(0.0, 1, 0.0));

        MachineConfig config = MACHINES.get(machine.getType());
        MachineTier tierConfig = config.getTiers().get(machine.getTier());

        machineInfo.getLines().appendItem(new ItemStack(config.getIcon()));
        machineInfo.getLines().appendText("&6" + machine.getState() + " &7" + config.getName());
        machineInfo.getLines().appendText("&7Tier [ " + machine.getTier() + "]");
        machineInfo.getLines().appendText("");
        machineInfo.getLines().appendText("&7Produced &6" + tierConfig.getOutputMaterials().get(0)+ "&7x&6" + tierConfig.getOutputAmounts().get(0) );
        machineInfo.getLines().appendText("&6[ Ready to Collect ]");

        holograms.put(String.valueOf(machine.getId()), machineInfo);
    }

    public static void removeHologram(String id) {
        holograms.get(id).delete();
        holograms.remove(id);
    }
}
