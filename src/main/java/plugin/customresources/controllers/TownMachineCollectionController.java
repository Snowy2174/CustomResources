package plugin.customresources.controllers;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class TownMachineCollectionController {

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
