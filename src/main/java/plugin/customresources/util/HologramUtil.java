package plugin.customresources.util;

import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import me.filoghost.holographicdisplays.api.hologram.line.ItemHologramLine;
import me.filoghost.holographicdisplays.api.hologram.line.TextHologramLine;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import plugin.customresources.CustomResources;
import plugin.customresources.objects.Machine;
import plugin.customresources.objects.MachineConfig;
import plugin.customresources.objects.MachineTier;

import java.util.HashMap;
import java.util.Map;

import static plugin.customresources.settings.CustomResourceMachineConfig.MACHINES;

public class HologramUtil {

    private static Map<String, Hologram> holograms = new HashMap<>();
    public static void createInfoHologram(Machine machine){
        
        HolographicDisplaysAPI api = HolographicDisplaysAPI.get(CustomResources.getPlugin());
        Hologram machineInfo = api.createHologram(machine.getLocation().add(0.0, 0.6, 0.0));

        MachineConfig config = MACHINES.get(machine.getType());
        MachineTier tierConfig = config.getTiers().get(machine.getTier());
        ItemStack output = tierConfig.getOutput().get(machine.getTier());

        machineInfo.getLines().appendItem(new ItemStack(config.getIcon()));
        machineInfo.getLines().appendText("&a" + machine.getState() + " &7" + config.getName());
        machineInfo.getLines().appendText("&7Tier [ " + machine.getTier() + "]");
        machineInfo.getLines().appendText(" ");
        machineInfo.getLines().appendText("&7Produces &a" + output.getType() + "&7x&a" + output.getAmount() );
        machineInfo.getLines().appendText("A hologram line");

        holograms.put(String.valueOf(machine.getId()), machineInfo);
    }


    public static void removeInfoHologram(Machine machine){
        String id = String.valueOf(machine.getId());
        if (holograms.containsKey(id)) {
            Hologram hologram = holograms.get(id);
            hologram.delete();
            // Remove from map
            holograms.remove(id);
                }
            }
}
