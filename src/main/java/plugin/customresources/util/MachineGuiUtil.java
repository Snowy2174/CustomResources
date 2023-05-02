package plugin.customresources.util;

import com.google.gson.Gson;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import plugin.customresources.CustomResources;
import plugin.customresources.objects.Machine;
import plugin.customresources.objects.MachineConfig;

import java.util.Arrays;
import java.util.UUID;

import static plugin.customresources.controllers.TownMachineManager.getMachine;
import static plugin.customresources.settings.CustomResourcesMachineConfig.MACHINES;
import static plugin.customresources.util.ConfirmGuiUtil.onConfirmationInteract;
import static plugin.customresources.util.ConfirmGuiUtil.openConfirmation;

public class MachineGuiUtil {
    // Main interface inventory
    private static Inventory inventory;

    // Create new GUI inventory
    public static void createMachineInterface(Player player, Machine machine) {
        String type = machine.getType();
        String state = String.valueOf(machine.getState());
        Integer tier = machine.getTier();

        String title = ChatColor.translateAlternateColorCodes('&', ("&a" + state + " &7| " + type + " | " + " [&6 " + tier + " &7]"));

        inventory = Bukkit.createInventory(null, 36, title);
        populateMachineInterface(inventory, machine);
        openInventory(player);
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
        if (machine.getStoredItem() != null) {
            inputSlot = machine.getStoredItem();
        }
        inventory.setItem(13, inputSlot);
    }

    protected static ItemStack createMachineIcon(final Material material, final String name, Machine machine, final String... lore) {
        final ItemStack item = new ItemStack(material);
        final ItemMeta meta = item.getItemMeta();

        // Set the name of the item
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));

        // Set the lore of the item
        meta.setLore(Arrays.asList(lore));

        meta.getPersistentDataContainer().set(new NamespacedKey(CustomResources.getPlugin(), "machineId"),
                PersistentDataType.STRING,
                machine.getId().toString());

        item.setItemMeta(meta);
        return item;
    }

    protected static ItemStack createGuiItem(final Material material, final String name, final String... lore) {
        final ItemStack item = new ItemStack(material);
        final ItemMeta meta = item.getItemMeta();

        // Set the name of the item
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));

        // Set the lore of the item
        meta.setLore(Arrays.asList(lore));

        item.setItemMeta(meta);
        return item;
    }


    public static void openInventory(final HumanEntity ent) {
        ent.openInventory(inventory);
    }

    // On click inside inventory, perform action
    public static void onInterfaceInteract(InventoryClickEvent event) {
        if (!event.getInventory().equals(ConfirmGuiUtil.confirmInventory)) {
            onConfirmationInteract(event);
        } else {
            if (!event.getInventory().equals(inventory)) {

                event.setCancelled(true);

                ItemStack clickedItem = event.getCurrentItem();

                if (clickedItem == null || clickedItem.getType().isAir()) return;

                // Retrieve the machine ID from the storage item's metadata
                ItemStack item = event.getInventory().getItem(22);
                ItemMeta meta = item.getItemMeta();
                PersistentDataContainer data = meta.getPersistentDataContainer();
                String machineId = data.get(new NamespacedKey(CustomResources.getPlugin(), "machineId"), PersistentDataType.STRING);

                // Retrieve the machine object using the getFromID() method
                Machine machine = getMachine(UUID.fromString(machineId));

                Player player = (Player) event.getWhoClicked();

                // Destroy machine button
                if (clickedItem.getType() == Material.REDSTONE_BLOCK) {
                    openConfirmation(ConfirmGuiUtil.ConfirmationAction.DESTROY, player, machine);
                }

                // Upgrade machine button
                if (clickedItem.getType() == Material.ANVIL) {
                    openConfirmation(ConfirmGuiUtil.ConfirmationAction.UPGRADE, player, machine);
                }

                if (event.getRawSlot() == 13) {
                    handleInputSlot(event, machine);
                }
            }

        }
    }

    private static void handleInputSlot(InventoryClickEvent event, Machine machine) {
        ItemStack inputItem = event.getCursor();
        ItemStack storedItem = machine.getStoredItem();
        if (inputItem == null || inputItem.getType() == Material.AIR) {
            return;
            // Player has clicked with empty hand, so we don't need to add anything
        } else if (storedItem == null) {
            // Machine has no stored item yet, so we can simply set the stored item to the clicked item
            if (MACHINES.get(machine.getType()).getTiers().get(machine.getTier()).getInputItems().contains(inputItem.getType())) {
                machine.setStoredItem(inputItem, (Player) event.getWhoClicked());
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