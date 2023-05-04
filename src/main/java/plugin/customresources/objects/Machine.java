package plugin.customresources.objects;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import plugin.customresources.enums.CustomResourcesMachineState;

import java.util.*;

public class Machine {

    private final Location location;
    private final String type;
    private CustomResourcesMachineState state;
    private Integer storedResources;
    private final String id;
    private final Integer tier;

    private ItemStack fuelItemStack = null;
    private ArrayList<String> storedMaterialStrings = new ArrayList<>();


    public Machine(String id, String type, Integer tier, Location location) {
        this.location = location;
        this.type = type;
        this.tier = tier;
        this.id = id;
        this.state = CustomResourcesMachineState.Active;
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

    /**
     * Set the state of the machine.
     *
     * @param state The state to set the machine to.
     */
    public void setState(CustomResourcesMachineState state) {
        this.state = state;
    }


    /**
     * Run the machine.
     */
    private void runMachine() {
        // TODO: Implement this method.
    }

    /**
     * Get the map of stored materials and amounts.
     *
     * @return The map of stored materials and amounts.
     */
    public Integer getStoredResourcesInteger() {
        return storedResources;
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
}