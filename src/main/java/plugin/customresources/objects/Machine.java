package plugin.customresources.objects;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.Translatable;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import plugin.customresources.metadata.CustomResourcesGovernmentMetaDataController;
import plugin.customresources.util.CustomResourcesMessagingUtil;

import static plugin.customresources.settings.CustomResourcesMachineConfig.MACHINES;
import static plugin.customresources.settings.MachineDataHandler.saveMachines;
import static plugin.customresources.util.ItemStackUtil.build;

import java.util.*;

public class Machine {

    private final Location location;
    private final String type;
    private CustomResourcesMachineState state;
    private Integer storedResources;
    private Integer resourceType;
    private final String id;
    private Integer tier;
    private Integer durability;

    private ItemStack fuelItemStack = null;
    private ArrayList<String> storedMaterialStrings = new ArrayList<>();


    public Machine(String id, String type, Integer tier, Location location) {
        this.type = type;
        this.tier = tier;
        this.id = id;

        this.location = location;
        this.state = CustomResourcesMachineState.Active;

        this.durability = getTierConfig().getDurability();
        this.resourceType = randomResourceType();
        this.storedResources = 0;
    }

    public enum CustomResourcesMachineState {
        Active, Broken, Upgrading, Repairing

    }

    private Integer randomResourceType() {
        Random random = new Random();
        List<String> resources = getTierConfig().getOutputMaterials();
        return random.nextInt(resources.size());
    }

    private void setResourceType(Integer i) {
        this.resourceType = i;
    }

    public Integer getResourceType() {
        return resourceType;
    }

    /**
     * Get the type of the machine.
     *
     * @return The type of the machine.
     */
    public String getType() {
        return type;
    }

    public MachineConfig getTypeConfig() {
        return MACHINES.get(type);
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
    public MachineTier getTierConfig() {
        return MACHINES.get(type).getTiers().get(tier);
    }

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
     * Set the model of the machine.
     */



    /**
     * Collect resources from the machine
     */
    public  void collectResources(Player player) {
        //See if player can hold any items at all.
        PlayerInventory inv = player.getInventory();

        if (inv.firstEmpty() == -1) {
            CustomResourcesMessagingUtil.sendMsg(player, Translatable.of("customresources.resource.you_have_no_room_in_your_inventory"));
            return;
        }

        Town government = TownyAPI.getInstance().getTown(player);

        MachineTier tierConfig = getTierConfig();
        Integer resourceAmount = getStoredResourcesInteger() * tierConfig.getOutputAmounts().get(getResourceType());

        ItemStack items = build(tierConfig.getOutputMaterials().get(getResourceType()));
        items.setAmount(resourceAmount);

        //Give items
        inv.addItem(items);
        //Clear available list
        clearStoredResourcesInteger();
        CustomResourcesGovernmentMetaDataController.setAvailableForCollection(government, Collections.emptyMap());

        //Save machine
        saveMachines();
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
    private void clearStoredResourcesInteger() {
        this.storedResources = 0;
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
     * Check if an item's material matches one of the stored material strings.
     *
     * @param item The item to check.
     * @return True if the item's material matches one of the stored material strings, false otherwise.
     */
    public boolean isStoredMaterial(ItemStack item) {
        Material material = item.getType();
        for (String storedMaterialString : storedMaterialStrings) {
            String[] parts = storedMaterialString.split(":");
            if (parts.length == 2) {
                Material storedMaterial = Material.getMaterial(parts[0]);
                if (storedMaterial != null && storedMaterial.equals(material)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check if an item's material matches one of the stored material strings.
     *
     * @param material The item to check.
     * @return The stored integer of a material within a machine
     */
    public int getStoredMaterialAmount(Material material) {
        for (String storedMaterialString : storedMaterialStrings) {
            String[] parts = storedMaterialString.split(":");
            if (parts.length == 2) {
                Material storedMaterial = Material.getMaterial(parts[0]);
                if (storedMaterial != null && storedMaterial.equals(material)) {
                    return Integer.parseInt(parts[1]);
                }
            }
        }
        return 0;
    }


    /**
     * Add a material and amount to the stored materials map.
     *
     * @param material The material to add.
     * @param amount   The amount of the material to add.
     * @return
     */
    public boolean addStoredMaterial(Material material, int amount) {
        boolean materialFound = false;
        for (int i = 0; i < storedMaterialStrings.size(); i++) {
            String storedMaterial = storedMaterialStrings.get(i);
            if (storedMaterial.startsWith(material.name() + ":")) {
                int storedAmount = Integer.parseInt(storedMaterial.substring(material.name().length() + 1));
                storedAmount += amount;
                storedMaterialStrings.set(i, material.name() + ":" + storedAmount);
                materialFound = true;
                break;
            }
        }
        if (!materialFound) {
            String materialString = material.name() + ":" + amount;
            storedMaterialStrings.add(materialString);
            materialFound = true;
        }
        return materialFound;
    }


    /**
     * Remove a material and amount from the stored materials map.
     *
     * @param material The material to remove.
     * @param amount The amount of the material to remove.
     * @return True if the removal was successful, false otherwise.
     */
    public boolean removeStoredMaterial(String material, int amount) {
        for (int i = 0; i < storedMaterialStrings.size(); i++) {
            String storedMaterial = storedMaterialStrings.get(i);
            if (storedMaterial.startsWith(material + ":")) {
                int storedAmount = Integer.parseInt(storedMaterial.substring(material.length() + 1));
                storedAmount -= amount;
                if (storedAmount > 0) {
                    storedMaterialStrings.set(i, material + ":" + storedAmount);
                } else {
                    storedMaterialStrings.remove(i);
                }
                return true;
            }
        }
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

            setResourceType(randomResourceType());
            setDurability(getTierConfig().getDurability());
            clearStoredResourcesInteger();

            // todo: update the model of the structure, either through replacement or model data modification
            setState(CustomResourcesMachineState.Active);
            // todo: (player feedback) notify players the machine has been upgraded (send message to members of town or create a persistent hologram on the machine's location)
        } else {
            setState(CustomResourcesMachineState.Upgrading);

            //Set the machine to be ready to accept upgrade materials
            List<String> upgradeMaterials = getTierConfig().getUpgradeMaterialTypes();
            for (String material : upgradeMaterials) {
                addStoredMaterial(Material.valueOf(material), 0);
            }

        }
    }
}