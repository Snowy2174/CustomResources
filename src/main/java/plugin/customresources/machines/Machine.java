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
    private final MachineStructure building;
    private boolean isActivated;
    private boolean isBroken;

    public Machine(String name, Material output, int outputAmount, int ticksPerOutput, MachineStructure machineStructure, CustomResources plugin) {
        this.name = name;
        this.output = output;
        this.outputAmount = outputAmount;
        this.ticksPerOutput = ticksPerOutput;
        this.ticksUntilOutput = ticksPerOutput;
        this.building = machineStructure;
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

    public boolean isComplete() {
        return building.isComplete();
    }

    public Location getOutputLocation() {
        return building.getOutputLocation();
    }

    public boolean isPartOfMachine(Location location) {
        return building.isPartOfMachine(location);
    }

    public boolean canBuildBlock(Location location, List<Location> existingBlocks) {
        if (location.getBlock().getType() != Material.AIR) {
            return false;
        }

        Location above = location.clone().add(0, 1, 0);
        if (above.getBlock().getType() != Material.AIR) {
            return false;
        }

        // Check if block is adjacent to any existing blocks in the machine
        for (Location block : existingBlocks) {
            if (location.distance(block) <= 1) {
                return true;
            }
        }

        return true;
    }

    public Location getCenterLocation(List<Location> blocks) {
        int centerX = 0;
        int centerY = 0;
        int centerZ = 0;

        for (Location block : blocks) {
            centerX += block.getBlockX();
            centerY += block.getBlockY();
            centerZ += block.getBlockZ();
        }

        int size = blocks.size();
        centerX /= size;
        centerY /= size;
        centerZ /= size;

        return new Location(blocks.get(0).getWorld(), centerX, centerY, centerZ);
    }


    public boolean onInteract(Player player, Block clickedBlock) {
        if (building.isPartOfMachine(clickedBlock.getLocation())) {
            // Player has interacted with a block in the machine
            if (!isComplete()) {
                player.sendMessage(ChatColor.RED + "This machine is not complete yet.");
                return true;
            }
            if (isBroken()) {
                player.sendMessage(ChatColor.RED + "This machine is broken.");
                return true;
            }
            if (isActivated()) {
                player.sendMessage(ChatColor.RED + "This machine is already activated.");
                return true;
            }
            // Check if the player has the required materials
            ItemStack[] materials = building.getMaterials();
            for (ItemStack material : materials) {
                if (!player.getInventory().contains(material)) {
                    player.sendMessage(ChatColor.RED + "You don't have all the materials to activate this machine.");
                    return true;
                }
            }
            // Remove the required materials from the player's inventory
            for (ItemStack material : materials) {
                player.getInventory().removeItem(material);
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


