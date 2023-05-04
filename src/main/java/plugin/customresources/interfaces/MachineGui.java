package plugin.customresources.interfaces;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.Translatable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import plugin.customresources.CustomResources;
import plugin.customresources.metadata.CustomResourcesGovernmentMetaDataController;
import plugin.customresources.objects.Machine;
import plugin.customresources.objects.MachineConfig;
import plugin.customresources.objects.MachineTier;
import plugin.customresources.util.CustomResourcesMessagingUtil;

import java.util.*;

import static plugin.customresources.controllers.TownMachineManager.getMachine;
import static plugin.customresources.settings.CustomResourcesMachineConfig.MACHINES;
import static plugin.customresources.settings.MachineDataHandler.saveMachines;
import static plugin.customresources.interfaces.ConfirmGui.onConfirmationInteract;
import static plugin.customresources.interfaces.ConfirmGui.openConfirmation;
import static plugin.customresources.util.ItemStackUtil.createGuiItem;
import static plugin.customresources.util.ItemStackUtil.createMachineIcon;

public class MachineGui {

    // Create new GUI inventory
    public static void createMachineInterface(Player player, Machine machine) {
        String type = machine.getType();
        String state = String.valueOf(machine.getState());
        Integer tier = machine.getTier();

        String title = ChatColor.translateAlternateColorCodes('&', ("&a" + state + " &7| " + type + " | " + " [&6 " + tier + " &7]"));

        Inventory inventory = Bukkit.createInventory(null, 36, title);
        populateMachineInterface(inventory, machine);
        openInventory(player, inventory);
    }

    private static void populateMachineInterface(Inventory inventory, Machine machine) {
        MachineConfig config = MACHINES.get(machine.getType());
        Material icon = Material.valueOf((config.getIcon()));

        ItemStack machineIcon = createMachineIcon(icon, config.getName(), machine, "");
        inventory.setItem(22, machineIcon);

        // Add destroy machine button
        inventory.setItem(20, createGuiItem(Material.REDSTONE_BLOCK, "Destroy Machine",  ChatColor.RED + "Click to destroy this machine"));

        // Add upgrade machine button
        inventory.setItem(24, createGuiItem(Material.ANVIL, "Upgrade Machine",  ChatColor.GREEN + "Click to upgrade this machine"));

        // Add item input slot
        ItemStack inputSlot = createGuiItem(Material.HOPPER, "Input Slot",  ChatColor.GRAY + "Drag and drop items here to input them");
        if (machine.getStoredFuelStack() != null) {
            inputSlot = machine.getStoredFuelStack();
        }
        inventory.setItem(13, inputSlot);

        // Add collect button if machine has stored resources
        if (machine.getStoredResourcesInteger() != null) {
            int storedResources = machine.getStoredResourcesInteger();
            ItemStack chestItem = createGuiItem(Material.CHEST, "Stored Resources", ChatColor.GRAY + "This machine has " + storedResources + " stored resources");
            inventory.setItem(15, chestItem);
        }
    }

    public static void openInventory(final HumanEntity ent, Inventory inventory) {
        ent.openInventory(inventory);
    }

    // On click inside inventory, perform action
    public static void onInterfaceInteract(InventoryClickEvent event) {
            if (event.getInventory() instanceof MachineGui) {

                ItemStack clickedItem = event.getCurrentItem();

                if (clickedItem == null || clickedItem.getType().isAir()) return;

                // Retrieve the machine ID from the storage item's metadata
                ItemStack item = event.getInventory().getItem(22);
                ItemMeta meta = item.getItemMeta();
                PersistentDataContainer data = meta.getPersistentDataContainer();
                String machineId = data.get(new NamespacedKey(CustomResources.getPlugin(), "machineId"), PersistentDataType.STRING);

                // Retrieve the machine object using the getFromID() method
                Machine machine = getMachine(machineId);

                Player player = (Player) event.getWhoClicked();

                // Destroy machine button
                if (clickedItem.getType() == Material.REDSTONE_BLOCK) {
                    event.setCancelled(true);
                    openConfirmation(ConfirmGui.ConfirmationAction.DESTROY, player, machine);
                }

                // Upgrade machine button
                if (clickedItem.getType() == Material.ANVIL) {
                    event.setCancelled(true);
                    openConfirmation(ConfirmGui.ConfirmationAction.UPGRADE, player, machine);
                }

                // Collect machine button
                if (clickedItem.getType() == Material.CHEST) {
                    event.setCancelled(true);
                    handleCollectButton(event, machine);
                }

                if (event.getRawSlot() == 13) {
                    handleInputSlot(event, machine);
                }
            }
        onConfirmationInteract(event);
    }

    private static void handleCollectButton(InventoryClickEvent event, Machine machine) {
        MachineTier tierConfig = MACHINES.get(machine.getType()).getTiers().get(machine.getTier());
        Player player = (Player) event.getWhoClicked();
        Town government = TownyAPI.getInstance().getTown(player);

        //TODO: Implement resource randomness
        String resourceMaterial = tierConfig.getOutputMaterials().get(0);
        int resourceAmount = tierConfig.getOutputAmounts().get(0) * machine.getStoredResourcesInteger();

        //Calculate stuff to give player
        //TODO: replace with an item stack builder
        ItemStack itemStack = new ItemStack(Material.getMaterial(resourceMaterial), resourceAmount);

        //See if player can hold any items at all.
        PlayerInventory inv = player.getInventory();

        if (inv.firstEmpty() == -1) {
            CustomResourcesMessagingUtil.sendMsg(player, Translatable.of("customresources.resource.you_have_no_room_in_your_inventory"));
            return;
        }
        //Give items
        inv.addItem(itemStack);
            //Clear available list
            CustomResourcesGovernmentMetaDataController.setAvailableForCollection(government, Collections.emptyMap());

        //Save machine
        saveMachines();
    }


    private static void handleInputSlot(InventoryClickEvent event, Machine machine) {
        ItemStack inputItem = event.getCursor();
        ItemStack storedItem = machine.getStoredFuelStack();
        if (inputItem == null || inputItem.getType() == Material.AIR) {
            return;
            // Player has clicked with empty hand, so we don't need to add anything
        } else if (storedItem == null) {
            // Machine has no stored item yet, so we can simply set the stored item to the clicked item
            if (MACHINES.get(machine.getType()).getTiers().get(machine.getTier()).getInputItems().contains(inputItem.getType())) {
                machine.setFuelItemStack(inputItem, (Player) event.getWhoClicked());
                inputItem.setAmount(0);
            }
        } else if (storedItem.getType() == inputItem.getType() && storedItem.getAmount() < storedItem.getMaxStackSize()) {
            // Player has clicked with an item that matches the stored item, so we add it to the stored item stack
            if (MACHINES.get(machine.getType()).getTiers().get(machine.getTier()).getInputItems().contains(inputItem.getType())) {
                int amountToAdd = Math.min(inputItem.getAmount(), storedItem.getMaxStackSize() - storedItem.getAmount());
                storedItem.setAmount(storedItem.getAmount() + amountToAdd);
                inputItem.setAmount(inputItem.getAmount() - amountToAdd);
                event.setCurrentItem(storedItem);
            }
        } else {
            // Player has clicked with an item that doesn't match the stored item, so we don't add anything
            return;
        }
        event.getView().setCursor(inputItem);
    }

}