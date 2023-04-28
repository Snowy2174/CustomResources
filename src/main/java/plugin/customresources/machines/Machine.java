package plugin.customresources.machines;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import plugin.customresources.CustomResources;

import java.util.List;

public class Machine {

    private final CustomResources plugin;
    private final String name;
    private final Material output;
    private final int outputAmount;
    private final int ticksPerOutput;
    private int ticksUntilOutput;
    private boolean isActivated;
    private boolean isBroken;

    public Machine(String name, Material output, int outputAmount, int ticksPerOutput, CustomResources plugin) {
        this.name = name;
        this.output = output;
        this.outputAmount = outputAmount;
        this.ticksPerOutput = ticksPerOutput;
        this.ticksUntilOutput = ticksPerOutput;
        this.isActivated = false;
        this.isBroken = false;
        this.plugin = plugin;
    }


    public String getName() {
        return name;
    }
    public void activate() {
        if (!isActivated && !isBroken) {
            isActivated = true;
            plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this::runMachine, 1, 1);
        }
    }

    public void deactivate() {
        if (isActivated) {
            isActivated = false;
            plugin.getServer().getScheduler().cancelTasks(plugin);
        }
    }

    public boolean isActivated() {
        return isActivated;
    }

    public void breakMachine() {
        isBroken = true;
        deactivate();
    }

    public boolean isBroken() {
        return isBroken;
    }


    public boolean onInteract(Player player, Block clickedBlock) {
        if (building.isPartOfMachine(clickedBlock.getLocation())) {
            // Player has interacted with a block in the machine
            if (isBroken()) {
                player.sendMessage(ChatColor.RED + "This machine is broken.");
                return true;
            }
            if (isActivated()) {
                player.sendMessage(ChatColor.RED + "This machine is already activated.");
                return true;
            }
            activate();
            player.sendMessage(ChatColor.GREEN + "Machine activated.");
            return true;
        }
        return false;
    }

    public void repairMachine() {
        isBroken = false;
    }

    private void runMachine() {
        if (isActivated) {
            if (ticksUntilOutput > 0) {
                ticksUntilOutput--;
            } else {
                ticksUntilOutput = ticksPerOutput;
                Location outputLocation = building.getOutputLocation();
                Block outputBlock = outputLocation.getBlock();
                if (outputBlock.getType() == Material.AIR) {
                    outputBlock.getWorld().playSound(outputLocation, Sound.BLOCK_ANVIL_PLACE, 1, 1);
                    outputBlock.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, outputLocation.add(0.5, 0.5, 0.5), 5, 0.2, 0.2, 0.2);
                    ItemStack itemStack = new ItemStack(output, outputAmount);
                    Item item = outputBlock.getWorld().dropItem(outputLocation.add(0.5, 1, 0.5), itemStack);
                    item.setVelocity(new Vector(0, 0, 0));
                }
            }
        }
    }
}


