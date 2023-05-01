package plugin.customresources.objects;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import plugin.customresources.enums.CustomResourcesMachineState;

import java.util.HashMap;
import java.util.UUID;

public class Machine {

    private final String type;
    private CustomResourcesMachineState state;
    private final UUID id;
    private final Integer tier;
    private boolean isActivated = false;
    private boolean isBroken = false;

    private ItemStack storedItem = null;

    public Machine(UUID id, String type, Integer tier) {
        this.type = type;
        this.tier = tier;
        this.state = CustomResourcesMachineState.Active;
        this.id = id;
    }

    /**
     * Get the type of the machine.
     *
     * @return The type of the machine.
     */
    public String getType() {
        return type;
    }

    /**
     * Get the ID of the machine.
     *
     * @return The ID of the machine.
     */
    public UUID getId() {
        return id;
    }

    /**
     * Get the state of the machine.
     *
     * @return The state of the machine.
     */
    public CustomResourcesMachineState getState() {
        return state;
    }

    /**
     * Get the tier of the machine.
     *
     * @return The tier of the machine.
     */
    public Integer getTier() {
        return tier;
    }

    /**
     * Set the state of the machine.
     *
     * @param state The state to set the machine to.
     */
    public void setState(CustomResourcesMachineState state) {
        this.state = state;
    }


    /**
     * Activate the machine.
     */
    public void activate() {
        if (!isActivated && !isBroken) {
            isActivated = true;
        }
    }

    /**
     * Deactivate the machine.
     */
    public void deactivate() {
        if (isActivated) {
            isActivated = false;
        }
    }

    /**
     * Check if the machine is activated.
     *
     * @return True if the machine is activated, false otherwise.
     */
    public boolean isActivated() {
        return isActivated;
    }

    /**
     * Break the machine.
     */
    public void breakMachine() {
        isBroken = true;
        deactivate();
    }

    /**
     * Check if the machine is broken.
     *
     * @return True if the machine is broken, false otherwise.
     */
    public boolean isBroken() {
        return isBroken;
    }

    /**
     * Run the machine.
     */
    private void runMachine() {
        // TODO: Implement this method.
    }

    /**
     * Get the ItemStack stored in the machine.
     *
     * @return The stored ItemStack, or null if there is no item stored.
     */
    public ItemStack getStoredItem() {
        return storedItem;
    }


    /**
     * Add an ItemStack to the machine.
     *
     * @param itemStack The ItemStack to add.
     */
    public void setStoredItem(ItemStack itemStack, Player player) {
        if (storedItem == null) {
            storedItem = itemStack;
        } else {
            int amountToAdd = itemStack.getAmount();
            int spaceLeft = storedItem.getMaxStackSize() - storedItem.getAmount();
            if (amountToAdd <= spaceLeft) {
                storedItem.setAmount(storedItem.getAmount() + amountToAdd);
            } else {
                storedItem.setAmount(storedItem.getMaxStackSize());
                ItemStack remaining = itemStack.clone();
                remaining.setAmount(amountToAdd - spaceLeft);
                addItem(remaining, player);
            }
        }
    }
    private void addItem(ItemStack remaining, Player player) {
        HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(remaining);
        if (!leftover.isEmpty()) {
            Location loc = player.getLocation();
            for (ItemStack item : leftover.values()) {
                player.getWorld().dropItem(loc, item);
            }
        }
    }
}