package plugin.customresources.objects;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import static plugin.customresources.settings.CustomResourcesMachineConfig.MACHINES;

import java.util.*;

public class Machine {

    private final Location location;
    private final String type;
    private CustomResourcesMachineState state;
    private Integer storedResources;
    private final String id;
    private Integer tier;
    private Integer durability;

    private ItemStack fuelItemStack = null;
    private ArrayList<String> storedMaterialStrings = new ArrayList<>();


    public Machine(String id, String type, Integer tier, Location location, Integer durability) {
        this.type = type;
        this.tier = tier;
        this.id = id;
        this.durability = durability;

        this.location = location;

        this.state = CustomResourcesMachineState.Active;
        this.storedResources = 0;
    }

    public enum CustomResourcesMachineState {
        Active, Broken, Upgrading, Repairing

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
     * Get the location of the machine.
     *
     * @return The location of the machine.
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Get the ID of the machine.
     *
     * @return The ID of the machine.
     */
    public String getId() {
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
    public void setTier(Integer tier) { this.tier = tier; }

    /**
     * Set the state of the machine.
     *
     * @param state The state to set the machine to.
     */
    public void setState(CustomResourcesMachineState state) {
        this.state = state;
    }

    public Integer getDurability(){
        return this.durability;
    }

    public void setDurability(Integer durability){ this.durability = durability; }

    public Integer getMaxDurability(){
        MachineConfig config = MACHINES.get(getType());
        Integer machineTier = getTier();
        Integer maxDurability = config.getTiers().get(machineTier).getDurability();
        return maxDurability;
    }

    /**
     * Run the machine.
     */
    private void runMachine() {
        // TODO: Implement this method.
    }

    /**
     * Get the stored resource instances
     *
     * @return The integer value of stored resources
     */
    public Integer getStoredResourcesInteger() {
        return storedResources;
    }

    /**
     * If the stored resources is less than the max, increment
     *
     * @return The integer value of stored resources
     */
    public void incrementStoredResources(int maxResources) {
        if (storedResources < maxResources) {
            storedResources++;
        }
    }

    /**
     * Get the map of stored materials and amounts.
     *
     * @return The map of stored materials and amounts.
     */
    public ArrayList<String> getStoredMaterials() {
        return storedMaterialStrings;
    }

    /**
     * Add a material and amount to the stored materials map.
     *
     * @param material The material to add.
     * @param amount The amount of the material to add.
     */
    public void addStoredMaterial(String material, int amount) {
      //Todo
    }

    /**
     * Remove a material and amount from the stored materials map.
     *
     * @param material The material to remove.
     * @param amount The amount of the material to remove.
     * @return True if the removal was successful, false otherwise.
     */
    public boolean removeStoredMaterial(String material, int amount) {
         //Todo:
            return false;
    }



    /**
     * Get the fuel ItemStack stored in the machine.
     *
     * @return The stored ItemStack, or null if there is no item stored.
     */
    public ItemStack getStoredFuelStack() {
        return fuelItemStack;
    }


    /**
     * Add a fuel ItemStack to the machine.
     *
     * @param itemStack The ItemStack to add.
     */
    public void setFuelItemStack(ItemStack itemStack, Player player) {
        if (fuelItemStack == null) {
            fuelItemStack = itemStack;
        } else {
            int amountToAdd = itemStack.getAmount();
            int spaceLeft = fuelItemStack.getMaxStackSize() - fuelItemStack.getAmount();
            if (amountToAdd <= spaceLeft) {
                fuelItemStack.setAmount(fuelItemStack.getAmount() + amountToAdd);
            } else {
                fuelItemStack.setAmount(fuelItemStack.getMaxStackSize());
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

    // todo: Not sure where to put this method. Feel free to move it (delete todo when read or moved)
    public void takeDamage(Integer damageAmount){
        Integer durability = getDurability();
        Integer durablityAfterDamage = durability - damageAmount;

        // Check if machine will be broken after taking damage
        if (durablityAfterDamage <= 0){
            setDurability(0);
            setState(CustomResourcesMachineState.Broken);
            // todo: notify players the machine is broken (send message to members of town or create a persistent hologram on the machine's location)
        } else {
            setDurability(durablityAfterDamage);
        }
    }

    // todo: Not sure where to put this method. Feel free to move it (delete todo when read or moved)
    public void repair(){
        // if machine is in repairing state, repair the machine. otherwise, set it to repairing
        if (getState() == CustomResourcesMachineState.Repairing){
            Integer maxDurability = getMaxDurability();
            setDurability(maxDurability);
            setState(CustomResourcesMachineState.Active);
            // todo: notify players the machine has been repaired (send message to members of town or create a persistent hologram on the machine's location)
        } else {
            setState(CustomResourcesMachineState.Repairing);
        }
    }

    // todo: Not sure where to put this method. Feel free to move it (delete todo when read or moved)
    public void upgrade(){
        // if machine is in upgrading state, upgrade the machine. otherwise, set it to upgrading
        if (getState() == CustomResourcesMachineState.Upgrading){
            setTier(getTier() + 1);
            setState(CustomResourcesMachineState.Active);
            // todo: (player feedback) notify players the machine has been upgraded (send message to members of town or create a persistent hologram on the machine's location)
        } else {
            setState(CustomResourcesMachineState.Upgrading);
        }
    }
}