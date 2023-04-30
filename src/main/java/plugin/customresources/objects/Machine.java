package plugin.customresources.objects;

import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import plugin.customresources.CustomResources;
import plugin.customresources.enums.CustomResourcesMachineState;

import java.util.UUID;

public class Machine {

    private final CustomResources plugin;
    private final String type;
    private final CustomResourcesMachineState state;
    private final UUID id;
    private final Integer tier;
    private boolean isActivated = false;
    private boolean isBroken = false;

    public Machine(UUID id, String type, Integer tier) {
        this.type = type;
        this.tier = tier;
        this.state = CustomResourcesMachineState.Active;
        this.id = id;
        this.plugin = CustomResources.getPlugin();
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
        // TODO: Implement this method.
    }

    /**
     * Activate the machine.
     */
    public void activate() {
        if (!isActivated && !isBroken) {
            isActivated = true;
            plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, this::runMachine, 1, 1);
        }
    }

    /**
     * Deactivate the machine.
     */
    public void deactivate() {
        if (isActivated) {
            isActivated = false;
            plugin.getServer().getScheduler().cancelTasks(plugin);
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
     * Interact with the machine.
     *
     * @param player The player interacting with the machine.
     */
    public void interact(Player player) {
        // TODO: Implement this method.
    }

    /**
     * Add an item to the machine.
     *
     * @param item The item to add.
     */
    public void addItem(Item item) {
        // TODO: Implement this method
    }
}